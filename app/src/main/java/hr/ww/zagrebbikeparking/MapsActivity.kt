package hr.ww.zagrebbikeparking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hr.ww.zagrebbikeparking.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var requestQueue: RequestQueue
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize API connection stuff
        val appnetwork = BasicNetwork(HurlStack())
        val appcache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap
        requestQueue = RequestQueue(appcache, appnetwork).apply {
            start()
        }



    }
    private fun addPinsToMap(googleMap: GoogleMap, lng: Double, lat: Double, dist: Double){

        //val url = "http://netwwork.duckdns.org:8080/pins/within?centerX=${lng}&centerY=${lat}&distance=${dist}";

        val url = "http://netwwork.duckdns.org:8080/pins/3";
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                // Learn to handle errors
                /*if(response.get("error") == "Internal Server Error"){
                    Log.d("database_connection", "Internal Server Error")
                }*/
                // ugly casts to string than to double
                val pinPos = LatLng(
                    response.getDouble("posX"),
                    response.getDouble("posY")
                )
                googleMap.addMarker(MarkerOptions().position(pinPos).title("TestPin"))


            },
            {error ->
                Log.d("vol", error.toString())

            }
        )

        requestQueue.add(jsonObjectRequest);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        addPinsToMap(googleMap, 0.0, 0.0, 100.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}