package com.example.uberclone

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager.NetworkInfoListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.uberclone.utils.Constants
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import java.util.Arrays
import java.util.concurrent.TimeUnit

class SplashScreenActivity : AppCompatActivity() {
    companion object {
        private val LOGIN_REQUEST_CODE = 214321
    }

    private lateinit var providers:List<AuthUI.IdpConfig>
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener

    private lateinit var getResult: ActivityResultLauncher<Intent>

    private lateinit var database : FirebaseDatabase
    private lateinit var driverInfoRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {

            }

        }
        init()



    }

    override fun onStart() {
        super.onStart()
        displaySplashScreen()
    }

    override fun onStop() {

        if(firebaseAuth != null && listener != null){
            firebaseAuth.removeAuthStateListener(listener)
        }
        super.onStop()
    }

    private fun displaySplashScreen(){
        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(){
            firebaseAuth.addAuthStateListener(listener)
        }
    }
    private fun init(){
        database = FirebaseDatabase.getInstance()
        driverInfoRef = database.getReference(Constants.DRIVER_INFO_REFERENCE)


        providers = Arrays.asList(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        firebaseAuth = FirebaseAuth.getInstance()
        listener = FirebaseAuth.AuthStateListener {myFirebaseAuth ->
            val user = myFirebaseAuth.currentUser
            if(user!= null){
                checkUserFromFirebase()
            } else{
                showLoginLayout()
            }
        }
    }

    fun showLoginLayout(){
        val authMethodPickerLayout = AuthMethodPickerLayout.Builder(R.layout.sign_in_layout)
            .setPhoneButtonId(R.id.button_phone_sign_in)
            .setGoogleButtonId(R.id.button_google_sign_in)
            .build()

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAuthMethodPickerLayout(authMethodPickerLayout)
            .setTheme(R.style.LoginTheme)
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
        getResult.launch(signInIntent)
    }

    private fun checkUserFromFirebase(){
        driverInfoRef
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: "SomeString" )
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        Toast.makeText(this@SplashScreenActivity,"the user already exists", Toast.LENGTH_SHORT).show()
                    } else{
                        showRegisterUserLayout()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SplashScreenActivity, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }


    private  fun showRegisterUserLayout(){
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        val itemView = LayoutInflater.from(this).inflate(R.layout.register_layout,null)

        val edit_text_name = itemView.findViewById<View>(R.id.edit_text_first_name) as TextInputEditText
        val edit_text_last_name = itemView.findViewById<View>(R.id.edit_text_last_name) as TextInputEditText
        val edit_text_phone_number = itemView.findViewById<View>(R.id.edit_text_phone_number) as TextInputEditText

        val btnContinue = itemView.findViewById<Button>(R.id.button_register)
    }
}