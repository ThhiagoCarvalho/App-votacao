package com.example.listinhadobruno

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private  val database = Firebase.database
    private val enqueteRef = database.getReference("enquetes/enquete1/opcoes")
    private val perguntaRef = database.getReference("enquetes/enquete1/pergunta")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)


            val perguntaText = findViewById<TextView>(R.id.perguntaText)
            val radioGroup = findViewById<RadioGroup>(R.id.opcoesGroup)
            val btnVotar = findViewById<Button>(R.id.botaoOK)

            val radio1 = findViewById<RadioButton>(R.id.opcao1)
            val radio2 = findViewById<RadioButton>(R.id.opcao2)
            val radio3 = findViewById<RadioButton>(R.id.opcao3)


            btnVotar.setOnClickListener {
                val selectedId = radioGroup.checkedRadioButtonId

                if (selectedId != -1) {
                    val selectedRadio = findViewById<RadioButton>(selectedId)
                    val opcaoSelecionada = selectedRadio.text.toString()

                    // Envia a opção selecionada pro Firebase:
                    votar(opcaoSelecionada)
                } else {
                    Toast.makeText(this, "Selecione uma opção!", Toast.LENGTH_SHORT).show()
                }
            }


            enqueteRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val radioButtons = listOf(radio1, radio2, radio3)
                    val opcoes = snapshot.children.toList()

                    for (i in 0 until minOf(opcoes.size, radioButtons.size)) {
                        val titulo = opcoes[i].child("titulo").getValue(String::class.java)
                        radioButtons[i].text = titulo ?: "Opção ${i + 1}"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Votacao", "Erro ao carregar opções.", error.toException())
                }
            })


            perguntaRef.addValueEventListener(object : ValueEventListener {
                 override fun onDataChange(snapshot: DataSnapshot) {
                        val pergunta = snapshot.getValue(String::class.java)
                        perguntaText.text = pergunta
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("Votacao", "Erro ao carregar pergunta.", error.toException())
                    }
                })
            }


    fun votar(opcao: String) {
        val opcaoRef = enqueteRef.child(opcao).child("votos")

        opcaoRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentValue = currentData.getValue(Int::class.java) ?: 0
                currentData.value = currentValue + 1
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (committed) {
                    val intent = Intent(this@MainActivity, SecondActivity::class.java)
                    startActivity(intent);

                }
            }
        })
    }


}