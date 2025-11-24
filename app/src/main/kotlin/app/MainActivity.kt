package app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import base.compose.local.LocalNavigation
import base.compose.theme.AppTheme
import feature.notification.presentaion.service.PollingService
import feature.photos.domain.entity.Photo
import feature.photos.domain.navigation.NavPhotos
import feature.photos.presentation.PhotoDetailScreen
import feature.photos.presentation.PhotosScreen
import feature.splash.domain.navigation.NavSplash
import feature.splash.presentation.SplashScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : FragmentActivity(), KoinComponent {

    private val pollingService: PollingService by inject()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        
        // Request notification permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            AppTheme {
                MainContent()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        pollingService.onActivityStarted()
    }

    override fun onStop() {
        super.onStop()
        pollingService.onActivityStopped()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainContent() {
        val navController = rememberNavController()

        val provider: ProvidedValue<NavController> = LocalNavigation.provides(navController)
        CompositionLocalProvider(provider) {
            NavHost(navController, startDestination = NavPhotos) {
                composable<NavPhotos> {
                    PhotosScreen()
                }
                composable<Photo> { backStackEntry: NavBackStackEntry ->
                    val photo = backStackEntry.toRoute<Photo>()
                    PhotoDetailScreen(photo = photo)
                }
                composable<NavSplash> { SplashScreen() }
            }
        }
    }
}
