package org.foi.rampu.geogallery

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.foi.rampu.geogallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
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


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //firebaseAuth = Firebase.auth
        firebaseAuth = FirebaseAuth.getInstance()

        viewBinding.btnSignIn.setOnClickListener{
            signInGoogle()
        }

        viewBinding.btnTest.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun signInGoogle() {
        val signInIntent : Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "Google sign in successful" + account.id)
                updateUI()
            }catch (e: ApiException){
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun updateUI() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}

