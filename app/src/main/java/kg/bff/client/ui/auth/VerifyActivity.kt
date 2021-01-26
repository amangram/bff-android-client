package kg.bff.client.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kg.bff.client.R
import kg.bff.client.changeActivity
import kg.bff.client.data.local.SharedPref
import kg.bff.client.data.model.User
import kg.bff.client.toast
import kg.bff.client.ui.BottomActivity
import kotlinx.android.synthetic.main.activity_verify.*
import org.koin.android.viewmodel.ext.android.viewModel

class VerifyActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var verificationId: String
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        verificationId = intent.getStringExtra("Verification")
        verify_btn.setOnClickListener {
            verify()
        }
    }

    private fun verify() {
        val verifyCode = verify_code_et.text.toString()
        if (verifyCode.isNotBlank()) {
            verify_btn.isEnabled = false
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(verificationId, verifyCode)
            signInWithPhone(credential)
        } else {
            toast("Введите код")
        }
    }

    private fun signInWithPhone(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnSuccessListener {result->
            toast("Logged Successfully")
            val user = result.user?.let {
                User("", "${it.phoneNumber}", "", null)
            }
            user?.let { _user -> viewModel.addToDataBase(_user) }
            changeActivity<BottomActivity>()
            finish()
        }.addOnFailureListener {
            toast("Неверный код")
        }
    }
}
