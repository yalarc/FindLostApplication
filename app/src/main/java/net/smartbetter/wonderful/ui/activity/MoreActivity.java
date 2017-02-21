package net.smartbetter.wonderful.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.base.BaseActivity;
import net.smartbetter.wonderful.entity.UserEntity;
import net.smartbetter.wonderful.utils.ConstantUtils;
import net.smartbetter.wonderful.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * 更多页
 * Created by joe on 2017/1/17.
 */
public class MoreActivity extends BaseActivity {

    @BindView(R.id.listview) ListView mListView;

    private static final String[] strs = new String[]{"修改密码", "用户协议", "关于我们", "退出"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchActivity(position);
            }
        });
    }

    private void switchActivity(int position) {
        switch (position) {
            case 0: // 修改密码
                if (UserEntity.getCurrentUser()!=null) {
                    startActivity(new Intent(this, ChangePasswordActivity.class));
                } else {
                    ToastUtils.showShort(this, "您并没有登录");
                }
                break;
            case 1: // 用户协议
                break;
            case 2: // 关于我们
                break;
            case 3: // 退出
                if (UserEntity.getCurrentUser()!=null) {
                    onExitUser();
                } else {
                    ToastUtils.showShort(this, "您并没有登录");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 退出登录
     */
    public void onExitUser() {
        // 兼容的 Material Design AlertDialog
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage("确定退出吗?")
                // .setCancelable(false) // 设置点击Dialog以外的界面不消失，按返回键也不起作用
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 清除缓存用户对象
                        UserEntity.logOut();
                        // 现在的currentUser是null了
                        BmobUser currentUser = UserEntity.getCurrentUser();
                        MoreActivity.this.setResult(ConstantUtils.RESULT_UPDATE_INFO, new Intent());
                        finish();
                    }
                })
                .show();
    }

}