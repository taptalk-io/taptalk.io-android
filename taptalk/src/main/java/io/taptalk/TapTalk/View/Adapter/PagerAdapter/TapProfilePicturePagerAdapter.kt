package io.taptalk.TapTalk.View.Adapter.PagerAdapter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.taptalk.TapTalk.Model.TapPhotosItemModel
import io.taptalk.TapTalk.R

class TapProfilePicturePagerAdapter(private val context: Context, val images: ArrayList<TapPhotosItemModel>, private val listener: ProfilePictureListener?) : PagerAdapter() {
    override fun getCount(): Int {
       return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout = LayoutInflater.from(context).inflate(R.layout.tap_cell_profile_picture, container, false)
        val ivImage = layout.findViewById<ImageView>(R.id.iv_image)
        ivImage?.let {
            Glide.with(context).load(images[position].fullsizeImageURL).fitCenter().listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    listener?.onFailed()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    ivImage.setOnLongClickListener {
                        listener?.onLongClick(resource as BitmapDrawable);
                        true
                    }
                    return false
                }
            }).into(ivImage)
        }
        container.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    interface ProfilePictureListener {
        fun onLongClick(bitmap: BitmapDrawable)
        fun onFailed()
    }
}
