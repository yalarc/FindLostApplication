package net.smartbetter.wonderful.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class BitmapLoader {
	private static final String TAG = "BitmapLoader";

	// 内存缓存,SoftReference实现自动回收
	private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	// 内存缓存,SoftReference实现自动回收
	private static HashMap<String, SoftReference<Drawable>> drawableCache = new HashMap<String, SoftReference<Drawable>>();
	/**
	 * 自动判断从内存还是从网络获取图片
	 * 
	 * @param imageURL
	 * @return
	 */
	public static Bitmap loadBitmap(String imageURL) {
		Bitmap bm = null;
		if (imageCache.containsKey(imageURL)) {// 从内存中获取
			Log.i(TAG, "loadBitmap: imageCache");
			SoftReference<Bitmap> reference = imageCache.get(imageURL);
			bm = reference.get();
		}
		if (null == bm) {// 到网络下载loadNetBitmap
			Log.i(TAG, "loadBitmap: loadNetBitmap");
			bm = loadNetBitmap(imageURL);
			if (null != bm) {
				imageCache.put(imageURL, new SoftReference<Bitmap>(bm)); // 保存到内存
			}
		}
		return bm;
	}

	// 从网络下载图片
	private static Bitmap loadNetBitmap(String imageURL) {
		try {
			URL url = new URL(imageURL);
			URLConnection connection;
			connection = url.openConnection();
			Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection
					.getContent());
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static Drawable loadDrawable(String addressUrl){
		Drawable bm = null;
		if (drawableCache.containsKey(addressUrl)) {// 从内存中获取
			System.out.println("imageCache");
			SoftReference<Drawable> reference = drawableCache.get(addressUrl);
			bm = reference.get();
		}
		if (null == bm) {// 到网络下载
			System.out.println("loadNetBitmap");
			bm = loadImageFromNetwork(addressUrl);
			if (null != bm) {
				drawableCache.put(addressUrl, new SoftReference<Drawable>(bm)); // 保存到内存
			}
		}
		return bm;
	}

	public static Drawable loadImageFromNetwork(String address) {
		Drawable drawable = null;
		try {
			drawable = Drawable.createFromStream(new URL(address).openStream(), "image.jpg");
		} catch (IOException e) {
			Log.d("test", e.getMessage());
		}
		return drawable;
	}

}
