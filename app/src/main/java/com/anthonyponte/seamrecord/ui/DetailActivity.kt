package com.anthonyponte.seamrecord.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.anthonyponte.seamrecord.R
import com.anthonyponte.seamrecord.viewmodel.Record
import com.anthonyponte.seamrecord.databinding.ActivityDetailBinding
import com.anthonyponte.seamrecord.viewmodel.RecordViewModel
import com.google.android.material.color.MaterialColors
import java.text.DateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val recordModel: RecordViewModel by viewModels()

    companion object {
        const val RECORD_VALUE = "RECORD_VALUE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val colorError = MaterialColors.getColor(root, R.attr.colorError)
        val colorSucces = MaterialColors.getColor(root, R.attr.colorSucces)
        val colorWarning = MaterialColors.getColor(root, R.attr.colorWarning)

        if (savedInstanceState == null) {
            val record = intent.getSerializableExtra(RECORD_VALUE) as Record
            recordModel.postRecord(record)
        }

        recordModel.record.observe(this, { record ->
            supportActionBar?.title =
                DateFormat.getDateTimeInstance().format(Date.from(record.fechaCreacion))
            binding.detailContent.tvEspesorCuerpo.text = record.espesorCuerpo.toString()
            binding.detailContent.tvEspesorTapa.text = record.espesorTapa.toString()
            binding.detailContent.tvGanchoCuerpo.text = record.ganchoCuerpo.toString()
            binding.detailContent.tvGanchoTapa.text = record.ganchoTapa.toString()
            binding.detailContent.tvAlturaCierre.text = record.alturaCierre.toString()
            binding.detailContent.tvEspesorCierre.text = record.espesorCierre.toString()
            binding.detailContent.detailMeasures.tvTraslape.text = record.traslape.toString()
            binding.detailContent.detailMeasures.tvSuperposicion.text =
                record.superposicion.toString()
            binding.detailContent.detailMeasures.tvPenetracion.text = record.penetracion.toString()
            binding.detailContent.detailMeasures.tvEspacioLibre.text =
                record.espacioLibre.toString()
            binding.detailContent.detailMeasures.tvCompacidad.text = record.compacidad.toString()

            if (record.traslape >= 1) {
                binding.detailContent.detailMeasures.tvTraslape.setTextColor(colorSucces)
            } else if (record.traslape < 1 && record.traslape != 0.0) {
                binding.detailContent.detailMeasures.tvTraslape.setTextColor(colorError)
            }

            if (record.superposicion >= 80) {
                binding.detailContent.detailMeasures.tvSuperposicion.setTextColor(colorSucces)
            } else if (record.superposicion < 80 && record.superposicion >= 45 && record.superposicion != 0.0) {
                binding.detailContent.detailMeasures.tvSuperposicion.setTextColor(colorWarning)
            } else if (record.superposicion < 45 && record.superposicion != 0.0) {
                binding.detailContent.detailMeasures.tvSuperposicion.setTextColor(colorError)
            }

            if (record.penetracion >= 95) {
                binding.detailContent.detailMeasures.tvPenetracion.setTextColor(colorSucces)
            } else if (record.penetracion < 95 && record.penetracion > 70) {
                binding.detailContent.detailMeasures.tvPenetracion.setTextColor(colorWarning)
            } else if (record.penetracion <= 70 && record.penetracion != 0.0) {
                binding.detailContent.detailMeasures.tvPenetracion.setTextColor(colorError)
            }

            if (record.espacioLibre > 0.19) {
                binding.detailContent.detailMeasures.tvEspacioLibre.setTextColor(colorError)
            } else if (record.espacioLibre <= 0.19 && record.espacioLibre != 0.0) {
                binding.detailContent.detailMeasures.tvEspacioLibre.setTextColor(colorSucces)
            }

            if (record.compacidad >= 85) {
                binding.detailContent.detailMeasures.tvCompacidad.setTextColor(colorSucces)
            } else if (record.compacidad < 85 && record.compacidad >= 75) {
                binding.detailContent.detailMeasures.tvCompacidad.setTextColor(colorWarning)
            } else if (record.compacidad < 75 && record.compacidad >= 1) {
                binding.detailContent.detailMeasures.tvCompacidad.setTextColor(colorError)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, ListActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}