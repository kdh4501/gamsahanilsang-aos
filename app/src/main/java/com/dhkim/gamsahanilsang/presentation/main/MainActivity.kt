package com.dhkim.gamsahanilsang.presentation.main

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.data.database.AppDatabase
import com.dhkim.gamsahanilsang.data.repository.RoomGratitudeRepository
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.usecase.SaveGratitudeUseCase
import com.dhkim.gamsahanilsang.presentation.adapter.GratitudeAdapter
import com.dhkim.gamsahanilsang.presentation.viewmodel.MainViewModel
import com.dhkim.gamsahanilsang.presentation.viewmodel.MainViewModelFactory
import com.tarkalabs.tarkaui.components.base.AvatarType

class MainActivity : ComponentActivity() {
    private val gratitudeDao by lazy { AppDatabase.getDatabase(this).gratitudeDao() }
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(saveGratitudeUseCase) }

    private lateinit var editTextGratitude: EditText
    private lateinit var buttonSave: Button
    private lateinit var recyclerViewGratitude: RecyclerView
    private lateinit var adapter: GratitudeAdapter
    private lateinit var saveGratitudeUseCase: SaveGratitudeUseCase

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveGratitudeUseCase = SaveGratitudeUseCase(RoomGratitudeRepository(gratitudeDao))

        setContent {
            GratitudeApp(viewModel)
        }

        viewModel.loadGratitudes()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    @Composable
    fun GratitudeApp(viewModel: MainViewModel) {
        var gratitudeText by remember { mutableStateOf("") }
        val gratitudeList by viewModel.gratitudeList.observeAsState(emptyList())

        Scaffold(
            topBar = {
                TopAppBar(title = { AvatarType.Text("Gratitude List") })
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    TextField(
                        value = gratitudeText,
                        onValueChange = { gratitudeText = it },
                        label = { Text("Enter gratitude") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (gratitudeText.isNotBlank()) {
                                viewModel.saveGratitude(gratitudeText)
                                gratitudeText = ""
                                hideKeyboard()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    GratitudeList(gratitudeList, viewModel)
                }
            }
        )
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    @Composable
    fun GratitudeList(gratitudeList: List<GratitudeItem>, viewModel: MainViewModel) {
        LazyColumn {
            itemsIndexed(gratitudeList) { index, item ->
                androidx.compose.material3.ListItem(
                    modifier = Modifier.fillMaxWidth(), // Modifier는 올바르게 전달
                    headlineContent = { Text(item.gratitudeText) }, // 'text' 대신 'headline' 사용
                    trailingContent = {
                        IconButton(onClick = { showEditDialog(item, viewModel) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                )
            }
        }
    }

    private fun showEditDialog(item: GratitudeItem, viewModel: MainViewModel) {
        val editText = EditText(this).apply {
            setText(item.gratitudeText)
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_button))
            .setView(editText)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val updateText = editText.text.toString()
                this.viewModel.updateGratitude(item.copy(gratitudeText = updateText))
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showSaveAnimation() {
        val animation = AlphaAnimation(0.2f, 1.0f)
        animation.duration = 500
        buttonSave.startAnimation(animation)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            imm.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    @Preview(showBackground = true)
    @Composable
    fun PreviewGratitudeApp() {
        GratitudeApp(viewModel)
    }
}