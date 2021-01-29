package com.lishuaihua.imageselector.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import com.lishuaihua.imageselector.entry.Image;
import com.lishuaihua.imageselector.utils.ImageUtil;
import com.lishuaihua.imageselector.utils.VersionUtils;
import com.lishuaihua.imageselector.photoview.PhotoView;
import com.lishuaihua.imageselector.photoview.PhotoViewAttacher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<PhotoView> viewList = new ArrayList<>();
    List<Image> mImgList;
    private OnItemClickListener mListener;
    private boolean isAndroidQ = VersionUtils.isAndroidQ();

    public ImagePagerAdapter(Context context, List<Image> imgList) {
        this.mContext = context;
        createImageViews();
        mImgList = imgList;
    }

    private void createImageViews() {
        for (int i = 0; i < mImgList.size(); i++) {
            PhotoView imageView = new PhotoView(mContext);
            imageView.setAdjustViewBounds(true);
            viewList.add(imageView);
        }
    }

    @Override
    public int getCount() {
        return mImgList == null ? 0 : mImgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof PhotoView) {
            PhotoView view = (PhotoView) object;
            view.setImageDrawable(null);
            viewList.add(view);
            container.removeView(view);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (position > 0) {
            final PhotoView currentView = viewList.remove(0);
            final Image image = mImgList.get(position);
            container.addView(currentView);
            if (image.isGif()) {
                currentView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(mContext).load(isAndroidQ ? image.getUri() : image.getPath())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).override(720, 1080)
                        .into(currentView);
            } else {
                Glide.with(mContext).asBitmap()
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                        .load(isAndroidQ ? image.getUri() : image.getPath()).into(new SimpleTarget<Bitmap>(720, 1080) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int bw = resource.getWidth();
                        int bh = resource.getHeight();
                        if (bw > 4096 || bh > 4096) {
                            Bitmap bitmap = ImageUtil.zoomBitmap(resource, 4096, 4096);
                            setBitmap(currentView, bitmap);
                        } else {
                            setBitmap(currentView, resource);
                        }
                    }
                });
            }
            currentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(position, image);
                    }
                }
            });
            return currentView;
        }else {
          return   new PhotoView(mContext);
        }
    }

    private void setBitmap(PhotoView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        if (bitmap != null) {
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            int vw = imageView.getWidth();
            int vh = imageView.getHeight();
            if (bw != 0 && bh != 0 && vw != 0 && vh != 0) {
                if (1.0f * bh / bw > 1.0f * vh / vw) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    float offset = (1.0f * bh * vw / bw - vh) / 2;
                    adjustOffset(imageView, offset);
                } else {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mListener = l;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Image image);
    }

    private void adjustOffset(PhotoView view, float offset) {
        PhotoViewAttacher attacher = view.getAttacher();
        try {
            Field field = PhotoViewAttacher.class.getDeclaredField("mBaseMatrix");
            field.setAccessible(true);
            Matrix matrix = (Matrix) field.get(attacher);
            matrix.postTranslate(0, offset);
            Method method = PhotoViewAttacher.class.getDeclaredMethod("resetMatrix");
            method.setAccessible(true);
            method.invoke(attacher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
