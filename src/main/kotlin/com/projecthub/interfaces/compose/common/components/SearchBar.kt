package com.projecthub.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    initialQuery: String = ""
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { query ->
            searchQuery = query
            onSearch(query)
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        placeholder = {
            Text(placeholder)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            AnimatedVisibility(visible = searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        searchQuery = ""
                        onSearch("")
                        focusManager.clearFocus()
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(searchQuery)
                focusManager.clearFocus()
            }
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    )

    // Request focus when the search bar is first displayed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}