package tools.android.finalurl;

import android.os.Handler;
import android.os.HandlerThread;

import java.net.HttpURLConnection;
import java.net.URL;

public class FinalUrl {
    static FinalUrl instance;
    static int timeoutMillis = 5333;

    private FinalUrl() {
    }

    public static FinalUrl get() {
        if (instance == null) {
            synchronized (FinalUrl.class) {
                if (instance == null) {
                    instance = new FinalUrl();
                }
            }
        }
        return instance;
    }

    public FinalUrl setEnableLogcat(boolean enable) {
        LogUtil.enableLogcat(enable);
        return this;
    }

    private String TAG = "FU";

    public FinalUrl setLogtag(String tag) {
        if (tag != null && tag.length() > 0) {
            this.TAG = tag;
        }
        return this;
    }

    public FinalUrl setTimeoutMillis(long timeoutMillis) {
        if (timeoutMillis < 1333L || timeoutMillis > 13333L) {
            timeoutMillis = 5333L;
        }
        this.timeoutMillis = (int) timeoutMillis;
        return this;
    }

    Handler mHandler;

    private void init() {
        if (mHandler == null) {
            HandlerThread ht = new HandlerThread("finalurl-single-work-thread");
            ht.start();
            mHandler = new Handler(ht.getLooper());
        }
    }

    public void request(final String url, final OnFinalUrlListener listener) {
        if (url == null || url.length() == 0 || listener == null) {
            return;
        }
        init();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                request1(url, listener);
            }
        });
    }

    private void request1(String origUrl, OnFinalUrlListener listener) {
        LogUtil.d(TAG, "requestFinalUrl|url|" + origUrl + "|start");
        int i = 0;
        String lastUrl = null;
        int responseCode = -1;
        final String originUrl = origUrl;
        String redirectUrl = null;
        String errorMessage = "";
        try {
            URL url = new URL(originUrl);
            do {
                i++;
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setConnectTimeout(timeoutMillis);
                connection.setReadTimeout(timeoutMillis);
                // 下面理论上应该写，理由源自RFC2616协议：https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.4
//                    connection.setRequestMethod("HEAD"); // 但是许多国内服务器都不支持，比如优酷
                connection.setUseCaches(false);
                connection.connect();
                responseCode = connection.getResponseCode();
                redirectUrl = connection.getHeaderField("Location");
                if (redirectUrl == null || redirectUrl.length() == 0) {
                    redirectUrl = connection.getHeaderField("location");
                }
                if (redirectUrl == null || redirectUrl.length() == 0) {
                    // 再找下去也没有L/location了，到此为止
                    LogUtil.d(TAG, "requestFinalUrl|no more L/location|" + redirectUrl + "|responseCode|" + responseCode + "|stop here");
                    redirectUrl = url.toString();
                    if (responseCode == 200) {
                        LogUtil.d(TAG, "^____^.requestFinalUrl[200][" + i + "']from|" + lastUrl + "|to|" + redirectUrl);
                    } else if (responseCode == 301 || responseCode == 302) {
                        LogUtil.d(TAG, "⊙____⊙.requestFinalUrl[" + responseCode + "][" + i + "']from|" + lastUrl + "|to|" + redirectUrl);
                    } else {
                        String msg = "T____T.requestFinalUrl[" + responseCode + "][" + i + "']from|" + lastUrl + "|to|" + redirectUrl;
                        LogUtil.d(TAG, msg);
                        errorMessage = errorMessage + "|" + msg;
                    }
                    break;
                } else {
                    // 内含L/location，继续找下一个L/location
                    lastUrl = url.toString();
                    url = new URL(redirectUrl);
                    if (responseCode == 200) {
                        LogUtil.d(TAG, "^_^.requestFinalUrl[200][" + i + "']from|" + lastUrl + "|to|" + redirectUrl);
                    } else if (responseCode == 301 || responseCode == 302) {
                        LogUtil.d(TAG, "⊙_⊙.requestFinalUrl[" + responseCode + "][" + i + "']from|" + lastUrl + "|to|" + redirectUrl);
                    } else {
                        String msg = "T_T.requestFinalUrl[" + responseCode + "][" + i + "']from|" + lastUrl + "|to|" + redirectUrl;
                        LogUtil.d(TAG, msg);
                        errorMessage = errorMessage + "|" + msg;
                    }
                }
            } while (responseCode == 301 || responseCode == 302); // 301和302都是重定向
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "requestFinalUrl|Exception|" + e.getMessage());
            errorMessage = errorMessage + "|Exception";
            listener.onFailureWithException(errorMessage, e);
            return;
        }
        String finalUrl = originUrl;
        if (redirectUrl != null && redirectUrl.length() > 0) {
            finalUrl = redirectUrl;
        }
        if (responseCode == 200) {
            listener.onSuccess(finalUrl);
        } else {
            listener.onFailure(errorMessage);
        }
    }
}