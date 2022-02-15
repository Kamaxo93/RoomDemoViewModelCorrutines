package com.example.roomdemo

import androidx.lifecycle.*
import com.example.roomdemo.db.Subscriber
import com.example.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.launch

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel() {

    val subscribers = repository.subscribers
    var isUpdateOrDelete: Boolean = false
    private var subscriberToUpdateOrDelete: Subscriber? = null
    val saveOrUpdateBtnLabel = MutableLiveData<String>()
    val clearAllOrDeleteBtnLabel = MutableLiveData<String>()
    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateBtnLabel.value = "Save"
        clearAllOrDeleteBtnLabel.value = "Clear All"
        isUpdateOrDelete = false
    }

    fun saveOrUpdate(nameSubscriber: String, emailSubscriber: String) {
        if (isUpdateOrDelete) {
            subscriberToUpdateOrDelete?.let { subscriber ->
                subscriber.name = nameSubscriber
                subscriber.email = emailSubscriber
                updateSubscribe(subscriber)
            }

        } else {
            insertSubscribe(Subscriber(0, nameSubscriber, emailSubscriber))

        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            subscriberToUpdateOrDelete?.let {
                deleteSubscribe(it)
            }

        } else {
            clearAllSubscribe()
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        subscriberToUpdateOrDelete = subscriber
        isUpdateOrDelete = true
        isSaveOrUpdate(isUpdateOrDelete)

    }

    fun insertSubscribe(subscriber: Subscriber) = viewModelScope.launch {
        val insertSuccess = repository.insert(subscriber)
        if (insertSuccess > -1) {
        statusMessage.value = Event("Subsiscrptor $insertSuccess insertado correctamente")

        } else {
            statusMessage.value = Event("Ocurrio un error al insertar")
        }
    }

    fun updateSubscribe(subscriber: Subscriber) = viewModelScope.launch {
        val updateSuccess = repository.update(subscriber)
        isUpdateOrDelete = false
        isSaveOrUpdate(isUpdateOrDelete)
        if (updateSuccess > 0) {
            statusMessage.value = Event("Subsiscrptor $updateSuccess actualizado correctamente")

        } else {
            statusMessage.value = Event("Ocurrio un error al actualizar")
        }
    }

    fun deleteSubscribe(subscriber: Subscriber) = viewModelScope.launch {
        val deleteuccess = repository.delete(subscriber)
        isUpdateOrDelete = false
        isSaveOrUpdate(isUpdateOrDelete)
        if (deleteuccess > 0) {
            statusMessage.value = Event("Subsiscrptor $deleteuccess eliminado correctamente")

        } else {
            statusMessage.value = Event("Ocurrio un error al eliminar")
        }
    }

    fun clearAllSubscribe() = viewModelScope.launch {
        val deleteAllSuccess = repository.deleteAll()
        if (deleteAllSuccess > 0) {
            statusMessage.value = Event("Subsiscrptores $deleteAllSuccess eliminados correctamente")

        } else {
            statusMessage.value = Event("Ocurrio un error al eliminar")
        }
    }

    fun isSaveOrUpdate(isSave: Boolean) {
        if (isSave) {
            saveOrUpdateBtnLabel.value = "Update"
            clearAllOrDeleteBtnLabel.value = "Delete"

        } else {
            saveOrUpdateBtnLabel.value = "Save"
            clearAllOrDeleteBtnLabel.value = "Clear All"
        }

    }
}

class SubscribeViewModelFactory(private val repository: SubscriberRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriberViewModel::class.java)) {
            return SubscriberViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }

}