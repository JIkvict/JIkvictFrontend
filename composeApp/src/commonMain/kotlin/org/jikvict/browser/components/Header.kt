package org.jikvict.browser.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jikvict.browser.LocalNavController
import org.jikvict.browser.auth.SessionManager
import org.jikvict.browser.auth.TokenHolder
import org.jikvict.browser.auth.toJwt
import org.jikvict.browser.constant.LocalAppColors
import org.jikvict.browser.icons.myiconpack.AdminIcon
import org.jikvict.browser.icons.myiconpack.Ijlogo
import org.jikvict.browser.screens.AdminScreen
import org.jikvict.browser.screens.HelpScreen
import org.jikvict.browser.screens.LoginScreen
import org.jikvict.browser.screens.MakeJarScreen
import org.jikvict.browser.screens.ProfileScreen
import org.jikvict.browser.screens.TasksScreen
import org.jikvict.browser.screens.navigateTo
import org.jikvict.browser.util.DefaultPreview
import org.jikvict.browser.util.LocalThemeSwitcherProvider
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Header(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val themeSwitcher = LocalThemeSwitcherProvider.current

    val darkPurple = LocalAppColors.current.Purple6
    val lightPurple = LocalAppColors.current.Purple3
    val theme by themeSwitcher.isDark
    val purple = if (theme) darkPurple else lightPurple

    val moonTint = if (!theme) Color.Black else Color.Unspecified
    val roles = TokenHolder.token()?.toJwt()?.roles.orEmpty()
    val version by TokenHolder.tokenVersion

    val sessionManager = koinInject<SessionManager>()
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState()
    key(version) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .then(modifier),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(35.dp),
                ) {
                    IconComponent(Ijlogo, hoverable = true, tint = purple, onClick = {
                        context(navController) {
                            MakeJarScreen.navigateTo()
                        }
                    })

                    AnimatedIconComponent(
                        animationPath = "files/code-animation.json",
                        hoverable = true,
                        onClick = {
                            context(navController) {
                                TasksScreen().navigateTo()
                            }
                        },
                        animationType = AnimationType.TOGGLE,
                        tint = MaterialTheme.colorScheme.onSurface,
                        speed = 2f,
                    )
                    if (AdminScreen.requiredRoles.any { it in roles }) {
                        IconComponent(AdminIcon(), hoverable = true, onClick = {
                            context(navController) {
                                AdminScreen.navigateTo()
                            }
                        })
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(35.dp),
                ) {
                    IconComponent(
                        Icons.AutoMirrored.Outlined.HelpOutline,
                        hoverable = true,
                        tint = MaterialTheme.colorScheme.onBackground,
                        onClick = {
                            context(navController) {
                                HelpScreen.navigateTo()
                            }
                        })

                    AnimatedIconComponent(
                        animationPath = "files/sun-moon.json",
                        hoverable = true,
                        tint = moonTint,
                        initialProgress = if (theme) 0f else 1f,
                        onEnd = {
                            themeSwitcher.switchTheme()
                        },
                        animationType = AnimationType.TOGGLE,
                        speed = 1f,
                    )

                    AnimatedIconComponent(
                        animationPath = "files/user-animation.json",
                        hoverable = true,
                        tint = MaterialTheme.colorScheme.onSurface,
                        animationType = AnimationType.ONCE_FORWARD,
                        speed = 1f,
                        onClick = {
                            context(navController) {
                                if (isLoggedIn) {
                                    ProfileScreen.navigateTo()
                                } else {
                                    LoginScreen.navigateTo()
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 1920, heightDp = 1080)
@Composable
private fun HeaderPreview() {
    DefaultPreview(true) {
        Header()
    }
}
