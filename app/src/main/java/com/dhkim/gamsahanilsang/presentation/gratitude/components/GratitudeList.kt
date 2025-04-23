package com.dhkim.gamsahanilsang.presentation.gratitude.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel

@Composable
fun GratitudeList(
    viewModel: MainViewModel,
    onItemClick: (GratitudeItem) -> Unit,
    onEditClick: ((GratitudeItem) -> Unit)? = null
) {
    val groupedItems by viewModel.groupedGratitudes.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn {
        groupedItems.forEach { (date, gratitudeItems) ->
            item {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            items(gratitudeItems) { item ->
                GratitudeListItem(
                    item = item,
                    onClick = { onItemClick(item) },
                    onEditClick = onEditClick
                )
            }
        }
    }
}

@Composable
fun GratitudeListItem(
    item: GratitudeItem,
    onClick: () -> Unit,
    onEditClick: ((GratitudeItem) -> Unit)?
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = { Text(item.gratitudeText) },
        trailingContent = {
            if (onEditClick != null) {
                IconButton(onClick = { onEditClick(item) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
            }
        }
    )
}