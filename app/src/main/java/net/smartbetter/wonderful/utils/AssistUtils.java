package net.smartbetter.wonderful.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import net.smartbetter.wonderful.application.App;

public class AssistUtils {

    public static int getScreenWidth(){
        Resources resources = App.app.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        return width;//1080
    }

    public static int getScreenDensity(){
        Resources resources = App.app.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int density = dm.densityDpi;
        return density;//480
    }

    public static int getScreenHeight(){
        Resources resources = App.app.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int height = dm.heightPixels;
        return height;//1920
    }
    public static int getScreenPixelsArea(){
        return getScreenHeight()*getScreenWidth();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}

