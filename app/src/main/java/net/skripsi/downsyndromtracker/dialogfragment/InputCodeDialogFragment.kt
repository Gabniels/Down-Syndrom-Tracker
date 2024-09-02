package net.skripsi.downsyndromtracker.dialogfragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.skripsi.downsyndromtracker.R
import net.skripsi.downsyndromtracker.databinding.FragmentInputCodeDialogBinding
import net.skripsi.downsyndromtracker.utils.CommonUtils

class InputCodeDialogFragment(private val listener: InputCodeDialogListener) : DialogFragment(),
    SuccessDialogFragment.SuccessAddDeviceListener {

    private var _binding: FragmentInputCodeDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputCodeDialogBinding.inflate(inflater, container, false)

        setupDialog()
        initLoadingDialog()
        setupOnClickListener()

        database = FirebaseDatabase.getInstance().reference.child("device")

        return binding.root
    }

    private fun setupDialog() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setWindowAnimations(R.style.dialog_animation_fade)
        dialog?.setCancelable(false)
    }

    private fun setupOnClickListener() {
        binding.btnSubmit.setOnClickListener {
            val deviceId = binding.edtCode.text.toString().trim()
            if (deviceId.isNotEmpty()) {
                getDeviceLocation(CommonUtils.sanitizeInput(deviceId))
            } else {
                showErrorAddDeviceDialog("input")
            }
        }
    }

    private fun getDeviceLocation(id: String) {
        showLoadingDialog()
        database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hideLoadingDialog()
                if (snapshot.value != null) {
                    val latitude = snapshot.child("latitude").value.toString()
                    val longitude = snapshot.child("longitude").value.toString()
                    showSuccessAddDeviceDialog(id, latitude, longitude)
                } else {
                    showErrorAddDeviceDialog("device")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showErrorAddDeviceDialog("device")
            }
        })
    }

    private fun showSuccessAddDeviceDialog(id: String, latitude: String, longitude: String) {
        val dialog = SuccessDialogFragment(this, id, latitude, longitude)
        dialog.show(parentFragmentManager, "success")
        dismiss()
    }

    private fun showErrorAddDeviceDialog(from: String) {
        val dialog = ErrorDialogFragment(from)
        dialog.show(parentFragmentManager, "error")
        dismiss()
    }

    private fun initLoadingDialog() {
        loadingDialog = Dialog(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.loading_dialog, null)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setContentView(view)
        loadingDialog.setCancelable(false)
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    override fun callbackDevice(id: String, latitude: String, longitude: String) {
        listener.callbackDevice(id, latitude, longitude)
    }

    interface InputCodeDialogListener {
        fun callbackDevice(id: String, latitude: String, longitude: String)
    }
}