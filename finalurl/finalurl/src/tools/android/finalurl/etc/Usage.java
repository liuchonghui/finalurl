package tools.android.finalurl.etc;

import tools.android.finalurl.FinalUrl;
import tools.android.finalurl.OnFinalUrlListener;

class Usage {
    public void run() {
        FinalUrl.get().setEnableLogcat(true).setLogtag("PPP")
                .setTimeoutMillis(5000L).request("http://xxx", new OnFinalUrlListener() {
            @Override
            public void onSuccess(String resultUrl) {
            }

            @Override
            public void onFailure(String errorMessage) {
            }

            @Override
            public void onFailureWithException(String errorMessage, Exception e) {
            }
        });
    }
}
