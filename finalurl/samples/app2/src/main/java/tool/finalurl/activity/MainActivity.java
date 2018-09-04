package tool.finalurl.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tool.finalurl.app2.R;
import tools.android.finalurl.FinalUrl;


public class MainActivity extends Activity {

    String origUrl = "https://api.youku.com/videos/player/file?data=WcEl1o6uUdTRNRGMyTURBNE1BPT18MnwyfDI1MjU5fDIO0O0O";
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn1 = (Button) findViewById(R.id.btn1);
        final TextView btn1ret = (TextView) findViewById(R.id.btn1_ret);
        final Button btn2 = (Button) findViewById(R.id.btn2);
        final TextView btn2ret = (TextView) findViewById(R.id.btn2_ret);

        if (mHandler == null) {
            HandlerThread ht = new HandlerThread("finalurl-single-thread") {
                {
                    start();
                }
            };
            mHandler = new Handler(ht.getLooper());
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mHandler.post(new FinalUrl(origUrl, new FinalUrl.OnReceiveUrlListener() {
                    @Override
                    public void onSuccess(final String resultUrl) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                btn1ret.setText(resultUrl);
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                btn1ret.setText("onFailure");
                            }
                        });
                    }
                }));
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            }
        });
    }
}