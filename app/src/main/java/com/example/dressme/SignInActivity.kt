package com.example.dressme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.dressme.util.KeyboardAPI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {

    companion object {
        val TAG: String = "SignInActivity"
    }

    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        this.signIn_signIn_button.setOnClickListener{
            spinner = signIn_spinner_progressBar
            spinner.setVisibility(View.VISIBLE)
            KeyboardAPI.hideKeyboard(this)

            signMeIn()
        }
    }


    private fun signMeIn() {
        // TODO update error text
        if (!validForm()) {
            spinner.setVisibility(View.GONE)
            Toast.makeText(this, "Form cannot be empty", Toast.LENGTH_LONG).show()
            return
        }

        val email    = signIn_email_textEdit.text.toString()
        val password = signIn_password_textEdit.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful)   {
                    spinner.setVisibility(View.GONE);
                    return@addOnCompleteListener
                }

                // else if successful
                Log.d(TAG, "Successfully logged in ${it.result?.user?.uid}")
                val intent = Intent(this, ProfileMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                spinner.setVisibility(View.GONE);
                startActivity(intent);
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to login ${it.message}")
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }


    private fun validForm(): Boolean {
        val email       = signIn_email_textEdit.text.toString()
        val password    = signIn_password_textEdit.text.toString()

        Log.d(TAG, "Email is  $email")
        Log.d(TAG, "Password is  $password")

        if(email.isEmpty() || password.isEmpty())
            return false

        return true
    }
}
