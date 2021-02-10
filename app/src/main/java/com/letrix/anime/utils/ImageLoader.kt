package com.letrix.anime.utils

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.google.android.material.imageview.ShapeableImageView

object ImageLoader {

    fun loadImage(poster: String, target: ShapeableImageView) {
        Glide.with(target).load(poster).transition(withCrossFade()).into(target)
    }

    fun loadImage(poster: Uri, target: ImageView) {
        Glide.with(target).load(poster).transition(withCrossFade()).into(target)
    }

}