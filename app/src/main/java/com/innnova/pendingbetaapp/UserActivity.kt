package com.innnova.pendingbetaapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.innnova.pendingbetaapp.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the user interface
        setupUI()
    }

    private fun setupUI() {
        // Load user data when the activity starts or resumes
        setUserData()

        // Set up the user menu
        val userMenu: TextView = binding.userMenu
        userMenu.setOnClickListener {
            showPopupMenu(userMenu)
        }
    }

    private fun setUserData() {
        // Fetch user data from Firestore based on the user ID
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is authenticated, proceed with data retrieval
            val userEmail = currentUser.email

            if (userEmail != null) {
                db.collection("users").document(userEmail).get().addOnSuccessListener { document ->
                    // Get user name and company from the document, defaulting to empty strings if null
                    val userName = document.getString("user_name") ?: ""
                    val userCompany = document.getString("user_company") ?: ""

                    // Set the retrieved user data to the corresponding TextViews
                    binding.editTxtUserName.text = Editable.Factory.getInstance().newEditable(userName)
                    binding.editTxtUserCompany.text = Editable.Factory.getInstance().newEditable(userCompany)
                }
            }

        } else {
            // User is not authenticated, handle accordingly (e.g., redirect to login screen)
            Log.e("UserActivity", "User not authenticated.")
            // You might want to redirect the user to the login screen or show a message.
        }
    }

    private fun showPopupMenu(view: View) {
        // Create a PopupMenu anchored to the specified view
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_user, popupMenu.menu)

        // Set up a listener to handle clicks on menu items
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_logout -> {
                    // Handle logout action
                    logout(view)
                    true
                }
                else -> false
            }
        }

        // Show the PopupMenu
        popupMenu.show()
    }

    override fun onResume() {
        super.onResume()
        // Reload user data when the activity resumes
        setUserData()
    }

    fun onBtnSaveClick(view: View) {
        // Retrieve user email from SharedPreferences
        val email = getSharedPreferences(
            getString(R.string.prefs_file),
            MODE_PRIVATE
        ).getString("email", null)

        // Check if email is not null before proceeding
        if (email != null) {
            // Create a HashMap to store user data
            val userData = hashMapOf(
                "user_name" to binding.editTxtUserName.text.toString(),
                "user_company" to binding.editTxtUserCompany.text.toString()
            )

            // Save user data to Firestore
            db.collection("users").document(email).set(userData)
                .addOnSuccessListener {
                    // Success message or additional actions if needed
                    Log.d("UserActivity", "User data saved successfully")
                }
                .addOnFailureListener { e ->
                    // Handle failure (show a Toast, log the error, etc.)
                    Log.e("UserActivity", "Error saving user data", e)
                }

            // Navigate to the main activity
            Intent(this, MainActivity::class.java).also { startActivity(it) }
        } else {
            // Handle the case when the user email is null
            // You might want to redirect the user to the login screen or show a message.
            Log.e("UserActivity", "User email is null")
        }
    }

    fun logout(view: View) {
        // Clear user email from SharedPreferences
        val sharedPreferencesEditor = getSharedPreferences(
            getString(R.string.prefs_file),
            MODE_PRIVATE
        ).edit()
        sharedPreferencesEditor.putString("email", null).apply()

        // Verify the email saved
        Log.i("UserActivity", "Stored Email: ${getSharedPreferences(
            getString(R.string.prefs_file),
            MODE_PRIVATE
        ).getString("email", null)}")

        // Navigate to the authentication activity
        Intent(this, AuthActivity::class.java).also { startActivity(it) }
        finish() // Finish the current activity to prevent going back to it when pressing the back button
    }
}
