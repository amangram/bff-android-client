package kg.bff.client.ui.map

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kg.bff.client.R
import kg.bff.client.data.model.Stadium
import kg.bff.client.ui.GlideApp
import kotlinx.android.synthetic.main.map_info_window.view.*
import java.util.logging.Handler

class InfoWindowAdapter(val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker?): View {
        var view = (context as Activity).layoutInflater.inflate(R.layout.map_info_window,null)
        val data = marker?.tag as Stadium
        if (!data.images.isNullOrEmpty()){
            GlideApp.with(context)
                .load(data.images.first().preview)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        val handler = android.os.Handler()
                        handler.postDelayed({
                          if (marker.isInfoWindowShown){
                              marker.showInfoWindow()
                          }
                        },100)
                        return false
                    }

                })
                .into(view.map_image)

        }
        view.map_name.text = data.name
        return view
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }
}