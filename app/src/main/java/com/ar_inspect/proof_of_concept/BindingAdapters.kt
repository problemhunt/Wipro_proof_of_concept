package com.ar_inspect.proof_of_concept

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.awesome_lib.core.gone
import com.awesome_lib.core.show
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * [BindingAdapters] :
 *
 * Object holds various methods for Data-binding related `BindingAdapters` of custom attributes
 * with custom logic implementation.
 *
 */
object BindingAdapters {
    /**
     * Set image url passed in as [imageUrl] variable
     */
    @JvmStatic
    @BindingAdapter(
        "imageUrl",
        requireAll = false
    )
    fun loadServerImage(
        imageView: ImageView?,
        imageUrl: String?
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            imageView?.let { iv ->
                Glide.with(iv.context)
                    .load(imageUrl)
                    .apply(
                        RequestOptions()
                            .override(iv.width, iv.height)
                            .placeholder(R.drawable.ic_default_img)
                            .error(R.drawable.ic_default_img)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iv)
                iv.show()
            }
        } else {
            imageView?.gone()
        }

    }

    /**
     * Method set state of [SwipeRefreshLayout] based on [isRefreshing] variable
     */
    @JvmStatic
    @BindingAdapter("refreshing", requireAll = false)
    fun setSwipeRefreshing(swipeRefreshLayout: SwipeRefreshLayout?, isRefreshing: Boolean) {
        swipeRefreshLayout?.isRefreshing = isRefreshing
    }

    /**
     * Change visibility of [View] based on flag [show],
     * simple [Boolean] flag which indicates **true** as showing and **false** as gone.
     */
    @JvmStatic
    @BindingAdapter("setVisibility", requireAll = false)
    fun setVisibility(view: View?, show: Boolean) {
        if (show) view?.show() else view?.gone()
    }
}