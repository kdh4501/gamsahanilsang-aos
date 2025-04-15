package com.dhkim.gamsahanilsang.presentation.screen.detil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem

@Composable
fun DetailDialog(showDialog: Boolean, onDismiss: () -> Unit, item: GratitudeItem) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.7f)
                    .padding(top = 100.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column (
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.date,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.gratitudeText,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.weight(1f)  // 내용이 많아질 경우 공간을 차지하게 하기 위해서
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {

                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "수정")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "삭제")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}