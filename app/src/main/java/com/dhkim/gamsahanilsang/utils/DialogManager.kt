package com.dhkim.gamsahanilsang.utils

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem

/**
 * 앱 전반에서 사용되는 다이얼로그를 관리하는 유틸리티 클래스
 */
class DialogManager(private val context: Context) {

    /**
     * 감사 항목 편집을 위한 다이얼로그를 표시합니다.
     *
     * @param item 편집할 감사 항목
     * @param onConfirm 편집 완료 후 호출될 콜백 (업데이트된 텍스트 전달)
     */
    fun showEditDialog(
        item: GratitudeItem,
        onConfirm: (String) -> Unit
    ) {
        val editText = EditText(context).apply {
            setText(item.gratitudeText)
        }

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.edit_button))
            .setView(editText)
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                val updateText = editText.text.toString()
                onConfirm(updateText)
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    /**
     * 삭제 확인 다이얼로그를 표시합니다.
     *
     * @param onConfirm 삭제 확인 시 호출될 콜백
     */
    fun showDeleteConfirmationDialog(onConfirm: () -> Unit) {
//        AlertDialog.Builder(context)
//            .setTitle(context.getString(R.string.delete_title))
//            .setMessage(context.getString(R.string.delete_confirmation))
//            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
//                onConfirm()
//            }
//            .setNegativeButton(context.getString(R.string.cancel), null)
//            .show()
    }

    /**
     * 정보 메시지 다이얼로그를 표시합니다.
     *
     * @param title 다이얼로그 제목
     * @param message 다이얼로그 메시지
     */
    fun showInfoDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.ok), null)
            .show()
    }
}