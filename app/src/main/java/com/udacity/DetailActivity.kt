package com.udacity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ContentDetailBinding
import com.udacity.databinding.ContentMainBinding
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
private lateinit var binding: ContentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = intent.getStringExtra(Const.INTENT_FILENAME)
        val status = intent.getStringExtra(Const.INTENT_STATUS)
        Log.d("detailactivity", "this is a message $status    $fileName")


        binding.tvFile.text = fileName.toString()
        binding.tvStatus.text = status.toString()
        if (status == getString(R.string.status_success)) {
            binding.tvStatus.setTextColor(this.getColor(R.color.green))
        } else {
            binding.tvStatus.setTextColor(this.getColor(R.color.red))

        }

        btn_ok.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


    }

}
