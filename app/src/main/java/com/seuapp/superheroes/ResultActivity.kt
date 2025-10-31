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

        val nome = intent.getStringExtra("NOME_USUARIO") ?: getString(R.string.app_name)
        val heroi = intent.getStringExtra("HEROI") ?: "Herói Misterioso"
        val pontuacao = intent.getIntExtra("PONTUACAO", 0)

        tvTitulo.text = getString(R.string.result_title)
        tvResultado.text = getString(R.string.result_message, nome, heroi, pontuacao)

        val heroImageRes = when (heroi) {
            "Homem de Ferro" -> R.mipmap.ic_launcher
            "Hulk" -> R.mipmap.ic_launcher
            "Mulher-Maravilha" -> R.mipmap.ic_launcher
            "Homem-Aranha" -> R.mipmap.ic_launcher
            else -> R.mipmap.ic_launcher
        }
        imgHero.setImageResource(heroImageRes)

        btnCompartilhar.setOnClickListener {
            val shareText = getString(R.string.share_text, getString(R.string.app_name), heroi)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
        }

        btnAbrirWiki.setOnClickListener {
            val url = when (heroi) {
                "Homem de Ferro" -> "https://pt.wikipedia.org/wiki/Homem_de_Ferro"
                "Hulk" -> "https://pt.wikipedia.org/wiki/Hulk"
                "Mulher-Maravilha" -> "https://pt.wikipedia.org/wiki/Mulher-Maravilha"
                "Homem-Aranha" -> "https://pt.wikipedia.org/wiki/Homem-Aranha"
                else -> "https://pt.wikipedia.org/wiki/Super-her%C3%B3i"
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            if (browserIntent.resolveActivity(packageManager) != null) {
                startActivity(browserIntent)
            } else {
                Toast.makeText(this, getString(R.string.toast_no_app), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
