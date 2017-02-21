package net.smartbetter.wonderful.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.ui.fragment.FindFragment;
import net.smartbetter.wonderful.ui.fragment.LostFragment;
import net.smartbetter.wonderful.ui.fragment.NewsFragment;
import net.smartbetter.wonderful.ui.fragment.UserFragment;
import net.smartbetter.wonderful.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页
 * Created by joe on 2017/1/16.
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_radio) RadioGroup mMainRadio;

    private Fragment currentFragment;
    private NewsFragment newsFragment;
    private UserFragment userFragment;
    private FindFragment findFragment;
    private LostFragment lostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initNavBottom();
        initFragment();
    }

    /**
     * Initialize bottom navigation.
     */
    public void initNavBottom() {
        // 给radiogroup设置按钮选中状态监听事件
        mMainRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            // checkedId : 选中的按钮的id
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 3.切换界面操作
                // 判断选中的哪个按钮
                switch (checkedId) {
                    case R.id.rb_news:
                        // 动态
                        switchFragment(newsFragment);
                        break;
                    case R.id.rb_find:
                        // 寻物
                        switchFragment(findFragment);
                        break;
                    case R.id.rb_lost:
                        // 招领
                        switchFragment(lostFragment);
                        break;
                    case R.id.rb_user:
                        // 我的
                        switchFragment(userFragment);
                        break;
                    default:
                        break;
                }
            }
        });
        // 设置默认选中首页
        mMainRadio.check(R.id.rb_news);
    }


    /**
     * Initialize fragment.
     */
    private void initFragment() {
        newsFragment = new NewsFragment();
        userFragment = new UserFragment();
        findFragment = new FindFragment();
        lostFragment = new LostFragment();
        setDefaultFragment(newsFragment);
    }

    /**
     * Set default fragment.
     */
    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        currentFragment = fragment;
    }

    /**
     * Switch fragment.
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {
        if (fragment != currentFragment) {
            if (!fragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(currentFragment)
                        .add(R.id.frame_layout, fragment).addToBackStack(null).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(currentFragment)
                        .show(fragment).addToBackStack(null).commit();
            }
            currentFragment = fragment;
        }
    }

    // 记录用户首次点击返回键的时间
    private long firstTime = 0;
    /**
     * 监听keyUp 实现双击退出程序
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                ToastUtils.showShort(this, "再按一次退出程序");
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

}