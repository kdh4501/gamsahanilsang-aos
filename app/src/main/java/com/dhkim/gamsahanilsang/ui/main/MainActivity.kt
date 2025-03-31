package com.dhkim.gamsahanilsang.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.data.repository.InMemoryGratitudeRepository
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository
import com.dhkim.gamsahanilsang.domain.usecase.SaveGratitudeUseCase
import com.dhkim.gamsahanilsang.ui.adapter.GratitudeAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var editTextGratitude: EditText
    private lateinit var buttonSave: Button
    private lateinit var recyclerViewGratitude: RecyclerView
    private lateinit var adapter: GratitudeAdapter
    private lateinit var saveGratitudeUseCase: SaveGratitudeUseCase
    private lateinit var gratitudeRepository: GratitudeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gratitudeRepository = InMemoryGratitudeRepository()
        saveGratitudeUseCase = SaveGratitudeUseCase(gratitudeRepository)

        editTextGratitude = findViewById(R.id.editTextGratitude)
        buttonSave = findViewById(R.id.buttonSave)
        recyclerViewGratitude = findViewById(R.id.recyclerViewGratitude)

        adapter = GratitudeAdapter(gratitudeRepository.getAllGratitudes())
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
    }

    private fun saveGratitude(text: String) {
        saveGratitudeUseCase.execute(text)
        val updatedList = gratitudeRepository.getAllGratitudes()
        Log.d("MainActivity", "Updated List: $updatedList") // 데이터 확인
        adapter.updateData(gratitudeRepository.getAllGratitudes())
        recyclerViewGratitude.scrollToPosition(adapter.itemCount - 1)
    }

    private fun showSaveAnimation() {
        val animation = AlphaAnimation(0.2f, 1.0f)
        animation.duration = 500
        buttonSave.startAnimation(animation)
    }
}