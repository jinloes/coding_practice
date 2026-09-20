package com.jinloes.practice_plugin.platform;

/** The host operating system facts the plugin branches on. */
public final class Os {
    /**
     * Whether the plugin is running on Windows. Read once: the host OS cannot change while the IDE
     * is running, and a single constant keeps every platform branch answering the same question.
     */
    public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    private Os() {
    }
}
