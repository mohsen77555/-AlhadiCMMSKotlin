package com.alhadi.cmms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
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
