package com.yutotakano.projectreader

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.ModalWideNavigationRailProperties
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.compose.rememberNavController
import com.yutotakano.projectreader.ui.icons.chevron_right
import com.yutotakano.projectreader.ui.icons.explore
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
    val coroutineScope = rememberCoroutineScope()

    val navigationRailState = rememberWideNavigationRailState(
        initialValue = WideNavigationRailValue.Collapsed,
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false, onClick = {},
                    icon = {
                        Icon(home, "home")
                    },
                    label = {
                        Text(text = "ホーム")
                    },
                )
            }
        },
    ) { innerPadding ->
        ModalWideNavigationRail(
            state = navigationRailState,
            hideOnCollapse = true,
            expandedProperties = ModalWideNavigationRailProperties(
                shouldDismissOnBackPress = true,
            ),
        ) {
            ProjectReaderNavigationRail()
        }
        Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding),
            expandNav = {
                coroutineScope.launch { navigationRailState.expand() }
            },
        )
    }
}

@Preview
@Composable
fun ProjectReaderNavigationRail() {
    val collapsedSections = remember { mutableStateSetOf<String>() }

    val animatedRotate: Float by animateFloatAsState(
        if ("section1" in collapsedSections) 90f else 270f,
        label = "rotation",
    )

    // Modals get their own Window separate from the main one, so we have to re-configure
    // the navbar contrast enforcement option to get true edge-to-edge.
    (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
        // Need to use SideEffect rather than LaunchedEffect here to guarantee it runs any
        // draw, otherwise we get a flash of nav bar contrast before it turns off
        SideEffect {
            if (Build.VERSION.SDK_INT >= 29) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }
    Column(
        // NavigationRail adds a 20dp right padding (assuming each item would add 20dp
        // left padding). We want to be flush so counteract that by placing with a larger
        // width but reporting the shorter width.
        modifier = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(
                constraints.copy(maxWidth = constraints.maxWidth + 20.dp.roundToPx()),
            )
            layout(placeable.width - 20.dp.roundToPx(), placeable.height) {
                placeable.place(0, 0)
            }
        },
    ) {
        NavigationDrawerItem(
            selected = false,
            icon = {
                Icon(explore, "explore")
            },
            shape = RectangleShape,
            modifier = Modifier.padding(0.dp),
            onClick = {},
            label = {
                Text(
                    text = "あてもなく散策する",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                )
            },
        )
        NavigationDrawerItem(
            selected = false,
            icon = {},
            shape = RectangleShape,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier.padding(0.dp),
            onClick = {
                if ("section1" in collapsedSections) {
                    collapsedSections.remove("section1")
                } else {
                    collapsedSections.add("section1")
                }
            },
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Section 1",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        chevron_right,
                        "Expand/Collapse",
                        modifier = Modifier.rotate(animatedRotate),
                    )
                }
            },
        )
        AnimatedVisibility("section1" !in collapsedSections) {
            NavigationDrawerItem(
                selected = false,
                icon = {
                    Icon(home, "home")
                },
                shape = RectangleShape,
                modifier = Modifier.padding(0.dp),
                onClick = {},
                label = {
                    Text(
                        text = "Home",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Left,
                    )
                },
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, expandNav: () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedIconButton(
            onClick = expandNav,
            shape = CircleShape,
            modifier = Modifier
                .width(80.dp)
                .height(80.dp),
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