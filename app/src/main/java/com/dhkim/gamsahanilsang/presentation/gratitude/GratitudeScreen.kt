package com.dhkim.gamsahanilsang.presentation.gratitude

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.presentation.gratitude.components.GratitudeList
import com.dhkim.gamsahanilsang.presentation.gratitude.dialogs.DetailDialog
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import com.dhkim.gamsahanilsang.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratitudeScreen(
    viewModel: MainViewModel,
    paddingValues: PaddingValues,
    showInputArea: Boolean = false,
    onInputAreaHidden: () -> Unit
){
    var gratitudeText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<GratitudeItem?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val streak = uiState.streak
    val isStreakToastShown = uiState.isStreakToastShown

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    fun hideKeyboard() {
        keyboardController?.hide()
    }

    val todayStr = remember {
        DateUtils.formatToday()
    }

    LaunchedEffect(key1 = todayStr) {
        if (!isStreakToastShown) {
            Toast.makeText(context,
                context.getString(R.string.streak_toast_message, streak), Toast.LENGTH_SHORT).show()
            viewModel.markStreakToastShown()
        }
    }

    uiState.error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        if (showInputArea) {
            GratitudeInputArea(
                gratitudeText = gratitudeText,
                onTextChange = { gratitudeText = it },
                onSave = {
                    if (gratitudeText.isNotBlank()) {
                        viewModel.saveGratitude(gratitudeText)
                        gratitudeText = ""
                        hideKeyboard()
                        onInputAreaHidden
                    }
                },
                onClear = { gratitudeText = ""}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GratitudeList(
            viewModel = viewModel,
            onItemClick = { item ->
                selectedItem = item
                showDialog = true
            }
        )

        if (showDialog && selectedItem != null) {
            DetailDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false; selectedItem = null },
                item = selectedItem!!,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun GratitudeInputArea(
    gratitudeText: String,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = gratitudeText,
        onValueChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        label = { Text(stringResource(R.string.input_hint)) },
        trailingIcon = {
            IconButton(onClick = onClear) {
                Icon(Icons.Filled.Clear, contentDescription = "Clear")
            }
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.save_button))
    }
}