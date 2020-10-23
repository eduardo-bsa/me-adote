package com.example.meadote.data.repository

import com.example.meadote.data.model.Endereco
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class CEPRepository {
    fun getCEP(): List<Endereco> {
        val urlCEP = "https://viacep.com.br/ws/13145878/json"
        val url = URL(urlCEP)
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.connectTimeout = 7000
        val content = urlConnection.inputStream.bufferedReader().use(BufferedReader::readText)
        val json = JSONObject(content)

        return if (json.has("erro")) {
            listOf(
                Endereco("erro",
                    "erro",
                    "erro")
            )
        } else {
            listOf(
                Endereco(json.getString("logradouro"),
                    json.getString("bairro"),
                    "${json.getString("localidade")}/${json.getString("uf")}")
            )
        }
    }
}