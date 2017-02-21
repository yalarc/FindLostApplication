package net.smartbetter.wonderful.base;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by joe on 2017/1/16.
 */
public class BaseFragment extends Fragment {

    /**
     * 设置空间隐藏, 不占位
     * @param views
     */
    public void setViewVisibilityGone(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 设置空间显示
     * @param views
     */
    public void setViewVisibilityVisible(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
