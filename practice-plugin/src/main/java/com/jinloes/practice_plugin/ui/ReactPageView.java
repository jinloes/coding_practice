package com.jinloes.practice_plugin.ui;

import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.JComponent;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Renders a {@link PracticePage} with the bundled React app ({@code web/}) in an embedded Chromium browser.
 *
 * <p>The page is one self-contained HTML file. After it loads, this view injects {@code window.practiceSend},
 * which forwards page events to Java through a {@link JBCefJSQuery}, and then calls
 * {@code window.practiceRender} with the {@link PageJson} state. The state is pushed again whenever the
 * page changes or the IDE theme changes.
 */
final class ReactPageView implements PageView {
    static final String PAGE = "/web/index.html";
    private static final Logger LOG = Logger.getInstance(ReactPageView.class);

    private final JBCefBrowser browser;
    private final JBCefJSQuery query;
    private PracticePage shown;
    private boolean loaded;
    private boolean disposed;

    ReactPageView(Disposable parent, Consumer<PageEvent> events) {
        browser = JBCefBrowser.createBuilder().build();
        Disposer.register(parent, browser);
        Disposer.register(browser, () -> disposed = true);
        query = JBCefJSQuery.create((JBCefBrowserBase) browser);
        Disposer.register(browser, query);
        query.addHandler(message -> {
            try {
                PageEvent event = PageEvent.parse(message);
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!disposed) {
                        events.accept(event);
                    }
                });
            } catch (IllegalArgumentException exception) {
                LOG.warn(exception.getMessage());
            }
            return null;
        });
        browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser cef, CefFrame frame, int httpStatusCode) {
                if (frame.isMain()) {
                    cef.executeJavaScript("window.practiceSend = function(message) {"
                            + query.inject("message") + "};", cef.getURL(), 0);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        loaded = true;
                        push();
                    });
                }
            }
        }, browser.getCefBrowser());
        ApplicationManager.getApplication().getMessageBus().connect(browser)
                .subscribe(LafManagerListener.TOPIC, source -> push());
        browser.loadHTML(Html.PAGE);
    }

    @Override
    public JComponent component() {
        return browser.getComponent();
    }

    @Override
    public void show(PracticePage page) {
        shown = page;
        push();
    }

    private void push() {
        if (!loaded || disposed || shown == null) {
            return;
        }
        String state = PageJson.toJson(shown, PageJson.Theme.current());
        CefBrowser cef = browser.getCefBrowser();
        cef.executeJavaScript("window.practiceRender(" + state + ");", cef.getURL(), 0);
    }

    /** The bundled page, read once and shared by every view. */
    private static final class Html {
        static final String PAGE = read();

        private static String read() {
            try (InputStream input = Objects.requireNonNull(
                    ReactPageView.class.getResourceAsStream(ReactPageView.PAGE), ReactPageView.PAGE)) {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }
    }
}
