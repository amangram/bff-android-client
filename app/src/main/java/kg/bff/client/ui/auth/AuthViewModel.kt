package kg.bff.client.ui.auth

import androidx.lifecycle.ViewModel
import kg.bff.client.data.model.User
import kg.bff.client.data.repository.UserRepository

class AuthViewModel(private val repository: UserRepository): ViewModel() {
    fun addToDataBase(user: User){
        repository.addToDataBase(user)
    }
}