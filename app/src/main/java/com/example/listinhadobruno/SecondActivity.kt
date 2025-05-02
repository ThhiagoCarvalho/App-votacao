package com.example.listinhadobruno

import android.os.Bundle
import android.util.Log
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class SecondActivity : AppCompatActivity() {

    private val database = Firebase.database
    private val opcoesRef = database.getReference("enquetes/enquete1/opcoes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val opcao1Text = findViewById<TextView>(R.id.opcao1Result)  // Matrix
        val opcao2Text = findViewById<TextView>(R.id.opcao2Result)  // Harry Potter
        val opcao3Text = findViewById<TextView>(R.id.opcao3Result)  // Senhor dos Anéis

        opcoesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val votosMatrix = snapshot.child("Matrix").child("votos").getValue(Int::class.java) ?: 0
                val votosHarry = snapshot.child("Harry Potter").child("votos").getValue(Int::class.java) ?: 0
                val votosSenhor = snapshot.child("Senhor dos Anéis").child("votos").getValue(Int::class.java) ?: 0

                opcao1Text.text = "Matrix: $votosMatrix votos"
                opcao2Text.text = "Harry Potter: $votosHarry votos"
                opcao3Text.text = "Senhor dos Anéis: $votosSenhor votos"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SecondActivity", "Erro ao buscar votos", error.toException())
            }
        })
    }
}
