package com.mckimquyen.atomicPeriodicTable.model

import androidx.annotation.Keep

@Keep
data class Dictionary(
    val category: String,
    val heading: String,
    val text: String,
    val wiki: String
)