package com.letrix.anime.utils

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import timber.log.Timber

object ImageLoader {

    fun loadImage(poster: String, target: ShapeableImageView) {
        Picasso.get().load(poster).into(target)
//        Glide.with(target).load(poster).transition(withCrossFade()).into(target)
    }

    fun loadImage(poster: String, target: ImageView) {
        Glide.with(target).load(poster).transition(withCrossFade()).into(target)
    }

}