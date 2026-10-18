package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.Disposable;

import java.util.function.Consumer;

/**
 * Creates React pages. Registered only through {@code META-INF/practice-jcef.xml}, the optional dependency
 * on the bundled JCEF plugin, so the tool window falls back to Swing HTML when JCEF is not installed.
 */
interface ReactPages {
    /** Whether this runtime can show an embedded browser and the plugin bundles the page. */
    boolean available();

    PageView create(Disposable parent, Consumer<PageEvent> events);
}
