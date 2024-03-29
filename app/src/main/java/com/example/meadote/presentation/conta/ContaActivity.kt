package com.example.meadote.presentation.conta

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.meadote.R
import com.example.meadote.data.model.Usuario
import com.example.meadote.data.repository.CEPRepository
import com.example.meadote.presentation.main.MainActivity
import com.example.meadote.util.Login
import com.example.meadote.util.Utilitarios
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_conta.*
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.activity_main.nav_view
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_conta.*
import kotlinx.android.synthetic.main.content_conta.tvEmail
import kotlinx.android.synthetic.main.content_conta.tvNome
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.*


class ContaActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewModel: ContaViewModel
    var progressBar: AlertDialog? = null
    lateinit var menuSave: Menu
    var verificaCEP: Boolean = true
    private lateinit var ref: DatabaseReference
    lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conta)

        ctx = this

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initDrawerLayoutAndNavigation()

        val email = intent.getStringExtra("email")

        etEmail.setText(email)
    }

    override fun onStart() {
        super.onStart()

        validaCampos()
        salva()

        btExit.setOnClickListener { sair() }

        viewModel = ViewModelProvider(
            this,
            ContaViewModel.ContaViewModelFactory(CEPRepository())
        ).get(ContaViewModel::class.java)

        viewModel.cepLiveData.observe(this, Observer { endereco ->
            if (endereco[0].rua.isNotEmpty() && endereco[0].rua != "erro"
                && endereco[0].bairro.isNotEmpty() && endereco[0].bairro != "erro"
                && endereco[0].cidEst.isNotEmpty() && endereco[0].cidEst != "erro"
            ) {
                etRua.setText(endereco[0].rua)
                etBairro.setText(endereco[0].bairro)
                tvCidEst.text = endereco[0].cidEst

                cdCidEst.visibility = View.VISIBLE
                etNumero.requestFocus()
            } else {
                etBairro.requestFocus()

                Toast.makeText(
                    this,
                    getString(R.string.cep_erro),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.criaUserLiveData.observe(this, Observer { complete ->
            progressBar!!.dismiss()

            if (complete) {
                Toast.makeText(this, getString(R.string.usuario_criado), Toast.LENGTH_SHORT).show()

                updateUi("main")
            } else {
                Toast.makeText(this, getString(R.string.login_erro), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.alteraUserLiveData.observe(this, Observer { complete ->
            progressBar!!.dismiss()

            if (complete) {
                Toast.makeText(this, getString(R.string.salva_altera), Toast.LENGTH_SHORT).show()

                updateUi("conta")
            } else {
                Toast.makeText(this, getString(R.string.erro_altera), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validaCampos() {
        Utilitarios.validaCampo(this, etNome, tiNome)
        Utilitarios.validaCampo(this, etRua, tiRua)
        Utilitarios.validaCampo(this, etNumero, tiNumero)
        Utilitarios.validaCampo(this, etBairro, tiBairro)

        etEmail.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    Utilitarios.limpaErroCampo(etEmail, tiEmail)

                    if (etEmail.text.toString().isEmpty()) {
                        tiEmail?.error = getString(R.string.campo_obrigatorio)
                    } else if (!etEmail.text.toString().contains("@") ||
                        !etEmail.text.toString().contains(".")) {
                        tiEmail?.error = getString(R.string.email_invalido)
                    }
                }
            }

        etCEP?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (etCEP.text.toString().length == 9 &&
                    verificaCEP) {
                    progressBar = Utilitarios.progressBar(this@ContaActivity)

                    CoroutineScope(Dispatchers.IO).launch {
                        async {
                            viewModel.getCEP(etCEP.text.toString())
                        }.await()
                        withContext(Dispatchers.Main) {
                            progressBar?.dismiss()
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        etCEP.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    Utilitarios.limpaErroCampo(etCEP, tiCEP)

                    if (etCEP.text.toString().isEmpty()) {
                        tiCEP?.error = getString(R.string.campo_obrigatorio)

                        limpaEndereco()
                    } else if (etCEP.text.toString().replace("-", "").length < 8) {
                        tiCEP?.error = getString(R.string.cep_invalido)

                        limpaEndereco()
                    }
                }
            }

        if (etTitulo.text == getString(R.string.nova_conta)) {
            Utilitarios.validaCampo(this, etSenha, tiSenha)

            etSenhaConfirma.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        Utilitarios.limpaErroCampo(etSenhaConfirma, tiSenhaConfirma)

                        if (etSenhaConfirma.text.toString().isEmpty()) {
                            tiSenhaConfirma?.error = getString(R.string.campo_obrigatorio)
                        } else if (etSenhaConfirma.text.toString() != etSenha.text.toString()) {
                            tiSenhaConfirma?.error = getString(R.string.senha_erro_confirma)
                        }
                    }
                }
        } else if (etTitulo.text != getString(R.string.nova_conta) && etSenha.text!!.isNotEmpty()) {
            etSenhaConfirma.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        Utilitarios.limpaErroCampo(etSenhaConfirma, tiSenhaConfirma)

                        if (etSenhaConfirma.text.toString().isEmpty()) {
                            tiSenhaConfirma?.error = getString(R.string.campo_obrigatorio)
                        } else if (etSenhaConfirma.text.toString() != etSenha.text.toString()) {
                            tiSenhaConfirma?.error = getString(R.string.senha_erro_confirma)
                        }
                    }
                }
        }

        cepMascara()
    }

    private fun salva() {
        btSave.setOnClickListener {
            var erro = false

            when {
                campoSalva(etNome, tiNome) -> erro = true
                campoSalva(etRua, tiRua) -> erro = true
                campoSalva(etNumero, tiNumero) -> erro = true
                campoSalva(etCEP, tiCEP) -> erro = true
                campoSalva(etBairro, tiBairro) -> erro = true
                campoSalva(etEmail, tiEmail) -> erro = true
            }

            if (etTitulo.text == getString(R.string.nova_conta)) {
                when {
                    campoSalva(etSenha, tiSenha) -> erro = true
                    campoSalva(etSenhaConfirma, tiSenhaConfirma) -> erro = true
                }
            }

            if (!etEmail.text.toString().contains("@") ||
                !etEmail.text.toString().contains(".")) {
                Utilitarios.limpaErroCampo(etEmail, tiEmail)
                tiEmail?.error = getString(R.string.email_invalido)

                Toast.makeText(
                    this,
                    getString(R.string.email_invalido),
                    Toast.LENGTH_SHORT
                ).show()

                erro = true
            } else if (etSenhaConfirma.text.toString() != etSenha.text.toString()) {
                Utilitarios.limpaErroCampo(etSenhaConfirma, tiSenhaConfirma)
                tiSenhaConfirma?.error = getString(R.string.senha_erro_confirma)

                Toast.makeText(
                    this,
                    getString(R.string.senha_erro_confirma),
                    Toast.LENGTH_SHORT
                ).show()

                erro = true
            }

            if (!erro) {
                progressBar = Utilitarios.progressBar(this@ContaActivity)

                if (etTitulo.text == getString(R.string.nova_conta)) {
                    ref = FirebaseDatabase.getInstance().getReference("usuario")
                    val userId = ref.push().key

                    val usuario = Usuario(etNome.text.toString(),
                        userId.toString(),
                        etEmail.text.toString(),
                        etCEP.text.toString(),
                        etBairro.text.toString(),
                        etRua.text.toString(),
                        etNumero.text.toString(),
                        etComplemento.text.toString(),
                        tvCidEst.text.toString().substringBefore("/"),
                        tvCidEst.text.toString().substringAfter("/"),
                        "")

                    viewModel.criaUsuario(usuario, this, etSenha.text.toString())
                } else {
                    val usuario = Usuario(etNome.text.toString(),
                        Utilitarios.consultaString(this, "id").toString(),
                        etEmail.text.toString(),
                        etCEP.text.toString(),
                        etBairro.text.toString(),
                        etRua.text.toString(),
                        etNumero.text.toString(),
                        etComplemento.text.toString(),
                        tvCidEst.text.toString().substringBefore("/"),
                        tvCidEst.text.toString().substringAfter("/"),
                        "")

                    viewModel.alteraUsuario(usuario, this, etSenha.text.toString())
                }
            }
        }
    }

    private fun updateUi(activity: String) {
        if (activity == "main") {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        } else {
            tvNome.text = etNome.text.toString()
            tvEmail.text = etEmail.text.toString()
            tvCEP.text = etCEP.text.toString()
            tvBairro.text = etBairro.text.toString()
            tvRua.text = etRua.text.toString()
            tvNumero.text = etNumero.text.toString()
            tvComplemento.text = etComplemento.text.toString()
            tvCidade.text = tvCidEst.text.toString().substringBefore("/")
            tvEstado.text = tvCidEst.text.toString().substringAfter("/")

            etTitulo.text = getString(R.string.conta)
            tiSenha.hint = getString(R.string.senha)
            tiSenhaConfirma.hint = getString(R.string.confirma_conta)

            if (tvComplemento.text.isEmpty()) {
                tvComplemento.visibility = View.GONE
                tvLabelComplemento.visibility = View.GONE
            } else {
                tvComplemento.visibility = View.VISIBLE
                tvLabelComplemento.visibility = View.VISIBLE
            }

            llConta.visibility = View.VISIBLE
            llCriaConta.visibility = View.GONE
            cdCidEst.visibility = View.GONE
            btExit.visibility = View.VISIBLE
            btCancela.visibility = View.GONE

            menuSave.findItem(R.id.itEdit)?.isVisible = true
        }
    }

    private fun cepMascara() {
        etCEP?.addTextChangedListener(object : TextWatcher {
            var text = ""
            var oldText = ""

            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 5
                    && !s.toString().contains("-")
                    && (s.toString().length > oldText.length
                            || (oldText.contains("-")
                            && !s.toString().contains("-")))
                ) {
                    text = s.toString().substring(0, 5) + "-" + s.toString().substring(5)
                    etCEP.setText(text)
                    etCEP.setSelection(text.length)
                }

                oldText = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun limpaEndereco() {
        etBairro.text = null
        etRua.text = null
        cdCidEst.visibility = View.GONE
    }

    private fun campoSalva(etCampo: EditText?, tiCampo: TextInputLayout?): Boolean {
        if (etCampo?.text.toString().isEmpty()) {
            Utilitarios.limpaErroCampo(etCampo, tiCampo)
            tiCampo?.error = getString(R.string.campo_obrigatorio)

            Toast.makeText(
                this,
                getString(R.string.erro_preenchimento),
                Toast.LENGTH_SHORT
            ).show()

            return true
        }

        return false
    }

    private fun initDrawerLayoutAndNavigation() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        nav_view.setNavigationItemSelectedListener(this)

        drawer_layout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {
                val inputMethodManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(drawerView.windowToken, 0)

                if (Utilitarios.consultaString(ctx, "email")!!.isEmpty()) {
                    btEntrar.visibility = View.VISIBLE

                    btEntrar.setOnClickListener {
                        drawer_layout.closeDrawer(GravityCompat.START)
                        Login.doLogin(ctx)
                    }
                } else {
                    tvNome.text = Utilitarios.consultaString(ctx, "nome")
                    tvEmail.text = Utilitarios.consultaString(ctx, "email")

                    val end = Utilitarios.consultaString(ctx, "rua") +
                            ", " +
                            Utilitarios.consultaString(ctx, "numero")

                    tvEndereco.text = end

                    btEntrar.visibility = View.GONE
                    liInfoConta.visibility = View.VISIBLE
                }
            }

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })

        toggle.syncState()

        if (Utilitarios.consultaString(this, "email")!!.isEmpty()) {
            etTitulo.text = getString(R.string.nova_conta)
        } else {
            etTitulo.text = getString(R.string.conta)

            tvNome.text = Utilitarios.consultaString(this, "nome")
            tvEmail.text = Utilitarios.consultaString(this, "email")
            tvRua.text = Utilitarios.consultaString(this, "rua")
            tvNumero.text = Utilitarios.consultaString(this, "numero")
            tvCEP.text = Utilitarios.consultaString(this, "cep")
            tvBairro.text = Utilitarios.consultaString(this, "bairro")
            tvCidade.text = Utilitarios.consultaString(this, "cidade")
            tvEstado.text = Utilitarios.consultaString(this, "estado")
            tvComplemento.text = Utilitarios.consultaString(this, "complemento")

            llCriaConta.visibility = View.GONE
            llConta.visibility = View.VISIBLE
            btExit.visibility = View.VISIBLE

            if (tvComplemento.text.isEmpty()) {
                tvComplemento.visibility = View.GONE
                tvLabelComplemento.visibility = View.GONE
            } else {
                tvComplemento.visibility = View.VISIBLE
                tvLabelComplemento.visibility = View.VISIBLE
            }
        }

        nav_view.menu.findItem(R.id.nav_conta).isVisible = false
    }

    override fun onCreateOptionsMenu(
        menu: Menu?
    ): Boolean {
        menuInflater.inflate(
            R.menu.activity_conta_toolbar,
            menu
        )

        if (etTitulo.text == getString(R.string.nova_conta)) {
            menu?.findItem(R.id.itEdit)?.isVisible = false
        }

        menuSave = menu!!

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {
        return when (item.itemId) {
            R.id.itEdit -> {
                tiSenha.error = null
                tiSenhaConfirma.error = null

                etNome.setText(tvNome.text)
                etEmail.setText(tvEmail.text)

                verificaCEP = false
                etCEP.setText(tvCEP.text)
                verificaCEP = true

                etBairro.setText(tvBairro.text)
                etRua.setText(tvRua.text)
                etNumero.setText(tvNumero.text)
                etComplemento.setText(tvComplemento.text)
                tvCidEst.text = tvCidade.text.toString() + "/" + tvEstado.text.toString()

                tiSenha.hint = getString(R.string.nova_senha)
                tiSenhaConfirma.hint = getString(R.string.confirma_nova_senha)
                etTitulo.text = getString(R.string.editar_conta)

                llConta.visibility = View.GONE
                llCriaConta.visibility = View.VISIBLE
                cdCidEst.visibility = View.VISIBLE
                btExit.visibility = View.GONE
                btCancela.visibility = View.VISIBLE

                menuSave.findItem(R.id.itEdit)?.isVisible = false

                btCancela.setOnClickListener {
                    etTitulo.text = getString(R.string.conta)
                    tiSenha.hint = getString(R.string.senha)
                    tiSenhaConfirma.hint = getString(R.string.confirma_conta)

                    llConta.visibility = View.VISIBLE
                    llCriaConta.visibility = View.GONE
                    cdCidEst.visibility = View.GONE
                    btExit.visibility = View.VISIBLE
                    btCancela.visibility = View.GONE

                    menuSave.findItem(R.id.itEdit)?.isVisible = true
                }

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun sair() {
        Utilitarios.limpaString(this)
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(intent)

        Toast.makeText(
            this,
            getString(R.string.saiu),
            Toast.LENGTH_SHORT
        ).show()
    }
}