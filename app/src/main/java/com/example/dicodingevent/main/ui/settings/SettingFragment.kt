package com.example.dicodingevent.main.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.FragmentSettingBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var workManager: WorkManager
    private var periodicWorkRequest: PeriodicWorkRequest? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        workManager = WorkManager.getInstance(requireActivity())
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switch_theme)
        val switchNotifications = binding.switchNotifications

        val pref = SettingPreferences.getInstance(requireActivity().dataStore)
        val settingsViewModel = ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingsViewModel.saveThemeSetting(isChecked)
        }

        settingsViewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationsEnabled: Boolean ->
            if (isNotificationsEnabled) {
                startPeriodicTask()
                switchNotifications.isChecked = true
            } else {
                cancelPeriodicTask()
                switchNotifications.isChecked = false
            }
        }
        switchNotifications.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingsViewModel.saveNotificationSetting(isChecked)
        }
    }

    private fun startPeriodicTask() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create the periodic work request for 1-day interval
        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "myNotif",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest!!
        )
    }

    private fun cancelPeriodicTask() {
        workManager.cancelUniqueWork("myNotif")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}