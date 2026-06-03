package com.papay.themoviedb.core.ui

import androidx.annotation.StringRes

data class UiMessage(
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    val canRetry: Boolean = true,
    val id: Long = UiMessageId.next()
)
