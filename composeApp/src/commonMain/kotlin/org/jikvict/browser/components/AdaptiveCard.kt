package org.jikvict.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jikvict.browser.util.responsive.adaptiveComponent

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SmallCard(
    modifier: Modifier = Modifier,
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    colors: CardColors =
        CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        ),
) {
    ElevatedCard(
        modifier = modifier,
        elevation = elevation,
        colors = colors,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                            RoundedCornerShape(24.dp),
                        ).fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                primaryContent()
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(colors.containerColor, RoundedCornerShape(24.dp))
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                secondaryContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LargeCard(
    modifier: Modifier = Modifier,
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    colors: CardColors =
        CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        ),
) {
    ElevatedCard(
        modifier = modifier,
        elevation = elevation,
        colors = colors,
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                            RoundedCornerShape(24.dp),
                        ).fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                primaryContent()
            }
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colors.containerColor, RoundedCornerShape(24.dp))
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                secondaryContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AdaptiveCard(
    modifier: Modifier = Modifier,
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    colors: CardColors =
        CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        ),
) = adaptiveComponent(
    small =
        {
            SmallCard(
                modifier = modifier,
                primaryContent = primaryContent,
                secondaryContent = secondaryContent,
                elevation = elevation,
                colors = colors,
            )
        },
    large = {
        LargeCard(
            modifier = modifier,
            primaryContent = primaryContent,
            secondaryContent = secondaryContent,
            elevation = elevation,
            colors = colors,
        )
    },
)
