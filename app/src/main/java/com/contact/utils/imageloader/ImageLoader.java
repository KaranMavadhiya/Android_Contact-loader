package com.contact.utils.imageloader;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;

public class ImageLoader {


    public static void loadImage(final Context context, final ImageView imageView, final Uri imageUri, final int placeHolderRes) {

        Glide.with(context)
                .using(new GlideContentProviderLoader(context))
                .load(imageUri)
                .placeholder(placeHolderRes)
                .dontAnimate()
                .dontTransform()
                .into(imageView);

    }

    public static void loadImageWithCircleTransform(final Context context, final ImageView imageView, final Uri imageUri, final int placeHolderRes) {

        Glide.with(context)
                .using(new GlideContentProviderLoader(context))
                .load(imageUri)
                .placeholder(placeHolderRes)
                .thumbnail(0.5f)
                .crossFade()
                .transform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }

    public static void loadImage(final Context context, final ImageView imageView, final String imageUrl, final int placeHolderRes) {

        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(placeHolderRes)
                .dontAnimate()
                .dontTransform()
                .into(imageView);

    }

    public static void loadImage(final Context context, final ImageView imageView, final int resource, final int placeHolderRes) {

        Glide.with(context)
                .load(resource)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(placeHolderRes)
                .dontAnimate()
                .dontTransform()
                .into(imageView);
    }

    public static void loadRoundedImage(final Context context, final ImageView imageView, final String imageUrl, final int placeHolderRes) {
        Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .dontAnimate()
                .dontTransform()
                .placeholder(placeHolderRes).centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                final RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static void loadImage(final Context context, final ImageView imageView, final File file, final int placeHolderRes) {

        Glide.with(context)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(placeHolderRes)
                .dontAnimate()
                .dontTransform()
                .into(imageView);
    }
}
