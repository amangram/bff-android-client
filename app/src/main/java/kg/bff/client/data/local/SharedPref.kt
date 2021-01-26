package kg.bff.client.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharedPref(private val context: Context) {
    private fun get(): SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    @SuppressLint("CommitPrefEdits")
    fun saveUser(id: String){
        val editor = get().edit()
        editor.putString("userId",id)

        editor.apply()
    }

    fun getId() = get().getString("userId","empty")

    fun delete(){
        val editor = get().edit()
        editor.remove("userId")
        editor.apply()
    }
    companion object{
        const val SP_NAME ="UserInfo"
    }
}