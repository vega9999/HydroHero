package com.example.hydrohero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydrohero.ui.theme.MainViewModel

@Composable
fun SocialScreen(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var postInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Social Feed",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = postInput,
                onValueChange = { postInput = it },
                label = { Text("Share your hydration journey") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (postInput.isNotBlank()) {
                        viewModel.addSocialPost(postInput)
                        postInput = ""
                    }
                }
            ) {
                Text("Post")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.shareProgress() }) {
            Text("Share Progress")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(uiState.socialFeed) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = post.content)
                        Text(
                            text = post.timestamp.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}