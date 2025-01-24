package com.example.hydrohero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.hydrohero.ui.theme.MainViewModel
import com.example.hydrohero.R

@Composable
fun SocialScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var postInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

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
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue)),
                ) {
                Text("Post")
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.shareProgress() },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue)),
        ) {
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