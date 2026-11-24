package com.jinloes.plugin_common.copilot;

import com.microsoft.copilot.generated.rpc.ModelPolicyState;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/** Filters the Copilot SDK's model list down to IDs the settings dialog can offer and pass as {@code --model}. */
public final class CopilotModels {
    public static final int MAX_MODELS = 200;
    private static final Pattern ID = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._:-]{0,99}");

    /** A model the account can use; {@code id} is what the CLI accepts as {@code --model}. */
    public record Model(String id, String name) {
    }

    private CopilotModels() {
    }

    /** Keeps enabled (or unrestricted) models with valid IDs, drops duplicates, keeps order, and caps the list. */
    public static List<Model> from(List<com.microsoft.copilot.generated.rpc.Model> infos) {
        List<Model> models = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        if (infos == null) {
            return models;
        }
        for (com.microsoft.copilot.generated.rpc.Model info : infos) {
            if (models.size() == MAX_MODELS) {
                break;
            }
            if (info == null || !enabled(info)) {
                continue;
            }
            String id = info.id();
            if (id == null || !ID.matcher(id).matches() || !seen.add(id)) {
                continue;
            }
            String name = info.name();
            models.add(new Model(id, name == null || name.isBlank() ? id : name.strip()));
        }
        return models;
    }

    /**
     * Builds the settings dropdown's model ID list: {@code ""} (CLI default) first, followed by {@code currentModel}
     * when it is non-blank and absent from {@code models}, followed by each discovered model ID in order.
     */
    public static List<String> dropdownIds(List<Model> models, String currentModel) {
        List<String> ids = new ArrayList<>();
        ids.add("");
        List<Model> entries = models == null ? List.of() : models;
        String current = currentModel == null ? "" : currentModel.strip();
        if (!current.isEmpty() && entries.stream().noneMatch(entry -> entry.id().equals(current))) {
            ids.add(current);
        }
        for (Model entry : entries) {
            ids.add(entry.id());
        }
        return List.copyOf(ids);
    }

    private static boolean enabled(com.microsoft.copilot.generated.rpc.Model info) {
        if (info.policy() == null) {
            return true;
        }
        ModelPolicyState state = info.policy().state();
        return state == null || ModelPolicyState.ENABLED.equals(state);
    }
}
