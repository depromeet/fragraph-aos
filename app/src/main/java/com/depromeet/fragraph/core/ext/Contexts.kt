package com.depromeet.fragraph.core.ext

import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.depromeet.fragraph.core.KEY_AUTH_SHARED

fun Context.authSharedPreferences(): SharedPreferences = getSharedPreferences(KEY_AUTH_SHARED, Context.MODE_PRIVATE)

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, resId, duration).show()
}

fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this,  text, duration).show()
}

fun Context.dpToPx(dp: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)