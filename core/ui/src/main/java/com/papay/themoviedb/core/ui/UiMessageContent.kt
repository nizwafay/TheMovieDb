package com.papay.themoviedb.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UiMessageContent(
    message: UiMessage,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes actionLabelRes: Int = R.string.action_retry
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(message.titleRes),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(message.descriptionRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (message.canRetry) {
            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = onActionClick
            ) {
                Text(text = stringResource(actionLabelRes))
            }
        }
    }
}
