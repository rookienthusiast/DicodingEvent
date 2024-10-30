package com.example.dicodingevent.main.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.adapter.ColumnEventAdapter
import com.example.dicodingevent.adapter.EventAdapter
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.example.dicodingevent.main.ui.detail.DetailActivity
import com.example.dicodingevent.main.ui.settings.SettingPreferences
import com.example.dicodingevent.main.ui.settings.SettingViewModel
import com.example.dicodingevent.main.ui.settings.SettingViewModelFactory
import com.example.dicodingevent.main.ui.settings.dataStore
import com.example.dicodingevent.main.viewModelFactory.EventViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        EventViewModelFactory.getInstance(requireContext())
    }

    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(SettingPreferences.getInstance(requireContext().dataStore))
    }

    private lateinit var upcomingAdapter: EventAdapter
    private lateinit var finishedAdapter: ColumnEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTheme()
        setupAdapters()
        setupObservers()
    }

    private fun setupTheme() {
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupAdapters() {
        upcomingAdapter = EventAdapter { eventItem ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_DATA, eventItem)
            }
            startActivity(intent)
        }

        binding.upcomingEventsRecycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = upcomingAdapter
        }

        finishedAdapter = ColumnEventAdapter { eventDetail ->
            // Aksi saat item upcoming event diklik (misal: pindah ke halaman detail)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DATA, eventDetail)
            startActivity(intent)
        }

        binding.finishedEventsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }
    }

    private fun setupObservers() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            Log.d("HomeFragment", "Upcoming Events: $events")
            upcomingAdapter.submitList(events)
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            Log.d("HomeFragment", "Finished Events: $events")
            finishedAdapter.submitList(events.take(5))
        }

        viewModel.upcomingEventsLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.upcomingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.finishedEventsLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.finishedProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showError(it)
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}