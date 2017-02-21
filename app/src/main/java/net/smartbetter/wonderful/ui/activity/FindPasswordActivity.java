package net.smartbetter.wonderful.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.base.BaseActivity;
import net.smartbetter.wonderful.utils.LogUtils;
import net.smartbetter.wonderful.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 找回密码
 * Created by joe on 2017/1/18.
 */
public class FindPasswordActivity extends BaseActivity {

    @BindView(R.id.et_phone) EditText mPhone;
    @BindView(R.id.et_code) EditText mCode;
    @BindView(R.id.tv_code) TextView mBtnCode;

    @BindView(R.id.et_new_pass) EditText mNewPass;
    @BindView(R.id.et_new_password) EditText mNewPassword;
    @BindView(R.id.btn_reset_password) Button mResetPassword;

    private MyCountTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
    }

    /**
     * 发送验证码
     * @param view
     */
    @OnClick(R.id.tv_code)
    public void onCode(View view) {
        String phoneNum = mPhone.getText().toString().trim();
        if (phoneNum.isEmpty()) {
            ToastUtils.showShort(this, getString(R.string.text_tost_empty));
            return;
        }
        timer = new MyCountTimer(60000, 1000);
        timer.start();
        BmobSMS.requestSMSCode(phoneNum,"default", new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId,BmobException e) {
                LogUtils.i("JAVA", e.toString());
                if (e==null) {//验证码发送成功
                    ToastUtils.showShort(getApplicationContext(),
                            getString(R.string.text_sms_code_reset_successfully));
                    Log.i("JAVA", "短信id："+smsId);//用于查询本次短信发送详情
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
            mBtnCode.setText(getString(R.string.text_reset_sms_code));
        }
    }

    /**
     * 重置密码
     * @param view
     */
    @OnClick(R.id.btn_reset_password)
    public void onResetPassword(View view) {
        String code = mCode.getText().toString().trim();
        String newPass = mNewPass.getText().toString().trim();
        String newPassword = mNewPassword.getText().toString().trim();
        if(!TextUtils.isEmpty(newPass) & !TextUtils.isEmpty(newPassword)){
            if(newPass.equals(newPassword)) {
                BmobUser.resetPasswordBySMSCode(code,newPassword, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            ToastUtils.showShort(FindPasswordActivity.this, getString(R.string.reset_successfully));
                            finish();
                        }else{
                            ToastUtils.showShort(FindPasswordActivity.this,
                                    getString(R.string.reset_failed)+"+:code ="
                                            +e.getErrorCode()+",msg = "+e.getLocalizedMessage());
                        }
                    }
                });
            }else {
                ToastUtils.showShort(FindPasswordActivity.this,
                        getString(R.string.text_two_input_not_consistent));
            }
        }else {
            ToastUtils.showShort(FindPasswordActivity.this, getString(R.string.text_tost_empty));
        }
    }

}
