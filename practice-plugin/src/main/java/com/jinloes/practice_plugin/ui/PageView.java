package com.jinloes.practice_plugin.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Shows a {@link PracticePage}. The tool window prefers the React pages in JCEF and falls back to the
 * Swing HTML renderer when the IDE runtime has no JCEF.
 */
interface PageView {
    JComponent component();

    void show(PracticePage page);

    default void setActive(boolean active) {
    }

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

    /**
     * Defers creating the underlying {@link PageView} and pushing state to it until {@link #setActive(boolean)}
     * marks it visible, so hidden tabs neither start a Chromium instance nor serialize page updates.
     */
    static PageView lazy(Disposable parent, Consumer<PageEvent> events, boolean initiallyActive) {
        return lazy(() -> create(parent, events), initiallyActive);
    }

    static PageView lazy(Supplier<PageView> factory, boolean initiallyActive) {
        return new LazyPageView(factory, initiallyActive);
    }
}

final class LazyPageView implements PageView {
    private final Supplier<PageView> factory;
    private final JPanel container = new JPanel(new BorderLayout());
    private PageView delegate;
    private PracticePage latest;
    private PracticePage pushed;
    private boolean active;

    LazyPageView(Supplier<PageView> factory, boolean initiallyActive) {
        this.factory = Objects.requireNonNull(factory, "factory");
        if (initiallyActive) {
            setActive(true);
        }
    }

    @Override
    public JComponent component() {
        return container;
    }

    @Override
    public void show(PracticePage page) {
        latest = page;
        flush();
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (active && delegate == null) {
            delegate = factory.get();
            container.add(delegate.component(), BorderLayout.CENTER);
            container.revalidate();
            container.repaint();
        }
        flush();
    }

    private void flush() {
        if (active && delegate != null && latest != null && !latest.equals(pushed)) {
            pushed = latest;
            delegate.show(latest);
        }
    }
}
