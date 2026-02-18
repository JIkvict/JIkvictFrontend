package org.jikvict.browser.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import jikvictfrontend.composeapp.generated.resources.Res
import jikvictfrontend.composeapp.generated.resources.configure_source
import jikvictfrontend.composeapp.generated.resources.gradle
import jikvictfrontend.composeapp.generated.resources.gradle_settings
import jikvictfrontend.composeapp.generated.resources.link_project
import jikvictfrontend.composeapp.generated.resources.open_gradle
import jikvictfrontend.composeapp.generated.resources.plugins
import jikvictfrontend.composeapp.generated.resources.project_structure
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jikvict.browser.components.Alert
import org.jikvict.browser.components.AlertType
import org.jikvict.browser.components.DefaultScreenScope
import org.jikvict.browser.components.PlatformSelectorComponent
import org.jikvict.browser.util.DefaultPreview
import kotlin.reflect.KClass

@Serializable
@SerialName("help")
data object HelpScreen : NavigableScreen {
    override val largeScreen: @Composable ((DefaultScreenScope) -> Unit)
        get() = { HelpScreenComposable(it) }
}

object HelpScreenRouterRegistrar : ScreenRouterRegistrar<HelpScreen> {
    override val screen: KClass<HelpScreen>
        get() = HelpScreen::class

    override fun constructScreen(params: Map<String, String?>): NavigableScreen = HelpScreen
}

object HelpScreenRegistrar : ScreenRegistrar<HelpScreen> by createRegistrar()

@Composable
fun HelpScreenComposable(scope: DefaultScreenScope) = with(scope) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = scope.screenHeight)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Important instructions",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HelpSection("Downloading") {
            SelectionContainer {
                Markdown(
                    """
                                After downloading you will receive a zip file named something like `assignment.zip`
                                
                                You should unzip it using your preferred tools.
                                
                                Now you have the next file structure:
                                ```
                                task<id>
                                    | default-structure
                                    |   | ... - all needed files and folders
                                ```
                                
                            """.trimIndent()
                )
            }
            Alert(AlertType.Important, "Do not change this structure!")
        }
        Spacer(modifier = Modifier.height(16.dp))


        HelpSection("Getting Started") {
            Markdown(
                """
                    First, after you open an assignment, you should open `build.gradle.kts`
                """.trimIndent()
            )

            Photo(
                Res.drawable.open_gradle,
                caption = "Click \"Link Gradle Project\"",
            )

            Markdown(
                """
                    Next you should select `default-structure` folder.
                """.trimIndent()
            )

            Photo(
                Res.drawable.link_project,
                caption = "Select \"default-structure\" folder",
            )

            Markdown(
                """
                    Now we need to tell IntelliJ where our sources are located.
                """.trimIndent()
            )

            Photo(
                Res.drawable.configure_source,
                caption = "Select \"java\" folder, click right mouse button and mark it as \"Sources Root\"",
            )

            Markdown(
                """
                    The next steps are aimed to configure right JDK version
                """.trimIndent()
            )

            Markdown("Click double shift and search for \"Project Structure\". Open it")

            Photo(
                Res.drawable.project_structure,
                caption = "Select JDK 21. You can download it, if you don't have it. Any vendor JDK will work, for example OpenJDK."
            )

            Markdown(
                """
                Now open `Gradle`, it looks like an elephant. If you don't have it on your toolbars, use Double shift and search for it.
            """.trimIndent()
            )

            Photo(
                Res.drawable.gradle,
                caption = "Click \"Gradle Settings\""
            )

            Photo(
                Res.drawable.gradle_settings,
                caption = "Select JDK 21. You can use the same JDK, as in Project Structure."
            )

            Alert(
                AlertType.Note,
                "Gradle is a build automation tool, used to manage dependencies and build processes in the project. It is build on JVM, this is why it also needs JDK."
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        var selectedPlatformIndex by remember { mutableStateOf(0) }

        HelpSection("Submitting") {
            PlatformSelectorComponent(
                selectedPlatformIndex = selectedPlatformIndex,
                onPlatformSelected = { selectedPlatformIndex = it },
                winContent = {
                    Markdown(
                        """
                            Take the whole `task` folder and zip it using your default archiver.
                        """.trimIndent()
                    )
                },
                macContent = {
                    SelectionContainer {
                        Markdown(
                            """
                            As Apple are very cool, their default compress tool is not working properly.
                            
                            Use this command instead:
                            ```
                            zip -r -X -0 solution.zip task<taskId>
                            ```
                        """.trimIndent()
                        )
                    }
                },
                linuxContent = {
                    Markdown(
                        """
                            As different Linux distros may have different compression tools,
                            
                            I can't guarantee that they will 100% work.
                            
                            However we have not found any issues.
                            
                            In case of any problems try to use the most common implementation of `zip`
                        """.trimIndent()
                    )
                }
            )
            Markdown(
                """
                Now you can submit your solution.
                
                Simply *drag and drop* the zip file to the assignment window or use *upload* button.
            """.trimIndent()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Alert(AlertType.Note, "Remove your tmp files and build directories before submitting.")
            Alert(
                AlertType.Important,
                "Make sure the structure is like task<id>.zip and inside is the default-structure folder."
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HelpSection("Use plugin") {
            Markdown(
                """
                You can use the IntelliJ plugin to make your life easier!
                
                It will automatically download and open assignments for you.
                
                It also will automatically zip and submit you solution, so no more frustration while submitting!
                
                Go to [releases](https://github.com/JIkvict/JIkvictIdeaPlugin/releases) and download the latest version.
            """.trimIndent()
            )

            Markdown("""
                Open IntelliJ settings (double shift and search settings).
                Find plugins section.
            """.trimIndent())

            Photo(
                Res.drawable.plugins,
                caption = "Simply select downloaded plugin and restart IntelliJ."
            )

            SelectionContainer {
                Markdown("""
                Now you can open plugin by Double shift and search for `Jikvict`.
                After first use, you will find it's icon in the toolbar.
            """.trimIndent())
            }
        }
    }
}

interface HelpSectionScope {
    @Composable
    fun Photo(
        painter: Painter,
        caption: String,
        modifier: Modifier = Modifier
    )

    @Composable
    fun Photo(
        resource: DrawableResource,
        caption: String,
        modifier: Modifier = Modifier
    )
}

private object HelpSectionScopeImpl : HelpSectionScope {
    @Composable
    override fun Photo(
        painter: Painter,
        caption: String,
        modifier: Modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = caption,
                modifier = modifier
                    .widthIn(max = 1600.dp)
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    @Composable
    override fun Photo(
        resource: DrawableResource,
        caption: String,
        modifier: Modifier
    ) {
        Photo(painterResource(resource), caption, modifier)
    }
}

@Composable
fun HelpSection(title: String, content: @Composable HelpSectionScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                title,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .padding(16.dp),
            ) {
                Column {
                    HelpSectionScopeImpl.content()
                }
            }
        }
    }

}

@Composable
fun HelpContent(platformName: String) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        HelpSection(
            title = "Installation on $platformName",
            description = "Download the installer for $platformName and follow the on-screen instructions. Make sure you have the necessary permissions."
        )
        HelpSection(
            title = "Configuration",
            description = "After installation, launch the application and go to Settings to configure your user profile and preferences specific to $platformName environment."
        )
        HelpSection(
            title = "Troubleshooting",
            description = "If you encounter any issues on $platformName, please check the logs in the application directory or contact support."
        )
    }
}

@Composable
fun HelpSection(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Preview(widthDp = 1980, heightDp = 1080)
@Composable
fun HelpScreenPreview() {
    DefaultPreview {
        HelpScreenComposable(it)
    }
}