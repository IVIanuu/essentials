package essentials.ui.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

@Composable fun SectionTitleDescriptionIcon(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    first: Boolean = false,
    last: Boolean = false,
    selected: Boolean = false,
    tone: Tone = Tone.NEUTRAL,
    textColor: Color = sectionTextColorForTone(selected, tone),
    icon: ImageVector,
    verticalPadding: Dp = 8.dp,
) {
    SectionTitleDescription(
        title = title,
        description = description,
        first = first,
        last = last,
        selected = selected,
        tone = tone,
        textColor = textColor,
        contentEnd = {
            Icon(
                modifier = Modifier.padding(end = 6.dp),
                imageVector = icon,
                contentDescription = title,
                tint = textColor
            )
        },
        modifier = modifier,
        verticalPadding = verticalPadding,
    )
}


@Composable fun SectionTitleDescription(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    first: Boolean = false,
    last: Boolean = false,
    selected: Boolean = false,
    tone: Tone = Tone.NEUTRAL,
    textColor: Color = sectionTextColorForTone(selected, tone),
    contentStart: (@Composable () -> Unit)? = null,
    contentEnd: (@Composable () -> Unit)? = null,
    contentTop: (@Composable () -> Unit)? = null,
    contentBottom: (@Composable () -> Unit)? = null,
    verticalPadding: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    Section(
        first = first,
        last = last,
        onClick = onClick,
        onLongClick = onLongClick,
        selected = selected,
        tone = tone,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                top = verticalPadding, bottom = verticalPadding
            )
        ) {
            contentStart?.invoke()
            Column(
                modifier = Modifier
                    .padding(
                        start = 4.dp,
                        end = 10.dp
                    )
                    .weight(1f)
            ) {
                contentTop?.invoke()
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                )
                description?.let {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = textColor,
                        textAlign = TextAlign.Start,
                    )
                }
                contentBottom?.invoke()
            }
            contentEnd?.invoke()
        }
    }
}
