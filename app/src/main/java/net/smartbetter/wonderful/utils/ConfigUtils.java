package net.smartbetter.wonderful.utils;

import android.content.SharedPreferences;

import net.smartbetter.wonderful.application.App;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 配置
 */
public class ConfigUtils {
    /**
     * 单例模式
     */
    static ConfigUtils instance;//句柄

    private ConfigUtils() {
    }

    public static ConfigUtils getInstance() {
        if (instance == null) instance = new ConfigUtils();
        return instance;
    }

    /**
     * 配置信息
     */
    String userInfo;//个人信息
    String token = "";//身份验证信息(未用到)
    int uid = 0;//用户ID


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 返回登录结果json对象
     * @return 如果没有登录成功返回null
     */
    public JSONObject getUserInfoJson() {
        JSONObject object = null;
        if (userInfo.length() != 0) {
            try {
                object = new JSONObject(userInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    /**
     * 返回昵称
     * @return 如果没有返回""
     */
    public String getNickname() {
        JSONObject object = getUserInfoJson();
        String nickname = "";
        if (object != null) {
            try {
                nickname = object.getString("nick_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nickname;
    }

    /**
     * 返回用户id
     * @return
     */
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * 返回头像网址
     * @return
     */
    public String getPhotoAddress() {
        JSONObject object = getUserInfoJson();
        String address = "";
        if (object != null) {
            try {
                address = object.getString("photo");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return address;
    }

    /**
     * 读取配置文件
     */
    public void readConfig() {
        SharedPreferences user = App.app.getSharedPreferences("user_info", 0);
        userInfo = user.getString("user_info", "");
        token = user.getString("token", "");
        uid = user.getInt("uid", 0);
    }

    /**
     * 保存配置文件
     */
    public void saveConfig() {
        SharedPreferences user = App.app.getSharedPreferences("user_info", 0);
        SharedPreferences.Editor ed = user.edit();
        ed.putString("user_info", userInfo);//客户信息
        ed.putString("token", token);//token
        ed.putInt("uid", uid);//uid
        ed.commit();
    }

}
