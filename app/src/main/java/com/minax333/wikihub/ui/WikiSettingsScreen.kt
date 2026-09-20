package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
    paddingValues: PaddingValues,
    wiki: Wiki,
    repository: WikiRepository,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    onBack: () -> Unit
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
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = {
                Text("Wiki名")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            label = {
                Text("説明")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            minLines = 4
        )

        Button(
            onClick = {
                if (name.trim().isNotEmpty()) {
                    repository.update(
                        id = wiki.id,
                        name = name.trim(),
                        description = description.trim()
                    )

                    onSaved()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("変更を保存")
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("戻る")
        }

        Button(
            onClick = {
                showDeleteConfirm = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Wikiを削除")
        }

        if (showDeleteConfirm) {
            Text(
                text = "このWikiを削除しますか？",
                modifier = Modifier.padding(top = 16.dp)
            )

            Button(
                onClick = {
                    repository.delete(wiki.id)
                    onDeleted()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("削除する")
            }

            Button(
                onClick = {
                    showDeleteConfirm = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("キャンセル")
            }
        }
    }
}
