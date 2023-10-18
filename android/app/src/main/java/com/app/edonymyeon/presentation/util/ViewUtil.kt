package com.app.edonymyeon.presentation.util

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar

fun View.makeSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setAnimationMode(ANIMATION_MODE_SLIDE).show()
}

fun View.makeSnackbarWithEvent(message: String, eventTitle: String, event: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setAction(eventTitle) {
            event()
        }.setAnimationMode(ANIMATION_MODE_SLIDE).show()
}

fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
    setOnClickListener(OnSingleClickListener {
        onSingleClick(it)
    })
}
