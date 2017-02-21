package net.smartbetter.wonderful.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.utils.ConstantUtils;
import net.smartbetter.wonderful.utils.SPUtils;
import net.smartbetter.wonderful.utils.ToastUtils;
import net.smartbetter.wonderful.utils.UtilTools;

/**
 * Loading页面
 * Created by joe on 2017/1/16.
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * 1.延时2000ms
     * 2.判断程序是否第一次运行
     * 3.自定义字体
     * 4.Activity全屏主题
     */
    private TextView tv_splash;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstantUtils.HANDLER_SPLASH:
                    // 判断程序是否是第一次运行
                    if (isFirst()) {
                        startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!ConstantUtils.BMOB_APP_ID.isEmpty()) {
            initView();
        } else {
            ToastUtils.showLong(getApplicationContext(), "Replace with your BmobSDK appkey.");
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        // 延时2000ms
        handler.sendEmptyMessageDelayed(ConstantUtils.HANDLER_SPLASH, 2000);
        tv_splash = (TextView) findViewById(R.id.tv_splash);
        // 设置字体
        UtilTools.setFont(this,tv_splash);
    }

    /**
     * 判断程序是否第一次运行
     * @return
     */
    private boolean isFirst() {
        boolean isFirst = SPUtils.getBoolean(this, ConstantUtils.SHARE_IS_FIRST,true);
        if(isFirst){
            SPUtils.putBoolean(this,ConstantUtils.SHARE_IS_FIRST,false);
            // 是第一次运行
            return true;
        }else {
            return false;
        }

    }

}