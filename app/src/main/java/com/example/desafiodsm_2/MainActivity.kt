package com.example.desafiodsm_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var btnLogout: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        btnLogout = findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            auth.signOut() // cierra sesión
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // cerrar MainActivity
        }
    }
}
