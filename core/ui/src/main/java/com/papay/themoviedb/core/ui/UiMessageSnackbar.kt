package com.papay.themoviedb.core.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource

@Composable
fun rememberUiMessageSnackbarHostState(
    message: UiMessage?,
    showMessage: Boolean,
    actionLabel: String?,
    onAction: () -> Unit
): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnAction = rememberUpdatedState(onAction)
    val snackbarText = message?.let {
        "${stringResource(it.titleRes)}\n${stringResource(it.descriptionRes)}"
    }

    LaunchedEffect(message?.id, showMessage) {
        if (message != null && snackbarText != null && showMessage) {
            val result = snackbarHostState.showSnackbar(
                message = snackbarText,
                actionLabel = actionLabel.takeIf { message.canRetry },
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                currentOnAction.value()
            }
        }
    }

    return snackbarHostState
}
