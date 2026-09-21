package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minax333.wikihub.data.Wiki
import com.minax333.wikihub.data.WikiRepository

@Composable
fun WikiSettingsScreen(
    wiki: Wiki,
    wikiRepository: WikiRepository,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    onCancel: () -> Unit
) {
    var name by remember(wiki.id) {
        mutableStateOf(wiki.name)
    }

    var description by remember(wiki.id) {
        mutableStateOf(wiki.description)
    }

    var showDeleteConfirm by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Wiki名")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text("説明")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                if (name.trim().isNotEmpty()) {
                    wikiRepository.update(
                        id = wiki.id,
                        name = name.trim(),
                        description = description.trim()
                    )

                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存")
        }

        Button(
            onClick = {
                showDeleteConfirm = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wikiを削除")
        }

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("キャンセル")
        }

        if (showDeleteConfirm) {

            Text(
                "本当にこのWikiを削除しますか？"
            )

            Button(
                onClick = {
                    wikiRepository.delete(wiki.id)
                    onDeleted()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("削除する")
            }

            TextButton(
                onClick = {
                    showDeleteConfirm = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("戻る")
            }
        }
    }
}
