package net.skripsi.downsyndromtracker.information

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import net.skripsi.downsyndromtracker.R
import net.skripsi.downsyndromtracker.databinding.FragmentInformationBinding
import net.skripsi.downsyndromtracker.dialogfragment.AboutApplicationDialogFragment
import net.skripsi.downsyndromtracker.dialogfragment.AboutUsDialogFragment
import net.skripsi.downsyndromtracker.model.SliderModel

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private lateinit var sliderAdapter: ImageSliderAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    private val images = listOf(
        R.drawable.img_slider_1,
        R.drawable.img_slider_2,
        R.drawable.img_slider_3
    )

//    private lateinit var sliderAdapter: SliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)

        setupHeader()
        setupSlider()
        setupAboutDev()
        setupAboutApps()
        setupOnClickListener()

        return binding.root
    }

    private fun setupHeader() {
        binding.layoutHeader.apply {
            txtTitle.text = "Informasi"
        }
    }

    private fun setupSlider() {
        sliderAdapter = ImageSliderAdapter(images)
        binding.viewPager.adapter = sliderAdapter

        val slideRunnable = object : Runnable {
            override fun run() {
                currentPage = (currentPage + 1) % images.size
                binding.viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, 2000)
            }
        }

        handler.post(slideRunnable)
    }

    private fun setupAboutApps() {
        binding.layoutAboutApps.apply {
            imgAbout.setImageResource(R.drawable.ic_apps)
            txtTitle.text = "Tentang Alat"
        }
    }

    private fun setupAboutDev() {
        binding.layoutAboutUs.apply {
            imgAbout.setImageResource(R.drawable.ic_dev)
            txtTitle.text = "Tentang Kami"
        }
    }

    private fun setupOnClickListener() {
        binding.apply {
            layoutAboutUs.root.setOnClickListener {
                showAboutUsDialog()
            }
            layoutAboutApps.root.setOnClickListener {
                showAboutApplication()
            }
        }
    }

    private fun showAboutUsDialog() {
        val dialog = AboutUsDialogFragment()
        dialog.show(parentFragmentManager, "about_us")
    }

    private fun showAboutApplication() {
        val dialog = AboutApplicationDialogFragment()
        dialog.show(parentFragmentManager, "about_application")
    }


}