package com.kinetra.app.ui.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraBadge
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraClickableCard
import com.kinetra.app.ui.components.SectionHeader
import com.kinetra.app.ui.theme.*

@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    onEventClick: (EventItem) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val categories = listOf("All sports", "Running", "Cycling", "Marathon", "Multisport")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "KINETRA · EVENTS",
                    color = BrandAccentText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Find Your\nNext Start.",
                    color = TextWhite,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 35.sp,
                    letterSpacing = (-0.4).sp
                )
            }

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search events, sports, cities", color = TextGray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = BrandAccentText,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandAccentText,
                    unfocusedBorderColor = ButtonBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    cursorColor = BrandAccentText
                ),
                singleLine = true
            )

            // Location & Date Filter Bar
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "LOCATION", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "London, UK", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "DATE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "From 16 Nov 2026", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Categories horizontal scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == state.selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) BrandPrimary else CardBackground)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) BrandPrimary else CardBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.selectCategory(category) }
                            .padding(horizontal = 15.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) OnAccent else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            SectionHeader(title = "Featured & Upcoming Events")

            state.events.filter { event ->
                val matchesCategory = state.selectedCategory == "All sports" || event.category.equals(state.selectedCategory, ignoreCase = true)
                val matchesQuery = event.title.contains(state.searchQuery, ignoreCase = true) || event.location.contains(state.searchQuery, ignoreCase = true)
                matchesCategory && matchesQuery
            }.forEach { event ->
                KinetraClickableCard(
                    onClick = { onEventClick(event) },
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (event.isFeatured) BrandPrimary.copy(alpha = 0.45f) else CardBorder,
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        KinetraBadge(
                            text = event.category.uppercase(),
                            textColor = if (event.isFeatured) OnAccent else BrandAccentText,
                            backgroundColor = if (event.isFeatured) BrandPrimary else AccentTintSurface,
                            borderColor = if (event.isFeatured) BrandPrimary else BrandPrimary.copy(alpha = 0.2f)
                        )
                        Text(
                            text = event.fee,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = event.title,
                        color = TextWhite,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${event.date} · ${event.distance}",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = event.location,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
