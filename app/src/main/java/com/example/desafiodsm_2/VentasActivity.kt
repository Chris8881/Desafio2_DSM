package com.example.desafiodsm_2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.Adapters.ProductoVentaAdapter
import com.example.desafiodsm_2.model.Cliente
import com.example.desafiodsm_2.model.Producto
import com.example.desafiodsm_2.model.Venta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class VentasActivity : AppCompatActivity() {

    private lateinit var spinnerClientes: Spinner
    private lateinit var recyclerProductos: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnRegistrar: Button

    private lateinit var dbRef: DatabaseReference
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val listaClientes = mutableListOf<Cliente>()
    private val listaProductos = mutableListOf<Producto>()
    private lateinit var adapter: ProductoVentaAdapter

    // aquí guardamos lo que el usuario selecciona (id del producto -> cantidad)
    private val productosSeleccionados = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas)

        // referencias de la vista
        spinnerClientes = findViewById(R.id.spinnerClientes)
        recyclerProductos = findViewById(R.id.recyclerProductosVenta)
        tvTotal = findViewById(R.id.tvTotal)
        btnRegistrar = findViewById(R.id.btnRegistrarVenta)

        // configuramos recycler
        recyclerProductos.layoutManager = LinearLayoutManager(this)
        adapter = ProductoVentaAdapter(listaProductos) { producto, cantidad, seleccionado ->
            if (seleccionado && cantidad > 0) {
                productosSeleccionados[producto.id] = cantidad
            } else {
                productosSeleccionados.remove(producto.id)
            }
            calcularTotal()
        }
        recyclerProductos.adapter = adapter

        // referencia principal a Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios/$uid")

        // cargamos clientes y productos
        cargarClientes()
        cargarProductos()

        // botón registrar
        btnRegistrar.setOnClickListener {
            registrarVenta()
        }
    }

    // leer clientes desde firebase
    private fun cargarClientes() {
        dbRef.child("clientes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaClientes.clear()
                for (data in snapshot.children) {
                    val cliente = data.getValue(Cliente::class.java)
                    cliente?.let { listaClientes.add(it) }
                }

                val nombres = listaClientes.map { it.nombre }
                val adapterSpinner = ArrayAdapter(
                    this@VentasActivity,
                    android.R.layout.simple_spinner_item,
                    nombres
                )
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerClientes.adapter = adapterSpinner
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VentasActivity, "Error cargando clientes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // leer productos desde firebase
    private fun cargarProductos() {
        dbRef.child("productos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                for (data in snapshot.children) {
                    val producto = data.getValue(Producto::class.java)
                    producto?.let { listaProductos.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VentasActivity, "Error cargando productos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // calcular el total en base a lo que se selecciona
    private fun calcularTotal() {
        var total = 0.0
        for ((id, cantidad) in productosSeleccionados) {
            val producto = listaProductos.find { it.id == id }
            producto?.let {
                total += it.precio * cantidad
            }
        }
        tvTotal.text = "Total: $${total}"
    }

    // registrar la venta y actualizar stock
    private fun registrarVenta() {
        if (productosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un producto", Toast.LENGTH_SHORT).show()
            return
        }

        val clientePos = spinnerClientes.selectedItemPosition
        if (clientePos == -1) {
            Toast.makeText(this, "Selecciona un cliente", Toast.LENGTH_SHORT).show()
            return
        }

        val clienteId = listaClientes[clientePos].id
        val ventaId = dbRef.child("ventas").push().key ?: ""
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        var total = 0.0
        val detalle = mutableMapOf<String, Int>()

        // recorremos productos seleccionados
        for ((id, cantidad) in productosSeleccionados) {
            val producto = listaProductos.find { it.id == id }
            producto?.let {
                total += it.precio * cantidad
                detalle[id] = cantidad

                // actualizar stock en Firebase
                val nuevoStock = it.stock - cantidad
                dbRef.child("productos").child(id).child("stock").setValue(nuevoStock)
            }
        }

        val venta = Venta(
            id = ventaId,
            clienteId = clienteId,
            productos = detalle,
            total = total,
            fecha = fecha
        )

        // guardar la venta en firebase
        dbRef.child("ventas").child(ventaId).setValue(venta)
            .addOnSuccessListener {
                Toast.makeText(this, "Venta registrada con éxito", Toast.LENGTH_SHORT).show()
                productosSeleccionados.clear()
                calcularTotal()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al registrar venta", Toast.LENGTH_SHORT).show()
            }
    }
}
