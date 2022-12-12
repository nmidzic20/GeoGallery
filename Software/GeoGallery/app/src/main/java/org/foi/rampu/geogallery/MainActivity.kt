package org.foi.rampu.geogallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private lateinit var firebaseAuth : FirebaseAuth


    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if(currentUser != null)
            updateUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        firebaseAuth = Firebase.auth

        viewBinding.tvNoAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

       viewBinding.btnLogin.setOnClickListener {
            performLogin()
       }

        viewBinding.button.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun performLogin() {
        val email = viewBinding.etEmail
        val password = viewBinding.etPassword

        if(email.text.isEmpty() || password.text.isEmpty()){
            Toast.makeText(this, "Please fill alll the fileds", Toast.LENGTH_SHORT).show()
            return
        }

        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()

        firebaseAuth.signInWithEmailAndPassword(emailInput,passwordInput)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    updateUI()
                }
                else{
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(baseContext, "Authentication failed: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}

