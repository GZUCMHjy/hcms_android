package com.example.mqtt_project;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mqtt_project.common.BaseResponse;
import com.example.mqtt_project.constant.Constant;
import com.example.mqtt_project.reponse.QRcode;
import com.example.mqtt_project.reponse.UserInfo;
import com.example.mqtt_project.utils.JsonUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class IndexActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private RadioButton tab1,tab2,tab3,tab4;  //四个单选按钮
    private List<View> mViews;   //存放视图
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int OPEN_CAMERA_REQUEST_CODE = 1002;

    private Button openCameraButton;

    /**
     * 启动IndexActivity
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();//初始化数据
        final RelativeLayout container = findViewById(R.id.container);
        //对单选按钮进行监听，选中、未选中
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.rb_index){
                    mViewPager.setCurrentItem(0);
                } else if (checkedId == R.id.rb_announce) {
                    mViewPager.setCurrentItem(1);
                } else if (checkedId == R.id.rb_search) {
                    mViewPager.setCurrentItem(2);
                } else if (checkedId == R.id.rb_me) {
                    mViewPager.setCurrentItem(3);
                }
            }
        });
    }

    /**
     * 扫码功能（直接调用zxing工具方法）
     * msg.xml绑定按钮触发事件
     * @param view
     */
    public void onScanCodeClicked(View view) {
        // 处理扫码逻辑
        // 在这里处理扫码的逻辑，例如启动扫码页面等
        // 当前页面跳转扫码页面
        Intent intent = new Intent(IndexActivity.this, CaptureActivity.class);
        new IntentIntegrator(IndexActivity.this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)// 扫码的类型,可选：一维码，二维码，一/二维码
                .setCaptureActivity(CarmerActivity.class)
                .setPrompt("请对准二维码")// 设置提示语
                .setCameraId(0)// 选择摄像头,可使用前置或者后置
                .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                .setBarcodeImageEnabled(false)// 扫完码之后生成二维码的图片
                .initiateScan();// 初始化扫码
    }
    /**
     * 初始化IndexActivity页面
     */
    private void initView() {
        //初始化控件
        mViewPager=findViewById(R.id.viewpager);
        mRadioGroup=findViewById(R.id.rg_tab);
        tab1=findViewById(R.id.rb_index);
        tab2=findViewById(R.id.rb_announce);
        tab3=findViewById(R.id.rb_search);
        tab4=findViewById(R.id.rb_me);

        mViews=new ArrayList<View>();//加载，添加视图
        mViews.add(LayoutInflater.from(this).inflate(R.layout.index,null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.announce,null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.search,null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.me,null));

        mViewPager.setAdapter(new MyViewPagerAdapter());//设置一个适配器
        //对viewpager监听，让分页和底部图标保持一起滑动
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override   //让viewpager滑动的时候，下面的图标跟着变动
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tab1.setChecked(true);
                        tab2.setChecked(false);
                        tab3.setChecked(false);
                        tab4.setChecked(false);
                        break;
                    case 1:
                        tab1.setChecked(false);
                        tab2.setChecked(true);
                        tab3.setChecked(false);
                        tab4.setChecked(false);
                        break;
                    case 2:
                        tab1.setChecked(false);
                        tab2.setChecked(false);
                        tab3.setChecked(true);
                        tab4.setChecked(false);
                        break;
                    case 3:
                        tab1.setChecked(false);
                        tab2.setChecked(false);
                        tab3.setChecked(false);
                        tab4.setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 调用后端获取用户信息接口
     * @param view
     */
    public void getUserInfo(View view) {
        Button me = findViewById(R.id.rb_me);
        me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 构建请求客户端
                            OkHttpClient okHttpClient = new OkHttpClient();
                            // 获取用户登录的session值
                            SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                            String cookie = sharedPreferences.getString("session","");
                            // 构建请求url
                            Request request = new Request.Builder()
                                    .url(Constant.BASE_URL + "user/getUserInfo") // 登录请求
                                    .addHeader("Cookie", "JSESSIONID=" + cookie)
                                    .get()
                                    .build();// 创建http请求
                            Response response = okHttpClient.newCall(request).execute();
                            // 解析服务器返回的json对象成json字符串
                            String objStr = response.body().string();
                            // 字符串再转成Java对象
                            BaseResponse baseResponse = JsonUtil.fromJson(objStr, BaseResponse.class);
                            // 取出data对象
                            Object data = baseResponse.getData();
                            // 转成字符串
                            String json = JsonUtil.toJson(data);
                            // 二次转成指定对象（否则直接强转会失败）
                            UserInfo user = JsonUtil.fromJson(json, UserInfo.class);
                            String gender = user.getUser_gender();
                            String name = user.getUser_name();
                            String tel = user.getUser_tel();
                            String lab_id = user.getLab_id().toString();
                            String position = user.getUser_position();
                            String institution = user.getUser_institution();

                            TextView textViewGender = findViewById(R.id.gender);
                            TextView textViewName = findViewById(R.id.name);
                            TextView textViewTel = findViewById(R.id.tel);
                            TextView textViewLab_id = findViewById(R.id.lab_id);
                            TextView textViewPosition = findViewById(R.id.position);
                            TextView textViewInstitution = findViewById(R.id.institution);

                            // 更新操作 要在子线程runOnUiThread中进行
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        textViewGender.setText(gender+"");
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    textViewName.setText(name+"");
                                    textViewTel.setText(tel+"");
                                    textViewLab_id.setText(lab_id+"");
                                    textViewPosition.setText(position+"");
                                    textViewInstitution.setText(institution+"");
                                    String message = baseResponse.getMessage();
                                    // 子线程运行
                                    // 是否进行页面跳转
                                    if(message.equals("ok")){
                                        Toast.makeText(IndexActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(IndexActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                    //return message;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(IndexActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                                   // return null;
                                }
                            });
                        }
                        //return null;
                    }
                }).start();

            }
        });
    }

    /**
     * 视图适配器
     */
    private class MyViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mViews.size();
        }


        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mViews.get(position));
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mViews.get(position));
            return mViews.get(position);
        }
    }

    /**
     * 回调函数
     * 获取扫描返回的结果
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // 1. 解析成功后端生成的二维码图片
                String content = result.getContents();
                QRcode qRcode = JsonUtil.fromJson(content, QRcode.class);
                String codeId = qRcode.getCode_id();
                int id = Integer.parseInt(codeId);
                // 2. 调用后端接口 改变二维码状态（已扫描未确定
                changeStatus(id);
                // 3. 将获取的数据对象添加手机型号信息
                String androidID = getAndroidID();
                qRcode.setOriginDevice(androidID);
                // 4. 获取移动端accountId
                SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                String accountId = sharedPreferences.getString("accountId","");
                qRcode.setAccount_id(accountId);
                // 5. 封装发送
                try {
                    boolean b = scanLogin(qRcode);
                    if(b){
                        // 4.1 如果成功，则弹出是否确定登录（弹窗）
                        Toast.makeText(this, "success", Toast.LENGTH_LONG).show();
                        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                                // 监听按钮（onclick）确定又发起请求（改变二维码状态）
                                .setPositiveButton("是否授权登录", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try{
                                                    // 5.1 成功 pc端成功登录（网页轮询调用的getCurrentCodeStatus ）
                                                    changeStatus(id);
                                                }catch (Exception e){
                                                    runOnUiThread(new Runnable() {
                                                        // 5.2 失败 重新再扫码一遍
                                                        @Override
                                                        public void run() {
                                                            Log.e("failed","失败");
                                                        }
                                                    });
                                                }
                                            }
                                        }).start();
                                    }
                                })
                                .setIcon(R.mipmap.ic_launcher)//图标
                                .create();
                        alertDialog1.show();
                    }else{
                        // 4.2 如果失败，则重新在扫描
                        Toast.makeText(this, "failed", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 获取android手机设备id
     * @return
     */
    public String getAndroidID() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return id == null ? "" : id;
    }

    /**
     * 申请修改二维码状态(两次改变状态)
     * @param id
     * @return
     */
    public void changeStatus(Integer id ){

        // 子线程完成网络接口请求操作
        new Thread(new Runnable() {
            String message = null;
            @Override
            public void run() {
                try {
                    // 构建请求客户端
                    OkHttpClient okHttpClient = new OkHttpClient();
                    // 创建一个 HttpUrl.Builder 对象
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.BASE_URL + "scan/changeStatus").newBuilder();
                    // 向 URL 中添加路径参数
                    urlBuilder.addPathSegment(String.valueOf(id));
                    // 构建请求url
                    Request request = new Request.Builder()
                            .post(RequestBody.create(null, new byte[0]))
                            .url(urlBuilder.build())
                            .build();// 创建http请求
                    Response response = okHttpClient.newCall(request).execute();
                    String objStr = response.body().string();
                    // 字符串再转成Java对象
                    BaseResponse baseResponse = JsonUtil.fromJson(objStr, BaseResponse.class);
                    message = baseResponse.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 同样也要在子线程进行报错提醒
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(IndexActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
    public boolean scanLogin(QRcode code) throws IOException {
        new Thread(new Runnable() {
            String message = null;
            @Override
            public void run() {
                try{
                    // 构建请求客户端
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String jsonStr = JsonUtil.toJson(code);
                    QRcode qRcode = JsonUtil.fromJson(jsonStr, QRcode.class);
                    qRcode.setStatus("已扫码待确定");
                    // 构建请求url
                    Request request = new Request.Builder()
                            .url(Constant.BASE_URL + "scan/updateQR") // 登录请求
                            .post(RequestBody.create(MediaType.parse("application/json"), jsonStr))
                            .build();// 创建http请求
                    Response response = okHttpClient.newCall(request).execute();
                    String objStr = response.body().string();
                    // 字符串再转成Java对象
                    BaseResponse baseResponse = JsonUtil.fromJson(objStr, BaseResponse.class);
                    message = baseResponse.getMessage();
                }catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(IndexActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
        return true;
    }
}
