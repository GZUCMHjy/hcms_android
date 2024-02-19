package com.example.mqtt_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mqtt_project.common.BaseResponse;
import com.example.mqtt_project.constant.Constant;
import com.example.mqtt_project.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IndexActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private RadioButton tab1,tab2,tab3,tab4;  //四个单选按钮
    private List<View> mViews;   //存放视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();//初始化数据
        //对单选按钮进行监听，选中、未选中
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case 2131231085:
                        mViewPager.setCurrentItem(0);
                        break;
                    case 2131231086:
                        mViewPager.setCurrentItem(1);
                        break;
                    case 2131231083:
                        mViewPager.setCurrentItem(2);
                        break;
                    case 2131231084:
                        mViewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //初始化控件
        mViewPager=findViewById(R.id.viewpager);
        mRadioGroup=findViewById(R.id.rg_tab);
        tab1=findViewById(R.id.rb_msg);
        tab2=findViewById(R.id.rb_people);
        tab3=findViewById(R.id.rb_find);
        tab4=findViewById(R.id.rb_me);

        mViews=new ArrayList<View>();//加载，添加视图
        mViews.add(LayoutInflater.from(this).inflate(R.layout.msg,null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.people,null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.find,null));
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
                                    .url(Constant.BASE_URL + "/getUserInfo") // 登录请求
                                    .addHeader("Cookie", "JSESSIONID=" + cookie)
                                    .get()
                                    .build();// 创建http请求
                            Response response = okHttpClient.newCall(request).execute();
                            // 解析服务器返回的json对象成json字符串
                            String objStr = response.body().string();
                            // 字符串再转成Java对象
                            BaseResponse baseResponse = JsonUtil.fromJson(objStr, BaseResponse.class);
                            Object data = (Object) baseResponse.getData();
                            String message = baseResponse.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 子线程运行
                                    // 是否进行页面跳转
                                    if(message.equals("ok")){
                                        Toast.makeText(IndexActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(IndexActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(IndexActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

            }
        });
    }

    //ViewPager适配器
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

}
