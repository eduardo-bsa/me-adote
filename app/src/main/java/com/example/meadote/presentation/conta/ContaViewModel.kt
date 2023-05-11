package com.example.meadote.presentation.conta

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meadote.data.model.Endereco
import com.example.meadote.data.model.Usuario
import com.example.meadote.data.repository.CEPRepository
import com.example.meadote.util.Utilitarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class ContaViewModel(private val cepRepo: CEPRepository) : ViewModel() {
    val cepLiveData = MutableLiveData<List<Endereco>>()
    val criaUserLiveData = MutableLiveData<Boolean>()
    val alteraUserLiveData = MutableLiveData<Boolean>()

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    suspend fun getCEP(cep: String) {
        val endereco = cepRepo.getCEP(cep)

        withContext(Dispatchers.Main) {
        cepLiveData.value = endereco
        }
    }

    fun criaUsuario(usuario: Usuario, ctx: Context, senha: String) {
        if (Utilitarios.isConnected(ctx)) {

        }
    }

    fun alteraUsuario(usuario: Usuario, ctx: Context, senha: String) {

    }

    class ContaViewModelFactory(private val cepRepo: CEPRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ContaViewModel(cepRepo) as T
        }

    }
}