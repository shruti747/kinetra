package com.kinetra.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun SettingsScreen(onBack: () -> Unit, onSignOut: () -> Unit) {
    val scrollState = rememberScrollState()
    var privateProfile by remember { mutableStateOf(true) }
    var locationAccess by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "KINETRA / ACCOUNT", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text(text = "Settings", color = TextWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "Back", color = GlowBlueBright, fontSize = 14.sp, modifier = Modifier.clickable { onBack() })
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Alex Morgan", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "alex.morgan@email.com", color = GlowBlueBright, fontSize = 13.sp)
                }
            }

            Text(text = "PRIVACY", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Private profile", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Hide route start / end", color = TextGray, fontSize = 12.sp)
                    }
                    Switch(checked = privateProfile, onCheckedChange = { privateProfile = it })
                }
            }

            Text(text = "GPS PERMISSIONS", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Location access", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Not granted · background off", color = TextGray, fontSize = 12.sp)
                    }
                    Switch(checked = locationAccess, onCheckedChange = { locationAccess = it })
                }
            }

            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Text("Sign out", color = TextWhite, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
