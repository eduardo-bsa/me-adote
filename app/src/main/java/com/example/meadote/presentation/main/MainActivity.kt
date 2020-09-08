package com.example.meadote.presentation.main

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.meadote.R
import com.example.meadote.util.Utilitarios
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var buscaMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initDrawerLayoutAndNavigation()
    }

    override fun onStart() {
        super.onStart()
        etPesquisa.setOnClickListener { search() }
    }

    private fun search() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_busca, null)

        val pesquisa = view.findViewById<EditText>(R.id.etPesquisa)
        val back = view.findViewById<ImageView>(R.id.ivBack)
        val close = view.findViewById<ImageView>(R.id.ivClose)

        if (etPesquisa.text.toString().isNotEmpty()) {
            close.visibility = View.VISIBLE
        }

        pesquisa.setText(etPesquisa.text.toString())
        pesquisa.onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus ->
                pesquisa.post {
                    val inputMethodManager: InputMethodManager =
                        this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(
                        pesquisa,
                        InputMethodManager.SHOW_IMPLICIT
                    )
                }
            }
        pesquisa.requestFocus()

        builder.setView(view)

        val alert = builder.create()
        alert.show()
        alert.getWindow()!!.setGravity(Gravity.TOP)
        alert.getWindow()!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        close.setOnClickListener {
            etPesquisa.text = null
            buscaMenu.setIcon(R.drawable.ic_baseline_search_24)
            etPesquisa.clearFocus()
            alert.hide()
        }

        pesquisa.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                etPesquisa.setText(pesquisa.text.toString())
                etPesquisa.clearFocus()

                if (pesquisa.text.toString().isNotEmpty()) {
                    buscaMenu.setIcon(R.drawable.ic_baseline_close_24)
                } else {
                    buscaMenu.setIcon(R.drawable.ic_baseline_search_24)
                }

                alert.hide()
            }
            false
        }

        back.setOnClickListener {
            etPesquisa.clearFocus()
            alert.hide()
        }
    }

    private fun login() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_login, null)

        val inputMethodManager: InputMethodManager =
            this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val imEmail = view.findViewById<ImageView>(R.id.imEmail)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val btEmail = view.findViewById<Button>(R.id.btEmail)
        val menu = view.findViewById<View>(R.id.menu)
        val tiEmail = view.findViewById<TextInputLayout>(R.id.tiEmail)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val ivContinua = view.findViewById<ImageView>(R.id.ivContinua)
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
            ivContinua.visibility = View.VISIBLE

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
                ivContinua.visibility = View.GONE

                etEmail.clearFocus()
                etSenha.clearFocus()
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            } else {
                alert.hide()
            }
        }

        ivContinua.setOnClickListener {
            var validaEmail = true
            var validaSenha = true
            if (tiSenha.visibility == View.VISIBLE) {
                if (etSenha.text.toString().isEmpty()) {
                    Utilitarios.limpaErroCampo(etSenha, tiSenha)
                    tiSenha.error = this.getString(R.string.erro_senha)
                    validaSenha = false
                }

                if (etEmail.text.toString().isEmpty()
                    || !etEmail.text.toString().contains("@")
                    || !etEmail.text.toString().contains(".")) {
                    Utilitarios.validaEmail(this, etEmail, tiEmail)
                    validaEmail = false
                }

                /*if (validaSenha && validaEmail) {

                }*/
            } else {
                if (etEmail.text.toString().isEmpty()
                    || !etEmail.text.toString().contains("@")
                    || !etEmail.text.toString().contains(".")) {
                    Utilitarios.validaEmail(this, etEmail, tiEmail)
                } else {
                    tiSenha.visibility = View.VISIBLE
                    etSenha.requestFocus()
                }
            }
        }

        etEmail.setOnEditorActionListener { v, actionId, event ->
            if (etEmail.text.toString().isEmpty()
                || !etEmail.text.toString().contains("@")
                || !etEmail.text.toString().contains(".")) {
                Utilitarios.validaEmail(this, etEmail, tiEmail)
            } else {
                if (tiSenha.visibility != View.VISIBLE) {
                    tiSenha.visibility = View.VISIBLE
                    etSenha.requestFocus()
                }
            }
            true
        }

        etSenha.setOnEditorActionListener { v, actionId, event ->
            var validaEmail = true
            var validaSenha = true
            if (etSenha.text.toString().isEmpty()) {
                Utilitarios.limpaErroCampo(etSenha, tiSenha)
                tiSenha.error = this.getString(R.string.erro_senha)
                validaSenha = false
            }

            if (etEmail.text.toString().isEmpty()
                || !etEmail.text.toString().contains("@")
                || !etEmail.text.toString().contains(".")) {
                Utilitarios.validaEmail(this, etEmail, tiEmail)
                validaEmail = false
            }

            /*if (validaSenha && validaEmail) {

            }*/

            false
        }
    }

    private fun initDrawerLayoutAndNavigation() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        val headerLayout = nav_view.getHeaderView(0)
        val entrar = headerLayout.findViewById<Button>(R.id.btEntrar)
        entrar.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            login()
        }
    }

    override fun onCreateOptionsMenu(
        menu: Menu?
    ): Boolean {
        menuInflater.inflate(
            R.menu.activity_main_toolbar,
            menu
        )

        buscaMenu = menu!!.findItem(R.id.itBusca)

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
    /*  when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "teste", Toast.LENGTH_SHORT).show()
            }
        } */
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {
        return when (item.itemId) {
            R.id.itBusca -> {
                search()
                true
            }
            R.id.itFiltro -> {
                val builder = AlertDialog.Builder(this)
                val inflater = LayoutInflater.from(this)
                val view = inflater.inflate(R.layout.dialog_filtro, null)

                builder.setView(view)

                val alert = builder.create()
                alert.show()
                alert.getWindow()!!.setGravity(Gravity.TOP)
                alert.getWindow()!!.setBackgroundDrawableResource(R.drawable.bg_filtro_round)
                alert.getWindow()!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}