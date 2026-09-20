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
fun WikiCreateScreen(
    paddingValues: PaddingValues,
    repository: WikiRepository,
    onBack: () -> Unit,
    onCreated: (Wiki) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf("")
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
                error = ""
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

        if (error.isNotEmpty()) {
            Text(
                text = error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                if (name.trim().isEmpty()) {
                    error = "Wiki名を入力してください。"
                    return@Button
                }

                val wiki = repository.create(
                    name = name.trim(),
                    description = description.trim()
                )

                onCreated(wiki)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Wikiを作成")
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("キャンセル")
        }
    }
}
