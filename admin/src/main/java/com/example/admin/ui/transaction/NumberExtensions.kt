package com.example.admin.ui.transaction

import java.text.DecimalFormat

fun Double.formatThousands(): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(this)
}
