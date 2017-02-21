package net.smartbetter.wonderful.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.base.BaseActivity;
import net.smartbetter.wonderful.entity.UserEntity;
import net.smartbetter.wonderful.utils.LogUtils;
import net.smartbetter.wonderful.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 注册页
 * Created by joe on 2017/1/18.
 */
public class RegisteredActivity extends BaseActivity {

    @BindView(R.id.et_phone) EditText mPhone; // 手机号
    @BindView(R.id.et_code) EditText mCode; // 验证码输入框
    @BindView(R.id.tv_code) TextView mBtnCode; // 验证码按钮
    @BindView(R.id.et_name) EditText mName;
    @BindView(R.id.et_pass) EditText mPass;
    @BindView(R.id.et_password) EditText mPassword;
    @BindView(R.id.et_age) EditText mEge;
    @BindView(R.id.rg_sex) RadioGroup mRadioGroup;
    @BindView(R.id.et_desc) EditText mDesc;

    private boolean isBoy = true;// 性别
    private MyCountTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        ButterKnife.bind(this);
    }

    /**
     * 发送验证码
     * @param view
     */
    @OnClick(R.id.tv_code)
    public void onSMSCodeButton(View view) {
        String phoneNum = mPhone.getText().toString().trim();
        if (phoneNum.isEmpty()) {
            ToastUtils.showShort(this, "手机号不能为空");
            return;
        }
        timer = new MyCountTimer(60000, 1000);
        timer.start();
        BmobSMS.requestSMSCode(phoneNum,"default", new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId,BmobException e) {
                LogUtils.i("JAVA", e.toString());
                if (e==null) {// 验证码发送成功
                    ToastUtils.showShort(getApplicationContext(), "验证码发送成功");
                    Log.i("JAVA", "短信id："+smsId);// 用于查询本次短信发送详情
                } else {
                }
            }
        });
    }

    /**
     * 倒计时器
     * 停止倒计时器用 timer.cancel();
     */
    class MyCountTimer extends CountDownTimer {
        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            mBtnCode.setText((millisUntilFinished / 1000) +"秒后重发");
        }
        @Override
        public void onFinish() {
            mBtnCode.setText("重新发送验证码");
        }
    }

    @OnClick(R.id.btn_registered)
    public void onRegistered(View view) {
        // 获取到输入框的值
        String phone = mPhone.getText().toString().trim();
        String code = mCode.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String age = mEge.getText().toString().trim();
        String pass = mPass.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String desc = mDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(name) & !TextUtils.isEmpty(age) & !TextUtils.isEmpty(pass) &
                !TextUtils.isEmpty(password) & !TextUtils.isEmpty(phone)) {
            if (pass.equals(password)) {
                // 判断性别
                mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.rb_boy) {
                            isBoy = true;
                        } else if (checkedId == R.id.rb_girl) {
                            isBoy = false;
                        }
                    }
                });
                if (TextUtils.isEmpty(desc)) {
                     desc = getString(R.string.text_nothing);
                }
                UserEntity userEntity = new UserEntity();
                userEntity.setMobilePhoneNumber(phone);
                userEntity.setName(name);
                userEntity.setPassword(password);
                userEntity.setAge(Integer.parseInt(age));
                userEntity.setSex(isBoy);
                userEntity.setDesc(desc);

                userEntity.signOrLogin(code, new SaveListener<UserEntity>() {
                    @Override
                    public void done(UserEntity user,BmobException e) {
                        if(e==null){
                            ToastUtils.showShort(RegisteredActivity.this, getString(R.string.text_registered_successful));
                            Log.i("JAVA", ""+user.getUsername()+"-"+user.getAge()+"-"+user.getObjectId());
                            finish();
                        }else{
                            ToastUtils.showShort(RegisteredActivity.this, getString(R.string.text_registered_failure) + e.toString());
                        }
                    }

                });
            } else {
                ToastUtils.showShort(this, getString(R.string.text_two_input_not_consistent));
            }
        } else {
            ToastUtils.showShort(this, getString(R.string.text_tost_empty));
        }
    }

}