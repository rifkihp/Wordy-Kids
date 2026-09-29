package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Category

val VibrantBgColor = Color(0xFFFFFDE7) // Warm soft cream
val CardBorderYellow = Color(0xFFFFCA28)
val DarkBrownText = Color(0xFF5D4037)
val SoftGraySlot = Color(0xFFF5F5F5)
val SoftGrayBorder = Color(0xFFBDBDBD)

val KeyboardColors = listOf(
    Color(0xFFAB47BC) to Color(0xFF7B1FA2), // Purple
    Color(0xFF26A69A) to Color(0xFF00796B), // Teal
    Color(0xFFFFCA28) to Color(0xFFF57F17), // Yellow
    Color(0xFF42A5F5) to Color(0xFF1565C0), // Blue
    Color(0xFFEF5350) to Color(0xFFC62828), // Red
    Color(0xFF66BB6A) to Color(0xFF2E7D32), // Green
    Color(0xFFFFA726) to Color(0xFFEF6C00), // Orange
    Color(0xFF78909C) to Color(0xFF455A64)  // Slate
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordGameScreen(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VibrantBgColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Header (Level, Stars, Sound)
            TopHeaderSection(
                level = state.level,
                stars = state.totalStars,
                isSoundEnabled = state.isSoundEnabled,
                onToggleSound = { viewModel.toggleSound() },
                onNextWord = { viewModel.nextWord() }
            )

            // 2. Category Selector Pills
            CategoryTabsSection(
                selectedCategory = state.selectedCategory,
                onCategorySelect = { viewModel.selectCategory(it) }
            )

            // 3. Main Word Card Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                MainGameCard(
                    state = state,
                    onSpeakWord = { viewModel.speakCurrentWord() },
                    onRemoveLetter = { index -> viewModel.onRemoveLetterClick(index) },
                    onUseHint = { viewModel.useHint() }
                )
            }

            // 4. Interactive Keyboard Footer
            KeyboardFooterSection(
                state = state,
                onLetterClick = { index, letter ->
                    viewModel.onKeyboardLetterClick(index, letter)
                },
                onUseHint = { viewModel.useHint() }
            )
        }

        // Victory Dialog Popup
        if (state.showVictoryDialog) {
            VictoryDialog(
                word = state.currentWordItem.word,
                emoji = state.currentWordItem.emoji,
                starsGained = 15,
                onNextLevel = { viewModel.nextWord() }
            )
        }
    }
}

@Composable
fun TopHeaderSection(
    level: Int,
    stars: Int,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onNextWord: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Refresh/Next Word Button + Level Progress
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PlayfulIconButton(
                bgColor = Color(0xFFFF7043),
                borderColor = Color(0xFFD84315),
                onClick = onNextWord,
                modifier = Modifier.testTag("refresh_word_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Next Word",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "LEVEL $level",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkBrownText,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { ((level % 10) / 10f).coerceIn(0.1f, 1.0f) },
                    modifier = Modifier
                        .width(90.dp)
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Color(0xFF66BB6A),
                    trackColor = Color(0xFFE0E0E0),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        // Right: Stars Badge + Sound Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Stars pill
            Surface(
                shape = CircleShape,
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(2.dp, CardBorderYellow),
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("stars_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "⭐", fontSize = 18.sp)
                    Text(
                        text = "$stars",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkBrownText
                    )
                }
            }

            // Sound Toggle Button
            PlayfulIconButton(
                bgColor = Color(0xFF42A5F5),
                borderColor = Color(0xFF1565C0),
                onClick = onToggleSound,
                modifier = Modifier.testTag("sound_toggle_button")
            ) {
                Icon(
                    imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Toggle Sound",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryTabsSection(
    selectedCategory: Category,
    onCategorySelect: (Category) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(Category.entries) { category ->
            val isSelected = category == selectedCategory
            val bgColor = if (isSelected) category.badgeColor else Color.White
            val textColor = if (isSelected) Color.White else Color(0xFF757575)
            val borderStroke = if (isSelected) null else androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE0E0E0))

            Surface(
                shape = CircleShape,
                color = bgColor,
                border = borderStroke,
                shadowElevation = if (isSelected) 4.dp else 0.dp,
                modifier = Modifier
                    .testTag("category_tab_${category.name.lowercase()}")
                    .clickable { onCategorySelect(category) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = category.emoji, fontSize = 16.sp)
                    Text(
                        text = category.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun MainGameCard(
    state: GameState,
    onSpeakWord: () -> Unit,
    onRemoveLetter: (Int) -> Unit,
    onUseHint: () -> Unit
) {
    val wordItem = state.currentWordItem

    Card(
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(4.dp, if (state.isWrongAnswerFlash) Color(0xFFEF5350) else CardBorderYellow, RoundedCornerShape(36.dp))
            .testTag("main_game_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Tag
            Surface(
                shape = CircleShape,
                color = wordItem.category.badgeColor.copy(alpha = 0.2f),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = "${wordItem.category.emoji} ${wordItem.category.displayName.uppercase()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = wordItem.category.badgeColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Central Emoji Visual with Sound Speak Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(Color(0xFFFFF9C4), CircleShape)
                        .clip(CircleShape)
                        .clickable { onSpeakWord() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = wordItem.emoji,
                        fontSize = 72.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Audio Speak FAB
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 6.dp, y = 6.dp)
                ) {
                    PlayfulIconButton(
                        bgColor = Color(0xFFFF7043),
                        borderColor = Color(0xFFD84315),
                        size = 44.dp,
                        onClick = onSpeakWord,
                        modifier = Modifier.testTag("speak_word_button")
                    ) {
                        Text(text = "🔊", fontSize = 20.sp)
                    }
                }
            }

            // Word Slot Boxes
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.currentGuessedLetters.forEachIndexed { index, char ->
                    WordSlotBox(
                        letter = char,
                        onClick = { onRemoveLetter(index) },
                        testTag = "word_slot_$index"
                    )
                }
            }

            // Hint sentence or question prompt
            if (state.showHintText) {
                Text(
                    text = "💡 ${wordItem.hint}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00796B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable { onUseHint() }
                ) {
                    Text(
                        text = "Need a clue? Tap for hint!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9E9E9E)
                    )
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Hint",
                        tint = CardBorderYellow,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WordSlotBox(
    letter: Char?,
    onClick: () -> Unit,
    testTag: String
) {
    val isFilled = letter != null
    val bgColor = if (isFilled) SoftGraySlot else Color.White
    val borderModifier = if (isFilled) {
        Modifier.border(2.dp, SoftGrayBorder, RoundedCornerShape(16.dp))
    } else {
        Modifier.border(2.dp, CardBorderYellow, RoundedCornerShape(16.dp))
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .then(borderModifier)
            .clickable(enabled = isFilled) { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter?.toString() ?: "?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = if (isFilled) DarkBrownText else CardBorderYellow
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeyboardFooterSection(
    state: GameState,
    onLetterClick: (Int, Char) -> Unit,
    onUseHint: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Letter Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                maxItemsInEachRow = 5,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.keyboardLetters.forEachIndexed { index, letter ->
                    val isUsed = state.usedKeyboardIndices.contains(index)
                    val (color, darkColor) = KeyboardColors[index % KeyboardColors.size]

                    KeyboardKeyButton(
                        letter = letter,
                        isUsed = isUsed,
                        bgColor = color,
                        borderColor = darkColor,
                        onClick = { onLetterClick(index, letter) },
                        testTag = "keyboard_key_$letter"
                    )
                }
            }
        }
    }
}

@Composable
fun KeyboardKeyButton(
    letter: Char,
    isUsed: Boolean,
    bgColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    var isPressed by remember { mutableStateOf(false) }
    val yOffset by animateFloatAsState(
        targetValue = if (isPressed) 4f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "press"
    )

    val actualBg = if (isUsed) Color(0xFFE0E0E0) else bgColor
    val actualBorder = if (isUsed) Color(0xFFBDBDBD) else borderColor
    val textColor = if (isUsed) Color(0xFF9E9E9E) else Color.White

    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(width = 58.dp, height = 54.dp)
            .offset(y = yOffset.dp)
            .shadow(if (isUsed || isPressed) 0.dp else 4.dp, RoundedCornerShape(16.dp))
            .background(actualBg, RoundedCornerShape(16.dp))
            .border(
                width = 3.dp,
                color = actualBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = !isUsed,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = textColor
        )
    }
}

@Composable
fun PlayfulIconButton(
    bgColor: Color,
    borderColor: Color,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .background(bgColor, CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun VictoryDialog(
    word: String,
    emoji: String,
    starsGained: Int,
    onNextLevel: () -> Unit
) {
    Dialog(onDismissRequest = onNextLevel) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(4.dp, CardBorderYellow),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("victory_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "GREAT JOB! 🎉",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF7043)
                    )

                    Text(
                        text = emoji,
                        fontSize = 80.sp
                    )

                    Text(
                        text = word,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkBrownText,
                        letterSpacing = 2.sp
                    )

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFF9C4),
                        border = androidx.compose.foundation.BorderStroke(2.dp, CardBorderYellow)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⭐", fontSize = 20.sp)
                            Text(
                                text = "+$starsGained Stars!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkBrownText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF66BB6A),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clickable { onNextLevel() }
                            .testTag("next_word_button")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "NEXT WORD ➡️",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
