package com.indonesiaemas.note

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.play.core.appupdate.AppUpdateManager // Tambahan Update
import com.google.android.play.core.appupdate.AppUpdateManagerFactory // Tambahan Update
import com.google.android.play.core.install.model.AppUpdateType // Tambahan Update
import com.google.android.play.core.install.model.UpdateAvailability // Tambahan Update
import com.indonesiaemas.note.ui.BiometricAvailability
import com.indonesiaemas.note.ui.BiometricHelper
import com.indonesiaemas.note.ui.BiometricResult
import com.indonesiaemas.note.ui.Screen
import com.indonesiaemas.note.ui.screens.*
import com.indonesiaemas.note.ui.theme.NoteAppTheme
import com.indonesiaemas.note.viewmodel.NoteViewModel
import androidx.core.net.toUri

class MainActivity : FragmentActivity() {

    private val viewModel: NoteViewModel by viewModels()
    private lateinit var appUpdateManager: AppUpdateManager

    private val updateLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdates()

        enableEdgeToEdge()
        setContent {
            NoteAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Teruskan 'this' (activity) ke dalam fungsi Composable
                    AppWithBiometricLock(activity = this, viewModel = viewModel)
                }
            }
        }
    }

    private fun checkForUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateLauncher,
                    com.google.android.play.core.appupdate.AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateLauncher,
                    com.google.android.play.core.appupdate.AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }
}

@Composable
fun AppWithBiometricLock(
    activity: MainActivity,
    viewModel: NoteViewModel
) {
    var isUnlocked by remember { mutableStateOf(false) }
    var lockStatus by remember { mutableStateOf(LockStatus.IDLE) }
    var errorMessage by remember { mutableStateOf("") }

    val biometricAvailability = remember {
        BiometricHelper.checkAvailability(activity)
    }

    fun triggerAuth() {
        lockStatus = LockStatus.WAITING
        BiometricHelper.authenticate(
            activity = activity,
            title = "Buka Aplikasi Catatan",
            subtitle = "Verifikasi sidik jari untuk melanjutkan",
            negativeText = "Batal"
        ) { result ->
            when (result) {
                is BiometricResult.Success -> {
                    lockStatus = LockStatus.SUCCESS
                    isUnlocked = true
                }
                is BiometricResult.Failed -> { lockStatus = LockStatus.FAILED }
                is BiometricResult.Error -> {
                    lockStatus = LockStatus.ERROR
                    errorMessage = result.message
                }
                is BiometricResult.Cancelled -> { lockStatus = LockStatus.IDLE }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (biometricAvailability is BiometricAvailability.Available) {
            triggerAuth()
        } else {
            isUnlocked = true
        }
    }

    AnimatedContent(
        targetState = isUnlocked,
        transitionSpec = {
            if (targetState) {
                fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 8 } togetherWith
                        fadeOut(tween(250))
            } else {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            }
        },
        label = "lockTransition"
    ) { unlocked ->
        if (unlocked) {
            // KIRIM activity ke NoteApp
            NoteApp(viewModel = viewModel, activity = activity)
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LockScreen(
                    lockStatus = lockStatus,
                    errorMessage = errorMessage,
                    onTryAgain = { triggerAuth() }
                )
            }
        }
    }
}

@Composable
fun NoteApp(
    viewModel: NoteViewModel,
    activity: MainActivity
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onAddNote = { navController.navigate(Screen.AddNote.route) },
                onNoteClick = { noteId -> navController.navigate(Screen.NoteDetail.createRoute(noteId)) },
                onArchiveClick = { navController.navigate(Screen.Archive.route) },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onAboutClick = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.AddNote.route) {
            AddEditNoteScreen(
                viewModel = viewModel,
                noteId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditNote.route,
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            AddEditNoteScreen(
                viewModel = viewModel,
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: return@composable
            NoteDetailScreen(
                viewModel = viewModel,
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() },
                onEditNote = { id -> navController.navigate(Screen.EditNote.createRoute(id)) }
            )
        }

        composable(Screen.Archive.route) {
            ArchiveScreen(
                viewModel = viewModel,
                onNoteClick = { noteId -> navController.navigate(Screen.NoteDetail.createRoute(noteId)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = viewModel,
                onNoteClick = { noteId -> navController.navigate(Screen.NoteDetail.createRoute(noteId)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                viewModel = viewModel,
                onNoteClick = {
                    val intent =
                        android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                            data = "mailto:oceanservicespoint@gmail.com".toUri()
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Tanya Aplikasi Catatan")
                        }

                    try {
                        // Gunakan parameter 'activity' yang dikirim dari atas
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        // Gunakan 'e' dalam log agar warning "never used" hilang
                        android.util.Log.e("AboutScreen", "Error opening mail", e)
                        android.widget.Toast.makeText(
                            activity,
                            "Tidak ada aplikasi email ditemukan",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}