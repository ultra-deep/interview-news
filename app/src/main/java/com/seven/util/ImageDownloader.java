package com.seven.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 * Image abd photo downloader
 */
public class ImageDownloader {
    private final Context context;
    private String url;
    private int size;
    private MutableLiveData<Drawable> liveDrawable;
    private MutableLiveData<Float> liveProgressing;
    private MutableLiveData<Boolean> liveProgressingBoolean;
    private ImageView imageView;
    private Integer defaultDrawableResId;
    private boolean useCache = true;
    private ImageLoadingListener listener;
    
    // constructors
    /**
     * @param context android application context
     */
    public ImageDownloader(Context context) {
        this.context = context;
    }
    /**
     * @param imageView the target image view to show downloaded photo
     */
    public ImageDownloader(ImageView imageView) {
        this.imageView = imageView;
        this.context = imageView.getContext();
    }
    
    // METHODS Builders
    /**
     * @param url The http path of the photo
     */
    public ImageDownloader url(String url) {
        this.url = url;
        return this;
    }
    /**
     * @param liveDrawable for update related view after finished downloading photo
     */
    public ImageDownloader liveDrawable(MutableLiveData<Drawable> liveDrawable) {
        this.liveDrawable = liveDrawable;
        return this;
    }
    /**
     * @param liveProgressing for update related view to notify the progress (float)
     */
    public ImageDownloader liveProgressing(MutableLiveData<Float> liveProgressing) {
        this.liveProgressing = liveProgressing;
        return this;
    }
    /**
     * @param liveProgressingBoolean for update related view of progressing download (boolean = true if downloading.. flase end of download)
     */
    public ImageDownloader liveProgressingBoolean(MutableLiveData<Boolean> liveProgressingBoolean) {
        this.liveProgressingBoolean = liveProgressingBoolean;
        return this;
    }
    /**
     * @param imageView the target image view for show the downloaded photo
     */
    public ImageDownloader imageView(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }
    /**
     * @param defaultDrawableResId the default drawable res id for set in image view or live drawable when download was progressing
     */
    public ImageDownloader defaultDrawableResId(@DrawableRes Integer defaultDrawableResId) {
        this.defaultDrawableResId = defaultDrawableResId;
        return this;
    }
    /**
     * @param listener for notifying downloading photo state
     */
    public ImageDownloader listener(ImageLoadingListener listener) {
        this.listener = listener;
        return this;
    }
    /**
     * @param useCache if ture the photo will cached after finishing download
     */
    public ImageDownloader useCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }
    /**
     * resize downloaded photo
     * @param size max size of with or height of the photo
     */
    public ImageDownloader size(int size) {
        this.size = size;
        return this;
    }
    /**
     * start download the photo
     */
    public void startDownload() {

        if(size == 0) size = 512;
        if (liveProgressing != null) liveProgressing.setValue(0.0f);
        if (liveProgressingBoolean != null) liveProgressingBoolean.setValue(true);
//        if(draw defaultDrawableResId != null)

        RequestBuilder<Drawable> glide = Glide.with(context) //
                .load(url) //
                .thumbnail(0.5f) //
                //                .transition(DrawableTransitionOptions.withCrossFade())
                .override(size) //as example
                .diskCacheStrategy(useCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE) //
                .listener(new RequestListener<Drawable>() {
                    @Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadingFailed(url, null);
                        }
                        if (liveProgressing != null) liveProgressing.setValue(null);
                        if (liveProgressingBoolean != null) liveProgressingBoolean.setValue(false);
                        return false;
                    }
                    @Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (isFirstResource) {
                            if (listener != null) {
                                listener.onLoadingComplete(url, null, resource);
                            }
                            if (liveDrawable != null) liveDrawable.setValue(resource);
                            if (liveProgressing != null) liveProgressing.setValue(null);
                            if (liveProgressingBoolean != null) liveProgressingBoolean.setValue(false);
                        }
                        return false;
                    }
                }); //
        if (imageView != null) {
            glide.into(imageView);
        } else {
            glide.into(new CustomTarget<Drawable>() {
                @Override public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    //                        imageView.setImageBitmap(resource);
                    //                        liveDataDrawable.setValue(resource);
                }
                @Override public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        }
    }
    
    // Classes
    /**
     * listener for notifying the download state (progressing, finish or failed)
     */
    public interface ImageLoadingListener {
        /**
         * occurred when download failed
         * @param photoUrl the photo http url path
         * @param imageView target image view
         */
        void onLoadingFailed(String photoUrl, ImageView imageView);
        /**
         * occurred when download finished
         * @param photoUrl the photo http url path
         * @param imageView the target imageView
         * @param drawable downloaded photo as a drawable
         */
        void onLoadingComplete(String photoUrl, ImageView imageView, Drawable drawable);
    }
}
