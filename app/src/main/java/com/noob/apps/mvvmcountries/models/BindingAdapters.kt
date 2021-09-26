package com.noob.apps.mvvmcountries.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object BindingAdapters {
    @BindingAdapter("app:imageThumb")
    @JvmStatic
    fun loadImage(imageView: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(imageView);

        }

    }
}