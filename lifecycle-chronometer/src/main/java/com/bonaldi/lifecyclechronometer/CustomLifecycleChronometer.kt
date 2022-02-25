package com.bonaldi.lifecyclechronometer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomLifecycleChronometer(
    private val lifecycleOwner: LifecycleOwner,
    private val listener: LifecycleChronometerListener
) {
    private var chronometerState: ChronometerState = ChronometerState.IDLE
    private var chronometerCountingJob: Job? = null
    private var currentDuration: Long = 0

    fun startChronometer() {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (chronometerState) {
                    ChronometerState.IDLE -> {
                        chronometerState = ChronometerState.RESUMED
                        resumeJobCounting()
                    }
                    ChronometerState.RESUMED -> {
                        chronometerState = ChronometerState.PAUSED
                        chronometerCountingJob?.cancel()
                    }
                    ChronometerState.PAUSED, ChronometerState.FINISHED -> {
                        chronometerCountingJob?.cancel()
                    }
                }
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                when (chronometerState) {
                    ChronometerState.IDLE -> {
                        chronometerState = ChronometerState.RESUMED
                        resumeJobCounting()
                    }
                    ChronometerState.RESUMED -> {
                        resumeJobCounting()
                    }
                    ChronometerState.PAUSED -> {
                        resumeJobCounting()
                    }
                    ChronometerState.FINISHED -> {
                        chronometerCountingJob?.cancel()
                    }
                }
            }
        }
    }

    fun resumeChronometer() {
        chronometerState = ChronometerState.RESUMED
        resumeJobCounting()
    }

    fun pauseChronometer() {
        chronometerCountingJob?.cancel()
        chronometerState = ChronometerState.PAUSED
    }

    fun finishChronometer(): Long {
        chronometerCountingJob?.cancel()
        chronometerState = ChronometerState.FINISHED
        val finalDuration = currentDuration
        currentDuration = 0L
        return finalDuration
    }

    fun cleanUp() {
        currentDuration = 0L
        listener.onChronometerChanged(0)
    }

    private fun resumeJobCounting() {
        chronometerCountingJob?.cancel()
        chronometerCountingJob = lifecycleOwner.lifecycleScope.launch {
            while (true) {
                currentDuration += CHRONOMETER_PERIOD
                listener.onChronometerChanged(currentDuration)
                delay(CHRONOMETER_PERIOD)
            }
        }
    }

    companion object {
        private const val CHRONOMETER_PERIOD = 25L
    }
}