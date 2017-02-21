package net.smartbetter.wonderful.entity;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by joe on 2017/2/6.
 */
public class NewsEntity extends BmobObject {

    private UserEntity author;
    private String content;// 内容
    private BmobFile img;// 图片
    private Boolean isFind;

    public Boolean getFind() {
        return isFind;
    }

    public void setFind(Boolean find) {
        isFind = find;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BmobFile getImg() {
        return img;
    }

    public void setImg(BmobFile img) {
        this.img = img;
    }
}