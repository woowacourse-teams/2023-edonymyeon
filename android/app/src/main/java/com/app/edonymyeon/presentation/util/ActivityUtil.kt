package com.app.edonymyeon.presentation.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.makeSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.makeSnackbarWithEvent(message: String, eventTitle: String, event: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setAction(eventTitle) {
            event()
        }.show()
}
