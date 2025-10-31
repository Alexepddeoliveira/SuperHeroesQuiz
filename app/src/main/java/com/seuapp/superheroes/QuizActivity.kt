package com.seuapp.superheroes

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private lateinit var rgTraco: RadioGroup
    private lateinit var cbLideranca: CheckBox
    private lateinit var cbCompPaixao: CheckBox
    private lateinit var cbEstrategia: CheckBox
    private lateinit var cbHumor: CheckBox
    private lateinit var skCoragem: SeekBar
    private lateinit var tvCoragemValor: TextView
    private lateinit var spPoder: Spinner
    private lateinit var btnVerResultado: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        rgTraco = findViewById(R.id.rgTraco)
        cbLideranca = findViewById(R.id.cbLideranca)
        cbCompPaixao = findViewById(R.id.cbCompPaixao)
        cbEstrategia = findViewById(R.id.cbEstrategia)
        cbHumor = findViewById(R.id.cbHumor)
        skCoragem = findViewById(R.id.skCoragem)
        tvCoragemValor = findViewById(R.id.tvCoragemValor)
        spPoder = findViewById(R.id.spPoder)
        btnVerResultado = findViewById(R.id.btnVerResultado)

        // Spinner: carrega opções e define seleção inicial
        ArrayAdapter.createFromResource(
            this,
            R.array.poderes_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPoder.adapter = adapter
        }
        if (spPoder.adapter != null && spPoder.adapter.count > 0) {
            spPoder.setSelection(0, false) // evita null ao ler depois
        }

        // SeekBar: texto inicial e listener
        tvCoragemValor.text = getString(R.string.coragem_valor, skCoragem.progress)
        skCoragem.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvCoragemValor.text = getString(R.string.coragem_valor, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnVerResultado.setOnClickListener {
            val nome = intent.getStringExtra("NOME_USUARIO") ?: getString(R.string.app_name)

            // Valida: precisa escolher ao menos um traço (Q1)
            if (rgTraco.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Escolha um traço na pergunta 1.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Valida: spinner com itens
            if (spPoder.adapter == null || spPoder.adapter.count == 0) {
                Toast.makeText(this, "Lista de poderes indisponível.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val scores = mutableMapOf(
                "Thor" to 0,
                "Homem de Ferro" to 0,
                "Capitão América" to 0,
                "Hulk" to 0
            )

            // Q1: traço principal
            when (rgTraco.checkedRadioButtonId) {
                R.id.rbInteligencia -> scores["Homem de Ferro"] = scores["Homem de Ferro"]!! + 2
                R.id.rbForca        -> scores["Hulk"] = scores["Hulk"]!! + 2
                R.id.rbCoragem      -> scores["Capitão América"] = scores["Capitão América"]!! + 2
                R.id.rbAgilidade    -> scores["Thor"] = scores["Thor"]!! + 2
            }


            // Q2: valores
            if (cbLideranca.isChecked)   scores["Capitão América"] = scores["Capitão América"]!! + 1
            if (cbCompPaixao.isChecked)  scores["Capitão América"] = scores["Capitão América"]!! + 1
            if (cbEstrategia.isChecked)  scores["Homem de Ferro"]  = scores["Homem de Ferro"]!! + 1
            if (cbHumor.isChecked)       scores["Thor"]            = scores["Thor"]!! + 1

            // Q3: autocontrole
            val autocontrole = skCoragem.progress
            when {
                autocontrole >= 7 -> {
                    // muito autocontrole → Cap ou Tony
                    scores["Capitão América"] = scores["Capitão América"]!! + 2
                    scores["Homem de Ferro"]  = scores["Homem de Ferro"]!! + 1
                }
                autocontrole in 4..6 -> {
                    // médio → Thor (poder contido, mas às vezes impulsivo)
                    scores["Thor"] = scores["Thor"]!! + 1
                }
                else -> {
                    // baixo autocontrole → tendência Hulk
                    scores["Hulk"] = scores["Hulk"]!! + 2
                }
            }

            // Q4: poder preferido (defensivo quanto ao índice)
            when (spPoder.selectedItemPosition.coerceIn(0, spPoder.adapter.count - 1)) {
                0 -> scores["Thor"]            = scores["Thor"]!! + 2            // Mjölnir
                1 -> scores["Homem de Ferro"]  = scores["Homem de Ferro"]!! + 2  // Armadura
                2 -> scores["Capitão América"] = scores["Capitão América"]!! + 2 // Escudo
                3 -> scores["Hulk"]            = scores["Hulk"]!! + 2            // Força
            }

            val (heroi, pontuacao) = scores.maxByOrNull { it.value }!!
            val i = Intent(this, ResultActivity::class.java).apply {
                putExtra("NOME_USUARIO", nome)
                putExtra("HEROI", heroi)
                putExtra("PONTUACAO", pontuacao)
            }
            startActivity(i)
        }
    }
}
