package com.yutotakano.projectreader

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.ModalWideNavigationRailProperties
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.compose.rememberNavController
import com.yutotakano.projectreader.ui.icons.home
import com.yutotakano.projectreader.ui.theme.ProjectReaderTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= 29) {
            // Make navigation bar flush with system app bar
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            ProjectReaderTheme(dynamicColor = false) {
                MainActivityComposable()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityComposable() {
    val navController = rememberNavController()
    val navigationRailState = rememberWideNavigationRailState(
        initialValue = WideNavigationRailValue.Collapsed
    )
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                // Need to override as it hardcodes a purple color by default
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(selected = false, onClick = {}, icon = {
                    Icon(home, "home")
                }, label = {
                    Text(text="Home")
                })
            }
        }
    ) { innerPadding ->
        ModalWideNavigationRail(
            state = navigationRailState,
            hideOnCollapse = true,
            expandedProperties = ModalWideNavigationRailProperties(
                shouldDismissOnBackPress = true
            ),
        ) {
            (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
                SideEffect {
                    if (Build.VERSION.SDK_INT >= 29) {
                        window.isNavigationBarContrastEnforced = false
                    }
                }
            }
            WideNavigationRailItem(
                railExpanded = true,
                selected = false,
                icon = {
                    Icon(home, "home")
                },
                onClick = {},
                label = {
                    Text(text = "Home", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Left)
                }
            )
        }
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding),
            expandNav = {
                coroutineScope.launch { navigationRailState.expand() }
            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, expandNav: () -> Unit) {
    Column(
        modifier = modifier
    ) {
        OutlinedIconButton(
            onClick = expandNav,
        ) {
            Icon(home, "home")
        }
        Text(
            text = "Hello $name!",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjectReaderTheme {
        Greeting("Android", expandNav = {})
    }
}