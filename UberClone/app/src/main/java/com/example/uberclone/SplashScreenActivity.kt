package com.example.uberclone

import android.net.wifi.p2p.WifiP2pManager.NetworkInfoListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



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
            Toast.makeText(this@SplashScreenActivity, "Splash Screen done",Toast.LENGTH_SHORT).show()
        }
    }
    private fun init(){
        providers = Arrays.asList()
    }
}