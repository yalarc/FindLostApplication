package net.smartbetter.wonderful.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.smartbetter.wonderful.R;
import net.smartbetter.wonderful.entity.NewsEntity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by joe on 2017/2/7.
 */
public class NewsListAdapter extends BaseAdapter {
    private Context context;
    private List<NewsEntity> list;
    private ViewHolder holder;
    private LayoutInflater inflater;

    public NewsListAdapter(Context context, List<NewsEntity> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list, null);
            holder = new ViewHolder();
            holder.avatar = (CircleImageView) convertView.findViewById(R.id.iv_avatar);
            holder.author = (TextView) convertView.findViewById(R.id.tv_author);
            holder.img = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.createdTime = (TextView) convertView.findViewById(R.id.tv_created_time);
            holder.sign = (TextView) convertView.findViewById(R.id.label_sign);
            holder.tel = (TextView) convertView.findViewById(R.id.text_tel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NewsEntity newsEntity = list.get(position);

        if (newsEntity.getAuthor().getAvatar() != null) {
            Glide.with(context)
                    .load(newsEntity.getAuthor().getAvatar().getFileUrl())
                    .into(holder.avatar);
        } else {
            Glide.with(context)
                    .load(R.drawable.default_avatar)
                    .into(holder.avatar);
        }
        holder.author.setText(newsEntity.getAuthor().getName());
        if(newsEntity.getFind() !=null && !newsEntity.getFind()){
            holder.sign.setText("这是我捡的");
        }else {
            holder.sign.setText("这是我丢的");
        }
        if (newsEntity.getImg() != null) {
            holder.img.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(newsEntity.getImg().getFileUrl())
                    .into(holder.img);
        } else {
            holder.img.setVisibility(View.GONE);
        }
        holder.tel.setText(newsEntity.getAuthor().getMobilePhoneNumber());
        holder.content.setText(newsEntity.getContent());
        holder.createdTime.setText(newsEntity.getCreatedAt());
        return convertView;
    }

    private class ViewHolder {
        CircleImageView avatar; // 头像
        TextView author; // 作者
        ImageView img; // 图片
        TextView content; // 内容
        TextView createdTime; // 创建时间
        TextView sign;//标签--find or lost
        TextView tel;
    }

}
