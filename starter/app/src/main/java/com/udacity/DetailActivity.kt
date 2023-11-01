package com.udacity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity.Companion.DOWNLOAD_TYPE
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val downloadType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(DOWNLOAD_TYPE, DownloadType::class.java)
        } else {
            intent.getSerializableExtra(DOWNLOAD_TYPE) as DownloadType
        }

        binding.detail.buttonOk.setOnClickListener {
            finish()
        }

        var nameContent = ""
        var statusContent = ""

        when (downloadType) {
            DownloadType.GLIDE -> {
                nameContent = getString(R.string.download_title_glide)
                statusContent = DownloadType.GLIDE.status.toString()
            }
            DownloadType.LOAD_APP -> {
                nameContent = getString(R.string.download_title_load_app)
                binding.detail.txtStatusContent.setTextColor(getColor(R.color.colorAccent))
                statusContent = DownloadType.LOAD_APP.status.toString()
            }
            DownloadType.RETROFIT -> {
                nameContent = getString(R.string.download_title_retrofit)
                statusContent = DownloadType.RETROFIT.status.toString()
            }
            else->{}
        }

        binding.detail.txtNameContent.text = nameContent
        binding.detail.txtStatusContent.text = statusContent
    }
}
