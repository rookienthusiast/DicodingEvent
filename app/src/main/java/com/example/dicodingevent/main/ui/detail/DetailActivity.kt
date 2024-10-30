package com.example.dicodingevent.main.ui.detail

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.local.room.FavoriteEventDao
import com.example.dicodingevent.data.remote.EventRepository
import com.example.dicodingevent.data.remote.response.Event
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.databinding.ActivityDetailBinding
import com.example.dicodingevent.di.Injection
import com.example.dicodingevent.main.viewModelFactory.EventViewModelFactory

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var repository: EventRepository
    private lateinit var favoriteDao: FavoriteEventDao

    private val viewModel: DetailViewModel by viewModels {
        EventViewModelFactory.getInstance(this)
    }

    private var isFavorite = false

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = Injection.provideRepository(this) // Ambil repository dari injection
        favoriteDao = Injection.provideFavoriteEventDao(this)

        setupActionBar()
        handleIncomingIntent()
        setupFavoriteButton()
    }

    private fun setupFavoriteButton() {
        val eventItem = getEventItemFromIntent()

        binding.fabFavorite.setOnClickListener {
            if (isFavorite) {
                viewModel.deleteFavoriteEvent(eventItem.id.toString())
            } else {
                viewModel.insertFavoriteEvent(eventItem)
            }
        }
    }

    private fun getEventItemFromIntent(): EventItem {
        return intent.getParcelableExtra(EXTRA_DATA) ?: throw IllegalArgumentException("Event is null")
    }

    private fun setupActionBar() {
        supportActionBar?.title = "Detail Events"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.primary_bar)))
    }

    private fun handleIncomingIntent() {
        val eventItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getParcelableExtra(EXTRA_DATA, EventItem::class.java)
        }else {
            intent.getParcelableExtra(EXTRA_DATA)
        }
        Log.d("SendingEvent", "Event ID: ${eventItem?.id}, Event Name: ${eventItem?.name}")
        if (eventItem != null) {
            Log.d("DetailActivity", "Event ID: ${eventItem.id}")
            viewModel.loadEventsDetail(eventItem.id!!) // Dapatkan update tambahan jika ada
            viewModel.checkIfFavorite(eventItem.id.toString())
            observeViewModel() // Pantau perubahan data dari ViewModel
        } else {
            showErrorMessage("Event ID tidak valid")
        }
    }

    private fun displayEventDetails(event: Event) {
        binding.apply {
            tvForDetail.text = event.name
            tvSummary.text = event.summary
            tvOrganizerName.text = event.ownerName
            tvTime.text = event.beginTime

            val quota = event.quota
            val registrants = event.registrants
            tvQuota.text = (quota - registrants).toString()

            Glide.with(this@DetailActivity)
                .load(event.mediaCover)
                .into(ivImage)

            tvDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            btnRegister.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(this) { detailResponse ->
            Log.d("DetailActivity", "Received detailResponse: $detailResponse")
            if (detailResponse != null) {
                val eventItem = detailResponse.event // Ambil objek Event dari DetailResponse
                displayEventDetails(eventItem) // Tampilkan detail event
            } else {
                showErrorMessage("Data event tidak tersedia")
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { message ->
            showErrorMessage(message)
        }

        viewModel.isFavorite.observe(this) { favoriteStatus ->
            isFavorite = favoriteStatus
            val fabIcon = if (favoriteStatus) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
            binding.fabFavorite.setImageResource(fabIcon)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
