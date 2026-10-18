package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.Disposable;
import com.intellij.ui.jcef.JBCefApp;

import java.util.function.Consumer;

/** The JCEF-backed {@link ReactPages} service, registered when the bundled JCEF plugin is present. */
final class JcefReactPages implements ReactPages {
    @Override
    public boolean available() {
        return JBCefApp.isSupported() && JcefReactPages.class.getResource(ReactPageView.PAGE) != null;
    }

    @Override
    public PageView create(Disposable parent, Consumer<PageEvent> events) {
        return new ReactPageView(parent, events);
    }
}
