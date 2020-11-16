package com.example.meadote.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.meadote.R
import com.example.meadote.data.model.Usuario
import com.example.meadote.presentation.conta.ContaActivity
import com.example.meadote.presentation.main.MainActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

object Utilitarios {

    private lateinit var auth: FirebaseAuth
    private val usuarios: MutableList<Usuario> = mutableListOf()
    private const val PREF = "com.example.meadote.util.PREF"

    private var context: Context? = null
    private var emailFB = ""
    private var tiFB: TextInputLayout? = null
    private var etFB: EditText? = null
    var progressBar: AlertDialog? = null
    lateinit var usuario: Usuario

    fun login(ctx: Context) {
        context = ctx

        val builder = AlertDialog.Builder(ctx)
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.dialog_login, null)

        val inputMethodManager: InputMethodManager =
            ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val imEmail = view.findViewById<ImageView>(R.id.imEmail)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val btEmail = view.findViewById<Button>(R.id.btEmail)
        val menu = view.findViewById<View>(R.id.menu)
        val tiEmail = view.findViewById<TextInputLayout>(R.id.tiEmail)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btContinuar = view.findViewById<Button>(R.id.btContinuar)
        val tiSenha = view.findViewById<TextInputLayout>(R.id.tiSenha)
        val etSenha = view.findViewById<EditText>(R.id.etSenha)
        val email = view.findViewById<View>(R.id.email)
        imEmail.bringToFront()

        builder.setView(view)

        val alert = builder.create()
        alert.getWindow()!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alert.show()

        btEmail.setOnClickListener {
            menu.visibility = View.GONE
            email.visibility = View.VISIBLE

            etSenha.text = null
            tiSenha.error = null
            tiEmail.error = null

            etEmail.post {
                inputMethodManager.showSoftInput(
                    etEmail,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }
            etEmail.requestFocus()
        }

        ivClose.setOnClickListener {
            if (menu.visibility == View.GONE) {
                menu.visibility = View.VISIBLE
                email.visibility = View.GONE
                tiSenha.visibility = View.GONE

                etEmail.clearFocus()
                etSenha.clearFocus()
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            } else {
                alert.hide()
            }
        }

        btContinuar.setOnClickListener {
            var validaEmail = true
            var validaSenha = true

            if (tiSenha.visibility == View.VISIBLE) {
                if (etSenha.text.toString().isEmpty()) {
                    limpaErroCampo(etSenha, tiSenha)
                    tiSenha.error = ctx.getString(R.string.erro_senha)
                    validaSenha = false
                }

                if (etEmail.text.toString().isEmpty()
                    || !etEmail.text.toString().contains("@")
                    || !etEmail.text.toString().contains(".")) {
                    validaEmail(ctx, etEmail, tiEmail)
                    validaEmail = false
                }

                if (validaSenha && validaEmail) {
                    progressBar = progressBar(ctx)

                    doLogin(ctx, etEmail.text.toString(), etSenha.text.toString(), alert)
                }
            } else {
                if (etEmail.text.toString().isEmpty()
                    || !etEmail.text.toString().contains("@")
                    || !etEmail.text.toString().contains(".")) {
                    validaEmail(ctx, etEmail, tiEmail)
                } else {
                    progressBar = progressBar(ctx)

                    emailFB = etEmail.text.toString()
                    tiFB = tiSenha
                    etFB = etSenha

                    val query = FirebaseDatabase.getInstance().getReference("usuario")
                        .orderByChild("email")
                        .equalTo(emailFB)

                    query.addListenerForSingleValueEvent(emailEventListener)
                }
            }
        }

        etEmail.setOnEditorActionListener { v, actionId, event ->
            if (etEmail.text.toString().isEmpty()
                || !etEmail.text.toString().contains("@")
                || !etEmail.text.toString().contains(".")) {
                validaEmail(ctx, etEmail, tiEmail)
            } else {
                emailFB = etEmail.text.toString()
                tiFB = tiSenha
                etFB = etSenha

                val query = FirebaseDatabase.getInstance().getReference("usuario")
                    .orderByChild("email")
                    .equalTo(emailFB)

                query.addListenerForSingleValueEvent(emailEventListener)
            }
            false
        }

        etSenha.setOnEditorActionListener { v, actionId, event ->
            var validaEmail = true
            var validaSenha = true

            if (etSenha.text.toString().isEmpty()) {
                limpaErroCampo(etSenha, tiSenha)
                tiSenha.error = ctx.getString(R.string.erro_senha)
                validaSenha = false
            }

            if (etEmail.text.toString().isEmpty()
                || !etEmail.text.toString().contains("@")
                || !etEmail.text.toString().contains(".")) {
                validaEmail(ctx, etEmail, tiEmail)
                validaEmail = false
            }

            if (validaSenha && validaEmail) {
                progressBar = progressBar(ctx)

                doLogin(ctx, etEmail.text.toString(), etSenha.text.toString(), alert)
            }

            false
        }
    }

    var emailEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            usuarios.clear()

            for (snapshot in dataSnapshot.children) {
                val usuario = snapshot.getValue(Usuario::class.java)
                usuarios.add(usuario!!)
            }

            var existeEmail = false

            if (usuarios.isNotEmpty()) {
                existeEmail = true
            }

            if (existeEmail) {
                progressBar?.dismiss()

                val usu = Usuario(usuarios[0].nome,
                    usuarios[0].id,
                    usuarios[0].email,
                    usuarios[0].cep,
                    usuarios[0].bairro,
                    usuarios[0].rua,
                    usuarios[0].numero,
                    usuarios[0].complemento,
                    usuarios[0].cidade,
                    usuarios[0].estado,
                    usuarios[0].foto)

                usuario = usu

                tiFB?.visibility = View.VISIBLE
                etFB?.requestFocus()
            } else {
                progressBar?.dismiss()

                val intent = Intent(context, ContaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("email", emailFB)

                context?.startActivity(intent)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun doLogin(ctx: Context, email: String, senha: String, alert: AlertDialog) {
        auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(ctx as Activity){ task ->
            if (task.isSuccessful) {
                progressBar?.dismiss()
                alert.dismiss()

                salvaString(
                    ctx,
                    "nome",
                    usuario.nome
                )

                salvaString(
                    ctx,
                    "email",
                    usuario.email
                )

                salvaString(
                    ctx,
                    "cep",
                    usuario.cep
                )

                salvaString(
                    ctx,
                    "bairro",
                    usuario.bairro
                )

                salvaString(
                    ctx,
                    "rua",
                    usuario.rua
                )

                salvaString(
                    ctx,
                    "numero",
                    usuario.numero
                )

                salvaString(
                    ctx,
                    "complemento",
                    usuario.complemento
                )

                salvaString(
                    ctx,
                    "foto",
                    usuario.foto
                )

                salvaString(
                    ctx,
                    "cidade",
                    usuario.cidade
                )

                salvaString(
                    ctx,
                    "estado",
                    usuario.estado
                )

                salvaString(
                    ctx,
                    "id",
                    usuario.id
                )

                Toast.makeText(
                    ctx,
                    ctx.getString(R.string.entrou),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                progressBar?.dismiss()

                Toast.makeText(
                    ctx,
                    ctx.getString(R.string.erro_login),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun limpaErroCampo(etCampo: EditText?, tiCampo: TextInputLayout?) {
        etCampo?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (!etCampo.text.toString().isEmpty()) {
                    tiCampo?.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun validaEmail(ctx: Context, etCampo: EditText?, tiCampo: TextInputLayout?) {
        limpaErroCampo(etCampo, tiCampo)

        if (etCampo?.text.toString().isEmpty()) {
            tiCampo?.error = ctx.getString(R.string.erro_email)
            etCampo?.clearFocus()
        } else if (!etCampo?.text.toString().contains("@")
            || !etCampo?.text.toString().contains(".")
        ) {
            tiCampo?.error = ctx.getString(R.string.email_invalido)
            etCampo?.clearFocus()
        }
    }

    fun validaCampo(ctx: Context, etCampo: EditText?, tiCampo: TextInputLayout?) {
        etCampo?.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    limpaErroCampo(etCampo, tiCampo)

                    if (etCampo?.text.toString().isEmpty()) {
                        tiCampo?.error = ctx.getString(R.string.campo_obrigatorio)
                    }
                }
            }
    }

    fun progressBar(ctx: Context) : AlertDialog {
        val builder = AlertDialog.Builder(ctx)

        val inflater = LayoutInflater.from(ctx)

        val view = inflater.inflate(R.layout.progress_bar, null)

        builder.setView(view)

        val alert = builder.create()

        alert.show()
        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)

        return alert
    }

    fun isConnected(
        context: Context?
    ): Boolean {
        val cm = context?.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val ni = cm.activeNetworkInfo
        if (ni != null && ni.isConnected) {
            return ni.isConnected
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.erro_conexao),
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    fun salvaString(context: Context, key: String?, value: String?) {
        val sp: SharedPreferences =
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit().putString(key, value).apply()
    }

    fun consultaString(context: Context?, key: String?): String? {
        val sp: SharedPreferences? =
            context?.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp?.getString(key, "")
    }

    fun limpaString(context: Context) {
        val sp: SharedPreferences =
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit().clear().apply()
    }
}