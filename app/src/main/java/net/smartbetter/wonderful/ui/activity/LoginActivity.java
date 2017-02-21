package net.smartbetter.wonderful.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.base.BaseActivity;
import net.smartbetter.wonderful.entity.UserEntity;
import net.smartbetter.wonderful.utils.ConstantUtils;
import net.smartbetter.wonderful.utils.LogUtils;
import net.smartbetter.wonderful.utils.SPUtils;
import net.smartbetter.wonderful.utils.ToastUtils;
import net.smartbetter.wonderful.view.CustomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * 用户登录页
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_phone) EditText mPhone;
    @BindView(R.id.et_password) EditText mPassword;
    @BindView(R.id.keep_password) CheckBox keepPassword;

    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        boolean isKeep = SPUtils.getBoolean(this, "keeppass", false);
        keepPassword.setChecked(isKeep);
        dialog = new CustomDialog(this, 100, 100, R.layout.dialog_loading, R.style.Theme_dialog, Gravity.CENTER, R.style.pop_anim_style);
        dialog.setCancelable(false);
        if (isKeep) {
            String phone = SPUtils.getString(this, "phone", "");
            String password = SPUtils.getString(this, "password", "");
            mPhone.setText(phone);
            mPassword.setText(password);
        }
    }

    @OnClick(R.id.tv_forget)
    public void onForget(View view) {
        startActivity(new Intent(this, FindPasswordActivity.class));
    }

    @OnClick(R.id.tv_registered)
    public void onRegistered(View view) {
        startActivity(new Intent(this, RegisteredActivity.class));
    }

    @OnClick(R.id.btn_login)
    public void onLogin(View view) {
        String phone = mPhone.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) & !TextUtils.isEmpty(password)) {
            dialog.show();
            // 登录
            BmobUser.loginByAccount(phone, password, new LogInListener<UserEntity>() {
                @Override
                public void done(UserEntity user, BmobException e) {
                    dialog.dismiss();
                    if (user != null) {
                        LogUtils.i("JAVA", "用户登陆成功");
                        LoginActivity.this.setResult(ConstantUtils.RESULT_UPDATE_INFO, new Intent());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        ToastUtils.showShort(getApplicationContext(), getString(R.string.text_login_failure)+ e.toString());
                    }
                }
            });
        } else {
            ToastUtils.showShort(getApplicationContext(), getString(R.string.text_tost_empty));
        }
    }

    /**
     * 假设我现在输入用户名和密码，但是我不点击登录，而是直接退出了
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 保存状态
        SPUtils.putBoolean(this, "keeppass", keepPassword.isChecked());
        // 是否记住密码
        if (keepPassword.isChecked()) {
            // 记住用户名和密码
            SPUtils.putString(this, "phone", mPhone.getText().toString().trim());
            SPUtils.putString(this, "password", mPassword.getText().toString().trim());
        } else {
            SPUtils.remove(this, "phone");
            SPUtils.remove(this, "password");
        }
    }

}