package net.skripsi.downsyndromtracker

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import net.skripsi.downsyndromtracker.databinding.ActivitySignInBinding
import net.skripsi.downsyndromtracker.dialogfragment.ErrorDialogFragment
import net.skripsi.downsyndromtracker.utils.CommonUtils.showValidationError
import net.skripsi.downsyndromtracker.utils.CommonUtils.showValidationSuccess

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var loadingDialog: Dialog

    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

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

    private fun loginUser(email: String, password: String) {
        showLoadingDialog()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideLoadingDialog()
                if (task.isSuccessful) {
                    showValidationSuccess(
                        binding = binding.layoutSuccessValidation,
                        context = applicationContext,
                        message = "Login Berhasil"
                    )
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showErrorDialog()
                }
            }
    }

    private fun setupTextAction() {
        binding.layoutTextAction.apply {
            txtDesc.text = "Belum punya akun ? "
            txtAction.text = "Buat disini"
        }
    }

    private fun setUpOnClickListener() {
        binding.apply {
            txtForgetPassword.setOnClickListener {
                startActivity(Intent(applicationContext, ForgetPasswordActivity::class.java))
            }
            layoutTextAction.txtAction.setOnClickListener {
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
            }
            btnSignIn.setOnClickListener {
                val email = layoutEmail.edtEmail.text.toString()
                val password = layoutPassword.edtPassword.text.toString()
                signInValidation(email, password)
            }
            btnGmail.setOnClickListener {
                googleSignInClient.signOut().addOnCompleteListener {
                    signIn()
                }
            }
        }
    }

    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                handleSignInResult(task)
            }
        }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.d("down", "handleSignInResult: $e")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showLoadingDialog()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                hideLoadingDialog()
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            saveUserToDatabase(user.uid, user.displayName ?: "", user.email ?: "")
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserToDatabase(userId: String, username: String, email: String) {
        val usersRef = database.getReference("users")

        val userData = HashMap<String, Any>()
        userData["username"] = username
        userData["email"] = email

        usersRef.child(userId).setValue(userData)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun signInValidation(email: String, password: String) {
        when {
            email.isEmpty() && password.isEmpty() -> {
                showValidationError(
                    binding = binding.layoutErrorValidation,
                    context = applicationContext,
                    message = "Email dan Password tidak boleh kosong"
                )
            }

            email.isEmpty() -> showValidationError(
                binding = binding.layoutErrorValidation,
                context = applicationContext,
                message = "Email tidak boleh kosong"
            )

            password.isEmpty() -> showValidationError(
                binding = binding.layoutErrorValidation,
                context = applicationContext,
                message = "Password tidak boleh kosong"
            )

            else -> loginUser(email, password)
        }
    }

    private fun showErrorDialog() {
        val dialog = ErrorDialogFragment("signin")
        dialog.show(supportFragmentManager, "error")
    }

    private fun initLoadingDialog() {
        loadingDialog = Dialog(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.loading_dialog, null)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
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