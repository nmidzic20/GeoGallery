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
        val currentUser = firebaseAuth.currentUser //Čim se aplikacija pokrene dohvati usera
        if(currentUser != null) //Ako ima usera update-aj UI
            updateUI()

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        firebaseAuth = Firebase.auth    //Inicijaliacija Firebase-a

        viewBinding.tvNoAccount.setOnClickListener {    //Na klik naziva nemate račun

            startActivity(Intent(this, RegisterActivity::class.java))   //Pokreni Register activity
            finish()    //Onemogući klik na gumb za nazad

        }

       viewBinding.btnLogin.setOnClickListener {    //Klik na login gumb

            performLogin() //Odrađuje login logiku

       }

        viewBinding.button.setOnClickListener {

            startActivity(Intent(this, HomeActivity::class.java))
            finish()

        }
    }

    private fun performLogin() {

        val email = viewBinding.etEmail //Dohvati edit text za email
        val password = viewBinding.etPassword //Dohvati edit text za lozinke

        if(email.text.isEmpty() || password.text.isEmpty()){ //Provjeri jesu li edit text-ovi za email ili lozinku prazni
            Toast.makeText(this, "Please fill alll the fileds", Toast.LENGTH_SHORT).show() //Ako jesu prikaži Toast sa porukom
                                                                                                        // da korisnik popuni polja
            return
        }

        val emailInput = email.text.toString() //Dohvati upisanu vrijednost u edit textu emaila
        val passwordInput = password.text.toString() //Dohvati upisanu vrijednost u edit textu lozinke

        firebaseAuth.signInWithEmailAndPassword(emailInput,passwordInput) //firebasova funkcija za signin sa emailom gdje se
                                                                        //prosljeđuje upisani email i lozinka
            .addOnCompleteListener(this){ task ->
                //Kada je dovršena provjera, provjeri je li uspješno obavljena, odnosno je li potvrda uspješna
                //Ako je update-aj UI, a ako nije prikaži korisniku Toast sa porukom neuspješne autentifikacije
                if(task.isSuccessful){
                    updateUI()
                }
                else{
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{
                //Osluškivač neuspjeha, ako je autentifikacija neuspjela ili je došlo do neke greške prikaži korisniku Toast
                //sa porukom neuspjele autentifikacije te grešku
                Toast.makeText(baseContext, "Authentication failed: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun updateUI() {

        startActivity(Intent(this, HomeActivity::class.java)) //Prijeđi na Home activity
        finish() //Onemogući klik na gumb za nazad
    }

}

