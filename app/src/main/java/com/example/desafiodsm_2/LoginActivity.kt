package com.example.desafiodsm_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passInput: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoRegister: TextView
    private lateinit var btnGithub: Button
    private lateinit var btnFacebook: Button
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // inicializar Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        // inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // agarrar los campos del layout
        emailInput = findViewById(R.id.txtEmail)
        passInput = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoRegister = findViewById(R.id.txtGoRegister)
        btnGithub = findViewById(R.id.btnGithub)
        btnFacebook = findViewById(R.id.btnFacebook)

        // login con correo y contraseña
        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(this, "Bienvenido ${user?.email}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // ir al registro
        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // login con GitHub
        btnGithub.setOnClickListener {
            loginWithGithub()
        }

        // login con Facebook (con formulario limpio siempre)
        btnFacebook.setOnClickListener {
            // cerrar cualquier sesión anterior
            LoginManager.getInstance().logOut()

            // forzar login en navegador, no sesión guardada
            LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY)

            // solicitar permisos
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
        }

        // registrar callback de Facebook
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    Toast.makeText(this@LoginActivity, "Login cancelado", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
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

        // scopes (para obtener el email del usuario, por ejemplo)
        val scopes = arrayListOf("user:email")
        provider.setScopes(scopes)

        // ⚡ Forzar re-login siempre
        val customParams = mutableMapOf<String, String>()
        customParams["prompt"] = "login"
        provider.addCustomParameters(customParams)

        val pendingResult = auth.pendingAuthResult
        if (pendingResult != null) {
            pendingResult.addOnSuccessListener {
                Toast.makeText(this, "Bienvenido ${it.user?.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
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

    // login con Facebook
    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Bienvenido ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
