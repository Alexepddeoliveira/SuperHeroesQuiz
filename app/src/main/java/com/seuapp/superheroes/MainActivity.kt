package com.seuapp.superheroes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val etNome = findViewById<EditText>(R.id.etNome)
        val btn = findViewById<Button>(R.id.btnComecar)

        btn.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val i = Intent(this, QuizActivity::class.java)
            i.putExtra("NOME_USUARIO", nome)
            startActivity(i)
        }
    }
}
