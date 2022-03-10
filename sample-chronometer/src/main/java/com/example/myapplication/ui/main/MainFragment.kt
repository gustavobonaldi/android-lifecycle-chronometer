package com.example.myapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bonaldi.lifecyclechronometer.CustomLifecycleChronometer
import com.bonaldi.lifecyclechronometer.LifecycleChronometerListener
import com.example.myapplication.databinding.MainFragmentBinding

class MainFragment : Fragment(), LifecycleChronometerListener {
    private lateinit var binding: MainFragmentBinding
    private val timer: CustomLifecycleChronometer by lazy {
        CustomLifecycleChronometer(
            viewLifecycleOwner,
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer.startChronometer()
        setupViews()
    }

    private fun setupViews() = binding.apply {
        btnFinish.setOnClickListener {
            timer.finishChronometer()
        }

        btnPause.setOnClickListener {
            timer.pauseChronometer()
        }

        btnPlay.setOnClickListener {
            timer.resumeChronometer()
        }

        btnCleanUp.setOnClickListener {
            timer.cleanUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.finishChronometer()
    }

    override fun onPause() {
        super.onPause()
        timer.pauseChronometer()
    }

    override fun onChronometerChanged(timeInMillis: Long) {
        binding.tvTimer.text = timeInMillis.toString()
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
