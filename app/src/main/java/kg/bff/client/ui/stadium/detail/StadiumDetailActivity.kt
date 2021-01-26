package kg.bff.client.ui.stadium.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kg.bff.client.R
import kg.bff.client.data.model.State
import kg.bff.client.gone
import kg.bff.client.toast
import kg.bff.client.ui.stadium.booking.StadiumBookingListActivity
import kg.bff.client.visible
import kotlinx.android.synthetic.main.activity_stadium.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class StadiumDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = this::class.java.simpleName
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var id: String
    private lateinit var latLng: LatLng
    private lateinit var stadiumName: String
    private lateinit var stadiumId: String
    private lateinit var adminId: String
    private lateinit var savedList: MutableList<String>
    private var saved = false
    private val myViewModel: StadiumDetailViewModel by viewModel {
        parametersOf(intent.getStringExtra("stadiumId"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stadium)
        stadiumId = intent.getStringExtra("stadiumId")
        auth.currentUser?.uid?.let {
            id = it
        }
        setStadiumData()
        booking_btn.setOnClickListener {
            val intent = Intent(this, StadiumBookingListActivity::class.java)
            startActivity(
                intent.putExtra("stadiumId", stadiumId)
                    .putExtra("ADMIN", adminId)
                    .putExtra("S_NAME", stadiumName)
            )
        }
        save_iv.setOnClickListener {
            if (savedList.contains(id)) {
                savedList.remove(id)
                toast("not")
                saved = false
            } else {
                savedList.add(id)
                toast("yes")
                saved = true
            }
            save()
        }
    }

    private fun setStadiumData() {
        myViewModel.stadium.observe(this, Observer { state ->
            when (state) {
                is State.Loading -> {
                    pb_stadium_detail.visible()
                }
                is State.Success -> {
                    pb_stadium_detail.gone()
                    state.data.let { stadium ->
                        adminId = stadium.userId
                        myViewModel.setAdmin(adminId)
                        stadiumName = stadium.name
                        savedList = stadium.saved.toMutableList()
                        stadium_images_viewPager.apply {
                            adapter = ImageSliderAdapter(this@StadiumDetailActivity, stadium.images)
                        }
                        indicator.setViewPager(stadium_images_viewPager)
                        if (stadium.saved.contains(id)) {
                            save_iv.setImageResource(R.drawable.ic_saved)
                        } else {
                            save_iv.setImageResource(R.drawable.ic_not_saved)
                        }
                        stadium_name_tv.text = stadium.name
                        stadium_price_tv.text = "${stadium.price} С/Час"
                        stadium_description_tv.text = stadium.description
                        stadium.location?.let { latLng = LatLng(it.latitude, it.longitude) }
                        settingMap()
                        getAdmin()
                    }
                }
                is State.Failed -> {
                    pb_stadium_detail.gone()
                    Log.e(TAG, state.message)
                    toast(getString(R.string.error))
                }
            }
        })
    }

    fun save() {
        myViewModel.save(stadiumId, savedList).observe(this, Observer { state ->
            when (state) {
                is State.Loading -> pb_stadium_detail.visible()
                is State.Success -> {
                    pb_stadium_detail.gone()
                    if (saved) {
                        save_iv.setImageResource(R.drawable.ic_saved)
                        toast(getString(R.string.saved))
                    } else{
                        save_iv.setImageResource(R.drawable.ic_not_saved)
                    }
                }
                is State.Failed -> {
                    pb_stadium_detail.gone()
                    toast(getString(R.string.cant_save))
                }
            }
        })
    }

    private fun getAdmin() {
        myViewModel.getAdmin().observe(this, Observer { state ->
            when (state) {
                is State.Success -> {
                    state.data.let { admin ->
                        Log.d("EWrT", "${admin.name} ")
                        whatsApp_btn.setOnClickListener { whatsAppCall(admin.numberPhone) }
                        telegram_btn.setOnClickListener { telegramCall(admin.status) }
                        phone_call_btn.setOnClickListener { phoneCall(admin.numberPhone) }
                    }
                }
            }
        })
    }

    private fun whatsAppCall(number: String) {
        val uri = Uri.parse("smsto:${number.replace("+", "")}")

        try {
            val packageInfo =
                packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
            val waIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = uri
                `package` = "com.whatsapp"
            }
            startActivity(waIntent)
        } catch (e: PackageManager.NameNotFoundException) {
        }
    }

    private fun telegramCall(name: String) {
        val telegram =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/$name"))
        startActivity(telegram)
    }

    private fun phoneCall(number: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$number")
        }
        startActivity(callIntent)
    }

    private fun settingToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun settingMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap!!.addMarker(MarkerOptions().position(latLng).title(stadiumName))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
    }
}
