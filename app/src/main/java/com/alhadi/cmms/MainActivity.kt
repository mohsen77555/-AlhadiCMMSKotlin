package com.alhadi.cmms

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.alhadi.cmms.notify.Reminders
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alhadi.cmms.ui.CmmsApp
import com.alhadi.cmms.ui.LoginScreen
import com.alhadi.cmms.ui.theme.AlhadiTheme
import com.alhadi.cmms.viewmodel.CmmsViewModel
import com.alhadi.cmms.viewmodel.CmmsViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: CmmsViewModel by viewModels {
        CmmsViewModelFactory((application as AlhadiApplication).repository)
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* result ignored */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Local maintenance reminders: ensure the channel, schedule the daily check, and ask for
        // notification permission on Android 13+.
        Reminders.ensureChannel(this)
        Reminders.schedulePeriodic(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            AlhadiTheme {
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                val loginError by viewModel.loginError.collectAsStateWithLifecycle()

                AnimatedContent(
                    targetState = isLoggedIn,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "auth"
                ) { loggedIn ->
                    if (loggedIn) {
                        CmmsApp(viewModel = viewModel)
                    } else {
                        LoginScreen(
                            error = loginError,
                            onLogin = viewModel::login,
                            onClearError = viewModel::clearLoginError
                        )
                    }
                }
            }
        }
    }
}
