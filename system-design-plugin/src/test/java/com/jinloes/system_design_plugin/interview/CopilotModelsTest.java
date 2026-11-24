package com.jinloes.system_design_plugin.interview;

import com.microsoft.copilot.generated.rpc.Model;
import com.microsoft.copilot.generated.rpc.ModelPolicy;
import com.microsoft.copilot.generated.rpc.ModelPolicyState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CopilotModelsTest {
    private static Model info(String id, String name, ModelPolicyState policyState, boolean withPolicy) {
        ModelPolicy policy = withPolicy ? new ModelPolicy(policyState, null) : null;
        return new Model(id, name, null, null, null, policy, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void keepsEnabledOrUnrestrictedModelsInOrder() {
        List<CopilotModels.Model> models = CopilotModels.from(Arrays.asList(
                info("b-model", "B", ModelPolicyState.ENABLED, true),
                info("a-model", "A", null, true),
                info("c-model", "C", null, false),
                info("off", "Off", ModelPolicyState.DISABLED, true),
                info("unconfigured", "U", ModelPolicyState.UNCONFIGURED, true),
                null));
        assertThat(models).extracting(CopilotModels.Model::id).containsExactly("b-model", "a-model", "c-model");
    }

    @Test
    void dropsInvalidAndDuplicateIdsAndDefaultsBlankNames() {
        List<CopilotModels.Model> models = CopilotModels.from(List.of(
                info("gpt-5.1:preview_x", "  Named  ", null, false),
                info("gpt-5.1:preview_x", "Duplicate", null, false),
                info("has space", "Bad", null, false),
                info("-leading", "Bad", null, false),
                info("x".repeat(101), "Too long", null, false),
                info("y".repeat(100), " ", null, false),
                info("blank", null, null, false),
                info(null, "No id", null, false)));
        assertThat(models).containsExactly(new CopilotModels.Model("gpt-5.1:preview_x", "Named"),
                new CopilotModels.Model("y".repeat(100), "y".repeat(100)),
                new CopilotModels.Model("blank", "blank"));
    }

    @Test
    void capsTheListAndToleratesNull() {
        List<Model> many = new ArrayList<>();
        for (int index = 0; index < CopilotModels.MAX_MODELS + 5; index++) {
            many.add(info("m" + index, "M", null, false));
        }
        assertThat(CopilotModels.from(many)).hasSize(CopilotModels.MAX_MODELS)
                .last().extracting(CopilotModels.Model::id).isEqualTo("m199");
        assertThat(CopilotModels.from(null)).isEmpty();
    }

    @Test
    void dropdownIdsPreservesCustomTypedModelWithoutDuplicatingListedOnes() {
        List<CopilotModels.Model> models = List.of(
                new CopilotModels.Model("gpt-5", "GPT 5"),
                new CopilotModels.Model("claude-sonnet", "Claude Sonnet"));

        assertThat(CopilotModels.dropdownIds(models, null)).containsExactly("", "gpt-5", "claude-sonnet");
        assertThat(CopilotModels.dropdownIds(models, "   ")).containsExactly("", "gpt-5", "claude-sonnet");
        assertThat(CopilotModels.dropdownIds(models, " claude-sonnet "))
                .containsExactly("", "gpt-5", "claude-sonnet");
        assertThat(CopilotModels.dropdownIds(models, " custom-preview-1 "))
                .containsExactly("", "custom-preview-1", "gpt-5", "claude-sonnet");
        assertThat(CopilotModels.dropdownIds(null, "custom-only")).containsExactly("", "custom-only");
    }
}
