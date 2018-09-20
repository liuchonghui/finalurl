package tools.android.finalurl;

import java.net.HttpURLConnection;
import java.net.URL;

public class FinalUrl implements Runnable {
    String origUrl;
    OnReceiveUrlListener listener;
    int timeoutMillis = 5000;

    public FinalUrl(String origUrl, OnReceiveUrlListener listener) {
        this.origUrl = origUrl;
        this.listener = listener;
    }

    public FinalUrl(String origUrl, OnReceiveUrlListener listener, int timeout) {
        this(origUrl, listener);
        if (timeout > 0 && timeout < Integer.MAX_VALUE) {
            this.timeoutMillis = timeout;
        }
    }

    public interface OnReceiveUrlListener {
        void onSuccess(String resultUrl);

        void onFailure();
    }

    @Override
    public void run() {
        int responseCode = -1;
        final String originUrl = origUrl;
        String redirectUrl = null;
        try {
            URL url = new URL(originUrl);
            do {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setConnectTimeout(timeoutMillis);
                connection.setReadTimeout(timeoutMillis);
                connection.setUseCaches(false);
                connection.connect();
                responseCode = connection.getResponseCode();
                redirectUrl = connection.getHeaderField("Location");
                if (redirectUrl == null || redirectUrl.length() == 0) {
                    redirectUrl = connection.getHeaderField("location");
                }
                if (redirectUrl == null || redirectUrl.length() == 0) {
                    // 再找下去也没有L/location了，到此为止
                    redirectUrl = url.toString();
                    if (responseCode == 200) {
                    } else if (responseCode == 301 || responseCode == 302) {
                    } else {
                    }
                    break;
                } else {
                    // 内含L/location，继续找下一个L/location
                    url = new URL(redirectUrl);
                    if (responseCode == 200) {
                    } else if (responseCode == 301 || responseCode == 302) {
                    } else {
                    }
                }
            } while (responseCode == 301 || responseCode == 302); // 301和302都是重定向
        } catch (Throwable t) {
            t.printStackTrace();
        }
        String finalUrl = originUrl;
        if (redirectUrl != null && redirectUrl.length() > 0) {
            finalUrl = redirectUrl;
        }
        if (responseCode == 200) {
            listener.onSuccess(finalUrl);
        } else {
            listener.onFailure();
        }
    }
}