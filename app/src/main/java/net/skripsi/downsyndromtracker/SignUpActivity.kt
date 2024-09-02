package net.skripsi.downsyndromtracker

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import net.skripsi.downsyndromtracker.databinding.ActivitySignUpBinding
import net.skripsi.downsyndromtracker.utils.CommonUtils.showValidationError
import net.skripsi.downsyndromtracker.utils.CommonUtils.showValidationSuccess

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setStatusBarColor()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        initLoadingDialog()
        setupTextAction()
        setUpOnClickListener()
    }

    private fun setStatusBarColor() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)
    }

    private fun registerUser(email: String, password: String) {
        showLoadingDialog()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            hideLoadingDialog()
            if (task.isSuccessful) {
                val user = auth.currentUser
                showValidationSuccess(
                    binding = binding.layoutSuccessValidation,
                    context = applicationContext,
                    message = "Registrasi Berhasil"
                )
                saveUserToDatabase(
                    user?.uid ?: "", binding.layoutUsername.editText?.text.toString(), email
                )
            } else {
                showValidationError(
                    binding = binding.layoutErrorValidation,
                    context = applicationContext,
                    message = "${task.exception?.message}"
                )
            }
        }
    }

    private fun saveUserToDatabase(userId: String, username: String, email: String) {
        val usersRef = database.getReference("users")

        val userData = HashMap<String, Any>()
        userData["username"] = username
        userData["email"] = email

        usersRef.child(userId).setValue(userData).addOnSuccessListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun setupTextAction() {
        binding.layoutTextAction.apply {
            txtDesc.text = "Sudah punya akun ? "
            txtAction.text = "Masuk disini"
        }
    }

    private fun setUpOnClickListener() {
        binding.apply {
            layoutTextAction.txtAction.setOnClickListener {
                startActivity(Intent(applicationContext, SignInActivity::class.java))
            }
            btnSignUp.setOnClickListener {
                val username = edtUserName.text.toString()
                val email = layoutEmail.edtEmail.text.toString()
                val password = layoutPassword.edtPassword.text.toString()
                val confirmPassword = layoutConfirmPassword.edtPassword.text.toString()

                signUpValidation(username, email, password, confirmPassword)
            }
        }
    }

    private fun signUpValidation(
        username: String, email: String, password: String, confirmPassword: String
    ) {
        when {
            username.isEmpty() && email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty() -> {
                showValidationError(
                    binding = binding.layoutErrorValidation,
                    context = applicationContext,
                    message = "Semua kolom tidak boleh kosong"
                )
            }

            username.isEmpty() -> {
                showValidationError(
                    binding = binding.layoutErrorValidation,
                    context = applicationContext,
                    message = "Username tidak boleh kosong"
                )
            }

            email.isEmpty() -> {
                showValidationError(
                    binding = binding.layoutErrorValidation,
                    context = applicationContext,
                    message = "Email tidak boleh kosong"
                )
            }

            password.isEmpty() -> {
                showValidationError(
                    binding = binding.layoutErrorValidation,
                    context = applicationContext,
                    message = "Password tidak boleh kosong"
                )
            }

            confirmPassword.isEmpty() -> {
                showValidationError(
                    binding = binding.layoutErrorValidation,
                    context = applicationContext,
                    message = "Confirm Password tidak boleh kosong"
                )
            }

            confirmPassword != password -> {
                showValidationError(
                    binding = binding.layoutErrorValidation,
                    context = applicationContext,
                    message = "Confirm Password tidak sama"
                )
            }

            else -> registerUser(email, password)

        }
    }

    private fun initLoadingDialog() {
        loadingDialog = Dialog(this)
        val inflater = LayoutInflater.from(this)
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

}