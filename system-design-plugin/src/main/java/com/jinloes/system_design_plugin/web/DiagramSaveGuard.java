package com.jinloes.system_design_plugin.web;

import java.io.IOException;
import java.util.Arrays;

/** Rejects whole-scene replacement when another editor has changed the loaded bytes. */
public final class DiagramSaveGuard {
    private byte[] baseline;

    public DiagramSaveGuard(byte[] loadedBytes) {
        baseline = loadedBytes.clone();
    }

    public void requireUnchanged(byte[] currentBytes) throws IOException {
        if (!Arrays.equals(baseline, currentBytes)) {
            throw new IOException("Another editor changed this diagram. Copy your unsaved scene before reopening it.");
        }
    }

    public void recordSaved(byte[] successfulBytes) {
        baseline = successfulBytes.clone();
    }
}
