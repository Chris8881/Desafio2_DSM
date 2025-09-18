package com.example.desafiodsm_2

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.adapter.ClienteAdapter
import com.example.desafiodsm_2.model.Cliente
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ClientesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClienteAdapter
    private lateinit var listaClientes: MutableList<Cliente>
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var dbRef: DatabaseReference
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        recyclerView = findViewById(R.id.recyclerClientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        listaClientes = mutableListOf()
        adapter = ClienteAdapter(listaClientes,
            onEdit = { cliente -> editarCliente(cliente) },
            onDelete = { cliente -> eliminarCliente(cliente.id) }
        )
        recyclerView.adapter = adapter

        fabAgregar = findViewById(R.id.fabAgregarCliente)

        dbRef = FirebaseDatabase.getInstance().getReference("usuarios/$uid/clientes")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaClientes.clear()
                for (data in snapshot.children) {
                    val cliente = data.getValue(Cliente::class.java)
                    cliente?.let { listaClientes.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ClientesActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        fabAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }
    }

    private fun mostrarDialogoAgregar() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val etNombre = EditText(this)
        etNombre.hint = "Nombre"
        layout.addView(etNombre)

        val etCorreo = EditText(this)
        etCorreo.hint = "Correo"
        layout.addView(etCorreo)

        val etTelefono = EditText(this)
        etTelefono.hint = "Teléfono"
        layout.addView(etTelefono)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Cliente")
            .setView(layout)
            .setPositiveButton("Guardar") { d, _ ->
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val telefono = etTelefono.text.toString().trim()

                if (nombre.isNotEmpty()) {
                    val id = dbRef.push().key ?: ""
                    val cliente = Cliente(id, nombre, correo, telefono)
                    dbRef.child(id).setValue(cliente)
                    Toast.makeText(this, "Cliente agregado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.show()
    }

    private fun editarCliente(cliente: Cliente) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val etNombre = EditText(this)
        etNombre.setText(cliente.nombre)
        layout.addView(etNombre)

        val etCorreo = EditText(this)
        etCorreo.setText(cliente.correo)
        layout.addView(etCorreo)

        val etTelefono = EditText(this)
        etTelefono.setText(cliente.telefono)
        layout.addView(etTelefono)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Cliente")
            .setView(layout)
            .setPositiveButton("Guardar") { d, _ ->
                val nuevoNombre = etNombre.text.toString().trim()
                val nuevoCorreo = etCorreo.text.toString().trim()
                val nuevoTelefono = etTelefono.text.toString().trim()

                if (nuevoNombre.isNotEmpty()) {
                    val clienteActualizado = Cliente(cliente.id, nuevoNombre, nuevoCorreo, nuevoTelefono)
                    dbRef.child(cliente.id).setValue(clienteActualizado)
                    Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.show()
    }

    private fun eliminarCliente(id: String) {
        dbRef.child(id).removeValue()
        Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
    }
}
