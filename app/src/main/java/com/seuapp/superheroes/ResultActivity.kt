package com.seuapp.superheroes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        val tvTitulo = findViewById<TextView>(R.id.tvResultadoTitulo)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)
        val imgHero = findViewById<ImageView>(R.id.imgHero)
        val btnCompartilhar = findViewById<Button>(R.id.btnCompartilhar)
        val btnAbrirWiki = findViewById<Button>(R.id.btnAbrirWiki)
        val btnRefazer = findViewById<Button>(R.id.btnRefazer) // <-- novo

        val nome = intent.getStringExtra("NOME_USUARIO") ?: getString(R.string.app_name)
        val heroi = intent.getStringExtra("HEROI") ?: "Herói Misterioso"
        val pontuacao = intent.getIntExtra("PONTUACAO", 0)

        tvTitulo.text = getString(R.string.result_title)
        tvResultado.text = getString(R.string.result_message, nome, heroi, pontuacao)

        val heroImageRes = when (heroi) {
            "Thor"             -> R.drawable.hero_thor
            "Homem de Ferro"   -> R.drawable.hero_ironman
            "Capitão América"  -> R.drawable.hero_capitao
            "Hulk"             -> R.drawable.hero_hulk
            else               -> R.drawable.app_logo
        }

        imgHero.setImageResource(heroImageRes)

        // Compartilhar
        btnCompartilhar.setOnClickListener {
            val shareText = getString(R.string.share_text, getString(R.string.app_name), heroi)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
        }

        // Abrir Wikipedia
        btnAbrirWiki.setOnClickListener {
            val url = when (heroi) {
                "Thor"            -> "https://pt.wikipedia.org/wiki/Thor_(Marvel_Comics)"
                "Homem de Ferro"  -> "https://pt.wikipedia.org/wiki/Homem_de_Ferro"
                "Capitão América" -> "https://pt.wikipedia.org/wiki/Capit%C3%A3o_Am%C3%A9rica"
                "Hulk"            -> "https://pt.wikipedia.org/wiki/Hulk"
                else              -> "https://pt.wikipedia.org/wiki/Vingadores"
            }

            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            if (browserIntent.resolveActivity(packageManager) != null) {
                startActivity(browserIntent)
            } else {
                Toast.makeText(this, getString(R.string.toast_no_app), Toast.LENGTH_SHORT).show()
            }
        }

        // Refazer quiz (volta para o QuizActivity e mantém o nome)
        btnRefazer?.setOnClickListener {
            val restart = Intent(this, QuizActivity::class.java).apply {
                putExtra("NOME_USUARIO", nome)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(restart)
            finish() // fecha a ResultActivity atual
        }
    }
}
