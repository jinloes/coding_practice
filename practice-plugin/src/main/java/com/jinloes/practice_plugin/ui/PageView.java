package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.JComponent;
import java.util.function.Consumer;

/**
 * Shows a {@link PracticePage}. The tool window prefers the React pages in JCEF and falls back to the
 * Swing HTML renderer when the IDE runtime has no JCEF.
 */
interface PageView {
    JComponent component();

    void show(PracticePage page);

    /** Creates a React page when JCEF is available, else the Swing HTML fallback; owned by {@code parent}. */
    static PageView create(Disposable parent, Consumer<PageEvent> events) {
        var application = ApplicationManager.getApplication();
        // Unit tests run headless and assert on the HTML renderer, so they never start Chromium.
        ReactPages react = application.isUnitTestMode() ? null : application.getService(ReactPages.class);
        if (react != null && react.available()) {
            return react.create(parent, events);
        }
        return new HtmlPageView();
    }
}
