package com.mzhang.heremapdemo.locationLanding.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mzhang.heremapdemo.databinding.ActivityFamilyMemberMapBinding

class FamilyMemberMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFamilyMemberMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFamilyMemberMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}