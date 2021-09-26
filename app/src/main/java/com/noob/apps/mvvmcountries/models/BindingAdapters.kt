package com.noob.apps.mvvmcountries.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object BindingAdapters {
    @BindingAdapter("app:imageThumb")
    @JvmStatic
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
//            SvgLoader.pluck()
//                .with(imageView.context as Activity?)
//                .setPlaceHolder(
//                    com.noob.apps.mvvmcountries.R.mipmap.ic_launcher,
//                    com.noob.apps.mvvmcountries.R.mipmap.ic_launcher
//                )
//                .load(imageUrl, imageView)
            Picasso.get().load(imageUrl).into(imageView);

        }

    }
}