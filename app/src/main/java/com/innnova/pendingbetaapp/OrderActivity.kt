package com.innnova.pendingbetaapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.innnova.pendingbetaapp.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityOrderBinding
    private lateinit var btnComplete: Button
    private lateinit var whatsappBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la interfaz de usuario y obtener datos
        setupUI()
        getData()
    }

    private fun setupUI() {
        // TextWatcher para manejar cambios en el monto total y pagado
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateToPayAmount()
            }
        }

        // Vincular el TextWatcher a los EditText de monto total y pagado
        binding.editTextTotalAmount.addTextChangedListener(textWatcher)
        binding.editTextPaidAmount.addTextChangedListener(textWatcher)

        // Menú de la orden, escuchador de clics
        binding.orderMenu.setOnClickListener {
            showPopupMenu(binding.orderMenu)
        }

        // Escuchador de clics del botón Guardar
        binding.btnSave.setOnClickListener {
            onBtnSaveClick()
        }

        // Escuchador de clics del botón de WhatsApp
        whatsappBtn = binding.whatsappBtn
        whatsappBtn.setOnClickListener {
            onBtnWhatsappClick()
        }

        // Después de obtener datos
        val btnComplete = binding.btnComplete
        if (intent.hasExtra("orderID")) {
            btnComplete.visibility = View.VISIBLE
        } else {
            btnComplete.visibility = View.GONE
        }
    }

    private fun getData() {
        if (intent.hasExtra("orderID")) {
            val orderID = intent.getStringExtra("orderID").toString()
            val docRef: DocumentReference =
                FirebaseFirestore.getInstance().collection("orders").document(orderID)

            // Obtener datos de la orden desde Firestore
            docRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val orderData = documentSnapshot.data

                    // Llenar la interfaz de usuario con los datos de la orden obtenidos
                    binding.editTxtOrder.text = Editable.Factory.getInstance()
                        .newEditable(orderData?.get("order").toString())
                    binding.editTxtProduct.text = Editable.Factory.getInstance()
                        .newEditable(orderData?.get("product").toString())
                    binding.editTxtClient.text = Editable.Factory.getInstance()
                        .newEditable(orderData?.get("client").toString())
                    binding.editTxtOrderDate.text = Editable.Factory.getInstance()
                        .newEditable(orderData?.get("order_date").toString())
                    binding.editTxtDeliveryDate.text = Editable.Factory.getInstance()
                        .newEditable(orderData?.get("delivery_date").toString())
                    binding.editTextTotalAmount.setText(orderData?.get("total_amount").toString())
                    binding.editTextPaidAmount.setText(orderData?.get("paid_amount").toString())
                    binding.editTextToPayAmount.setText(orderData?.get("to_pay_amount").toString())
                    binding.editTxtDetails.text = Editable.Factory.getInstance()
                        .newEditable(orderData?.get("details").toString())

                    // Modificar el botón de completar según el estado de la orden
                    btnComplete = binding.btnComplete
                    if (orderData?.get("status").toString() == "Active") {
                        btnComplete.text = "Completar"
                        btnComplete.setOnClickListener {
                            completeOrder()
                        }
                    } else if (orderData?.get("status").toString() == "Completed") {
                        btnComplete.text = "Asignar pendiente"
                        btnComplete.setOnClickListener {
                            setPendingOrder()
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("Firebase", "Error al recuperar el documento: $e")
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_order, popupMenu.menu)

        // Manejar clics en los elementos del menú
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_delete_order -> {
                    // Eliminar la orden si hay un orderID presente
                    if (intent.getStringExtra("orderID") != null) {
                        val orderID = intent.getStringExtra("orderID")
                        db.collection("orders").document(orderID!!).delete()
                    }
                    // Navegar de regreso a MainActivity
                    intent = Intent(this, MainActivity::class.java).also { startActivity(it) }
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun updateToPayAmount() {
        // Calcular y mostrar el monto restante por pagar
        val totalAmount = binding.editTextTotalAmount.text.toString().toDoubleOrNull() ?: 0.0
        val paidAmount = binding.editTextPaidAmount.text.toString().toDoubleOrNull() ?: 0.0
        val toPayAmount = totalAmount - paidAmount

        binding.editTextToPayAmount.setText(toPayAmount.toString())
    }

    private fun onBtnWhatsappClick() {
        val phoneNumber = binding.editTxtPhoneNumber.text.toString()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setPackage("com.whatsapp")
        intent.data = Uri.parse(
            "https://api.whatsapp.com/send?phone=$phoneNumber"
        )

        // Verificar si WhatsApp está instalado en el dispositivo
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Si WhatsApp no está instalado, redirigir a la página web
            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data =
                Uri.parse("https://wa.me/$phoneNumber")
            startActivity(webIntent)
        }
    }

    // Mostrar el selector de fecha para la fecha de la orden
    fun showDateOrderPicker(view: View) {
        showDatePicker(binding.editTxtOrderDate)
    }

    // Mostrar el selector de fecha para la fecha de entrega
    fun showDateDeliveryPicker(view: View) {
        showDatePicker(binding.editTxtDeliveryDate)
    }

    private fun showDatePicker(editText: EditText) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()

        // Configurar el selector de fecha y actualizar la fecha seleccionada en el EditText correspondiente
        picker.addOnPositiveButtonClickListener { selection ->
            val formattedDate = picker.headerText
            editText.setText(formattedDate)
        }

        picker.show(supportFragmentManager, picker.toString())
    }

    private fun onBtnSaveClick() {
        // Obtener el correo electrónico del SharedPreferences
        val email = getSharedPreferences(
            getString(R.string.prefs_file), MODE_PRIVATE
        ).getString("email", null)

        // Construir los datos de la orden a guardar en Firestore
        val orderData = hashMapOf(
            "order" to binding.editTxtOrder.text.toString(),
            "product" to binding.editTxtProduct.text.toString(),
            "client" to binding.editTxtClient.text.toString(),
            "order_date" to binding.editTxtOrderDate.text.toString(),
            "delivery_date" to binding.editTxtDeliveryDate.text.toString(),
            "total_amount" to binding.editTextTotalAmount.text.toString(),
            "paid_amount" to binding.editTextPaidAmount.text.toString(),
            "to_pay_amount" to binding.editTextToPayAmount.text.toString(),
            "details" to binding.editTxtDetails.text.toString(),
            "status" to "Active",
            "creator_mail" to email
        )

        if (intent.hasExtra("orderID")) {
            // Actualizar una orden existente en Firestore
            val orderID = intent.getStringExtra("orderID").toString()
            val docRef = db.collection("orders").document(orderID)
            docRef.set(orderData)
        } else {
            // Agregar una nueva orden a Firestore
            db.collection("orders").add(orderData)
        }

        // Navegar de regreso a MainActivity
        startActivity(Intent(this, MainActivity::class.java))
    }

    // Marcar una orden como completada
    fun completeOrder() {
        val btnComplete = binding.btnComplete

        // Verificar si el botón de completar es visible
        if (btnComplete.visibility == View.VISIBLE) {
            val orderID = intent.getStringExtra("orderID").toString()
            val docRef = db.collection("orders").document(orderID)
            docRef.update("status", "Completed")

            // Navegar de regreso a MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    // Establecer una orden como pendiente
    fun setPendingOrder() {
        val orderID = intent.getStringExtra("orderID")
        db.collection("orders").document(orderID!!).update("status", "Active")
        // Navegar de regreso a MainActivity
        intent = Intent(this, MainActivity::class.java).also { startActivity(it) }
    }
}
