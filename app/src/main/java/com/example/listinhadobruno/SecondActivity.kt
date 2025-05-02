package com.example.listinhadobruno

import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import android.widget.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.os.Looper
import okhttp3.*

import okhttp3.Response
import okhttp3.Call


class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val textTexto  = findViewById<TextView>(R.id.Texto)
      

        val textTextoTraduzido = findViewById<TextView>(R.id.TextoTraduzido)
        val avisoText = findViewById<TextView>(R.id.avText)

        val cbolist = findViewById<Spinner>(R.id.idioma1)
        val cbolist2 = findViewById<Spinner>(R.id.idioma2)
        val btnTraduzir = findViewById<Button>(R.id.btnTraduzir)
        val btnLimpar = findViewById<Button>(R.id.btnLimpar)

        val nomesIdiomas = arrayOf("Inglês", "Português", "Espanhol", "Francês")
        val codigosIdiomas = arrayOf("en", "pt", "es", "fr")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nomesIdiomas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        cbolist.adapter = adapter
        cbolist2.adapter = adapter

        btnTraduzir.setOnClickListener {

            val textoParaTraduzir = textTexto.text.toString()
            if (TextUtils.isEmpty(textoParaTraduzir)) {
                avisoText.text = "Preencha o texto base"
            } else {
                avisoText.text = null

                val indexCbo1 = cbolist.selectedItemPosition
                val indexCbo2 = cbolist2.selectedItemPosition

                val idiomaOrigem = codigosIdiomas[indexCbo1]
                val idiomaDestino = codigosIdiomas[indexCbo2]
                Toast.makeText(this, "Traduzindo de $idiomaOrigem para $idiomaDestino", Toast.LENGTH_SHORT).show()

                traduzirTexto(textoParaTraduzir, idiomaOrigem, idiomaDestino, textTextoTraduzido)
            }
        }
        btnLimpar.setOnClickListener {
            textTexto.text = null
            textTextoTraduzido.text = null
            avisoText.text = null
        }
    }

    private fun traduzirTexto(texto: String, origem: String, destino: String, textView: TextView) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val url = "https://api.mymemory.translated.net/get?q=$texto&langpair=$origem|$destino"
        val request = Request.Builder().url(url).get().build()

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Erro HTTP: ${response.code}")

                    val jsonObject = JSONObject(response.body?.string() ?: "{}")
                    jsonObject.optJSONObject("responseData")?.optString("translatedText", "Erro ao traduzir")
                }
            }.onSuccess { translatedText ->
                withContext(Dispatchers.Main) { textView.text = translatedText }
            }.onFailure { e ->
                withContext(Dispatchers.Main) { textView.text = "Erro: ${e.message}" }
            }
        }
    }


}
