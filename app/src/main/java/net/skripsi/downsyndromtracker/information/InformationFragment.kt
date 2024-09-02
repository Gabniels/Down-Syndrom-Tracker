package net.skripsi.downsyndromtracker.information

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import net.skripsi.downsyndromtracker.R
import net.skripsi.downsyndromtracker.databinding.FragmentInformationBinding
import net.skripsi.downsyndromtracker.dialogfragment.AboutApplicationDialogFragment
import net.skripsi.downsyndromtracker.dialogfragment.AboutUsDialogFragment
import net.skripsi.downsyndromtracker.model.SliderModel

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private lateinit var sliderAdapter: SliderAdapter

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
        sliderAdapter = SliderAdapter(requireContext())
        sliderAdapter.renewItems(sliderData())
        binding.imageSlider.setSliderAdapter(sliderAdapter)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        binding.imageSlider.indicatorSelectedColor = Color.WHITE
        binding.imageSlider.indicatorUnselectedColor = Color.GRAY
        binding.imageSlider.scrollTimeInSec = 4
        binding.imageSlider.startAutoCycle()
    }

    private fun sliderData(): List<SliderModel> {
        return listOf(
            SliderModel("https://arsitagx-master.s3.ap-southeast-1.amazonaws.com/img_medium/4521/4526/31703/photo-exterior-view-phase-1-telkom-university-convention-hall-desain-arsitek-oleh-arah-studio.jpeg"),
            SliderModel("https://live.staticflickr.com/3924/14377575453_1fe2698010_z.jpg"),
            SliderModel("https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiCS3q1e5qv0h7BV93AI4poNjASogxXruQFGOEa7ViOzMSnrAifDbVwnYSqiIincJMpR5wGgnFe0ri-5j6IEEBU1luFTzjNBzi8rADNGa_nkfPwUa5uvRT2YApjx7JSyX0DsC3dcCz6SeDI/s1920/telkomsel-medium.jpg")
        )
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