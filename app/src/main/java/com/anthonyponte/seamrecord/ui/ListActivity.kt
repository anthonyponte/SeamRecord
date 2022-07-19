package com.anthonyponte.seamrecord.ui

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anthonyponte.seamrecord.R
import com.anthonyponte.seamrecord.viewmodel.Record
import com.anthonyponte.seamrecord.SeamRecordApp
import com.anthonyponte.seamrecord.databinding.ActivityListBinding
import com.anthonyponte.seamrecord.viewmodel.RoomViewModel
import com.anthonyponte.seamrecord.viewmodel.RecordViewModelFactory
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var adapter: OneAdapter
    private val model: RoomViewModel by viewModels {
        RecordViewModelFactory((application as SeamRecordApp).repository)
    }
    private var colorBackground: Int = 0
    private var colorSurface: Int = 0
    private var colorPrimary: Int = 0
    private var colorPrimaryVariant: Int = 0
    private var visibleDelete: Boolean = false
    private var visibleDeleteAll: Boolean = false

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

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_list, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.eliminar)?.isVisible = visibleDelete
                menu.findItem(R.id.eliminar_todo)?.isVisible = visibleDeleteAll
                super.onPrepareMenu(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        adapter.modules.itemSelectionModule?.actions?.clearSelection()
                        return true
                    }

                    R.id.eliminar -> {
                        val seleccionados =
                            adapter.modules.itemSelectionModule?.actions?.getSelectedItems()?.size

                        MaterialAlertDialogBuilder(this@ListActivity)
                            .setTitle(getString(R.string.eliminar_seleccionados, seleccionados))
                            .setMessage(
                                getString(
                                    R.string.eliminar_registros_seleccionados,
                                    seleccionados
                                )
                            )
                            .setPositiveButton(
                                R.string.eliminar
                            ) { dialog, _ ->
                                val selected =
                                    adapter.modules.itemSelectionModule?.actions?.getSelectedItems() as List<Any>

                                val iterator = selected.iterator()

                                lateinit var ids: List<Long>
                                while (iterator.hasNext()) {
                                    val record = iterator.next() as Record
                                    ids = listOf(record.id)
                                }
                                model.delete(ids)


                                adapter.modules.itemSelectionModule?.actions?.removeSelectedItems()
                                dialog.dismiss()

                                Snackbar.make(
                                    binding.root,
                                    getString(
                                        R.string.registros_seleccionados_eliminados,
                                        seleccionados
                                    ),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            .setNeutralButton(
                                R.string.cancelar
                            ) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()

                        return true
                    }

                    R.id.eliminar_todo -> {
                        MaterialAlertDialogBuilder(this@ListActivity)
                            .setTitle(getString(R.string.eliminar_todos))
                            .setMessage(getString(R.string.eliminar_todos_registros))
                            .setPositiveButton(
                                R.string.eliminar_todo
                            ) { dialog, _ ->
                                model.deleteAll()

                                adapter.modules.itemSelectionModule?.actions?.removeSelectedItems()
                                dialog.dismiss()

                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.registros_eliminados),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            .setNeutralButton(
                                R.string.cancelar
                            ) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()

                        return true
                    }

                    else -> true
                }
            }
        }, this)

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

                    model.insert(inspection)
                }
            }

        binding.fab.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            launcher.launch(intent)
        }

        adapter = OneAdapter(binding.listContent.recycler) {
            itemModules += RecordModule()
            emptinessModule = EmptinessModuleImpl()
            itemSelectionModule = RecordSelectionModuleImpl()
        }

        model.getAll.observe(this) { records ->
            records?.let {
                visibleDeleteAll = records.isNotEmpty()
                invalidateOptionsMenu()

                println("size " + records.size)
                println("isNotEmpty " + records.isNotEmpty())
                println("isEmpty " + records.isEmpty())

                adapter.setItems(records)
            }
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

                visibleDelete = true
                visibleDeleteAll = false
                invalidateOptionsMenu()
            }

            onUpdateSelection { selectedCount ->
                if (adapter.modules.itemSelectionModule?.actions?.isSelectionActive() == true) {
                    supportActionBar?.title = selectedCount.toString()
                }
            }

            onEndSelection {
                supportActionBar?.title = getString(R.string.app_name)
                supportActionBar?.setBackgroundDrawable(ColorDrawable(colorPrimary))
                supportActionBar?.setDisplayHomeAsUpEnabled(false)

                visibleDelete = false
                visibleDeleteAll = true
                invalidateOptionsMenu()
            }
        }
    }
}