package com.seuapp.superheroes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private lateinit var tvStep: TextView
    private lateinit var groupQ1: View
    private lateinit var groupQ2: View
    private lateinit var groupQ3: View
    private lateinit var groupQ4: View
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button

    private lateinit var rgTraco: RadioGroup
    private lateinit var cbLideranca: CheckBox
    private lateinit var cbCompPaixao: CheckBox
    private lateinit var cbEstrategia: CheckBox
    private lateinit var cbHumor: CheckBox
    private lateinit var skCoragem: SeekBar
    private lateinit var tvCoragemValor: TextView
    private lateinit var spPoder: Spinner

    private var step = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        // Grupos/controles de navegação
        tvStep = findViewById(R.id.tvStep)
        groupQ1 = findViewById(R.id.groupQ1)
        groupQ2 = findViewById(R.id.groupQ2)
        groupQ3 = findViewById(R.id.groupQ3)
        groupQ4 = findViewById(R.id.groupQ4)
        btnBack = findViewById(R.id.btnBack)
        btnNext = findViewById(R.id.btnNext)

        // Inputs
        rgTraco = findViewById(R.id.rgTraco)
        cbLideranca = findViewById(R.id.cbLideranca)
        cbCompPaixao = findViewById(R.id.cbCompPaixao)
        cbEstrategia = findViewById(R.id.cbEstrategia)
        cbHumor = findViewById(R.id.cbHumor)
        skCoragem = findViewById(R.id.skCoragem)
        tvCoragemValor = findViewById(R.id.tvCoragemValor)
        spPoder = findViewById(R.id.spPoder)

        // Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.poderes_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPoder.adapter = adapter
        }
        if (spPoder.adapter != null && spPoder.adapter.count > 0) {
            spPoder.setSelection(0, false)
        }

        // SeekBar
        tvCoragemValor.text = getString(R.string.coragem_valor, skCoragem.progress)
        skCoragem.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvCoragemValor.text = getString(R.string.coragem_valor, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Navegação
        btnBack.setOnClickListener {
            if (step > 0) {
                step--
                updateStepUI()
            }
        }

        btnNext.setOnClickListener {
            if (!validateCurrentStep()) return@setOnClickListener
            if (step < 3) {
                step++
                updateStepUI()
            } else {
                // calcular resultados e ir para a tela de resultado
                computeAndGo()
            }
        }

        updateStepUI() // inicia na etapa 0 (Q1)
    }

    private fun updateStepUI() {
        // Mostrar apenas o grupo da etapa atual
        groupQ1.visibility = if (step == 0) View.VISIBLE else View.GONE
        groupQ2.visibility = if (step == 1) View.VISIBLE else View.GONE
        groupQ3.visibility = if (step == 2) View.VISIBLE else View.GONE
        groupQ4.visibility = if (step == 3) View.VISIBLE else View.GONE

        // Atualizar indicador de passo
        tvStep.text = getString(R.string.step_label, step + 1)

        // Bt
        btnBack.isEnabled = step > 0
        btnNext.text = if (step == 3) getString(R.string.btn_ver_resultado) else getString(R.string.btn_next)
    }

    private fun validateCurrentStep(): Boolean {
        return when (step) {
            0 -> { // Q1
                if (rgTraco.checkedRadioButtonId == -1) {
                    Toast.makeText(this, getString(R.string.error_q1), Toast.LENGTH_SHORT).show()
                    false
                } else true
            }
            1 -> true // Q2 sem obrigatoriedade
            2 -> true // Q3 sem obrigatoriedade
            3 -> { // Q4
                val ok = spPoder.adapter != null && spPoder.adapter.count > 0
                if (!ok) Toast.makeText(this, getString(R.string.error_q4), Toast.LENGTH_SHORT).show()
                ok
            }
            else -> true
        }
    }

    private fun computeAndGo() {
        val nome = intent.getStringExtra("NOME_USUARIO") ?: getString(R.string.app_name)

        val scores = mutableMapOf(
            "Thor" to 0,
            "Homem de Ferro" to 0,
            "Capitão América" to 0,
            "Hulk" to 0
        )

        // Q1
        when (rgTraco.checkedRadioButtonId) {
            R.id.rbInteligencia -> scores["Homem de Ferro"] = scores["Homem de Ferro"]!! + 2
            R.id.rbForca        -> scores["Hulk"] = scores["Hulk"]!! + 2
            R.id.rbCoragem      -> scores["Capitão América"] = scores["Capitão América"]!! + 2
            R.id.rbAgilidade    -> scores["Thor"] = scores["Thor"]!! + 2
        }

        // Q2
        if (cbLideranca.isChecked)   scores["Capitão América"] = scores["Capitão América"]!! + 1
        if (cbCompPaixao.isChecked)  scores["Capitão América"] = scores["Capitão América"]!! + 1
        if (cbEstrategia.isChecked)  scores["Homem de Ferro"]  = scores["Homem de Ferro"]!! + 1
        if (cbHumor.isChecked)       scores["Thor"]            = scores["Thor"]!! + 1

        // Q3: autocontrole
        val autocontrole = skCoragem.progress
        when {
            autocontrole >= 7 -> {
                scores["Capitão América"] = scores["Capitão América"]!! + 2
                scores["Homem de Ferro"]  = scores["Homem de Ferro"]!! + 1
            }
            autocontrole in 4..6 -> {
                scores["Thor"] = scores["Thor"]!! + 1
            }
            else -> {
                scores["Hulk"] = scores["Hulk"]!! + 2
            }
        }

        // Q4
        when (spPoder.selectedItemPosition.coerceIn(0, spPoder.adapter.count - 1)) {
            0 -> scores["Thor"]            = scores["Thor"]!! + 2  // Mjölnir
            1 -> scores["Homem de Ferro"]  = scores["Homem de Ferro"]!! + 2  // Armadura
            2 -> scores["Capitão América"] = scores["Capitão América"]!! + 2  // Escudo
            3 -> scores["Hulk"]            = scores["Hulk"]!! + 2  // Força
        }

        val (heroi, pontuacao) = scores.maxByOrNull { it.value }!!

        val i = Intent(this, ResultActivity::class.java).apply {
            putExtra("NOME_USUARIO", nome)
            putExtra("HEROI", heroi)
            putExtra("PONTUACAO", pontuacao)

            val traitIdx = when (rgTraco.checkedRadioButtonId) {
                R.id.rbInteligencia -> 0
                R.id.rbForca        -> 1
                R.id.rbCoragem      -> 2
                R.id.rbAgilidade    -> 3
                else -> -1
            }
            putExtra("TRAIT_INDEX", traitIdx)
            putExtra("VAL_LIDERANCA", cbLideranca.isChecked)
            putExtra("VAL_COMPAIXAO", cbCompPaixao.isChecked)
            putExtra("VAL_ESTRATEGIA", cbEstrategia.isChecked)
            putExtra("VAL_HUMOR", cbHumor.isChecked)
            putExtra("AUTOCONTROLE", skCoragem.progress)
            putExtra("PODER_INDEX", spPoder.selectedItemPosition.coerceIn(0, spPoder.adapter.count - 1))
        }
        startActivity(i)
    }
}
