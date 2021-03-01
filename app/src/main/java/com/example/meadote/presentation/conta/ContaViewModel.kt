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
            auth = Firebase.auth

            auth.createUserWithEmailAndPassword(usuario.email, senha)
                .addOnCompleteListener(ctx as Activity) { task ->
                    if (task.isSuccessful) {
                        ref = FirebaseDatabase.getInstance().getReference("usuario")
                        ref.child(usuario.id).setValue(usuario).addOnCompleteListener {

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

                            Utilitarios.salvaString(
                                ctx,
                                "id",
                                usuario.id
                            )

                            criaUserLiveData.value = true
                        }
                    } else {
                        criaUserLiveData.value = false
                    }
                }
        }
    }

    fun alteraUsuario(usuario: Usuario, ctx: Context, senha: String) {
        val dbUsuario = FirebaseDatabase.getInstance().getReference("usuario")
        dbUsuario.child(usuario.id).setValue(usuario).addOnCompleteListener { task ->
            if (task.isSuccessful) {
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

                if (senha.isNotEmpty()) {
                    var firebaseUser: FirebaseUser? = null
                    auth = Firebase.auth
                    firebaseUser = auth.currentUser

                    firebaseUser!!.updatePassword(senha).addOnCompleteListener { task ->
                        alteraUserLiveData.value = task.isSuccessful
                    }
                } else {
                    alteraUserLiveData.value = true
                }
            } else {
                alteraUserLiveData.value = false
            }
        }
    }

    class ContaViewModelFactory(private val cepRepo: CEPRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ContaViewModel(cepRepo) as T
        }

    }
}