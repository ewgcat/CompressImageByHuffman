package com.lishuaihua.test;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;



import java.util.ArrayList;
import java.util.List;


public class ImagePreviewActivity extends AppCompatActivity {
    private TouchEventViewPager viewPager;
    private TextView tvBack;
    private TextView tvPos;
    private Context mContext;
    private List<String> imageUrls = new ArrayList<>();
    private int initPosition = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mContext = this;
        imageUrls = getIntent().getStringArrayListExtra("URLS");
        if (null == imageUrls) {
            return;
        }
        initPosition = getIntent().getIntExtra("POS", 0);
        viewPager = findViewById(R.id.view_pager);
        tvBack = findViewById(R.id.button_back);
        tvPos = findViewById(R.id.tv_pos);
        tvPos.setText(String.format("%s/%s", initPosition + 1, imageUrls.size()));
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(this, imageUrls);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(initPosition);
        initListener();
    }

    private void initListener() {
        tvBack.setOnClickListener(v -> finish());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                initPosition = position;
                tvPos.setText(String.format("%s/%s", position + 1, imageUrls.size()));
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }





    private class ImagePagerAdapter extends PagerAdapter {
        private List<String> images;
        private LayoutInflater inflater;
        private Context mContext;

        ImagePagerAdapter(Context context, List<String> images) {
            this.mContext = context;
            this.images = images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @NotNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.activity_browser_images_item_pager,
                    view, false);
            PhotoView imageView2 = imageLayout.findViewById(R.id.image2);
            ProgressBar imageProgressbar = imageLayout.findViewById(R.id.image_progressbar);
            imageView2.setMinimumScale(0.5f);
            String imgPath = "";
            if (!StringUtil.isEmpty(images.get(position))) {
                imgPath = images.get(position).trim();
            }
            imageProgressbar.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .format(DecodeFormat.PREFER_RGB_565);

            Glide.with(mContext).setDefaultRequestOptions(options)
                    .load(imgPath).into(imageView2);
            view.addView(imageLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

}

