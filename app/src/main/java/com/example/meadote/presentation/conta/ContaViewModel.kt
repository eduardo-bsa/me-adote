package com.example.meadote.presentation.conta

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meadote.data.model.Endereco
import com.example.meadote.data.model.Usuario
import com.example.meadote.data.repository.CEPRepository
import com.example.meadote.util.Utilitarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class ContaViewModel(private val cepRepo: CEPRepository) : ViewModel() {
    val cepLiveData = MutableLiveData<List<Endereco>>()
    val criaUserLiveData = MutableLiveData<Boolean>()

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    suspend fun getCEP(cep: String) {
        val endereco = cepRepo.getCEP(cep)

        withContext(Dispatchers.Main) {
        cepLiveData.value = endereco
        }
    }

    fun criaUsuario(usuario: Usuario, ctx: Context) {
        if (Utilitarios.isConnected(ctx)) {
            auth = Firebase.auth

            auth.createUserWithEmailAndPassword(usuario.email, usuario.senha)
                .addOnCompleteListener(ctx as Activity) { task ->
                    if (task.isSuccessful) {
                        ref = FirebaseDatabase.getInstance().getReference("usuario")
                        val userId = ref.push().key
                        ref.child(userId!!).setValue(usuario).addOnCompleteListener {
                            criaUserLiveData.value = true

                            Utilitarios.salvaString(
                                ctx,
                                "nome",
                                usuario.nome
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "email",
                                usuario.email
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "cep",
                                usuario.cep
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "bairro",
                                usuario.bairro
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "rua",
                                usuario.rua
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "numero",
                                usuario.numero
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "complemento",
                                usuario.complemento
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "foto",
                                usuario.foto
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "cidade",
                                usuario.cidade
                            )

                            Utilitarios.salvaString(
                                ctx,
                                "estado",
                                usuario.estado
                            )
                        }
                    } else {
                        criaUserLiveData.value = false
                    }
                }
        }
    }

    class ContaViewModelFactory(private val cepRepo: CEPRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ContaViewModel(cepRepo) as T
        }

    }
}