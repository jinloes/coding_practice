package com.jinloes.plugin_common.copilot;

import com.microsoft.copilot.generated.rpc.Model;
import com.microsoft.copilot.generated.rpc.ModelPolicy;
import com.microsoft.copilot.generated.rpc.ModelPolicyState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CopilotModelsTest {

    @Test
    void filtersEnabledValidUniqueModelsAndCapsAtMaxModels() {
        assertThat(CopilotModels.from(null)).isEmpty();

        List<Model> raw = new ArrayList<>();
        raw.add(null);
        raw.add(modelWithPolicy("gpt-5", "  GPT-5  ", null));
        raw.add(model("gpt-5", "Duplicate", ModelPolicyState.ENABLED));
        raw.add(model("claude-sonnet", "", ModelPolicyState.ENABLED));
        raw.add(modelWithPolicy("o3-mini", null, null));
        raw.add(modelWithPolicy("policy-no-state", "No State", policyWithNullState()));
        raw.add(model("disabled-model", "Disabled", ModelPolicyState.DISABLED));
        raw.add(model("bad id with space", "Bad", ModelPolicyState.ENABLED));
        raw.add(model(null, "Missing Id", ModelPolicyState.ENABLED));

        List<CopilotModels.Model> parsed = CopilotModels.from(raw);
        assertThat(parsed).containsExactly(
                new CopilotModels.Model("gpt-5", "GPT-5"),
                new CopilotModels.Model("claude-sonnet", "claude-sonnet"),
                new CopilotModels.Model("o3-mini", "o3-mini"),
                new CopilotModels.Model("policy-no-state", "No State"));

        List<Model> overflow = new ArrayList<>();
        for (int i = 0; i < CopilotModels.MAX_MODELS + 5; i++) {
            overflow.add(model("model-" + i, "Model " + i, ModelPolicyState.ENABLED));
        }
        assertThat(CopilotModels.from(overflow)).hasSize(CopilotModels.MAX_MODELS);
    }

    @Test
    void buildsDropdownIdsWithCliDefaultAndPreservedCustomModel() {
        List<CopilotModels.Model> models = List.of(
                new CopilotModels.Model("gpt-5", "GPT-5"),
                new CopilotModels.Model("claude-sonnet", "Claude Sonnet"));

        assertThat(CopilotModels.dropdownIds(models, "")).containsExactly("", "gpt-5", "claude-sonnet");
        assertThat(CopilotModels.dropdownIds(models, null)).containsExactly("", "gpt-5", "claude-sonnet");
        assertThat(CopilotModels.dropdownIds(models, "  gpt-5  ")).containsExactly("", "gpt-5", "claude-sonnet");
        assertThat(CopilotModels.dropdownIds(models, " custom-model "))
                .containsExactly("", "custom-model", "gpt-5", "claude-sonnet");
        assertThat(CopilotModels.dropdownIds(null, "custom-model")).containsExactly("", "custom-model");
    }

    private static Model model(String id, String name, ModelPolicyState policyState) {
        ModelPolicy policy = policyState == null ? null : new ModelPolicy(policyState, null);
        return modelWithPolicy(id, name, policy);
    }

    private static Model modelWithPolicy(String id, String name, ModelPolicy policy) {
        return new Model(id, name, null, null, null, policy, null, null, null, null, null, null, null, null, null, null);
    }

    private static ModelPolicy policyWithNullState() {
        return new ModelPolicy(null, null);
    }
}
