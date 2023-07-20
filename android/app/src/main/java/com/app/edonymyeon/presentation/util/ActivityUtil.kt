package com.app.edonymyeon.presentation.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.makeSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}
