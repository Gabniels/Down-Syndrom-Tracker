package net.skripsi.downsyndromtracker

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import net.skripsi.downsyndromtracker.databinding.ActivityForgetPasswordBinding


class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setStatusBarColor()
        auth = FirebaseAuth.getInstance()
        setOnClickListener()
    }

    private fun setStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)
    }

    private fun setOnClickListener() {
        binding.btnSubmit.setOnClickListener {
            val email = binding.layoutEmail.edtEmail.text.toString().trim()

            if (email.isEmpty()) {
                binding.layoutEmail.edtEmail.error = "Email is required";
                binding.layoutEmail.edtEmail.requestFocus()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Periksa email Anda untuk mengatur ulang kata sandi Anda",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                } else {
                    Toast.makeText(this, "Gagal mengirim email reset", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}