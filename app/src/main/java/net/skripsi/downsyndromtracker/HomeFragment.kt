package net.skripsi.downsyndromtracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.skripsi.downsyndromtracker.databinding.FragmentHomeBinding
import net.skripsi.downsyndromtracker.dialogfragment.DisconnectDialogFragment
import net.skripsi.downsyndromtracker.dialogfragment.InputCodeDialogFragment
import net.skripsi.downsyndromtracker.utils.CommonUtils
import net.skripsi.downsyndromtracker.utils.CommonUtils.isValidCoordinate
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


class HomeFragment : Fragment(), InputCodeDialogFragment.InputCodeDialogListener,
    DisconnectDialogFragment.DisconnectListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    private var marker: Marker? = null

    private var mId: String? = null
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null

    private lateinit var handler: Handler
    private lateinit var periodicCheckerRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        handler = Handler(Looper.getMainLooper())
        periodicCheckerRunnable = object : Runnable {
            override fun run() {
                getDeviceLocation()
                handler.postDelayed(this, 3000)
            }
        }

        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            setupProfile(userId)
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
        setupMap()
        setUpHeader()
        setupOnClickListener()
        updateMapLocation()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.layoutMaps.mapView.onResume()
        if (mId != null && mLatitude != null && mLongitude != null) {
            startPeriodicChecker()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.layoutMaps.mapView.onPause()
        stopPeriodicChecker()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMap() {
        getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(context))
        binding.layoutMaps.mapView.setMultiTouchControls(true)

        binding.layoutMaps.mapView.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

        val mapController: IMapController? = binding.layoutMaps.mapView.controller
        mapController?.setZoom(20.0)
    }


    private fun setUpHeader() {
        binding.layoutHeader.apply {
            txtTitle.text = "Beranda"
            txtWelcome.visibility = View.VISIBLE
            txtWelcome.text = "Hello Satria"
        }
    }

    private fun setupProfile(userId: String) {
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java)
                binding.layoutHeader.txtWelcome.text = username
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun startPeriodicChecker() {
        handler.postDelayed(periodicCheckerRunnable, 1000)
    }

    private fun stopPeriodicChecker() {
        handler.removeCallbacks(periodicCheckerRunnable)
    }


    private fun getDeviceLocation() {
        database.reference.child("device").child(mId ?: "")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val latitude = snapshot.child("latitude").value.toString()
                    val longitude = snapshot.child("longitude").value.toString()

                    if (isValidCoordinate(latitude) && isValidCoordinate(longitude)) {
                        mLatitude = latitude.toDouble()
                        mLongitude = longitude.toDouble()
                        updateMapLocation()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    private fun updateMapLocation() {
        val newLocation = GeoPoint(mLatitude ?: 0.0, mLongitude ?: 0.0)
        if (marker == null) {
            marker = Marker(binding.layoutMaps.mapView)
            marker?.position = newLocation
            marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding.layoutMaps.mapView.overlays.add(marker)
        } else {
            marker?.position = newLocation
        }
        binding.layoutMaps.mapView.controller.setCenter(newLocation)
    }


    private fun setupOnClickListener() {
        binding.btnAddDevice.setOnClickListener {
            showInputCodeDialog()
        }
        binding.layoutMaps.btnDisconnect.setOnClickListener {
            showDisconnectDialog()
        }
    }

    private fun showInputCodeDialog() {
        val dialog = InputCodeDialogFragment(this)
        dialog.show(parentFragmentManager, "input_code")
    }

    private fun showDisconnectDialog() {
        val dialog = DisconnectDialogFragment(this)
        dialog.show(parentFragmentManager, "disconnect")
    }

    override fun callbackDevice(id: String, latitude: String, longitude: String) {
        binding.layoutMaps.root.visibility = View.VISIBLE
        binding.cvAddDevice.visibility = View.GONE
        mId = id
        mLatitude = latitude.toDouble()
        mLongitude = longitude.toDouble()
        updateMapLocation()
        startPeriodicChecker()
    }

    override fun callbackDisconnect() {
        stopPeriodicChecker()
        binding.layoutMaps.root.visibility = View.GONE
        binding.cvAddDevice.visibility = View.VISIBLE
        mId = null
        mLatitude = null
        mLongitude = null
        marker = null
        binding.layoutMaps.mapView.overlays.clear()
    }

}