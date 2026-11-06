package com.jinloes.system_design_plugin.ui;

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
import java.util.function.Consumer;

/**
 * One bundled page in an embedded browser on the plugin's private origin. Messages the page sends through
 * {@code window.designSend} reach {@code messages} on the calling CEF thread; {@code onReady} runs on the EDT
 * after each load and whenever the IDE theme changes.
 */
final class BrowserPage implements Disposable {
    private static final Logger LOG = Logger.getInstance(BrowserPage.class);

    private final JBCefBrowser browser;
    private volatile boolean loaded;
    private volatile boolean disposed;

    BrowserPage(Disposable parent, String url, Consumer<String> messages, Runnable onReady) {
        browser = JBCefBrowser.createBuilder().build();
        Disposer.register(parent, this);
        Disposer.register(this, browser);
        JBCefJSQuery query = JBCefJSQuery.create((JBCefBrowserBase) browser);
        Disposer.register(browser, query);
        query.addHandler(message -> {
            if (!disposed) {
                try {
                    messages.accept(message);
                } catch (RuntimeException exception) {
                    LOG.warn("Page message failed", exception);
                }
            }
            return null;
        });
        CefBrowser cef = browser.getCefBrowser();
        browser.getJBCefClient().addRequestHandler(new WebAssetHandler(), cef);
        browser.getJBCefClient().addLifeSpanHandler(new WebAssetHandler.Popups(), cef);
        browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser loadedBrowser, CefFrame frame, int httpStatusCode) {
                if (frame.isMain()) {
                    loadedBrowser.executeJavaScript("window.designSend = function(message) {"
                            + query.inject("message") + "};window.dispatchEvent(new Event('designbridge'));",
                            loadedBrowser.getURL(), 0);
                    loaded = true;
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!disposed) {
                            onReady.run();
                        }
                    });
                }
            }
        }, cef);
        ApplicationManager.getApplication().getMessageBus().connect(this)
                .subscribe(LafManagerListener.TOPIC, source -> {
                    if (loaded && !disposed) {
                        onReady.run();
                    }
                });
        browser.loadURL(url);
    }

    JComponent component() {
        return browser.getComponent();
    }

    boolean isLoaded() {
        return loaded && !disposed;
    }

    /** Runs a script in the page when it is loaded; otherwise does nothing. Callable from any thread. */
    void execute(String script) {
        if (isLoaded()) {
            CefBrowser cef = browser.getCefBrowser();
            cef.executeJavaScript(script, cef.getURL(), 0);
        }
    }

    @Override
    public void dispose() {
        disposed = true;
    }
}
