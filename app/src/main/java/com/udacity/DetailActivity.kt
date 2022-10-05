package com.udacity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val filename = intent.getStringExtra(URL_FILE_NAME_KEY)
        val downloadStatus = intent.getStringExtra(URL_STATUS_KEY)

        binding.contentDetails.textViewFileNAme.text = filename
        binding.contentDetails.textViewDownloadStatus.text = downloadStatus
        if (downloadStatus == getString(R.string.downloadFail))
            binding.contentDetails.textViewDownloadStatus.setTextColor(Color.RED)

//      cancel notifications if not use broadcast receiver
        applicationContext.cancelNotifications()

        binding.contentDetails.okButton.setOnClickListener {

            //finish this activity to go back to previous activity
            finish()
            //   startActivity( Intent(this, MainActivity::class.java))

        }

    }


}
