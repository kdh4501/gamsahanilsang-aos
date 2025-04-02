package com.dhkim.gamsahanilsang.presentation.main

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.data.database.AppDatabase
import com.dhkim.gamsahanilsang.data.repository.RoomGratitudeRepository
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.usecase.SaveGratitudeUseCase
import com.dhkim.gamsahanilsang.presentation.adapter.GratitudeAdapter
import com.dhkim.gamsahanilsang.presentation.viewmodel.MainViewModel
import com.dhkim.gamsahanilsang.presentation.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var editTextGratitude: EditText
    private lateinit var buttonSave: Button
    private lateinit var recyclerViewGratitude: RecyclerView
    private lateinit var adapter: GratitudeAdapter
    private lateinit var saveGratitudeUseCase: SaveGratitudeUseCase

    private val gratitudeDao by lazy { AppDatabase.getDatabase(this).gratitudeDao() }
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(saveGratitudeUseCase) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = RoomGratitudeRepository(gratitudeDao)
        saveGratitudeUseCase = SaveGratitudeUseCase(repository)

        editTextGratitude = findViewById(R.id.editTextGratitude)
        buttonSave = findViewById(R.id.buttonSave)
        recyclerViewGratitude = findViewById(R.id.recyclerViewGratitude)

        adapter = GratitudeAdapter(emptyList()) { item ->
            showEditDialog(item)
        }
        recyclerViewGratitude.layoutManager = LinearLayoutManager(this)
        recyclerViewGratitude.adapter = adapter

        viewModel.gratitudeList.observe(this, Observer { gratitudeList ->
            adapter.updateData(gratitudeList)
        })

        buttonSave.setOnClickListener {
            val gratitudeText = editTextGratitude.text.toString()
            if (gratitudeText.isNotBlank()) {
                viewModel.saveGratitude(gratitudeText)
                viewModel.loadGratitudes()
                showSaveAnimation()
                editTextGratitude.text.clear()
            }
        }

        viewModel.loadGratitudes()
    }

    private fun showEditDialog(item: GratitudeItem) {
        val editText = EditText(this).apply {
            setText(item.gratitudeText)
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_button))
            .setView(editText)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val updateText = editText.text.toString()
                viewModel.updateGratitude(item.copy(gratitudeText = updateText))
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showSaveAnimation() {
        val animation = AlphaAnimation(0.2f, 1.0f)
        animation.duration = 500
        buttonSave.startAnimation(animation)
    }
}