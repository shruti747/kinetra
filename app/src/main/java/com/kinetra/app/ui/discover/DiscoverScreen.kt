package com.kinetra.app.ui.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "FIND YOUR\nNEXT START.",
                color = TextWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 36.sp
            )

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search events, sports, cities", color = TextGray) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GlowBlueBright,
                    unfocusedBorderColor = ButtonBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = ButtonBackground,
                    unfocusedContainerColor = ButtonBackground
                ),
                singleLine = true
            )

            // Location & Date Filter Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "LOCATION", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "London, UK", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "DATE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                    Button(
                        onClick = { viewModel.selectCategory(category) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) IndicatorActive else ButtonBackground
                        ),
                        border = if (!isSelected) BorderStroke(1.dp, ButtonBorder) else null
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) TextWhite else TextGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "FEATURED  /  ALL EVENTS",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            // Event Cards List
            state.events.filter { event ->
                val matchesCategory = state.selectedCategory == "All sports" || event.category.equals(state.selectedCategory, ignoreCase = true)
                val matchesQuery = event.title.contains(state.searchQuery, ignoreCase = true) || event.location.contains(state.searchQuery, ignoreCase = true)
                matchesCategory && matchesQuery
            }.forEach { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEventClick(event) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                    border = BorderStroke(1.dp, if (event.isFeatured) GlowBlueBright else ButtonBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = event.category,
                                color = GlowBlueBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = event.fee,
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = event.title,
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${event.date} · ${event.distance}",
                            color = TextGray,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = event.location,
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
