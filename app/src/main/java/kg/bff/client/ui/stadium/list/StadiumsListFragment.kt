package kg.bff.client.ui.stadium.list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import kg.bff.client.R
import kg.bff.client.data.model.State
import kg.bff.client.gone
import kg.bff.client.toast
import kg.bff.client.ui.stadium.detail.StadiumDetailActivity
import kg.bff.client.visible
import kotlinx.android.synthetic.main.fragment_stadiums_list.*
import org.koin.android.viewmodel.ext.android.viewModel

class StadiumsListFragment : Fragment(), MenuItem.OnActionExpandListener,
    SearchView.OnQueryTextListener {

    private val TAG = this::class.java.simpleName
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var id: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_CODE = 42
    private val myViewModel: StadiumListViewModel by viewModel()
    private lateinit var stadiumsAdapter: StadiumsAdapter
    private lateinit var nearbyAdapter: StadiumsHorizontalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stadiums_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth.currentUser?.uid?.let { uid ->
            id = uid
        }
        setHasOptionsMenu(true)
        setAdapter()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLastLocation()
        getNearByStadiums()
        near_id.setOnClickListener { getLastLocation() }
        getAllStadiums()
    }

    private fun getAllStadiums() {
        myViewModel.stadiumsList.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> {
                    pb_stadiums.visible()
                }
                is State.Success -> {
                    stadiumsAdapter.swapData(state.data)
                    pb_stadiums.gone()
                }
                is State.Failed -> {
                    pb_stadiums.gone()
                    Log.e(TAG, state.message)
                    context?.toast("Невозможно связаться с сервером")
                }
            }
        })
    }

    private fun setAdapter() {
        stadiumsAdapter = StadiumsAdapter({ stadium ->
            startActivity(
                Intent(context, StadiumDetailActivity::class.java)
                    .putExtra("stadiumId", stadium.id)
            )
        }, { stadium ->
            val list = stadium.saved.toMutableList()
            if (stadium.saved.contains(id)) {
                list.remove(id)
            } else {
                list.add(id)
            }
            myViewModel.save(stadium.id, list).observe(viewLifecycleOwner, Observer { state ->
                stateListener(state)
            })
        })
        nearbyAdapter = StadiumsHorizontalAdapter { stadium ->
            startActivity(
                Intent(context, StadiumDetailActivity::class.java)
                    .putExtra("stadiumId", stadium.id)
            )
        }
        stadiums_recycler_view.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = stadiumsAdapter
            isNestedScrollingEnabled = false
        }
        rv_nearby_stadiums.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = nearbyAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun getNearByStadiums() {
        myViewModel.getLiveData().observe(viewLifecycleOwner, Observer { stadiums ->
            Log.d("TESTIR", stadiums.size.toString())
            nearbyAdapter.swapData(stadiums)
        })
    }

    private fun stateListener(state: State<String>) {
        when (state) {
            is State.Success -> {

            }
            is State.Failed->{
                activity?.toast(getString(R.string.error))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Log.d("TESTIR", "getLastLocation: ${location.latitude} ")
                        myViewModel.setLocation(location)
                        getNearByStadiums()
                    }
                }
            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            myViewModel.setLocation(mLastLocation)
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.search)
        item.setOnActionExpandListener(this)
        val searchView = item?.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        near_id.gone()
        rv_nearby_stadiums.gone()
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        near_id.visible()
        rv_nearby_stadiums.visible()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (this::stadiumsAdapter.isInitialized)
            stadiumsAdapter.filter.filter(newText)
        return true
    }
}