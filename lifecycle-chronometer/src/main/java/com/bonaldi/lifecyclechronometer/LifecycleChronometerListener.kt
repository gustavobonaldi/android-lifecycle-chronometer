package com.bonaldi.lifecyclechronometer

interface LifecycleChronometerListener {
    fun onChronometerChanged(timeInMillis: Long)
}