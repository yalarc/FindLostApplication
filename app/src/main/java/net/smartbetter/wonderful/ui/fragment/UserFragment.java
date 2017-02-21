package net.smartbetter.wonderful.ui.fragment;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.base.BaseFragment;
import net.smartbetter.wonderful.entity.UserEntity;
import net.smartbetter.wonderful.ui.activity.LoginActivity;
import net.smartbetter.wonderful.ui.activity.MoreActivity;
import net.smartbetter.wonderful.utils.BitmapLoader;
import net.smartbetter.wonderful.utils.ConstantUtils;
import net.smartbetter.wonderful.utils.LogUtils;
import net.smartbetter.wonderful.utils.SPUtils;
import net.smartbetter.wonderful.utils.ToastUtils;
import net.smartbetter.wonderful.view.CustomDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by joe on 2017/1/16.
 */
public class UserFragment extends BaseFragment {

    @BindView(R.id.profile_image) de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    @BindView(R.id.tv_user) TextView mUser; // 编辑资料
    @BindView(R.id.tv_login) TextView mLogin; // 登录

    @BindView(R.id.ll_line1) LinearLayout mLine1;
    @BindView(R.id.ll_line2) LinearLayout mLine2;
    @BindView(R.id.ll_line3) LinearLayout mLine3;
    @BindView(R.id.ll_line4) LinearLayout mLine4;

    @BindView(R.id.et_name) EditText mName;
    @BindView(R.id.et_sex) EditText mSex;
    @BindView(R.id.et_age) EditText mAge;
    @BindView(R.id.et_desc) EditText mDesc;

    @BindView(R.id.btn_update_ok) Button mUpdateok;
    @BindView(R.id.tv_default_no_login) TextView mDefaultNoLogin;

    private android.support.v7.app.AlertDialog photoDialog;
    public static final String PHOTO_IMAGE_FILE_NAME = "fileImg.jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 10;
    private File tempFile = null;
    private Bitmap mBitmap = null;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 1:
                    mProfileImage.setImageBitmap(mBitmap);
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("失物招领");
        setHasOptionsMenu(true); // 配置Actionbar可先的属性
        View view = inflater.inflate(R.layout.fragment_user, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (UserEntity.getCurrentUser()!=null) {
            initView();
        }
    }



    public void initView() {
        setEnabled(false); // 默认是不可点击的/不可输入
        setViewVisibilityVisible(mLine1,mLine2,mLine3,mLine4,mUser);
        setViewVisibilityGone(mLogin,mDefaultNoLogin);

        if (BmobUser.getCurrentUser()!=null) {
            UserEntity userEntity = BmobUser.getCurrentUser(UserEntity.class);
            mName.setText(userEntity.getName());
            mSex.setText(userEntity.isSex()?getString(R.string.text_boy):getString(R.string.text_girl));
            mAge.setText(userEntity.getAge() + "");
            mDesc.setText(userEntity.getDesc());
        }
    }

    @Override
    public void onResume() {
        if (BmobUser.getCurrentUser()!=null) {
            final UserEntity userEntity = BmobUser.getCurrentUser(UserEntity.class);
            if (userEntity.getAvatar()!=null) {
//                Glide.with(getActivity()).load(userEntity.getAvatar().getFileUrl()).into(mProfileImage);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mBitmap = BitmapLoader.loadBitmap(userEntity.getAvatar().getFileUrl());
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        }
        super.onResume();
    }

    /**
     * 控制焦点
     * @param is
     */
    private void setEnabled(boolean is) {
        mName.setEnabled(is);
        mSex.setEnabled(is);
        mAge.setEnabled(is);
        mDesc.setEnabled(is);
    }

    /**
     * 登录
     * @param view
     */
    @OnClick(R.id.tv_login)
    public void onLogin(View view) {
        startActivityForResult(new Intent(getActivity(), LoginActivity.class), ConstantUtils.RESULT_UPDATE_INFO);
    }

    /**
     * 编辑资料
     * @param view
     */
    @OnClick(R.id.tv_user)
    public void onEditUser(View view) {
        setEnabled(true);
        setViewVisibilityVisible(mUpdateok);
    }

    /**
     * 确定修改
     * @param view
     */
    @OnClick(R.id.btn_update_ok)
    public void onUpdateOk(View view) {
        // 1.拿到输入框的值
        String name = mName.getText().toString();
        String age = mAge.getText().toString();
        String sex = mSex.getText().toString();
        String desc = mDesc.getText().toString();
        // 2.判断是否为空
        if (!TextUtils.isEmpty(name) & !TextUtils.isEmpty(age) & !TextUtils.isEmpty(sex)) {
            // 3.更新属性
            UserEntity userEntity = new UserEntity();
            userEntity.setName(name);
            userEntity.setAge(Integer.parseInt(age));
            if (sex.equals(getString(R.string.text_boy))) {
                userEntity.setSex(true);
            } else {
                userEntity.setSex(false);
            }
            if (!TextUtils.isEmpty(desc)) {
                userEntity.setDesc(desc);
            } else {
                userEntity.setDesc(getString(R.string.text_nothing));
            }
            BmobUser bmobUser = BmobUser.getCurrentUser();
            userEntity.update(bmobUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //修改成功
                        setEnabled(false);
                        setViewVisibilityGone(mUpdateok);
                        ToastUtils.showShort(getActivity(), getString(R.string.text_editor_success));
                    } else {
                        ToastUtils.showShort(getActivity(), getString(R.string.text_editor_failure));
                        LogUtils.i("JAVA", getString(R.string.text_editor_failure)+e.toString());
                    }
                }
            });
        } else {
            ToastUtils.showShort(getActivity(), getString(R.string.text_tost_empty));
        }
    }


    /**
     * 头像点击事件
     * @param view
     */
    @OnClick(R.id.profile_image)
    public void onProfileImage(View view) {
        if (BmobUser.getCurrentUser() == null) {
            ToastUtils.showShort(getActivity(), getString(R.string.text_default_no_login));
        } else {
            showDialog();
        }
    }

    /**
     * 点击头像的提示对话框
     */
    private void showDialog() {
        photoDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
        photoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        photoDialog.show();
        Window window = photoDialog.getWindow();
        window.setContentView(R.layout.dialog_photo); // 修改整个dialog窗口的显示
        window.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = photoDialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels;
        photoDialog.getWindow().setAttributes(lp); // 设置宽度

        photoDialog.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCamera();
            }
        });
        photoDialog.findViewById(R.id.btn_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPicture();
            }
        });
        photoDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoDialog.dismiss();
            }
        });
    }

    /**
     * 跳转相机
     */
    public void toCamera() {
        requestWESPermission(); // 安卓6.0以上需要申请权限
        photoDialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统的拍照功能
        // 指定调用相机拍照后照片的储存路径
//        tempFileCameraPath = getTempPhotoFileName();
//        tempFileCamera = new File(tempFileCameraPath);
        // 判断内存卡是否可用，可用的话就进行储存
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFileCamera));
//        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     */
    private String getTempPhotoFileName() {
        // 创建一个以当前时间为名称的文件
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return Environment.getExternalStorageDirectory() + "/" + dateFormat.format(date) + ".jpg";
    }

    /**
     * 跳转相册
     */
    private void toPicture() {
        photoDialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    /**
     * 裁剪
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            LogUtils.e("JAVA", "裁剪uri == null");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // 裁剪宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        // 发送数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 设置icon并上传服务器
     * @param data
     */
    private void setImageToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            final Bitmap bitmap = bundle.getParcelable("data");
            final BmobFile bmobFile = new BmobFile(bitmapToFile(bitmap));

            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null) {
                        // 此时上传成功
                        UserEntity userEntity = new UserEntity();
                        userEntity.setAvatar(bmobFile);// 获取文件并赋值给实体类
                        BmobUser bmobUser = BmobUser.getCurrentUser();
                        userEntity.update(bmobUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    mProfileImage.setImageBitmap(bitmap);
                                    ToastUtils.showShort(getActivity(), getString(R.string.avatar_editor_success));
                                } else {
                                    ToastUtils.showShort(getActivity(), getString(R.string.avatar_editor_failure));
                                }
                            }
                        });
                    } else {
                        ToastUtils.showShort(getActivity(), getString(R.string.avatar_editor_failure));
                    }
                    // 既然已经设置了图片，我们原先的就应该删除
                    if (tempFile != null) {
                        tempFile.delete();
                        LogUtils.i("JAVA", "tempFile已删除");
                    }
                }
                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            });
        }
    }

    /**
     * Bitmap转File
     */
    public File bitmapToFile(Bitmap bitmap) {
        tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                bos.flush();
                bos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * 动态申请权限
     */
    private void requestWESPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                // 判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    ToastUtils.showShort(getActivity(),"Need write external storage permission.");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_BLUETOOTH_PERMISSION);
                return;
            } else {
            }
        } else {
        }
    }

    /**
     * 授权回调处理
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_BLUETOOTH_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功
                } else {
                    // 授权拒绝
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 登录
            case ConstantUtils.RESULT_UPDATE_INFO:
                if (UserEntity.getCurrentUser()!=null) {
                    initView();
                }
                break;
            // 退出登录
            case ConstantUtils.RESULT_UPDATE_EXIT_INFO:
                if (UserEntity.getCurrentUser()==null) {
                    setViewVisibilityGone(mLine1,mLine2,mLine3,mLine4,mUser);
                    setViewVisibilityVisible(mLogin,mDefaultNoLogin);
                }
                break;
            case IMAGE_REQUEST_CODE: // 相册数据
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            case CAMERA_REQUEST_CODE: // 相机数据
                tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(tempFile));
                break;
            case RESULT_REQUEST_CODE: // 有可能点击舍弃
                if (data != null) {
                    // 拿到图片设置, 然后需要删除tempFile
                    setImageToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPUtils.putImage(getActivity(), "profile_image", mProfileImage); // 保存
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                startActivityForResult(new Intent(getActivity(), MoreActivity.class), ConstantUtils.RESULT_UPDATE_EXIT_INFO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}