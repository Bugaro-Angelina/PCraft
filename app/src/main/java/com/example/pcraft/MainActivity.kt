package com.example.pcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.RowScope
import com.example.pcraft.ui.navigation.NavGraph
import com.example.pcraft.ui.navigation.Screen
import com.example.pcraft.ui.theme.PCraftTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PCraftTheme {
                val navController = rememberNavController()
                val items = listOf(
                    BottomBarItem("Каталог", Icons.Filled.Home, Screen.Home.route),
                    BottomBarItem("Сборка", Icons.Filled.Build, Screen.Builder.route),
                    BottomBarItem("Избранное", Icons.Filled.Favorite, Screen.Favorites.route),
                    BottomBarItem("Аккаунт", Icons.Filled.Person, Screen.Profile.route)
                )

                Scaffold(
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        BottomBar(
                            items = items,
                            currentRoute = currentRoute,
                            onItemClick = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { padding ->
                    NavGraph(navController, padding)
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    items: List<BottomBarItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xF011131A))
            .navigationBarsPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.pcraft_launcher_source),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .blur(18.dp)
                .alpha(0.14f),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = when (item.route) {
                    Screen.Home.route -> currentRoute?.startsWith(Screen.Home.route) == true
                    else -> currentRoute == item.route
                }

                BottomBarButton(
                    item = item,
                    selected = selected,
                    onClick = { onItemClick(item.route) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomBarButton(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconColor = if (selected) Color(0xFFFF8DCA) else Color(0xFFD7DCE6)
    val textColor = if (selected) Color(0xFFFFC6E4) else Color(0xFFE5EAF4)

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = item.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private data class BottomBarItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

