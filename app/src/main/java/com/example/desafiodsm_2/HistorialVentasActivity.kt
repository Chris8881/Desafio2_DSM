package com.example.desafiodsm_2

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.Adapters.VentaAdapter
import com.example.desafiodsm_2.model.Cliente
import com.example.desafiodsm_2.model.Venta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistorialVentasActivity : AppCompatActivity() {

    private lateinit var recyclerHistorial: RecyclerView
    private lateinit var btnVolver: Button
    private lateinit var dbRef: DatabaseReference
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val listaVentas = mutableListOf<Venta>()
    private val listaClientes = mutableListOf<Cliente>()
    private lateinit var adapter: VentaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_ventas)

        // Referencias a los views
        recyclerHistorial = findViewById(R.id.recyclerHistorialVentas)
        btnVolver = findViewById(R.id.btnVolverVentas)

        // Configurar RecyclerView
        recyclerHistorial.layoutManager = LinearLayoutManager(this)
        adapter = VentaAdapter(listaVentas, listaClientes)
        recyclerHistorial.adapter = adapter

        // Botón volver
        btnVolver.setOnClickListener {
            finish() // vuelve a la activity anterior
        }

        // Referencia a Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios/$uid")

        // Cargar clientes y ventas
        cargarClientesYVentas()
    }

    private fun cargarClientesYVentas() {
        dbRef.child("clientes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaClientes.clear()
                for (data in snapshot.children) {
                    val cliente = data.getValue(Cliente::class.java)
                    cliente?.let { listaClientes.add(it) }
                }
                cargarVentas()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistorialVentasActivity, "Error cargando clientes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarVentas() {
        dbRef.child("ventas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaVentas.clear()
                for (data in snapshot.children) {
                    val venta = data.getValue(Venta::class.java)
                    venta?.let { listaVentas.add(it) }
                }
                // Ordenar por fecha descendente
                listaVentas.sortByDescending { it.fecha }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistorialVentasActivity, "Error cargando ventas", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
