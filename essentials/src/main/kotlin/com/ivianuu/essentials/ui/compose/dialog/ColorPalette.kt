/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.compose.dialog

import android.graphics.Color.parseColor

fun Color(colorString: String): androidx.ui.graphics.Color =
    androidx.ui.graphics.Color(parseColor(colorString))

val PrimaryColors = listOf(
    Color("#F44336"),
    Color("#FFEBEE"),
    Color("#FFCDD2"),
    Color("#EF9A9A"),
    Color("#E57373"),
    Color("#EF5350"),
    Color("#F44336"),
    Color("#E53935"),
    Color("#D32F2F"),
    Color("#C62828"),
    Color("#B71C1C"),

    Color("#E91E63"),
    Color("#FCE4EC"),
    Color("#F8BBD0"),
    Color("#F48FB1"),
    Color("#F06292"),
    Color("#EC407A"),
    Color("#E91E63"),
    Color("#D81B60"),
    Color("#C2185B"),
    Color("#AD1457"),
    Color("#880E4F"),

    Color("#9C27B0"),
    Color("#F3E5F5"),
    Color("#E1BEE7"),
    Color("#CE93D8"),
    Color("#BA68C8"),
    Color("#AB47BC"),
    Color("#9C27B0"),
    Color("#8E24AA"),
    Color("#7B1FA2"),
    Color("#6A1B9A"),
    Color("#4A148C"),

    Color("#673AB7"),
    Color("#EDE7F6"),
    Color("#D1C4E9"),
    Color("#B39DDB"),
    Color("#9575CD"),
    Color("#7E57C2"),
    Color("#673AB7"),
    Color("#5E35B1"),
    Color("#512DA8"),
    Color("#4527A0"),
    Color("#311B92"),

    Color("#3F51B5"),
    Color("#E8EAF6"),
    Color("#C5CAE9"),
    Color("#9FA8DA"),
    Color("#7986CB"),
    Color("#5C6BC0"),
    Color("#3F51B5"),
    Color("#3949AB"),
    Color("#303F9F"),
    Color("#283593"),
    Color("#1A237E"),

    Color("#2196F3"),
    Color("#E3F2FD"),
    Color("#BBDEFB"),
    Color("#90CAF9"),
    Color("#64B5F6"),
    Color("#42A5F5"),
    Color("#2196F3"),
    Color("#1E88E5"),
    Color("#1976D2"),
    Color("#1565C0"),
    Color("#0D47A1"),

    Color("#03A9F4"),
    Color("#E1F5FE"),
    Color("#B3E5FC"),
    Color("#81D4FA"),
    Color("#4FC3F7"),
    Color("#29B6F6"),
    Color("#03A9F4"),
    Color("#039BE5"),
    Color("#0288D1"),
    Color("#0277BD"),
    Color("#01579B"),

    Color("#00BCD4"),
    Color("#E0F7FA"),
    Color("#B2EBF2"),
    Color("#80DEEA"),
    Color("#4DD0E1"),
    Color("#26C6DA"),
    Color("#00BCD4"),
    Color("#00ACC1"),
    Color("#0097A7"),
    Color("#00838F"),
    Color("#006064"),

    Color("#009688"),
    Color("#E0F2F1"),
    Color("#B2DFDB"),
    Color("#80CBC4"),
    Color("#4DB6AC"),
    Color("#26A69A"),
    Color("#009688"),
    Color("#00897B"),
    Color("#00796B"),
    Color("#00695C"),
    Color("#004D40"),

    Color("#4CAF50"),
    Color("#E8F5E9"),
    Color("#C8E6C9"),
    Color("#A5D6A7"),
    Color("#81C784"),
    Color("#66BB6A"),
    Color("#4CAF50"),
    Color("#43A047"),
    Color("#388E3C"),
    Color("#2E7D32"),
    Color("#1B5E20"),

    Color("#8BC34A"),
    Color("#F1F8E9"),
    Color("#DCEDC8"),
    Color("#C5E1A5"),
    Color("#AED581"),
    Color("#9CCC65"),
    Color("#8BC34A"),
    Color("#7CB342"),
    Color("#689F38"),
    Color("#558B2F"),
    Color("#33691E"),

    Color("#CDDC39"),
    Color("#F9FBE7"),
    Color("#F0F4C3"),
    Color("#E6EE9C"),
    Color("#DCE775"),
    Color("#D4E157"),
    Color("#CDDC39"),
    Color("#C0CA33"),
    Color("#AFB42B"),
    Color("#9E9D24"),
    Color("#827717"),

    Color("#FFEB3B"),
    Color("#FFFDE7"),
    Color("#FFF9C4"),
    Color("#FFF59D"),
    Color("#FFF176"),
    Color("#FFEE58"),
    Color("#FFEB3B"),
    Color("#FDD835"),
    Color("#FBC02D"),
    Color("#F9A825"),
    Color("#F57F17"),

    Color("#FFC107"),
    Color("#FFF8E1"),
    Color("#FFECB3"),
    Color("#FFE082"),
    Color("#FFD54F"),
    Color("#FFCA28"),
    Color("#FFC107"),
    Color("#FFB300"),
    Color("#FFA000"),
    Color("#FF8F00"),
    Color("#FF6F00"),

    Color("#FF9800"),
    Color("#FFF3E0"),
    Color("#FFE0B2"),
    Color("#FFCC80"),
    Color("#FFB74D"),
    Color("#FFA726"),
    Color("#FF9800"),
    Color("#FB8C00"),
    Color("#F57C00"),
    Color("#EF6C00"),
    Color("#E65100"),

    Color("#FF5722"),
    Color("#FBE9E7"),
    Color("#FFCCBC"),
    Color("#FFAB91"),
    Color("#FF8A65"),
    Color("#FF7043"),
    Color("#FF5722"),
    Color("#F4511E"),
    Color("#E64A19"),
    Color("#D84315"),
    Color("#BF360C"),

    Color("#795548"),
    Color("#EFEBE9"),
    Color("#D7CCC8"),
    Color("#BCAAA4"),
    Color("#A1887F"),
    Color("#8D6E63"),
    Color("#795548"),
    Color("#6D4C41"),
    Color("#5D4037"),
    Color("#4E342E"),
    Color("#3E2723"),

    Color("#9E9E9E"),
    Color("#FAFAFA"),
    Color("#F5F5F5"),
    Color("#EEEEEE"),
    Color("#E0E0E0"),
    Color("#BDBDBD"),
    Color("#9E9E9E"),
    Color("#757575"),
    Color("#616161"),
    Color("#424242"),
    Color("#212121"),

    Color("#607D8B"),
    Color("#ECEFF1"),
    Color("#CFD8DC"),
    Color("#B0BEC5"),
    Color("#90A4AE"),
    Color("#78909C"),
    Color("#607D8B"),
    Color("#546E7A"),
    Color("#455A64"),
    Color("#37474F"),
    Color("#263238")
)

// todo

val PRIMARY_COLORS = intArrayOf(
    parseColor("#F44336"), parseColor("#E91E63"), parseColor("#9C27B0"),
    parseColor("#673AB7"), parseColor("#3F51B5"), parseColor("#2196F3"),
    parseColor("#03A9F4"), parseColor("#00BCD4"), parseColor("#009688"),
    parseColor("#4CAF50"), parseColor("#8BC34A"), parseColor("#CDDC39"),
    parseColor("#FFEB3B"), parseColor("#FFC107"), parseColor("#FF9800"),
    parseColor("#FF5722"), parseColor("#795548"), parseColor("#9E9E9E"),
    parseColor("#607D8B")
)

val PRIMARY_COLORS_SUB = arrayOf(
    intArrayOf(
        parseColor("#FFEBEE"), parseColor("#FFCDD2"), parseColor("#EF9A9A"),
        parseColor("#E57373"), parseColor("#EF5350"), parseColor("#F44336"),
        parseColor("#E53935"), parseColor("#D32F2F"), parseColor("#C62828"),
        parseColor("#B71C1C")
    ), intArrayOf(
        parseColor("#FCE4EC"), parseColor("#F8BBD0"), parseColor("#F48FB1"),
        parseColor("#F06292"), parseColor("#EC407A"), parseColor("#E91E63"),
        parseColor("#D81B60"), parseColor("#C2185B"), parseColor("#AD1457"),
        parseColor("#880E4F")
    ), intArrayOf(
        parseColor("#F3E5F5"), parseColor("#E1BEE7"), parseColor("#CE93D8"),
        parseColor("#BA68C8"), parseColor("#AB47BC"), parseColor("#9C27B0"),
        parseColor("#8E24AA"), parseColor("#7B1FA2"), parseColor("#6A1B9A"),
        parseColor("#4A148C")
    ), intArrayOf(
        parseColor("#EDE7F6"), parseColor("#D1C4E9"), parseColor("#B39DDB"),
        parseColor("#9575CD"), parseColor("#7E57C2"), parseColor("#673AB7"),
        parseColor("#5E35B1"), parseColor("#512DA8"), parseColor("#4527A0"),
        parseColor("#311B92")
    ), intArrayOf(
        parseColor("#E8EAF6"), parseColor("#C5CAE9"), parseColor("#9FA8DA"),
        parseColor("#7986CB"), parseColor("#5C6BC0"), parseColor("#3F51B5"),
        parseColor("#3949AB"), parseColor("#303F9F"), parseColor("#283593"),
        parseColor("#1A237E")
    ), intArrayOf(
        parseColor("#E3F2FD"), parseColor("#BBDEFB"), parseColor("#90CAF9"),
        parseColor("#64B5F6"), parseColor("#42A5F5"), parseColor("#2196F3"),
        parseColor("#1E88E5"), parseColor("#1976D2"), parseColor("#1565C0"),
        parseColor("#0D47A1")
    ), intArrayOf(
        parseColor("#E1F5FE"), parseColor("#B3E5FC"), parseColor("#81D4FA"),
        parseColor("#4FC3F7"), parseColor("#29B6F6"), parseColor("#03A9F4"),
        parseColor("#039BE5"), parseColor("#0288D1"), parseColor("#0277BD"),
        parseColor("#01579B")
    ), intArrayOf(
        parseColor("#E0F7FA"), parseColor("#B2EBF2"), parseColor("#80DEEA"),
        parseColor("#4DD0E1"), parseColor("#26C6DA"), parseColor("#00BCD4"),
        parseColor("#00ACC1"), parseColor("#0097A7"), parseColor("#00838F"),
        parseColor("#006064")
    ), intArrayOf(
        parseColor("#E0F2F1"), parseColor("#B2DFDB"), parseColor("#80CBC4"),
        parseColor("#4DB6AC"), parseColor("#26A69A"), parseColor("#009688"),
        parseColor("#00897B"), parseColor("#00796B"), parseColor("#00695C"),
        parseColor("#004D40")
    ), intArrayOf(
        parseColor("#E8F5E9"), parseColor("#C8E6C9"), parseColor("#A5D6A7"),
        parseColor("#81C784"), parseColor("#66BB6A"), parseColor("#4CAF50"),
        parseColor("#43A047"), parseColor("#388E3C"), parseColor("#2E7D32"),
        parseColor("#1B5E20")
    ), intArrayOf(
        parseColor("#F1F8E9"), parseColor("#DCEDC8"), parseColor("#C5E1A5"),
        parseColor("#AED581"), parseColor("#9CCC65"), parseColor("#8BC34A"),
        parseColor("#7CB342"), parseColor("#689F38"), parseColor("#558B2F"),
        parseColor("#33691E")
    ), intArrayOf(
        parseColor("#F9FBE7"), parseColor("#F0F4C3"), parseColor("#E6EE9C"),
        parseColor("#DCE775"), parseColor("#D4E157"), parseColor("#CDDC39"),
        parseColor("#C0CA33"), parseColor("#AFB42B"), parseColor("#9E9D24"),
        parseColor("#827717")
    ), intArrayOf(
        parseColor("#FFFDE7"), parseColor("#FFF9C4"), parseColor("#FFF59D"),
        parseColor("#FFF176"), parseColor("#FFEE58"), parseColor("#FFEB3B"),
        parseColor("#FDD835"), parseColor("#FBC02D"), parseColor("#F9A825"),
        parseColor("#F57F17")
    ), intArrayOf(
        parseColor("#FFF8E1"), parseColor("#FFECB3"), parseColor("#FFE082"),
        parseColor("#FFD54F"), parseColor("#FFCA28"), parseColor("#FFC107"),
        parseColor("#FFB300"), parseColor("#FFA000"), parseColor("#FF8F00"),
        parseColor("#FF6F00")
    ), intArrayOf(
        parseColor("#FFF3E0"), parseColor("#FFE0B2"), parseColor("#FFCC80"),
        parseColor("#FFB74D"), parseColor("#FFA726"), parseColor("#FF9800"),
        parseColor("#FB8C00"), parseColor("#F57C00"), parseColor("#EF6C00"),
        parseColor("#E65100")
    ), intArrayOf(
        parseColor("#FBE9E7"), parseColor("#FFCCBC"), parseColor("#FFAB91"),
        parseColor("#FF8A65"), parseColor("#FF7043"), parseColor("#FF5722"),
        parseColor("#F4511E"), parseColor("#E64A19"), parseColor("#D84315"),
        parseColor("#BF360C")
    ), intArrayOf(
        parseColor("#EFEBE9"), parseColor("#D7CCC8"), parseColor("#BCAAA4"),
        parseColor("#A1887F"), parseColor("#8D6E63"), parseColor("#795548"),
        parseColor("#6D4C41"), parseColor("#5D4037"), parseColor("#4E342E"),
        parseColor("#3E2723")
    ), intArrayOf(
        parseColor("#FAFAFA"), parseColor("#F5F5F5"), parseColor("#EEEEEE"),
        parseColor("#E0E0E0"), parseColor("#BDBDBD"), parseColor("#9E9E9E"),
        parseColor("#757575"), parseColor("#616161"), parseColor("#424242"),
        parseColor("#212121")
    ), intArrayOf(
        parseColor("#ECEFF1"), parseColor("#CFD8DC"), parseColor("#B0BEC5"),
        parseColor("#90A4AE"), parseColor("#78909C"), parseColor("#607D8B"),
        parseColor("#546E7A"), parseColor("#455A64"), parseColor("#37474F"),
        parseColor("#263238")
    )
)

internal val ACCENT_COLORS = intArrayOf(
    parseColor("#FF1744"), parseColor("#F50057"), parseColor("#D500F9"),
    parseColor("#651FFF"), parseColor("#3D5AFE"), parseColor("#2979FF"),
    parseColor("#00B0FF"), parseColor("#00E5FF"), parseColor("#1DE9B6"),
    parseColor("#00E676"), parseColor("#76FF03"), parseColor("#C6FF00"),
    parseColor("#FFEA00"), parseColor("#FFC400"), parseColor("#FF9100"),
    parseColor("#FF3D00")
)

internal val ACCENT_COLORS_SUB = arrayOf(
    intArrayOf(
        parseColor("#FF8A80"), parseColor("#FF5252"), parseColor("#FF1744"),
        parseColor("#D50000")
    ), intArrayOf(
        parseColor("#FF80AB"), parseColor("#FF4081"), parseColor("#F50057"),
        parseColor("#C51162")
    ), intArrayOf(
        parseColor("#EA80FC"), parseColor("#E040FB"), parseColor("#D500F9"),
        parseColor("#AA00FF")
    ), intArrayOf(
        parseColor("#B388FF"), parseColor("#7C4DFF"), parseColor("#651FFF"),
        parseColor("#6200EA")
    ), intArrayOf(
        parseColor("#8C9EFF"), parseColor("#536DFE"), parseColor("#3D5AFE"),
        parseColor("#304FFE")
    ), intArrayOf(
        parseColor("#82B1FF"), parseColor("#448AFF"), parseColor("#2979FF"),
        parseColor("#2962FF")
    ), intArrayOf(
        parseColor("#80D8FF"), parseColor("#40C4FF"), parseColor("#00B0FF"),
        parseColor("#0091EA")
    ), intArrayOf(
        parseColor("#84FFFF"), parseColor("#18FFFF"), parseColor("#00E5FF"),
        parseColor("#00B8D4")
    ), intArrayOf(
        parseColor("#A7FFEB"), parseColor("#64FFDA"), parseColor("#1DE9B6"),
        parseColor("#00BFA5")
    ), intArrayOf(
        parseColor("#B9F6CA"), parseColor("#69F0AE"), parseColor("#00E676"),
        parseColor("#00C853")
    ), intArrayOf(
        parseColor("#CCFF90"), parseColor("#B2FF59"), parseColor("#76FF03"),
        parseColor("#64DD17")
    ), intArrayOf(
        parseColor("#F4FF81"), parseColor("#EEFF41"), parseColor("#C6FF00"),
        parseColor("#AEEA00")
    ), intArrayOf(
        parseColor("#FFFF8D"), parseColor("#FFFF00"), parseColor("#FFEA00"),
        parseColor("#FFD600")
    ), intArrayOf(
        parseColor("#FFE57F"), parseColor("#FFD740"), parseColor("#FFC400"),
        parseColor("#FFAB00")
    ), intArrayOf(
        parseColor("#FFD180"), parseColor("#FFAB40"), parseColor("#FF9100"),
        parseColor("#FF6D00")
    ), intArrayOf(
        parseColor("#FF9E80"), parseColor("#FF6E40"), parseColor("#FF3D00"),
        parseColor("#DD2C00")
    )
)
