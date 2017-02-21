package net.smartbetter.wonderful.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.adapter.NewsListAdapter;
import net.smartbetter.wonderful.base.BaseFragment;
import net.smartbetter.wonderful.entity.NewsEntity;
import net.smartbetter.wonderful.entity.UserEntity;
import net.smartbetter.wonderful.ui.activity.ShareActivity;
import net.smartbetter.wonderful.utils.ConstantUtils;
import net.smartbetter.wonderful.utils.LogUtils;
import net.smartbetter.wonderful.utils.ToastUtils;
import net.smartbetter.wonderful.view.RefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by joe on 2017/1/16.
 */
public class FindFragment extends BaseFragment {

    @BindView(R.id.srl_news) RefreshLayout mRefreshLayout;
    @BindView(R.id.lv_news) ListView mListView;

    private NewsListAdapter mAdapter;
    private boolean loading = false; // 判断是否在加载更多,避免重复请求网络
    private int currentPage = 0; // 当前页面
    private List<NewsEntity> newsEntitys = new ArrayList<>();

    private static final int STATE_REFRESH = 0; // 下拉刷新
    private static final int STATE_MORE = 1; // 加载更多
    private int limit = 5; // 每页的数据是10条
    private String lastTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("失物招领");
        setHasOptionsMenu(true); // 配置Actionbar可先的属性
        View view = inflater.inflate(R.layout.fragment_news, null);
        ButterKnife.bind(this, view);
        initRefreshLayout();
        return view;
    }

    /**
     * 初始化RefreshLayout
     */
    private void initRefreshLayout() {
        // 设置进度动画的颜色
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark);
        // 设置进度圈的大小,只有两个值:DEFAULT、LARGE
        mRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        // true:下拉过程会自动缩放,230:下拉刷新的高度
        mRefreshLayout.setProgressViewEndTarget(true, 230);

        // 进入页面就执行下拉动画
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getData(0, STATE_REFRESH);
            }
        });
        // 下拉刷新操作
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(0, STATE_REFRESH);
            }
        });
        // 上拉加载更多操作
        mRefreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                if (!loading) {
                    LogUtils.i("JAVA", "ListView开始加载更多了");
                    loading = true;
                    getData(currentPage, STATE_MORE);
                }
            }
        });
        // ListView条目点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        mAdapter = new NewsListAdapter(getContext(), newsEntitys);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 分页获取数据
     * @param page 页码
     * @param actionType istView的操作类型（下拉刷新、上拉加载更多）
     */
    public void getData(final int page, final int actionType) {
        BmobQuery<NewsEntity> query = new BmobQuery<>();
        query.order("-createdAt"); // 按时间降序查询
        query.addWhereEqualTo("isFind", true);
        query.include("author"); // 希望在查询帖子信息的同时也把发布人的信息查询出来
        if(actionType == STATE_MORE) { //加载更多
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            // 只查询小于等于最后一个item发表时间的数据
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            // query.setSkip(page*limit+1); // 跳过之前页数并去掉重复数据
        } else {
            query.setSkip(0);
        }
        query.setLimit(limit);//设置每页数据个数
        //查找数据
        query.findObjects(new FindListener<NewsEntity>() {
            @Override
            public void done(List<NewsEntity> list, BmobException e) {
                if (e==null) {
                    if (list.size()>0) {
                        if(actionType == STATE_REFRESH) {
                            currentPage = 0;
                            newsEntitys.clear();
                        }
                        for (NewsEntity td : list) {
                            newsEntitys.add(td);
                        }
                        currentPage++;
                        mAdapter.notifyDataSetChanged();
                        lastTime = newsEntitys.get(newsEntitys.size()-1).getCreatedAt(); // 获取最后时间
                        if (actionType == STATE_MORE) {
                            mRefreshLayout.setLoading(false); // 结束旋转ProgressBar
                        }
                        LogUtils.i("JAVA", "第"+currentPage+"页数据加载完成");
                    } else if (actionType == STATE_MORE) {
                        ToastUtils.showShort(getActivity(), "没有更多数据了");
                        mRefreshLayout.setLoading(false); // 结束旋转ProgressBar
                    } else if (actionType == STATE_REFRESH) {
                        ToastUtils.showShort(getActivity(), "服务器没有数据");
                    }
                    loading = false;
                    mRefreshLayout.setRefreshing(false); // 请求完成结束刷新状态
                } else {
                    if (actionType == STATE_MORE) {
                        mRefreshLayout.setLoading(false); // 结束旋转ProgressBar
                    }
                    ToastUtils.showShort(getActivity(), "请求服务器异常,请稍后重试");
                    loading = false;
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 登录
            case ConstantUtils.RESULT_UPDATE_INFO:
                if (UserEntity.getCurrentUser()!=null) {
                    getData(0, STATE_REFRESH);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share_news, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                if (UserEntity.getCurrentUser()!=null) {
                    startActivityForResult(new Intent(getActivity(), ShareActivity.class),
                            ConstantUtils.RESULT_UPDATE_INFO);
                } else {
                    ToastUtils.showShort(getActivity(), getString(R.string.text_default_no_login));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
