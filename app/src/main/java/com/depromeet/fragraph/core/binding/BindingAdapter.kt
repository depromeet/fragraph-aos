package com.depromeet.fragraph.core.binding

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.depromeet.fragraph.base.GlideApp
import com.depromeet.fragraph.base.ui.IRecyclerViewAdapter
import com.depromeet.fragraph.core.ext.enums.toEffect
import com.depromeet.fragraph.core.ext.enums.toNormal
import com.depromeet.fragraph.domain.model.enums.IncenseTitle
import com.shawnlin.numberpicker.NumberPicker

@BindingAdapter("bind_items")
fun <T> RecyclerView.setItems(items: List<T>) {
    (adapter as IRecyclerViewAdapter<T>).setItems(items)
}

@BindingAdapter("bind_view_pager_items")
fun <T> ViewPager2.setItemsForViewPager(items: List<T>) {
    (adapter as IRecyclerViewAdapter<T>).setItems(items)
}

@BindingAdapter("image_from_url")
fun ImageView.bindImageFromUrl(imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideApp.with(this.context)
            .load(imageUrl)
            .placeholder(ColorDrawable(Color.GRAY))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}

@BindingAdapter("bind_total_value")
fun ProgressBar.bindTotalValue(value: Int) {
    this.max = value
}

@BindingAdapter("bind_progress_value")
fun ProgressBar.bindProgressValue(value: Int) {
    this.progress = value
}

@BindingAdapter("bind_incense_title_text_normal")
fun TextView.bindIncenseTitleTextNormal(value: IncenseTitle) {
    this.text = value.toNormal()
}

@BindingAdapter("bind_incense_title_text_effect")
fun TextView.bindIncenseTitleTextEffect(value: IncenseTitle) {
    this.text = value.toEffect()
}

@BindingAdapter("bind_visible_alpha_anim")
fun FrameLayout.bindHistoryAlphaAnim(visibility: Int) {
    val transition = Fade()
    transition.duration = 200
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
    this.visibility = if (visibility == View.VISIBLE) View.VISIBLE else View.GONE
}

@BindingAdapter("bind_number_picker_valueChangeListener")
fun NumberPicker.bindPlaytimeValueChangeListener(onValueChangedListener: NumberPicker.OnValueChangeListener) {
    this.setOnValueChangedListener(onValueChangedListener)
}
