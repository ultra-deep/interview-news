package com.seven.util

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.BindingAdapter


/**
 * The base binding of undefined binds implemented here
 */
class BaseBindings {
    companion object {

        @JvmStatic @BindingAdapter("app:imageUrl") fun loadImage(view: ImageView, url: String?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (url != null && url.isEmpty() == false) {
                ImageDownloader(view.context).url(url).imageView(view).startDownload();
            }
        }

        @JvmStatic @BindingAdapter("android:layout_marginTop") fun View.setMarginTopValue(marginValue: Float) {
            (layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin = marginValue.toInt()
            }
        }
        @JvmStatic @BindingAdapter("android:layout_marginBottom") fun View.setMarginBottomValue(marginValue: Float) {
            (layoutParams as ViewGroup.MarginLayoutParams).apply {
                bottomMargin = marginValue.toInt()
            }
        }
        @JvmStatic @BindingAdapter("android:layout_marginStart") fun View.android_layout_marginStart(marginValue: Float) {
            (layoutParams as ViewGroup.MarginLayoutParams).apply {
                marginStart = marginValue.toInt()
            }
        }
        @JvmStatic
        @BindingAdapter("android:layout_marginEnd")
        fun View.android_layout_marginEnd(marginValue: Float) {
            (layoutParams as ViewGroup.MarginLayoutParams).apply {
                marginEnd = marginValue.toInt()
            }
        }


        @JvmStatic
        @BindingAdapter("android:layout_width")
        fun View.setLayoutWidth(width: Float) {
            (layoutParams as ViewGroup.LayoutParams).apply {
                this.width = width.toInt()
            }
        }

        @JvmStatic
        @BindingAdapter("android:layout_height")
        fun View.setLayoutHeight(height: Float) {
            (layoutParams as ViewGroup.LayoutParams).apply {
                this.height = height.toInt()
            }
        }

        @JvmStatic
        @BindingAdapter("app:srcCompat")
        fun setSrcCompat(imageView: ImageView, resId: Int?) {
            if (resId != null) {
                imageView.setImageResource(resId)
            }
        }

        @JvmStatic
        @BindingAdapter("app:srcCompat")
        fun setSrcCompatDrawable(imageView: ImageView, drawable: Drawable?) {
            imageView.setImageDrawable(drawable)
        }

        @JvmStatic
        @BindingAdapter("app:tint")
        fun app_tint(view: ImageView, colorStateList: ColorStateList?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.imageTintList = colorStateList
            };
        }

        @JvmStatic
        @BindingAdapter("app:backgroundTint")
        fun app_background_tint(view: View, colorStateList: ColorStateList?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.backgroundTintList = colorStateList
            };
        }
//        @JvmStatic
//        @BindingAdapter("android:indeterminateTint")
//        fun android_indeterminateTint(view: ProgressBar, ColoS: String?) { // This methods should not have any return type, = declaration would make it return that object declaration.
//            //            Glide.with(view.context).load(url).into(view)
//            NetUtils.downloadImage(view ,url)
//        }
        @JvmStatic
        @BindingAdapter("android:indeterminateTint")
        fun android_indeterminateTint(view: ProgressBar, colorStateList: ColorStateList?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.backgroundTintList = colorStateList
            };
        }
        @JvmStatic
        @BindingAdapter("app:indeterminateTint")
        fun app_indeterminateTint(view: ProgressBar, colorStateList: ColorStateList?) {
            android_indeterminateTint(view, colorStateList)
        }

        @JvmStatic
        @BindingAdapter("android:layout_weight")
        fun set_android_layout_weight(view: ScrollView, weight: Float?) {
            if (view.layoutParams != null) {
                (view.layoutParams as LinearLayout.LayoutParams).weight = weight ?: 0f
            } else {
                view.layoutParams =
                    LinearLayout.LayoutParams(view.measuredWidth, view.measuredHeight, weight ?: 0f)
            }
        }
    }
}
