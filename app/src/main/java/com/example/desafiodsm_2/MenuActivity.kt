package com.example.desafiodsm_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MenuActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // inicializamos firebase auth
        auth = FirebaseAuth.getInstance()

        // agarramos los botones del menú
        val btnProductos = findViewById<Button>(R.id.btnProductos)
        val btnClientes = findViewById<Button>(R.id.btnClientes)
        val btnVentas = findViewById<Button>(R.id.btnVentas)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // ir a productos
        btnProductos.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        // ir a clientes
        btnClientes.setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }

        // ir a ventas
        btnVentas.setOnClickListener {
            startActivity(Intent(this, VentasActivity::class.java))
        }

        // cerrar sesión y volver al login
        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
