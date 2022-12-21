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

        firebaseAuth = Firebase.auth    //Inicijaliacija Firebase-a

        viewBinding.tvHasAccount.setOnClickListener { //Na klik naziva imate račun
            startActivity(Intent(this, MainActivity::class.java)) //Pokreni Home activity(Login)
            finish()    //Onemogući klik na gumb za nazad
        }

        viewBinding.btnRegister.setOnClickListener { //Klik na register gumb

            val em = viewBinding.etEmailRegister //Dohvati edit text za email
            val pass = viewBinding.etPasswordRegister //Dohvati edit text za lozinke

            if(em.text.isEmpty() || pass.text.isEmpty()){  //Provjeri jesu li edit text-ovi za email ili lozinku prazni
                Toast.makeText(this,"Fill e-mail and password fields", Toast.LENGTH_SHORT).show() //Ako jesu prikaži Toast sa porukom
                                                                                                             // da korisnik popuni polja
            }
            else{ //Ako nisu prazna polja pokreni registraciju
                PerformRegister()
            }

        }
    }

    private fun PerformRegister() {

        val email = viewBinding.etEmailRegister.text.toString() //Dohvati upisanu vrijednost u edit textu emaila
        val password = viewBinding.etPasswordRegister.text.toString() //Dohvati upisanu vrijednost u edit textu lozinke

        firebaseAuth.createUserWithEmailAndPassword(email, password) //firebasova funkcija za signin sa emailom gdje se
                                                                    //prosljeđuje upisani email i lozinka
            .addOnCompleteListener(this) { task ->
                //Kada je dovršena provjera, provjeri je li uspješno obavljena, odnosno je li potvrda uspješna
                //Ako je prijeđi na Home activity, a ako nije prikaži korisniku Toast sa porukom neuspješne autentifikacije
                if (task.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener {
                //Osluškivač neuspjeha, ako je autentifikacija neuspjela ili je došlo do neke greške prikaži korisniku Toast
                //sa porukom neuspjele registracije te grešku
                Toast.makeText(
                    this,
                    "Error while register: ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }
}