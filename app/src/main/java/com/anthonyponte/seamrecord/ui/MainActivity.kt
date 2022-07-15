package com.anthonyponte.seamrecord.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import com.anthonyponte.seamrecord.R
import com.anthonyponte.seamrecord.viewmodel.Record
import com.anthonyponte.seamrecord.databinding.ActivityMainBinding
import com.google.android.material.color.MaterialColors
import java.text.DecimalFormat
import java.time.Instant

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var colorOnBackground: Int = 0
    private var colorError: Int = 0
    private var colorSuccess: Int = 0
    private var colorWarning: Int = 0

    companion object {
        const val RECORD_VALUE = "RECORD_VALUE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        colorOnBackground = MaterialColors.getColor(root, R.attr.colorOnBackground)
        colorError = MaterialColors.getColor(root, R.attr.colorError)
        colorSuccess = MaterialColors.getColor(root, R.attr.colorSuccess)
        colorWarning = MaterialColors.getColor(root, R.attr.colorWarning)

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        navigateUpTo(Intent(this@MainActivity, ListActivity::class.java))
                        return true
                    }

                    R.id.limpiar -> {
                        binding.mainContent.etEspesorCuerpo.text?.clear()
                        binding.mainContent.etEspesorTapa.text?.clear()
                        binding.mainContent.etGanchoCuerpo.text?.clear()
                        binding.mainContent.etGanchoTapa.text?.clear()
                        binding.mainContent.etAlturaCierre.text?.clear()
                        binding.mainContent.etEspesorCierre.text?.clear()

                        binding.mainContent.etEspesorCuerpo.requestFocusFromTouch()
                    }

                    R.id.guardar -> {
                        if (isNotEmpty()) {
                            val record = Record(
                                Instant.now(),
                                binding.mainContent.etEspesorCuerpo.text.toString().toDouble(),
                                binding.mainContent.etEspesorTapa.text.toString().toDouble(),
                                binding.mainContent.etGanchoCuerpo.text.toString().toDouble(),
                                binding.mainContent.etGanchoTapa.text.toString().toDouble(),
                                binding.mainContent.etAlturaCierre.text.toString().toDouble(),
                                binding.mainContent.etEspesorCierre.text.toString().toDouble(),
                                binding.mainContent.mainMeasures.tvTraslape.text.toString()
                                    .toDouble(),
                                binding.mainContent.mainMeasures.tvSuperposicion.text.toString()
                                    .toDouble(),
                                binding.mainContent.mainMeasures.tvPenetracion.text.toString()
                                    .toDouble(),
                                binding.mainContent.mainMeasures.tvEspacioLibre.text.toString()
                                    .toDouble(),
                                binding.mainContent.mainMeasures.tvCompacidad.text.toString()
                                    .toDouble()
                            )

                            val intent = Intent().apply {
                                putExtra(RECORD_VALUE, record)
                            }

                            setResult(RESULT_OK, intent)
                            finish()
                        } else {
                            finish()
                        }
                        return true
                    }
                    else -> true
                }
            }
        })

        binding.mainContent.etEspesorCuerpo.addTextChangedListener(watcher)
        binding.mainContent.etEspesorTapa.addTextChangedListener(watcher)
        binding.mainContent.etGanchoCuerpo.addTextChangedListener(watcher)
        binding.mainContent.etGanchoTapa.addTextChangedListener(watcher)
        binding.mainContent.etAlturaCierre.addTextChangedListener(watcher)
        binding.mainContent.etEspesorCierre.addTextChangedListener(watcher)
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            if (isNotEmpty()) {
                val espesorCuerpo = binding.mainContent.etEspesorCuerpo.text.toString().toDouble()
                val espesorTapa = binding.mainContent.etEspesorTapa.text.toString().toDouble()
                val ganchoCuerpo = binding.mainContent.etGanchoCuerpo.text.toString().toDouble()
                val ganchoTapa = binding.mainContent.etGanchoTapa.text.toString().toDouble()
                val alturaCierre = binding.mainContent.etAlturaCierre.text.toString().toDouble()
                val espesorCierre = binding.mainContent.etEspesorCierre.text.toString().toDouble()
                val traslape = traslape(ganchoTapa, ganchoCuerpo, espesorTapa, alturaCierre)
                val superposicion = superposicion(
                    ganchoTapa,
                    ganchoCuerpo,
                    espesorTapa,
                    alturaCierre,
                    espesorCuerpo
                )
                val penetracion =
                    penetracion(ganchoCuerpo, espesorCuerpo, alturaCierre, espesorTapa)
                val espacioLibre = espacioLibre(espesorCierre, espesorTapa, espesorCuerpo)
                val compacidad = compacidad(espesorCuerpo, espesorTapa, espesorCierre)

                binding.mainContent.mainMeasures.tvTraslape.text = format(traslape)
                binding.mainContent.mainMeasures.tvSuperposicion.text = format(superposicion)
                binding.mainContent.mainMeasures.tvPenetracion.text = format(penetracion)
                binding.mainContent.mainMeasures.tvEspacioLibre.text = format(espacioLibre)
                binding.mainContent.mainMeasures.tvCompacidad.text = format(compacidad)

                if (traslape >= 1) {
                    binding.mainContent.mainMeasures.tvTraslape.setTextColor(colorSuccess)
                } else if (traslape < 1 && traslape != 0.0) {
                    binding.mainContent.mainMeasures.tvTraslape.setTextColor(colorError)
                }

                if (superposicion >= 80) {
                    binding.mainContent.mainMeasures.tvSuperposicion.setTextColor(colorSuccess)
                } else if (superposicion < 80 && superposicion >= 45 && superposicion != 0.0) {
                    binding.mainContent.mainMeasures.tvSuperposicion.setTextColor(colorWarning)
                } else if (superposicion < 45 && superposicion != 0.0) {
                    binding.mainContent.mainMeasures.tvSuperposicion.setTextColor(colorError)
                }

                if (penetracion >= 95) {
                    binding.mainContent.mainMeasures.tvPenetracion.setTextColor(colorSuccess)
                } else if (penetracion < 95 && penetracion > 70) {
                    binding.mainContent.mainMeasures.tvPenetracion.setTextColor(colorWarning)
                } else if (penetracion <= 70 && penetracion != 0.0) {
                    binding.mainContent.mainMeasures.tvPenetracion.setTextColor(colorError)
                }

                if (espacioLibre > 0.19) {
                    binding.mainContent.mainMeasures.tvEspacioLibre.setTextColor(colorError)
                } else if (espacioLibre <= 0.19 && espacioLibre != 0.0) {
                    binding.mainContent.mainMeasures.tvEspacioLibre.setTextColor(colorSuccess)
                }

                if (compacidad >= 85) {
                    binding.mainContent.mainMeasures.tvCompacidad.setTextColor(colorSuccess)
                } else if (compacidad < 85 && compacidad >= 75) {
                    binding.mainContent.mainMeasures.tvCompacidad.setTextColor(colorWarning)
                } else if (compacidad < 75 && compacidad >= 1) {
                    binding.mainContent.mainMeasures.tvCompacidad.setTextColor(colorError)
                }
            } else {
                binding.mainContent.mainMeasures.tvTraslape.text = getString(R.string.cero)
                binding.mainContent.mainMeasures.tvSuperposicion.text = getString(R.string.cero)
                binding.mainContent.mainMeasures.tvPenetracion.text = getString(R.string.cero)
                binding.mainContent.mainMeasures.tvEspacioLibre.text = getString(R.string.cero)
                binding.mainContent.mainMeasures.tvCompacidad.text = getString(R.string.cero)
                binding.mainContent.mainMeasures.tvTraslape.setTextColor(colorOnBackground)
                binding.mainContent.mainMeasures.tvSuperposicion.setTextColor(colorOnBackground)
                binding.mainContent.mainMeasures.tvPenetracion.setTextColor(colorOnBackground)
                binding.mainContent.mainMeasures.tvEspacioLibre.setTextColor(colorOnBackground)
                binding.mainContent.mainMeasures.tvCompacidad.setTextColor(colorOnBackground)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    fun isNotEmpty(): Boolean {
        return binding.mainContent.etEspesorCuerpo.text.toString().isNotEmpty() &&
                binding.mainContent.etEspesorTapa.text.toString().isNotEmpty() &&
                binding.mainContent.etGanchoCuerpo.text.toString().isNotEmpty() &&
                binding.mainContent.etGanchoTapa.text.toString().isNotEmpty() &&
                binding.mainContent.etAlturaCierre.text.toString().isNotEmpty() &&
                binding.mainContent.etEspesorCierre.text.toString().isNotEmpty()
    }

    private fun traslape(
        ganchoTapa: Double,
        ganchoCuerpo: Double,
        espesorTapa: Double,
        alturaCierre: Double
    ): Double {
        return ganchoTapa + ganchoCuerpo + 1.1 * espesorTapa - alturaCierre
    }

    private fun superposicion(
        ganchoTapa: Double,
        ganchoCuerpo: Double,
        espesorTapa: Double,
        alturaCierre: Double,
        espesorCuerpo: Double
    ): Double {
        return ((ganchoTapa + ganchoCuerpo + 1.1 * espesorTapa - alturaCierre) / (alturaCierre - 1.1 * (2 * espesorTapa + espesorCuerpo))) * 100
    }

    private fun penetracion(
        ganchoCuerpo: Double,
        espesorCuerpo: Double,
        alturaCierre: Double,
        espesorTapa: Double
    ): Double {
        return ((ganchoCuerpo - (1.1 * espesorCuerpo)) * 100) / (alturaCierre - 1.1 * ((2 * espesorTapa) + espesorCuerpo))
    }

    private fun espacioLibre(
        espesorCierre: Double,
        espesorTapa: Double,
        espesorCuerpo: Double
    ): Double {
        return espesorCierre - (3 * espesorTapa + 2 * espesorCuerpo)
    }

    private fun compacidad(
        espesorCuerpo: Double,
        espesorTapa: Double,
        espesorCierre: Double
    ): Double {
        return ((2 * espesorCuerpo + 3 * espesorTapa) / espesorCierre) * 100
    }

    private fun format(num: Double): String {
        val df = DecimalFormat("#.###")
        return df.format(num)
    }
}