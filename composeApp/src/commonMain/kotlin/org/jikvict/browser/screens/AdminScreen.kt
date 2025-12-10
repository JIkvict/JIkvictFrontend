package org.jikvict.browser.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.IlluminatingText
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.responsive.adaptiveValue
import kotlin.reflect.KClass

@Composable
fun AdminScreenComposable(scope: DefaultScreenScope) = with(scope) {
    val isBig = adaptiveValue(small = false, medium = false, large = true)
    val navHostController = LocalNavController.current
    if (!isBig) {
        val textSize = adaptiveValue(small = 20.sp, medium = 35.sp, large = 0.sp)
        Box(
            modifier = Modifier.fitContentToScreen(),
            contentAlignment = Alignment.Center,
        ) {
            IlluminatingText(
                "Admin panel is for big screens only",
                textSize,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shadowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            )
        }
        return@with
    }
    Box(
        modifier = Modifier.fitContentToScreen(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PanelSelector(icon = Icons.Default.Group, name = "Assignment Groups") {
                with(navHostController) {
                    UserGroupScreen.navigateTo()
                }
            }
            PanelSelector(icon = Icons.Default.Task, name = "Assignments") {
                with(navHostController) {
                    AssignmentsAdminScreen.navigateTo()
                }
            }
            PanelSelector(icon = Icons.Default.Person, name = "Students") {
                with(navHostController) {
                    StudentsScreen().navigateTo()
                }
            }
        }
    }
}

@Composable
fun PanelSelector(
    icon: ImageVector,
    name: String,
    onNavigate: () -> Unit = {}
) {
    Card(
        modifier = Modifier.width(400.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onNavigate,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Serializable
@SerialName("admin")
object AdminScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { AdminScreenComposable(it) }
    override val requiredRoles: List<String>
        get() = listOf("TEACHER")
}

object AdminScreenRouterRegistrar : ScreenRouterRegistrar<AdminScreen> {
    override val screen: KClass<AdminScreen>
        get() = AdminScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen = AdminScreen
}

object AdminScreenRegistrar : ScreenRegistrar<AdminScreen> by createRegistrar()


@Composable
@Preview
@Preview(widthDp = 1920, heightDp = 1080)
@Preview(widthDp = 1000, heightDp = 1080)
fun AdminScreenPreview() {
    DefaultPreview(false) {
        AdminScreenComposable(it)
    }
}