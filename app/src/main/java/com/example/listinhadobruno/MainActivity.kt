package com.example.listinhadobruno

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val btnOpenPage = findViewById<Button>(R.id.botaoIniciarTraducao)
            btnOpenPage.setOnClickListener {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)  // Abre a nova página
            }
        }


}