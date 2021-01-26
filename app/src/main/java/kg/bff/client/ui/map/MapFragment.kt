package kg.bff.client.ui.map

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import kg.bff.client.R
import kg.bff.client.gone
import kg.bff.client.data.model.Stadium
import kg.bff.client.data.model.State
import kg.bff.client.toast
import kg.bff.client.ui.stadium.detail.StadiumDetailActivity
import kg.bff.client.visible
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.android.viewmodel.ext.android.viewModel

class MapFragment : androidx.fragment.app.Fragment(), OnMapReadyCallback {

    companion object {
        private val TAG = this::class.java.simpleName
    }

    private val viewModel: MapViewModel by viewModel()
    private lateinit var stadiums: List<Stadium>
    private val marker by lazy { vectorToBitmapDesc(R.drawable.ic_marker) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.stadiums.observe(viewLifecycleOwner, Observer { state ->
            when(state){
                is State.Loading -> {
                    pb_map.visible()
                }
                is State.Success -> {
                    pb_map.gone()
                    state.data.let {stadiums->
                        this.stadiums = stadiums
                        settingMap()
                    }
                }
                is State.Failed->{
                    pb_map.gone()
                    context?.toast("Невозможно связаться с сервером")
                    Log.e(TAG,state.message)
                }
            }
        })
    }

    private fun settingMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.full_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        var infoWindowAdapter = InfoWindowAdapter(requireContext())
        googleMap?.setInfoWindowAdapter(infoWindowAdapter)
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.setMinZoomPreference(11f)
        googleMap?.setOnInfoWindowClickListener { marker ->
            val stadium = marker.tag as Stadium
            startActivity(Intent(context,StadiumDetailActivity::class.java)
                .putExtra("stadiumId",stadium.id))
        }
        for (item in stadiums) {
            if (item.location!=null){
            val marker =googleMap?.addMarker(
                MarkerOptions().position(
                    LatLng(
                        item.location.latitude,
                        item.location.longitude
                    )
                ).icon(marker)
            )
            marker?.tag = item}
        }
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(42.882004, 74.582748),
                11.0f
            )
        )
    }

    private fun vectorToBitmapDesc(@DrawableRes drawable: Int): BitmapDescriptor{
        val vectorDrawable = context?.let { ContextCompat.getDrawable(it,drawable) }
        vectorDrawable?.setBounds(0,0,vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth,vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}