package com.example.calculator.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalculatorButton(
    label: String,
    size: Dp,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val interaction = Modifier
        .combinedClickable(
            onClick = onClick,
            onLongClick = { onLongClick?.invoke() }
        )
    val isEqual = label == "="
    val buttonColor = if (isEqual) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
    val textColor = if (isEqual) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(size)
            .background(
                color = buttonColor,
                shape = RoundedCornerShape(15.dp)
            )
            .then(interaction),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}