package com.dhkim.gamsahanilsang.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem

/**
 * Compose 환경에서 사용할 수 있는 다이얼로그 관리자
 */
class DialogManager {
    /**
     * 감사 항목 편집을 위한 다이얼로그 상태와 컴포넌트
     */
    @Composable
    fun EditDialog(
        item: GratitudeItem?,
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit
    ) {
        if (item != null) {
            var editedText by remember { mutableStateOf(item.gratitudeText) }

            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(stringResource(R.string.edit_button)) },
                text = {
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.input_hint)) }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (editedText.isNotBlank()) {
                                onConfirm(editedText)
                            }
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }

    /**
     * 삭제 확인 다이얼로그 컴포넌트
     */
    @Composable
    fun DeleteConfirmationDialog(
        show: Boolean,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        if (show) {
            /*
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(stringResource(R.string.delete_title)) },
                text = { Text(stringResource(R.string.delete_confirmation)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
             */
        }
    }

    /**
     * 정보 메시지 다이얼로그 컴포넌트
     */
    @Composable
    fun InfoDialog(
        show: Boolean,
        title: String,
        message: String,
        onDismiss: () -> Unit
    ) {
        if (show) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(title) },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }
    }
}
