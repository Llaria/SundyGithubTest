package sun.sundy.sundygithubtest.network;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sun.sundy.sundygithubtest.R;

public class OkHttp3Activity extends AppCompatActivity {

    private TextView tvShow;
    private Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(Message msg) {
            tvShow.setText(msg.obj + "");
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http3);
        tvShow = findViewById(R.id.tv_show);
        Button btnGet = findViewById(R.id.btn_get);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://wwww.baidu.com";
                OkHttpClient okHttpClient1 = new OkHttpClient.Builder().build();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        System.out.println("request=====>>>>" + request.toString());
                        Response response = chain.proceed(request);
                        System.out.println("response====>>>>" + response.toString());
                        return response;
                    }
                }).build();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                final Call call = okHttpClient.newCall(request);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = call.execute();
                            Message message = handler.obtainMessage();
                            message.obj = response.body().string();
                            handler.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
               /* call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
//                        tvShow.setText(e.getMessage());
                        System.out.println("=========>>>>>>" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
//                        tvShow.setText(response.body().string());
                        System.out.println("=========>>>>>>" + Thread.currentThread().getName() + ":" + response.body().string());
//                        Message message = handler.obtainMessage();
//                        message.obj = response.body().string();
//                        handler.sendMessage(message);
                    }
                });*/


            }
        });
    }
}
