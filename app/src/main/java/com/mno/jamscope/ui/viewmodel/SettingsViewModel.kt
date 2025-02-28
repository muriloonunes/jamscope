package com.mno.jamscope.ui.viewmodel

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.R
import com.mno.jamscope.data.repository.FriendsRepository
import com.mno.jamscope.data.repository.SettingsRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.util.Stuff
import com.mno.jamscope.util.Stuff.openUrl
import com.mno.jamscope.util.switches
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val navigator: Navigator,
    private val friendsRepository: FriendsRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _switchStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val switchStates: StateFlow<Map<String, Boolean>> = _switchStates

    private val _themePreference = MutableStateFlow(0)
    val themePreference: StateFlow<Int> = _themePreference

    init {
        viewModelScope.launch {
            loadSwitchStates()
            _themePreference.value = settingsRepository.getThemePreferenceFlow()
        }
    }

    private fun loadSwitchStates() {
        viewModelScope.launch {
            val states = switches.associate { switch ->
                switch.key to settingsRepository.getSwitchState(switch.key, switch.initialState)
                    .first()
            }
            _switchStates.value = states
        }
    }

    fun toggleSwitch(key: String) {
        val currentState = _switchStates.value[key] ?: return
        val newState = !currentState
        _switchStates.value = _switchStates.value.toMutableMap().apply {
            this[key] = newState
        }
        viewModelScope.launch {
            settingsRepository.saveSwitchState(key, newState)
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            userRepository.clearUserSession()
            friendsRepository.deleteFriends()
            delay(500)
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.LoginRoute,
                navOptions = {
                    popUpTo(Destination.AppRoute) {
                        inclusive = true
                    }
                }
            )
        }
    }

    fun navigateToWebView() {
        viewModelScope.launch {
            navigator.navigate(Destination.WebViewScreen)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            navigator.back()
        }
    }

    fun setThemePreference(theme: Int) {
        viewModelScope.launch {
            settingsRepository.saveThemePref(theme)
            _themePreference.value = theme
        }
    }

    //thx pano scrobbler
    fun sendBugReportMail(context: Context) {
        viewModelScope.launch {
            var bgRam = -1
            val manager =
                ContextCompat.getSystemService(context, ActivityManager::class.java)!!
            for (proc in manager.runningAppProcesses) {
                if (proc?.processName?.contains("com.mno.jamscope") == true) {
                    // https://stackoverflow.com/questions/2298208/how-do-i-discover-memory-usage-of-my-application-in-android
                    val memInfo = manager.getProcessMemoryInfo(intArrayOf(proc.pid)).first()
                    bgRam = memInfo.totalPss / 1024
                    break
                }
            }

            var lastExitInfo: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                lastExitInfo =
                    Stuff.getAppExitReasons(printAll = true, context = context).firstOrNull()
                        ?.toString()
            }
            val packageName = context.packageName
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val appName = pm.getApplicationLabel(appInfo).toString()
            val versionName = pm.getPackageInfo(packageName, 0).versionName
            var text = ""
            text += "$appName v$versionName\n"
            text += "Android " + Build.VERSION.RELEASE + "\n"
            text += "Device: " + Build.BRAND + " " + Build.MODEL + " / " + Build.DEVICE + "\n"
            val mi = ActivityManager.MemoryInfo()
            manager.getMemoryInfo(mi)
            text += "Background RAM usage: " + bgRam + "M \n"
            if (lastExitInfo != null)
                text += "Last exit reason: $lastExitInfo\n"
            text += "----------------------------------"
            text += "\n\n[Describe the issue]\n"
            Log.d("SettingsViewModel", "text: $text")

            val emailAddress = Stuff.EMAIL
            val emailIntent =
                Intent(
                    Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", emailAddress, null)
                ).apply {
                    // https://stackoverflow.com/questions/33098280/how-to-choose-email-app-with-action-sendto-also-support-attachment
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, emailAddress)
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        "$appName - Bug report"
                    )
                    putExtra(Intent.EXTRA_TEXT, text)
//                putExtra(Intent.EXTRA_STREAM, logUri)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

            try {
                context.startActivity(emailIntent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    context.getString(R.string.no_email_app_found),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun openDeleteAccount(context: Context) {
        context.openUrl("https://www.last.fm/settings/account/delete")
    }

    fun openBuyMeACoffee(context: Context) {
        context.openUrl("https://buymeacoffee.com/muriloonunes")
    }

    fun openGithubProject(context: Context) {
        context.openUrl("https://github.com/muriloonunes/jamscope")
    }
}