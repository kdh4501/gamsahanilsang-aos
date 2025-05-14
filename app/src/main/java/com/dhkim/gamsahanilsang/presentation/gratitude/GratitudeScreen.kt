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
import com.dhkim.gamsahanilsang.presentation.common.DialogManager
import com.dhkim.gamsahanilsang.presentation.common.components.EditDialogContent
import com.dhkim.gamsahanilsang.presentation.gratitude.components.GratitudeList
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import com.dhkim.gamsahanilsang.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratitudeScreen(
    viewModel: MainViewModel,
    paddingValues: PaddingValues,
    showInputArea: Boolean = false,
    onInputAreaHidden: () -> Unit,
    isSearchActive: Boolean = false
){
    var gratitudeText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<GratitudeItem?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val streak = uiState.streak
    val isStreakToastShown = uiState.isStreakToastShown

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    // 다이얼로그 관리자
    val dialogManager = remember { DialogManager(context) }

    // 현재 표시 중인 다이얼로 상태 구독
    val currentDialog by dialogManager.currentDialog.collectAsState()

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
        // 검색 모드가 아닐 때만 입력 영역 표시
        if (showInputArea && !isSearchActive) {
            GratitudeInputArea(
                gratitudeText = gratitudeText,
                onTextChange = { gratitudeText = it },
                onSave = {
                    if (gratitudeText.isNotBlank()) {
                        viewModel.saveGratitude(gratitudeText)
                        gratitudeText = ""
                        hideKeyboard()
                        onInputAreaHidden()
                    }
                },
                onClear = { gratitudeText = ""},
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 감사 목록 또는 검색 결과 표시
        GratitudeList(
            viewModel = viewModel,
            onItemClick = { item ->
                // DialogManager를 통해 다이얼로그 표시
                dialogManager.showEditDialog(
                    gratitudeItem = item,
                    onSave = { updatedItem ->
                        viewModel.updateGratitude(updatedItem)
                    },
                    onDelete = { itemToDelete ->
                        viewModel.deleteGratitude(itemToDelete)
                    }
                )
            },
            isSearchMode = isSearchActive // 검색 모드 전달
        )

        when (val dialog = currentDialog) {
            is DialogManager.DialogType.EditDialog -> {
                EditDialogContent(
                    gratitudeItem = dialog.gratitudeItem,
                    onSave = dialog.onSave,
                    onDelete = dialog.onDelete,
                    onDismiss = dialog.onDismiss
                )
            }
            // 다른 다이얼로그 타입 처리...
            else -> { /* 표시할 다이얼로그 없음 */ }
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