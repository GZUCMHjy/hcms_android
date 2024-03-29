package com.example.mqtt_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mqtt_project.common.BaseResponse;
import com.example.mqtt_project.constant.Constant;
import com.example.mqtt_project.reponse.LoginResponse;
import com.example.mqtt_project.request.LoginRequest;
import com.example.mqtt_project.utils.JsonUtil;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button dengluButton = findViewById(R.id.login);
        EditText account = findViewById(R.id.account);
        EditText password = findViewById(R.id.password);
        dengluButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 构建请求客户端
                            OkHttpClient okHttpClient = new OkHttpClient();
                            LoginRequest loginRequest = new LoginRequest();
                            // 获取activity_main.xml文件传来的账号和密码
                            loginRequest.setUser_pwd(password.getText().toString());
                            loginRequest.setUser_acct(account.getText().toString());
                            String jsonStr = JsonUtil.toJson(loginRequest);
                            // 构建请求url
                            Request request = new Request.Builder()
                                    .url(Constant.BASE_URL + "user/login") // 登录请求
                                    .post(RequestBody.create(MediaType.parse("application/json"), jsonStr))
                                    .build();// 创建http请求
                            Response response = okHttpClient.newCall(request).execute();
                            // 获取响应头中的所有 Cookie
                            List<String> cookies = response.headers("Set-Cookie");
                            // 遍历所有的 Cookie，寻找 JSESSIONID
                            String sessionid = null;
                            for (String cookie : cookies) {
                                if (cookie.contains("JSESSIONID")) {
                                    // 找到了 JSESSIONID，解析出来
                                    String[] parts = cookie.split(";");
                                    for (String part : parts) {
                                        if (part.contains("JSESSIONID")) {
                                            sessionid = part.split("=")[1];
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            // 全局保存sessionId
                            // JsessionId.saveUser(jsessionid);
                            SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("session",sessionid);

                            // 解析服务器返回的json对象成json字符串
                            String objStr = response.body().string();
                            // 字符串再转成Java对象
                            BaseResponse baseResponse = JsonUtil.fromJson(objStr, BaseResponse.class);
                            Object data = (Object) baseResponse.getData();
                            // 转成字符串
                            String json = JsonUtil.toJson(data);
                            // 转成指定对象类型
                            LoginResponse loginResponse = JsonUtil.fromJson(json, LoginResponse.class);
                            // 获取accountId
                            Integer accountId = loginResponse.getAccount_id();
                            editor.putString("accountId",accountId.toString());
                            String message = baseResponse.getMessage();
                            // 要提交上去，否则取不到
                            editor.commit();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 子线程运行
                                    // 是否进行页面跳转
                                    if(message.equals("登陆成功")){
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                    // return null;
                                }
                            });
                            // 跳转页面
                            loginSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                                    // return null;
                                }
                            });
                        }
                        // return null;
                    }
                }).start();

            }
        });
    }
    private void loginSuccess() {
        Intent intent = new Intent(MainActivity.this, IndexActivity.class);
        startActivity(intent);
        finish(); // 关闭当前登录页面(防止跳回登录页面）
    }
}