package kg.bff.client.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kg.bff.client.R
import kg.bff.client.changeActivity
import kg.bff.client.data.model.User
import kg.bff.client.toast
import kg.bff.client.ui.BottomActivity
import kg.bff.client.visible
import kotlinx.android.synthetic.main.activity_auth.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class AuthActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var isLogged = false
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        send_code_btn.setOnClickListener {
            send_code_btn.isEnabled = false
            pb_send_code.visible()
            sendingCode()
        }
        phone_number_et.setSelection(phone_number_et.text.length)
    }

    private fun verificationCallbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhone(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                toast("ERROR!")
            }

            override fun onCodeSent(
                verification: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                val handler = Handler()
                handler.postDelayed({
                    if (!isLogged)
                    startActivity(
                        Intent(this@AuthActivity, VerifyActivity::class.java)
                            .putExtra("Verification", verification.toString())
                    )
                }, 10000)
                resendToken = token
            }
        }
    }

    private fun sendingCode() {
        verificationCallbacks()
        val phoneNumber = phone_number_et.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,      // Phone number to verify
            60,               // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this,             // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks
    }

    private fun signInWithPhone(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnSuccessListener { result ->
            val user = result.user?.let {
                User("", "${it.phoneNumber}", "", null)
            }
            user?.let { _user -> viewModel.addToDataBase(_user) }
            isLogged = true
            startMainScreen()
        }
    }


    private fun startMainScreen() {
        toast("Logged Successfully")
        changeActivity<BottomActivity>()
        finish()
    }
}
