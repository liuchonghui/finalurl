package tools.android.finalurl;

public interface OnFinalUrlListener {
    void onSuccess(String resultUrl);
    void onFailure(String errorMessage);
    void onFailureWithException(String errorMessage, Exception e);
}
