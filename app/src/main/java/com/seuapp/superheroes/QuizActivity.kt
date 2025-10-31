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

        ArrayAdapter.createFromResource(
            this,
            R.array.poderes_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPoder.adapter = adapter
        }

        skCoragem.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvCoragemValor.text = getString(R.string.coragem_valor, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnVerResultado.setOnClickListener {
            val nome = intent.getStringExtra("NOME_USUARIO") ?: getString(R.string.app_name)

            val scores = mutableMapOf(
                "Homem de Ferro" to 0,
                "Hulk" to 0,
                "Mulher-Maravilha" to 0,
                "Homem-Aranha" to 0
            )

            when (rgTraco.checkedRadioButtonId) {
                R.id.rbInteligencia -> scores["Homem de Ferro"] = scores["Homem de Ferro"]!! + 2
                R.id.rbForca -> scores["Hulk"] = scores["Hulk"]!! + 2
                R.id.rbCoragem -> scores["Mulher-Maravilha"] = scores["Mulher-Maravilha"]!! + 2
                R.id.rbAgilidade -> scores["Homem-Aranha"] = scores["Homem-Aranha"]!! + 2
            }

            if (cbLideranca.isChecked) scores["Homem de Ferro"] = scores["Homem de Ferro"]!! + 1
            if (cbCompPaixao.isChecked) scores["Mulher-Maravilha"] = scores["Mulher-Maravilha"]!! + 1
            if (cbEstrategia.isChecked) scores["Homem de Ferro"] = scores["Homem de Ferro"]!! + 1
            if (cbHumor.isChecked) scores["Homem-Aranha"] = scores["Homem-Aranha"]!! + 1

            val coragem = skCoragem.progress
            if (coragem >= 7) {
                scores["Mulher-Maravilha"] = scores["Mulher-Maravilha"]!! + 2
            } else if (coragem >= 4) {
                scores["Homem-Aranha"] = scores["Homem-Aranha"]!! + 1
            }

            when (spPoder.selectedItemPosition) {
                0 -> scores["Homem de Ferro"] = scores["Homem de Ferro"]!! + 2
                1 -> scores["Hulk"] = scores["Hulk"]!! + 2
                2 -> scores["Mulher-Maravilha"] = scores["Mulher-Maravilha"]!! + 2
                3 -> scores["Homem-Aranha"] = scores["Homem-Aranha"]!! + 2
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
