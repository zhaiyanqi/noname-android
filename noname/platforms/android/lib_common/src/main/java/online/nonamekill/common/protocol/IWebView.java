package online.nonamekill.common.protocol;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Message;

public interface IWebView {
    void setHorizontalScrollbarOverlay(boolean overlay);

    void setVerticalScrollbarOverlay(boolean overlay);

    boolean overlayHorizontalScrollbar();

    boolean overlayVerticalScrollbar() ;

    void savePassword(String host, String username, String password);

    void setHttpAuthUsernamePassword(String host, String realm, String username, String password);

    String[] getHttpAuthUsernamePassword(String host, String realm);

    void destroy();

    void loadUrl(String url);

    void loadData(String data, String mimeType, String encoding);

    void loadDataWithBaseURL(String baseUrl, String data,
                                    String mimeType, String encoding, String failUrl);

    void stopLoading();

    void reload();

    boolean canGoBack();

    void goBack();

    boolean canGoForward();

    void goForward();

    boolean canGoBackOrForward(int steps);

    void goBackOrForward(int steps);

    boolean pageUp(boolean top);

    boolean pageDown(boolean bottom);

    void clearView();

    Picture capturePicture();

    float getScale();

    void setInitialScale(int scaleInPercent);

    void invokeZoomPicker();

    void requestFocusNodeHref(Message hrefMsg);

    void requestImageRef(Message msg);

    String getUrl();

    String getTitle();

    Bitmap getFavicon();

    int getProgress();

    int getContentHeight();

    void pauseTimers();

    void resumeTimers();

    void clearFormData();

    void clearHistory();

    void clearSslPreferences();

    void documentHasImages(Message response);

    void addJavascriptInterface(Object obj, String interfaceName);

    boolean zoomIn();

    boolean zoomOut();
}
