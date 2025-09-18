package com.example.desafiodsm_2

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.Adapters.ProductoAdapter
import com.example.desafiodsm_2.model.Producto
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var listaProductos: MutableList<Producto>
    private lateinit var fabAgregar: FloatingActionButton
    private lateinit var dbRef: DatabaseReference
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        // configurar el recycler
        recyclerView = findViewById(R.id.recyclerProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // inicializamos lista y adapter
        listaProductos = mutableListOf()
        adapter = ProductoAdapter(listaProductos,
            onEdit = { producto -> editarProducto(producto) },
            onDelete = { producto -> eliminarProducto(producto.id) }
        )
        recyclerView.adapter = adapter

        // botón flotante para agregar
        fabAgregar = findViewById(R.id.fabAgregarProducto)

        // referencia a firebase (productos del usuario actual)
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios/$uid/productos")

        // escuchar cambios en firebase (tiempo real)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProductos.clear()
                for (data in snapshot.children) {
                    val producto = data.getValue(Producto::class.java)
                    producto?.let { listaProductos.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProductosActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // al presionar el + abrimos un dialogo
        fabAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }
    }

    // diálogo para agregar producto
    private fun mostrarDialogoAgregar() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val etNombre = EditText(this)
        etNombre.hint = "Nombre del producto"
        layout.addView(etNombre)

        val etDescripcion = EditText(this)
        etDescripcion.hint = "Descripción"
        layout.addView(etDescripcion)

        val etPrecio = EditText(this)
        etPrecio.hint = "Precio"
        layout.addView(etPrecio)

        val etStock = EditText(this)
        etStock.hint = "Stock"
        layout.addView(etStock)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Producto")
            .setView(layout)
            .setPositiveButton("Guardar") { d, _ ->
                val nombre = etNombre.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()
                val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                val stock = etStock.text.toString().toIntOrNull() ?: 0

                if (nombre.isNotEmpty()) {
                    val id = dbRef.push().key ?: ""
                    val producto = Producto(id, nombre, descripcion, precio, stock)
                    dbRef.child(id).setValue(producto)
                    Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.show()
    }

    // editar producto existente
    private fun editarProducto(producto: Producto) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val etNombre = EditText(this)
        etNombre.setText(producto.nombre)
        layout.addView(etNombre)

        val etDescripcion = EditText(this)
        etDescripcion.setText(producto.descripcion)
        layout.addView(etDescripcion)

        val etPrecio = EditText(this)
        etPrecio.setText(producto.precio.toString())
        layout.addView(etPrecio)

        val etStock = EditText(this)
        etStock.setText(producto.stock.toString())
        layout.addView(etStock)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(layout)
            .setPositiveButton("Guardar") { d, _ ->
                val nuevoNombre = etNombre.text.toString().trim()
                val nuevaDescripcion = etDescripcion.text.toString().trim()
                val nuevoPrecio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                val nuevoStock = etStock.text.toString().toIntOrNull() ?: 0

                if (nuevoNombre.isNotEmpty()) {
                    val productoActualizado = Producto(
                        id = producto.id,
                        nombre = nuevoNombre,
                        descripcion = nuevaDescripcion,
                        precio = nuevoPrecio,
                        stock = nuevoStock
                    )
                    dbRef.child(producto.id).setValue(productoActualizado)
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.show()
    }

    // eliminar producto
    private fun eliminarProducto(id: String) {
        dbRef.child(id).removeValue()
        Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
    }
}
