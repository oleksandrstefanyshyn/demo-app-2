package com.oleksandrstefanyshyn.servicedemoapp

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isGone")
fun View.setIsGone(value: Boolean) {
    isGone = value
}

@BindingAdapter("isVisible")
fun View.setIsVisible(value: Boolean) {
    isVisible = value
}
