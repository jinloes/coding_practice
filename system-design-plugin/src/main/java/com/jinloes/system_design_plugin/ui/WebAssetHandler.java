package com.jinloes.system_design_plugin.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.jinloes.system_design_plugin.web.WebAssets;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefCallback;
import org.cef.handler.CefLifeSpanHandlerAdapter;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.handler.CefResourceRequestHandlerAdapter;
import org.cef.misc.BoolRef;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Serves the bundled pages on {@link WebAssets#ORIGIN} and blocks every other request, navigation, and popup, so
 * the embedded browser never reaches the network. A popup opened by an explicit click on an http(s) link (for
 * example a link the learner attached to a shape) opens in the system browser instead.
 */
final class WebAssetHandler extends CefRequestHandlerAdapter {
    private static final Logger LOG = Logger.getInstance(WebAssetHandler.class);

    @Override
    public boolean onBeforeBrowse(CefBrowser browser, CefFrame frame, CefRequest request, boolean userGesture,
                                  boolean isRedirect) {
        String url = request.getURL();
        if (WebAssets.isOwnOrigin(url)) {
            return false;
        }
        LOG.debug("Blocked navigation to ", url);
        if (userGesture && !isRedirect && frame.isMain()) {
            openExternally(url);
        }
        return true;
    }

    @Override
    public boolean onOpenURLFromTab(CefBrowser browser, CefFrame frame, String url, boolean userGesture) {
        if (userGesture) {
            openExternally(url);
        }
        return true;
    }

    @Override
    public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame, CefRequest request,
                                                               boolean isNavigation, boolean isDownload,
                                                               String requestInitiator, BoolRef disableDefault) {
        disableDefault.set(true);
        Optional<WebAssets.Asset> asset = WebAssets.resolve(request.getURL());
        if (asset.isEmpty()) {
            LOG.debug("Blocked request for ", request.getURL());
        }
        return new CefResourceRequestHandlerAdapter() {
            @Override
            public CefResourceHandler getResourceHandler(CefBrowser cef, CefFrame cefFrame, CefRequest cefRequest) {
                return asset.<CefResourceHandler>map(AssetResource::new).orElseGet(Blocked::new);
            }
        };
    }

    /** Cancels popups; http(s) links open in the system browser. */
    static final class Popups extends CefLifeSpanHandlerAdapter {
        @Override
        public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String targetUrl, String targetFrameName) {
            LOG.debug("Blocked popup for ", targetUrl);
            openExternally(targetUrl);
            return true;
        }
    }

    private static void openExternally(String url) {
        if (url != null && (url.startsWith("https://") || url.startsWith("http://"))
                && !WebAssets.isOwnOrigin(url)) {
            BrowserUtil.browse(url);
        }
    }

    /*
     * Both handlers use the legacy processRequest/readResponse API: out-of-process (remote) JCEF does not forward
     * the newer open/read API, and an unhandled request fails with ERR_UNKNOWN_URL_SCHEME.
     */

    /** Cancels the request immediately. */
    private static final class Blocked extends CefResourceHandlerAdapter {
        @Override
        public boolean processRequest(CefRequest request, CefCallback callback) {
            return false;
        }
    }

    /** Streams one bundled classpath resource. */
    private static final class AssetResource extends CefResourceHandlerAdapter {
        private final WebAssets.Asset asset;
        private InputStream input;

        AssetResource(WebAssets.Asset asset) {
            this.asset = asset;
        }

        @Override
        public boolean processRequest(CefRequest request, CefCallback callback) {
            input = WebAssetHandler.class.getResourceAsStream(asset.resourcePath());
            if (input == null) {
                return false;
            }
            callback.Continue();
            return true;
        }

        @Override
        public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
            response.setStatus(200);
            response.setStatusText("OK");
            response.setMimeType(asset.mimeType());
            response.setHeaderByName("Cache-Control", "no-store", true);
            responseLength.set(-1);
        }

        @Override
        public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
            try {
                int count = input.read(dataOut, 0, bytesToRead);
                if (count <= 0) {
                    bytesRead.set(0);
                    close();
                    return false;
                }
                bytesRead.set(count);
                return true;
            } catch (IOException exception) {
                LOG.warn("Could not read " + asset.resourcePath(), exception);
                bytesRead.set(-2);
                close();
                return false;
            }
        }

        @Override
        public void cancel() {
            close();
        }

        private void close() {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                    // Nothing else to release.
                }
                input = null;
            }
        }
    }
}
