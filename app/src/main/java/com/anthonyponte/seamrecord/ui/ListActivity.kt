package com.anthonyponte.seamrecord.ui

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anthonyponte.seamrecord.R
import com.anthonyponte.seamrecord.viewmodel.Record
import com.anthonyponte.seamrecord.SeamRecordApp
import com.anthonyponte.seamrecord.databinding.ActivityListBinding
import com.anthonyponte.seamrecord.viewmodel.RecordViewModel
import com.anthonyponte.seamrecord.viewmodel.RoomViewModel
import com.anthonyponte.seamrecord.viewmodel.RecordViewModelFactory
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.idanatz.oneadapter.OneAdapter
import com.idanatz.oneadapter.external.event_hooks.ClickEventHook
import com.idanatz.oneadapter.external.modules.EmptinessModule
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.external.modules.ItemSelectionModule
import com.idanatz.oneadapter.external.modules.ItemSelectionModuleConfig
import com.idanatz.oneadapter.external.states.SelectionState
import com.idanatz.oneadapter.external.states.SelectionStateConfig
import java.text.DateFormat
import java.util.*

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private lateinit var oneAdapter: OneAdapter
    private val roomModel: RoomViewModel by viewModels {
        RecordViewModelFactory((application as SeamRecordApp).repository)
    }
    private var colorBackground: Int = 0
    private var colorSurface: Int = 0
    private var colorPrimary: Int = 0
    private var colorPrimaryVariant: Int = 0
    private var visibility: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListBinding.inflate(layoutInflater)
        val root = binding.root

        setContentView(root)
        setSupportActionBar(binding.listToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        colorBackground = MaterialColors.getColor(root, android.R.attr.colorBackground)
        colorSurface = MaterialColors.getColor(root, R.attr.colorSurface)
        colorPrimary = MaterialColors.getColor(root, R.attr.colorPrimary)
        colorPrimaryVariant = MaterialColors.getColor(root, R.attr.colorPrimaryVariant)

        binding.listContent.recycler.addItemDecoration(
            DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL
            )
        )

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent: Intent? = result.data
                    val inspection =
                        intent?.getSerializableExtra(MainActivity.RECORD_VALUE) as Record

                    roomModel.insert(inspection)


                }
            }

        binding.fab.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            launcher.launch(intent)
        }

        oneAdapter = OneAdapter(binding.listContent.recycler) {
            itemModules += RecordModule()
            emptinessModule = EmptinessModuleImpl()
            itemSelectionModule = RecordSelectionModuleImpl()
        }

        roomModel.getAll.observe(this, { records ->
            oneAdapter.setItems(records)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.eliminar)?.isVisible = visibility
        menu?.findItem(R.id.eliminar_todo)?.isVisible = visibility
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                oneAdapter.modules.itemSelectionModule?.actions?.clearSelection()
                return true
            }

            R.id.eliminar -> {
                val seleccionados =
                    oneAdapter.modules.itemSelectionModule?.actions?.getSelectedItems()?.size

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.eliminar_seleccionados, seleccionados))
                builder.setMessage(
                    getString(
                        R.string.eliminar_registros_seleccionados,
                        seleccionados
                    )
                )
                builder.setPositiveButton(
                    R.string.eliminar
                ) { dialog, _ ->

                    val selected =
                        oneAdapter.modules.itemSelectionModule?.actions?.getSelectedItems() as List<Any>

                    val iterator = selected.iterator()

                    while (iterator.hasNext()) {
                        val record = iterator.next() as Record
                        roomModel.delete(record)
                    }

                    oneAdapter.modules.itemSelectionModule?.actions?.removeSelectedItems()
                    dialog.dismiss()

                    Snackbar.make(
                        binding.root,
                        getString(R.string.registros_seleccionados_eliminados, seleccionados),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                builder.setNegativeButton(
                    R.string.cancelar
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create()
                builder.show()

                return true
            }

            R.id.eliminar_todo -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.eliminar_todos))
                builder.setMessage(getString(R.string.eliminar_todos_registros))
                builder.setPositiveButton(
                    R.string.eliminar_todo
                ) { dialog, _ ->
                    roomModel.deleteAll()

                    oneAdapter.modules.itemSelectionModule?.actions?.removeSelectedItems()
                    dialog.dismiss()

                    Snackbar.make(
                        binding.root,
                        getString(R.string.registros_eliminados),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                builder.setNegativeButton(
                    R.string.cancelar
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create()
                builder.show()

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class RecordModule : ItemModule<Record>() {
        init {
            config {
                layoutResource = R.layout.item
            }

            onBind { model, viewBinder, metadata ->
                val clItem = viewBinder.findViewById<ConstraintLayout>(R.id.clItem)
                val ivRecord = viewBinder.findViewById<ImageView>(R.id.ivRecord)
                val ivCheck = viewBinder.findViewById<ImageView>(R.id.ivCheck)
                val tvFecha = viewBinder.findViewById<TextView>(R.id.tvFecha)
                val tvHora = viewBinder.findViewById<TextView>(R.id.tvHora)
                clItem.setBackgroundColor(if (metadata.isSelected) colorSurface else colorBackground)
                ivRecord.visibility = if (metadata.isSelected) View.INVISIBLE else View.VISIBLE
                ivCheck.visibility = if (metadata.isSelected) View.VISIBLE else View.INVISIBLE
                tvFecha.text = DateFormat.getDateInstance().format(Date.from(model.fechaCreacion))
                tvHora.text = DateFormat.getTimeInstance().format(Date.from(model.fechaCreacion))
            }

            eventHooks += ClickEventHook<Record>().apply {
                onClick { model, _, _ ->
                    val intent = Intent(this@ListActivity, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.RECORD_VALUE, model)
                    startActivity(intent)
                }
            }

            states += SelectionState<Record>().apply {
                config {
                    selectionTrigger = SelectionStateConfig.SelectionTrigger.LongClick
                }

                onSelected { _, _ ->
                }
            }
        }
    }

    class EmptinessModuleImpl : EmptinessModule() {
        init {
            config {
                layoutResource = R.layout.content_empty
            }
        }
    }

    private inner class RecordSelectionModuleImpl : ItemSelectionModule() {
        init {
            config {
                selectionType = ItemSelectionModuleConfig.SelectionType.Multiple
            }

            onStartSelection {
                supportActionBar?.setBackgroundDrawable(ColorDrawable(colorPrimaryVariant))
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                invalidateOptionsMenu()
                visibility = true
            }

            onUpdateSelection { selectedCount ->
                if (oneAdapter.modules.itemSelectionModule?.actions?.isSelectionActive() == true) {
                    supportActionBar?.title = selectedCount.toString()
                }
            }

            onEndSelection {
                supportActionBar?.title = getString(R.string.app_name)
                supportActionBar?.setBackgroundDrawable(ColorDrawable(colorPrimary))
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                invalidateOptionsMenu()
                visibility = false
            }
        }
    }
}