package com.innnova.pendingbetaapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.innnova.pendingbetaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // View Binding
    private lateinit var binding: ActivityMainBinding
    private lateinit var customAdapter: CustomAdapter
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private val auth = FirebaseAuth.getInstance()

    companion object {
        const val MY_CHANNEL_ID = "pending_beta_app_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate and assign the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup UI
        checkLogin()
        setup()
    }

    private fun checkLogin() {
        Log.i("UserActivity", "Checking data in SharedPreferences")

        val sharedPreferences = getSharedPreferences(
            getString(R.string.prefs_file),
            MODE_PRIVATE
        )

        val email = sharedPreferences.getString("email", null)

        Log.i("UserActivity", "Stored Email: $email")

        if (email == null) {
            // If email is null, navigate to AuthActivity and finish the current activity
            val authIntent = Intent(this, AuthActivity::class.java)
            startActivity(authIntent)
            finish()
        }
    }

    private fun setup() {
        // Set the activity title
        title = "Home"

        // Add button
        val btnAdd: Button = binding.btnAdd
        btnAdd.setOnClickListener {
            Intent(this, OrderActivity::class.java).also { startActivity(it) }
        }

        // Update Mail
        val linkEmail: TextView = binding.linkEmail
        linkEmail.text = getSharedPreferences(
            getString(R.string.prefs_file), MODE_PRIVATE
        ).getString("email", null)

        // Config email link
        val userDataLink: LinearLayout = binding.userDataLink
        userDataLink.setOnClickListener {
            // Go to UserActivity
            Intent(this, UserActivity::class.java).also { startActivity(it) }
        }

        // Update text name from Firebase user
        val txtBarUserName: TextView = binding.txtBarUserName

        val userID = auth.currentUser?.email

        if (userID != null) {
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userID)

            // Use addOnCompleteListener to handle the asynchronous operation
            userDocRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // El documento existe, obtenemos el valor de "user_company"
                        val userCompany = document.getString("user_company")

                        // Verificamos si userCompany es nulo o vacío
                        val userName = userCompany.takeIf { !it.isNullOrEmpty() } ?: "Ingresar datos"

                        // Establecemos el valor en el TextView
                        txtBarUserName.text = userName
                    } else {
                        Log.d("TAG", "No such document")
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.exception)
                }
            }
        }

        // Setup RecyclerView
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        customAdapter = CustomAdapter(this)
        recyclerView.adapter = customAdapter
        customAdapter.fetchDataFromFirestore()

        // Main menu
        val mainMenu: TextView = binding.mainMenu
        mainMenu.setOnClickListener {
            showPopupMenu(mainMenu)
        }
    }

    override fun onResume() {
        super.onResume()
        customAdapter.fetchDataFromFirestore()
    }

    // Menu
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_main, popupMenu.menu)

        // Set up a listener to handle clicks on menu items
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_all_orders -> {
                    customAdapter.fetchDataFromFirestore(showAll = true)
                    true
                }

                R.id.action_pendig_orders -> {
                    customAdapter.fetchDataFromFirestore(showAll = false)
                    true
                }

                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }
}
