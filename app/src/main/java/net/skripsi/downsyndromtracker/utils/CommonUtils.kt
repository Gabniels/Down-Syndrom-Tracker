package net.skripsi.downsyndromtracker.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import net.skripsi.downsyndromtracker.R
import net.skripsi.downsyndromtracker.databinding.ViewValidationErrorBinding
import net.skripsi.downsyndromtracker.databinding.ViewValidationSuccessBinding

object CommonUtils {

    fun showValidationError(
        binding: ViewValidationErrorBinding,
        context: Context?,
        message: String
    ) {
        binding.apply {
            root.visibility = View.VISIBLE
            txtDescError.text = message
            val fadeOutAnimation =
                AnimationUtils.loadAnimation(context, R.anim.fade_out_error)
            fadeOutAnimation.duration = 3000
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    root.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            root.startAnimation(fadeOutAnimation)
            imgClose.setOnClickListener {
                root.clearAnimation()
                root.visibility = View.GONE
            }
        }
    }

    fun showValidationSuccess(
        binding: ViewValidationSuccessBinding,
        context: Context?,
        message: String
    ) {
        binding.apply {
            root.visibility = View.VISIBLE
            txtDesc.text = message
            val fadeOutAnimation =
                AnimationUtils.loadAnimation(context, R.anim.fade_out_error)
            fadeOutAnimation.duration = 3000
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    root.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            root.startAnimation(fadeOutAnimation)
            imgClose.setOnClickListener {
                root.clearAnimation()
                root.visibility = View.GONE
            }
        }
    }

    fun sanitizeInput(input: String): String {
        return input.replace(".", "")
            .replace("#", "")
            .replace("$", "")
            .replace("[", "")
            .replace("]", "")
    }

    fun isValidCoordinate(value: String): Boolean {
        val regex = Regex("^-?\\d+(\\.\\d+)?$")
        return regex.matches(value)
    }

}