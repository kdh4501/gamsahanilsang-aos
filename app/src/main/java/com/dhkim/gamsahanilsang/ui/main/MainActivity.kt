package com.dhkim.gamsahanilsang.ui.main

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.data.database.AppDatabase
import com.dhkim.gamsahanilsang.data.repository.RoomGratitudeRepository
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.usecase.SaveGratitudeUseCase
import com.dhkim.gamsahanilsang.ui.adapter.GratitudeAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var editTextGratitude: EditText
    private lateinit var buttonSave: Button
    private lateinit var recyclerViewGratitude: RecyclerView
    private lateinit var adapter: GratitudeAdapter
    private lateinit var saveGratitudeUseCase: SaveGratitudeUseCase

    private val gratitudeDao by lazy { AppDatabase.getDatabase(this).gratitudeDao() }

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

        buttonSave.setOnClickListener {
            val gratitudeText = editTextGratitude.text.toString()
            if (gratitudeText.isNotBlank()) {
                saveGratitude(gratitudeText)
                showSaveAnimation()
                editTextGratitude.text.clear()
            }
        }

        loadGratitudes()
    }

    private fun showEditDialog(item: GratitudeItem) {
        val editText = EditText(this).apply {
            setText(item.gratitudeText)
        }

        AlertDialog.Builder(this)
            .setTitle("수정하기")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val updateText = editText.text.toString()
                updateGratitude(item.copy(gratitudeText = updateText))
            }
            .setNegativeButton("최소", null)
            .show()
    }

    private fun updateGratitude(item: GratitudeItem) {
        lifecycleScope.launch {
            saveGratitudeUseCase.update(item)
            loadGratitudes()
        }
    }

    private fun saveGratitude(text: String) {
        val item = GratitudeItem(gratitudeText = text)
        lifecycleScope.launch {
            saveGratitudeUseCase.execute(item)
            loadGratitudes()
            showSaveAnimation()
        }
    }

    private fun loadGratitudes() {
        lifecycleScope.launch {
            val gratitudeList = saveGratitudeUseCase.getAllGratitudes() // 모든 감사한 일 로드
            adapter.updateData(gratitudeList)
        }
    }

    private fun showSaveAnimation() {
        val animation = AlphaAnimation(0.2f, 1.0f)
        animation.duration = 500
        buttonSave.startAnimation(animation)
    }
}