package com.innnova.pendingbetaapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.innnova.pendingbetaapp.databinding.CardLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class CustomAdapter(private val context: Context) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var data = mutableListOf<UserData>()

    inner class ViewHolder(val binding: CardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set up a click listener for each item in the RecyclerView
            binding.cardView.setOnClickListener {
                val intent = Intent(itemView.context, OrderActivity::class.java)
                intent.putExtra("orderID", data[adapterPosition].orderID)
                itemView.context.startActivity(intent)
            }
        }

        // Bind data to the ViewHolder
        fun bind(orderData: UserData) {
            with(binding) {
                orderName.text = orderData.orderName
                orderDeliveryDate.text = orderData.orderDeliveryDate
                orderClient.text = orderData.orderClient
                checkBtn.setBackgroundResource(
                    if (orderData.status == "Completed") R.drawable.ic_circle
                    else R.drawable.ic_boton_redondo
                )
            }
        }
    }

    // Create a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    // Bind data to the ViewHolder and handle click events
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])

        // Get the document reference for the current order
        val documentRef = db.collection("orders").document(data[position].orderID)

        // Handle click event on the check button
        holder.binding.checkBtn.setOnClickListener {
            // Determine the new status based on the current status
            val newStatus = if (data[position].status == "Active") "Completed" else "Active"

            // Update the status in Firestore
            documentRef.update("status", newStatus).addOnSuccessListener {
                // Show a Toast message based on the updated status
                val message = if (newStatus == "Completed") "Order completed"
                else "Order marked as pending"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                // Refresh the data in the RecyclerView
                fetchDataFromFirestore()
            }.addOnFailureListener {
                // Show a Toast message in case of an update failure
                Toast.makeText(
                    context, "Error updating the status.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Get the number of items in the RecyclerView
    override fun getItemCount(): Int = data.size

    // Fetch data from Firestore
    fun fetchDataFromFirestore(showAll: Boolean = false) {
        // Check if the user is authenticated
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is authenticated, proceed with data retrieval
            val userEmail = currentUser.email

            // Build the query based on the authenticated user
            val ordersCollection = db.collection("orders")
            val query = if (showAll) {
                ordersCollection.whereEqualTo("creator_mail", userEmail)
            } else {
                ordersCollection.whereEqualTo("creator_mail", userEmail)
                    .whereEqualTo("status", "Active")
            }

            // Get documents based on the query
            query.get().addOnSuccessListener { result ->
                // Clear existing data
                data.clear()

                // Map Firestore documents to UserData objects
                data.addAll(
                    result.documents.map { document ->
                        with(document) {
                            UserData(
                                id,
                                getString("order") ?: "",
                                getString("delivery_date") ?: "",
                                getString("client") ?: "",
                                getString("status") ?: ""
                            )
                        }
                    }.sortedBy { it.formattedDeliveryDate }
                )

                // Notify the adapter that the data has changed
                notifyDataSetChanged()
            }.addOnFailureListener { exception ->
                // Log an error message in case of data retrieval failure
                Log.e("CustomAdapter", "Error getting the list of orders.", exception)
            }
        } else {
            // User is not authenticated, handle accordingly (e.g., redirect to login screen)
            Log.e("CustomAdapter", "User not authenticated.")
            // You might want to redirect the user to the login screen or show a message.
        }
    }

    // Data class representing the structure of order data
    data class UserData(
        val orderID: String,
        val orderName: String,
        val orderDeliveryDate: String,
        val orderClient: String,
        val status: String
    ) {
        // Get the formatted delivery date as a Date object
        val formattedDeliveryDate: Date
            get() {
                val format = SimpleDateFormat("MMM d, yyyy", Locale.ROOT)
                return try {
                    format.parse(orderDeliveryDate) ?: Date()
                } catch (e: Exception) {
                    Date()
                }
            }
    }
}
