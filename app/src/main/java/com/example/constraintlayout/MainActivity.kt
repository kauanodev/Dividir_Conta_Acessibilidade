package com.example.constraintlayout

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity(), TextWatcher, TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var edtConta: EditText
    private lateinit var edtPessoas: EditText
    private lateinit var txtResultado: TextView
    private var ttsSucess: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        edtConta = findViewById(R.id.edtConta)
        edtPessoas = findViewById(R.id.edtPessoas)
        txtResultado = findViewById(R.id.txtResultado)

        // Add text change listeners
        edtConta.addTextChangedListener(this)
        edtPessoas.addTextChangedListener(this)

        // Initialize TTS engine
        tts = TextToSpeech(this, this)

        // Button setup
        val b: Button = findViewById(R.id.btFalar)
        b.setOnLongClickListener {
            val it_Youtube = Intent(android.content.Intent.ACTION_VIEW)
            it_Youtube.data = Uri.parse("https://www.youtube.com/")
            startActivity(it_Youtube)
            true
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.d("PDM24", "Antes de mudar")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d("PDM24", "Mudando")
    }

    override fun afterTextChanged(s: Editable?) {
        Log.d("PDM24", "Depois de mudar")
        calculateAndDisplay()
    }

    private fun calculateAndDisplay() {
        val valorText = edtConta.text.toString()
        val pessoasText = edtPessoas.text.toString()

        if (valorText.isNotEmpty() && pessoasText.isNotEmpty()) {
            try {
                val valor = valorText.toDouble()
                val pessoas = pessoasText.toInt()

                if (pessoas > 0) {
                    val resultado = valor / pessoas
                    val formattedResult = String.format(Locale.getDefault(), "%.2f", resultado)
                    txtResultado.text = "R$ $formattedResult por pessoa"

                    // Speak the result
                    if (ttsSucess) {
                        tts.speak("O valor por pessoa é $formattedResult reais", TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                } else {
                    txtResultado.text = "Número de pessoas deve ser maior que zero"
                }
            } catch (e: NumberFormatException) {
                txtResultado.text = "Valores inválidos"
            }
        }
    }

    fun clickFalar(v: View) {
        val resultText = txtResultado.text.toString()
        if (resultText.isNotEmpty() && !resultText.startsWith("Valores") && !resultText.startsWith("Número")) {
            // Share the result
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, resultText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } else {
            Toast.makeText(this, "Calcule um valor válido primeiro", Toast.LENGTH_SHORT).show()
        }
    }

    fun clickYoutube(v: View) {
        val it_Youtube = Intent(Intent.ACTION_VIEW)
        it_Youtube.data = Uri.parse("https://www.youtube.com/")
        startActivity(it_Youtube)
    }

    fun abrirShareActivity(v: View) {
        val intent = Intent(this, ShareActivity::class.java).apply {
            putExtra("nome", edtConta.text.toString())
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        // Release TTS engine resources
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // TTS engine is initialized successfully
            tts.setLanguage(Locale("pt", "BR"))
            ttsSucess=true
            Log.d("PDM23","Sucesso na Inicialização")
        } else {
            // TTS engine failed to initialize
            Log.e("PDM23", "Failed to initialize TTS engine.")
            ttsSucess=false
        }
    }
}