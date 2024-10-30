    package com.example.dicodingevent.main.ui.favorite

    import android.content.Intent
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.viewModels
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.dicodingevent.adapter.EventAdapter
    import com.example.dicodingevent.databinding.FragmentFavoriteBinding
    import com.example.dicodingevent.main.ui.detail.DetailActivity
    import com.example.dicodingevent.main.viewModelFactory.EventViewModelFactory

    class FavoriteFragment : Fragment() {

        private var _binding: FragmentFavoriteBinding? = null
        private val binding get() = _binding!!

        private val viewModel: FavoriteViewModel by viewModels {
            EventViewModelFactory.getInstance(requireContext())
        }
        private lateinit var favoriteAdapter: EventAdapter

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            setupAdapters()
            setupObservers()
        }

        private fun setupAdapters() {
            favoriteAdapter = EventAdapter { eventItem ->
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_DATA, eventItem)
                }
                startActivity(intent)
            }

            binding.favoriteEventsRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = favoriteAdapter
            }
        }

        private fun setupObservers() {
            viewModel.favoriteEvents.observe(viewLifecycleOwner) { favoriteEvents ->
                favoriteAdapter.submitList(favoriteEvents)
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                showLoading(isLoading)
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                errorMessage?.let {
                    showError(it)
                }
            }
        }

        private fun showLoading(isLoading: Boolean) {
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        private fun showError(message: String) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }