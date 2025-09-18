package com.example.desafiodsm_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passInput: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoRegister: TextView
    private lateinit var btnGithub: Button  // botón extra para GitHub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // conectamos Firebase Auth
        auth = FirebaseAuth.getInstance()

        // agarramos los campos del layout
        emailInput = findViewById(R.id.txtEmail)
        passInput = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoRegister = findViewById(R.id.txtGoRegister)
        btnGithub = findViewById(R.id.btnGithub) // este lo agregas al xml

        // cuando le dan a iniciar sesión con correo/contraseña
        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // intentamos loguear con Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(this, "Bienvenido ${user?.email}", Toast.LENGTH_SHORT).show()

                        // aquí lo mandamos al menú principal
                        startActivity(Intent(this, MenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // si el user no tiene cuenta, lo mandamos al registro
        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // si quiere entrar con GitHub
        btnGithub.setOnClickListener {
            loginWithGithub()
        }
    }

    override fun onStart() {
        super.onStart()
        // si ya hay sesión abierta, saltamos directo al menú
        auth.currentUser?.let {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

    // login con GitHub
    private fun loginWithGithub() {
        val provider = OAuthProvider.newBuilder("github.com")
        val pendingResult = auth.pendingAuthResult

        if (pendingResult != null) {
            // si ya había login pendiente
            pendingResult.addOnSuccessListener {
                Toast.makeText(this, "Bienvenido ${it.user?.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // lanzamos el flujo de login normal
            auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener {
                    Toast.makeText(this, "Login con GitHub exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
