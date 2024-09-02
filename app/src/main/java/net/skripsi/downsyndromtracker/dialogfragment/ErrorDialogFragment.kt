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
import net.skripsi.downsyndromtracker.databinding.FragmentErrorDialogBinding

class ErrorDialogFragment(val from: String) : DialogFragment() {

    private var _binding: FragmentErrorDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentErrorDialogBinding.inflate(inflater, container, false)

        setupDialog()
        setupLayout()
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

    private fun setupLayout() {
        when (from) {
            "signin" -> {
                binding.txtDesc.text = "Akun tidak ditemukan"
            }

            "input" -> {
                binding.txtDesc.text = "Kode tidak boleh kosong"
            }

            "device" -> {
                binding.txtDesc.text = "Device gagal ditambahkan"
            }
        }
    }

    private fun setupOnClickListener() {
        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.btnViewGps.setOnClickListener {
            dismiss()
        }
    }

}