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
import net.skripsi.downsyndromtracker.databinding.FragmentDisconnectDialogBinding

class DisconnectDialogFragment(
    private val listener: DisconnectListener
) : DialogFragment() {

    private var _binding: FragmentDisconnectDialogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDisconnectDialogBinding.inflate(inflater, container, false)

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
        binding.btnNegative.setOnClickListener {
            dismiss()
        }

        binding.btnPositive.setOnClickListener {
            listener.callbackDisconnect()
            dismiss()
        }
    }

    interface DisconnectListener {
        fun callbackDisconnect()
    }

}