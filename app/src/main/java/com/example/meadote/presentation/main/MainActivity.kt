package com.example.meadote.presentation.main

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.meadote.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initDrawerLayoutAndNavigation()
    }

    override fun onStart() {
        super.onStart()
        enableSearch()
    }

    private fun enableSearch() {
        etPesquisa.setOnClickListener { search() }
    }

    private fun search() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_busca, null)

        val pesquisa = view.findViewById<EditText>(R.id.etPesquisa)
        val back = view.findViewById<ImageView>(R.id.ivBack)

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

        pesquisa.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                etPesquisa.setText(pesquisa.text.toString())
                etPesquisa.clearFocus()

                alert.hide()
            }
            false
        }

        back.setOnClickListener {
            etPesquisa.text = null
            etPesquisa.clearFocus()
            alert.hide()
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
    }

    override fun onCreateOptionsMenu(
        menu: Menu?
    ): Boolean {
        menuInflater.inflate(
            R.menu.activity_main_toolbar,
            menu
        )
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
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}