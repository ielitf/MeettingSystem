package com.hskj.meettingsys.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hskj.meettingsys.R;

public class GlideHelper {

    public static void showImageWithFullUrl(@Nullable Context context, String url, @Nullable ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.default_weather)
                .error(R.mipmap.default_weather)
                .dontAnimate()
                .into(imageView);
    }
}
