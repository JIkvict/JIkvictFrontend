package org.jikvict.browser.constant

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

interface ThemeColors {
    // Gray shades
    val Gray1: Color
    val Gray2: Color
    val Gray3: Color
    val Gray4: Color
    val Gray5: Color
    val Gray6: Color
    val Gray7: Color
    val Gray8: Color
    val Gray9: Color
    val Gray10: Color
    val Gray11: Color
    val Gray12: Color
    val Gray13: Color
    val Gray14: Color

    // Blue shades
    val Blue1: Color
    val Blue2: Color
    val Blue3: Color
    val Blue4: Color
    val Blue5: Color
    val Blue6: Color
    val Blue7: Color
    val Blue8: Color
    val Blue9: Color
    val Blue10: Color
    val Blue11: Color

    // Green shades
    val Green1: Color
    val Green2: Color
    val Green3: Color
    val Green4: Color
    val Green5: Color
    val Green6: Color
    val Green7: Color
    val Green8: Color
    val Green9: Color
    val Green10: Color
    val Green11: Color

    // Yellow shades
    val Yellow1: Color
    val Yellow2: Color
    val Yellow3: Color
    val Yellow4: Color
    val Yellow5: Color
    val Yellow6: Color
    val Yellow7: Color
    val Yellow8: Color
    val Yellow9: Color
    val Yellow10: Color
    val Yellow11: Color

    // Red shades
    val Red1: Color
    val Red2: Color
    val Red3: Color
    val Red4: Color
    val Red5: Color
    val Red6: Color
    val Red7: Color
    val Red8: Color
    val Red9: Color
    val Red10: Color
    val Red11: Color

    // Orange shades
    val Orange1: Color
    val Orange2: Color
    val Orange3: Color
    val Orange4: Color
    val Orange5: Color
    val Orange6: Color
    val Orange7: Color
    val Orange8: Color
    val Orange9: Color
    val Orange10: Color
    val Orange11: Color

    // Purple shades
    val Purple1: Color
    val Purple2: Color
    val Purple3: Color
    val Purple4: Color
    val Purple5: Color
    val Purple6: Color
    val Purple7: Color
    val Purple8: Color
    val Purple9: Color
    val Purple10: Color
    val Purple11: Color

    // Teal shades
    val Teal1: Color
    val Teal2: Color
    val Teal3: Color
    val Teal4: Color
    val Teal5: Color
    val Teal6: Color
    val Teal7: Color
    val Teal8: Color
    val Teal9: Color
    val Teal10: Color
    val Teal11: Color
}

// Make DarkColors implement the interface
object DarkColors : ThemeColors {
    // Gray
    override val Gray1 = Color(0xFF1E1F22)  // Main background (editor)
    override val Gray2 = Color(0xFF2B2D30)  // Secondary background
    override val Gray3 = Color(0xFF393B40)  // Lines & separators
    override val Gray4 = Color(0xFF43454A)  // General icons: Fill
    override val Gray5 = Color(0xFF4E5157)
    override val Gray6 = Color(0xFF5A5D63)
    override val Gray7 = Color(0xFF6F737A)  // Secondary text & icons
    override val Gray8 = Color(0xFF868A91)  // Text on lighter backgrounds
    override val Gray9 = Color(0xFF9DA0A8)
    override val Gray10 = Color(0xFFB4B8BF)
    override val Gray11 = Color(0xFFCED0D6)
    override val Gray12 = Color(0xFFDFE1E5)
    override val Gray13 = Color(0xFFF0F1F2)
    override val Gray14 = Color(0xFFFFFFFF)  // White

    // Blue
    override val Blue1 = Color(0xFF25324D)
    override val Blue2 = Color(0xFF2E436E)
    override val Blue3 = Color(0xFF35538F)
    override val Blue4 = Color(0xFF375FAD)
    override val Blue5 = Color(0xFF366ACF)
    override val Blue6 = Color(0xFF3574F0)  // Primary
    override val Blue7 = Color(0xFF467FF2)
    override val Blue8 = Color(0xFF548AF7)
    override val Blue9 = Color(0xFF6B9BFA)
    override val Blue10 = Color(0xFF83ACFC)
    override val Blue11 = Color(0xFF99BBFF)

    // Green
    override val Green1 = Color(0xFF253627)
    override val Green2 = Color(0xFF375239)
    override val Green3 = Color(0xFF436946)
    override val Green4 = Color(0xFF4E8052)
    override val Green5 = Color(0xFF57965C)
    override val Green6 = Color(0xFF5FAD65)  // Primary
    override val Green7 = Color(0xFF73BD79)
    override val Green8 = Color(0xFF89CC8E)
    override val Green9 = Color(0xFFA0DBA5)
    override val Green10 = Color(0xFFB9EBBD)
    override val Green11 = Color(0xFFD4FAD7)

    // Yellow
    override val Yellow1 = Color(0xFF3D3223)
    override val Yellow2 = Color(0xFF5E4D33)
    override val Yellow3 = Color(0xFF826A41)
    override val Yellow4 = Color(0xFF9E814A)
    override val Yellow5 = Color(0xFFBA9752)
    override val Yellow6 = Color(0xFFD6AE58)
    override val Yellow7 = Color(0xFFF2C55C)  // Primary
    override val Yellow8 = Color(0xFFF5D273)
    override val Yellow9 = Color(0xFFF7DE8B)
    override val Yellow10 = Color(0xFFFCEBA4)
    override val Yellow11 = Color(0xFFFFF6BD)

    // Red
    override val Red1 = Color(0xFF402929)
    override val Red2 = Color(0xFF5E3838)
    override val Red3 = Color(0xFF7A4343)
    override val Red4 = Color(0xFF9C4E4E)
    override val Red5 = Color(0xFFBD5757)
    override val Red6 = Color(0xFFDB5C5C)  // Primary
    override val Red7 = Color(0xFFE37774)
    override val Red8 = Color(0xFFEB938D)
    override val Red9 = Color(0xFFF2B1AA)
    override val Red10 = Color(0xFFF7CCC6)
    override val Red11 = Color(0xFFFAE3DE)

    // Orange
    override val Orange1 = Color(0xFF45322B)
    override val Orange2 = Color(0xFF614438)
    override val Orange3 = Color(0xFF825845)
    override val Orange4 = Color(0xFFA36B4E)
    override val Orange5 = Color(0xFFC77D55)
    override val Orange6 = Color(0xFFE08855)  // Primary
    override val Orange7 = Color(0xFFE5986C)
    override val Orange8 = Color(0xFFF0AC81)
    override val Orange9 = Color(0xFFF5BD98)
    override val Orange10 = Color(0xFFFACEAF)
    override val Orange11 = Color(0xFFFFDFC7)

    // Purple
    override val Purple1 = Color(0xFF2F2936)
    override val Purple2 = Color(0xFF433358)
    override val Purple3 = Color(0xFF583D7A)
    override val Purple4 = Color(0xFF6C469C)
    override val Purple5 = Color(0xFF8150BE)
    override val Purple6 = Color(0xFF955AE0)  // Primary
    override val Purple7 = Color(0xFFA571E6)
    override val Purple8 = Color(0xFFB589EC)
    override val Purple9 = Color(0xFFC4A0F3)
    override val Purple10 = Color(0xFFD4B8F9)
    override val Purple11 = Color(0xFFE4CEFF)

    // Teal
    override val Teal1 = Color(0xFF1D3838)
    override val Teal2 = Color(0xFF1E4D4A)
    override val Teal3 = Color(0xFF20635D)
    override val Teal4 = Color(0xFF21786F)
    override val Teal5 = Color(0xFF238E82)
    override val Teal6 = Color(0xFF24A394)  // Primary
    override val Teal7 = Color(0xFF42B1A4)
    override val Teal8 = Color(0xFF60C0B5)
    override val Teal9 = Color(0xFF7DCEC5)
    override val Teal10 = Color(0xFF9BDDD6)
    override val Teal11 = Color(0xFFB9EBE6)
}
// New LightColors implementation
object LightColors : ThemeColors {
    // Gray
    override val Gray1 = Color(0xFF000000)  // Primary text
    override val Gray2 = Color(0xFF27282E)  // Dark background
    override val Gray3 = Color(0xFF383A42)
    override val Gray4 = Color(0xFF494B57)
    override val Gray5 = Color(0xFF5A5D6B)
    override val Gray6 = Color(0xFF6C707E)  // Status bar text, General icons
    override val Gray7 = Color(0xFF818594)  // Secondary text
    override val Gray8 = Color(0xFFA8ADBD)  // Disabled text & placeholders
    override val Gray9 = Color(0xFFC9CCD6)
    override val Gray10 = Color(0xFFD3D5DB)
    override val Gray11 = Color(0xFFDFE1E5)  // Inactive selection
    override val Gray12 = Color(0xFFEBECF0)  // Lines & separators
    override val Gray13 = Color(0xFFF7F8FA)  // Secondary background
    override val Gray14 = Color(0xFFFFFFFF)  // Main background

    // Blue
    override val Blue1 = Color(0xFF2E55A3)
    override val Blue2 = Color(0xFF315FBD)  // Link, Button: Pressed
    override val Blue3 = Color(0xFF3369D6)  // Button: Hovered
    override val Blue4 = Color(0xFF3574F0)  // Primary, Button: Default
    override val Blue5 = Color(0xFF4682FA)  // Solid icons
    override val Blue6 = Color(0xFF588CF3)
    override val Blue7 = Color(0xFF709CF5)
    override val Blue8 = Color(0xFF88ADF7)  // Diff changed: Stripe mark
    override val Blue9 = Color(0xFFA0BDF8)
    override val Blue10 = Color(0xFFC2D6FC)  // Banner stroke, Diff changed
    override val Blue11 = Color(0xFFD4E2FF)  // Selection active

    // Green
    override val Green1 = Color(0xFF1E6B33)
    override val Green2 = Color(0xFF1F7536)  // Button: Pressed, Text
    override val Green3 = Color(0xFF1F8039)  // Button: Hovered
    override val Green4 = Color(0xFF208A3C)  // Primary, Button: Default
    override val Green5 = Color(0xFF369650)  // Outline icons
    override val Green6 = Color(0xFF55A76A)  // Solid icons & modifiers
    override val Green7 = Color(0xFF89C398)  // Diff added: Stripe mark
    override val Green8 = Color(0xFFAFDBB8)
    override val Green9 = Color(0xFFC5E5CC)  // Diff added: Stroke & Word
    override val Green10 = Color(0xFFE6F7E9)  // Test root in project tree
    override val Green11 = Color(0xFFF2FCF3)  // General icons: Fill

    // Yellow
    override val Yellow1 = Color(0xFFA46704)  // Text
    override val Yellow2 = Color(0xFFC27D04)  // General icons: Stroke
    override val Yellow3 = Color(0xFFDF9303)
    override val Yellow4 = Color(0xFFFFAF0F)  // Primary, Solid & Outline icons
    override val Yellow5 = Color(0xFFFDBD3D)
    override val Yellow6 = Color(0xFFFED277)  // Banner: Stroke
    override val Yellow7 = Color(0xFFFEE6B1)
    override val Yellow8 = Color(0xFFFFF1D1)
    override val Yellow9 = Color(0xFFFFF6DE)  // Target root in project tree
    override val Yellow10 = Color(0xFFFFFAEB)  // General icons: Fill
    override val Yellow11 = Color(0xFFFFFAEB)  // General icons: Fill

    // Red
    override val Red1 = Color(0xFFAD2B38)
    override val Red2 = Color(0xFFBC303E)  // Button: Pressed
    override val Red3 = Color(0xFFCC3645)  // Button: Hovered, Validation text
    override val Red4 = Color(0xFFDB3B4B)  // Primary, Button: Default
    override val Red5 = Color(0xFFE55765)  // Solid icons & modifiers
    override val Red6 = Color(0xFFE46A76)
    override val Red7 = Color(0xFFED99A1)
    override val Red8 = Color(0xFFF2B6BB)  // Diff conflict: Stripe mark
    override val Red9 = Color(0xFFFAD4D8)  // Banner stroke, Diff conflict
    override val Red10 = Color(0xFFFFF2F3)  // Diff conflict: Fill
    override val Red11 = Color(0xFFFFF7F7)  // General icons: Fill

    // Orange
    override val Orange1 = Color(0xFFA14916)
    override val Orange2 = Color(0xFFB85516)  // Text
    override val Orange3 = Color(0xFFCE6117)
    override val Orange4 = Color(0xFFE56D17)  // Primary
    override val Orange5 = Color(0xFFEC8F4C)
    override val Orange6 = Color(0xFFF2B181)
    override val Orange7 = Color(0xFFF9D2B6)
    override val Orange8 = Color(0xFFFCE6D6)
    override val Orange9 = Color(0xFFFFF4EB)  // General icons: Fill
    override val Orange10 = Color(0xFFFFF4EB)  // General icons: Fill
    override val Orange11 = Color(0xFFFFF4EB)  // General icons: Fill

    // Purple
    override val Purple1 = Color(0xFF55339C)
    override val Purple2 = Color(0xFF643CB8)
    override val Purple3 = Color(0xFF7444D4)
    override val Purple4 = Color(0xFF834DF0)  // Primary
    override val Purple5 = Color(0xFFA177F4)
    override val Purple6 = Color(0xFFBFA1F8)
    override val Purple7 = Color(0xFFDCCBFB)
    override val Purple8 = Color(0xFFEFE5FF)
    override val Purple9 = Color(0xFFFAF5FF)  // General icons: Fill
    override val Purple10 = Color(0xFFFAF5FF)  // General icons: Fill
    override val Purple11 = Color(0xFFFAF5FF)  // General icons: Fill

    // Teal
    override val Teal1 = Color(0xFF096A6E)
    override val Teal2 = Color(0xFF077A7F)
    override val Teal3 = Color(0xFF058B90)
    override val Teal4 = Color(0xFF039BA1)  // Primary
    override val Teal5 = Color(0xFF3FB3B8)
    override val Teal6 = Color(0xFF7BCCCF)
    override val Teal7 = Color(0xFFB6E4E5)
    override val Teal8 = Color(0xFFDAF4F5)
    override val Teal9 = Color(0xFFF2FCFC)  // General icons: Fill
    override val Teal10 = Color(0xFFF2FCFC)  // General icons: Fill
    override val Teal11 = Color(0xFFF2FCFC)  // General icons: Fill
}

val LocalAppColors = staticCompositionLocalOf<ThemeColors> {
    error("No ThemeColors provided")
}
