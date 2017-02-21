package net.smartbetter.wonderful.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Activity基类
 * 定义统一的属性,接口,方法
 * Created by joe on 2017/1/16.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        // 显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 菜单栏操作
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
