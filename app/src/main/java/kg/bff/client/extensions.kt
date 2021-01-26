package kg.bff.client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.Job

fun Context.toast(msg: CharSequence, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, duration).show()
}

inline fun <reified T : Any> Activity.changeActivity() {
    startActivity(Intent(this, T::class.java))
}
fun Job?.cancelIfActive(){ if (this?.isActive==true){cancel()}}
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}
