package kg.bff.client.ui.profile

import androidx.lifecycle.*
import kg.bff.client.data.model.Image
import kg.bff.client.data.model.State
import kg.bff.client.data.model.User
import kg.bff.client.data.repository.StadiumsRepository
import kg.bff.client.data.repository.StorageRepository
import kg.bff.client.data.repository.UserRepository

class ProfileViewModel(
    private val firestoreRepository: UserRepository,
    private val storageRepository: StorageRepository,
    private val stadiumRepository: StadiumsRepository
) : ViewModel() {
    private val mUser = firestoreRepository.getUser().asLiveData(viewModelScope.coroutineContext)
    val user: LiveData<State<User>>
        get() = mUser
    private val imagePath by lazy { MutableLiveData<Image>() }


    fun updateUser(name: String, birthdate: String, imageBytes: Array<ByteArray>? = null) {
        if (imageBytes != null)
            storageRepository.uploadProfilePhoto(imageBytes) { imageBytes ->
                imagePath.value = imageBytes
                firestoreRepository.updateCurrentUser(name, birthdate, imageBytes)
            } else
            firestoreRepository.updateCurrentUser(name, birthdate)
    }

    fun getSavedStadiums(id: String) =
        stadiumRepository.getSavedStadiums(id).asLiveData(viewModelScope.coroutineContext)

    fun save(id: String, savedUsers: List<String>) = stadiumRepository.save(id, savedUsers)
}