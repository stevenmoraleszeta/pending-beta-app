package com.innnova.pendingbetaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.innnova.pendingbetaapp.databinding.ActivityAuthBinding
import org.w3c.dom.Text

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Firebase Authentication (assign to auth variable)
        auth = FirebaseAuth.getInstance()

        // Initialize View Binding
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup UI
        setup()
    }

    override fun onResume() {
        super.onResume()
        setup()
    }

    private fun setup() {
        // Set the activity title
        title = "Authentication"

        // Define the UI elements using View Binding
        val btnSignUp: TextView = binding.btnSignUp
        val btnLogin: Button = binding.btnLogin
        val txtEmail: EditText = binding.txtMail
        val txtPassword: EditText = binding.txtPassword

        // Configure Sign Up button listener
        btnSignUp.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(
                    txtEmail.text.toString(), txtPassword.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveLogin()
                    } else {
                        showAlert("Error creating user: ${task.exception?.message}")
                    }
                }
            }
        }

        // Configure Login button listener
        btnLogin.setOnClickListener {
            if (txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()) {
                auth.signInWithEmailAndPassword(
                    txtEmail.text.toString(), txtPassword.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("AuthActivity", "signInWithEmail:success")
                        saveLogin()
                    } else {
                        Log.i("AuthActivity", "signInWithEmail:failure", task.exception)
                        showAlert("Error signing in: ${task.exception?.message}")
                    }
                }
            }
        }
    }

    private fun saveLogin() {
        Log.i("AuthActivity", "Saving data in SharedPreferences")
        val sharedPreferencesEditor = getSharedPreferences(
            getString(R.string.prefs_file),
            MODE_PRIVATE
        ).edit()

        val txtEmail: EditText = binding.txtMail
        sharedPreferencesEditor.putString("email", txtEmail.text.toString()).apply()

        //Go to UserActivity
        Intent(this, UserActivity::class.java).also { startActivity(it) }
        finish()
    }

    private fun showAlert(message: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Accept", null)
        val dialog: android.app.AlertDialog? = builder.create()
        dialog?.show()
    }
}
