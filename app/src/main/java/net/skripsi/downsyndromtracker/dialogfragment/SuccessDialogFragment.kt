package net.skripsi.downsyndromtracker.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import net.skripsi.downsyndromtracker.R
import net.skripsi.downsyndromtracker.databinding.FragmentSuccessDialogBinding

class SuccessDialogFragment(
    private val listener: SuccessAddDeviceListener,
    private val id: String,
    private val latitude: String,
    private val longitude: String
) : DialogFragment() {

    private var _binding: FragmentSuccessDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSuccessDialogBinding.inflate(inflater, container, false)

        setupDialog()
        setupOnClickListener()

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
        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.btnViewGps.setOnClickListener {
            listener.callbackDevice(id, latitude, longitude)
            dismiss()
        }
    }

    interface SuccessAddDeviceListener {
        fun callbackDevice(id: String, latitude: String, longitude: String)
    }

}