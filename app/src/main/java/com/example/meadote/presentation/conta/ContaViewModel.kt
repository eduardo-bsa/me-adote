package com.example.meadote.presentation.conta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meadote.data.model.Endereco
import com.example.meadote.data.repository.CEPRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContaViewModel(private val repository: CEPRepository) : ViewModel() {

    val cepLiveData = MutableLiveData<List<Endereco>>()

    suspend fun getCEP() {
        /*CoroutineScope(Dispatchers.Main).launch {
            val endereco = withContext(Dispatchers.Default) {
                repository.getCEP()
            }

            repository.getCEP()
            cepLiveData.value = endereco
        }*/
        val endereco = repository.getCEP()

        withContext(Dispatchers.Main) {
        cepLiveData.value = endereco
        }
    }

    class ContaViewModelFactory(private val repository: CEPRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ContaViewModel(repository) as T
        }

    }
}