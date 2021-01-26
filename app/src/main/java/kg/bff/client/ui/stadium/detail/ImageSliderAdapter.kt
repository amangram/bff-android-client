package kg.bff.client.ui.stadium.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import kg.bff.client.R
import kg.bff.client.data.model.Image
import kg.bff.client.ui.GlideApp

class ImageSliderAdapter(val context: Context, val images: List<Image>) : PagerAdapter() {

    lateinit var inflater: LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = images.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.sliding_images_layout, container, false)
        val imageView = view.findViewById<ImageView>(R.id.stadium_image_imageView)
        GlideApp.with(context).load(images[position].preview).centerCrop().into(imageView)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }
}