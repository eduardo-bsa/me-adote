package com.example.meadote.presentation.conta

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meadote.data.model.Endereco
import com.example.meadote.data.model.Usuario
import com.example.meadote.data.repository.CEPRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class ContaViewModel(private val cepRepo: CEPRepository) : ViewModel() {
    val cepLiveData = MutableLiveData<List<Endereco>>()
    val criaUserLiveData = MutableLiveData<Boolean>()

    private lateinit var auth: FirebaseAuth

    suspend fun getCEP(cep: String) {
        val endereco = cepRepo.getCEP(cep)

        withContext(Dispatchers.Main) {
        cepLiveData.value = endereco
        }
    }

    fun criaUsuario(usuario: Usuario, ctx: Context) {
        auth = Firebase.auth

        auth.createUserWithEmailAndPassword(usuario.email, usuario.senha)
            .addOnCompleteListener(ctx as Activity) { task ->
                criaUserLiveData.value = task.isSuccessful
            }
    }

    class ContaViewModelFactory(private val cepRepo: CEPRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ContaViewModel(cepRepo) as T
        }

    }
}