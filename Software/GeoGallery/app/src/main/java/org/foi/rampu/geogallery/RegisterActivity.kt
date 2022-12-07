package org.foi.rampu.geogallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.databinding.ActivityMainBinding
import org.foi.rampu.geogallery.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        firebaseAuth = Firebase.auth

        viewBinding.tvHasAccount.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        viewBinding.btnRegister.setOnClickListener {
            val em = viewBinding.etEmailRegister
            val pass = viewBinding.etPasswordRegister

            if(em.text.isEmpty() || pass.text.isEmpty()){
                Toast.makeText(this,"Fill e-mail and password fields", Toast.LENGTH_SHORT).show()
            }
            else{
                PerformRegister()
            }
        }
    }

    private fun PerformRegister() {
        val email = viewBinding.etEmailRegister.text.toString()
        val password = viewBinding.etPasswordRegister.text.toString()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error while register: ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
}