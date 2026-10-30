package com.shadowboxstudios.focustimer

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView

/**
 * Focus Timer — a simple Pomodoro timer by ShadowBox Studios.
 * No accounts, no network, no data collection: everything runs on-device.
 */
class MainActivity : Activity() {

    private enum class Mode(val label: String, val minutes: Long, val isFocus: Boolean) {
        FOCUS("Focus", 25, true),
        SHORT_BREAK("Short break", 5, false),
        LONG_BREAK("Long break", 15, false)
    }

    private lateinit var timeLabel: TextView
    private lateinit var modeLabel: TextView
    private lateinit var cycleLabel: TextView
    private lateinit var startPauseButton: Button

    private var mode: Mode = Mode.FOCUS
    private var remainingMillis: Long = Mode.FOCUS.minutes * 60_000L
    private var timer: CountDownTimer? = null
    private var running = false
    private var completedSessions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeLabel = findViewById(R.id.timeLabel)
        modeLabel = findViewById(R.id.modeLabel)
        cycleLabel = findViewById(R.id.cycleLabel)
        startPauseButton = findViewById(R.id.startPauseButton)

        findViewById<Button>(R.id.focusButton).setOnClickListener { setMode(Mode.FOCUS) }
        findViewById<Button>(R.id.shortBreakButton).setOnClickListener { setMode(Mode.SHORT_BREAK) }
        findViewById<Button>(R.id.longBreakButton).setOnClickListener { setMode(Mode.LONG_BREAK) }
        findViewById<Button>(R.id.resetButton).setOnClickListener { reset() }
        startPauseButton.setOnClickListener { if (running) pause() else start() }

        render()
    }

    private fun setMode(newMode: Mode) {
        stopTimer()
        mode = newMode
        remainingMillis = newMode.minutes * 60_000L
        render()
    }

    private fun start() {
        timer = object : CountDownTimer(remainingMillis, 250) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                renderTime()
            }

            override fun onFinish() {
                running = false
                if (mode.isFocus) {
                    completedSessions++
                    setMode(Mode.SHORT_BREAK)
                } else {
                    setMode(Mode.FOCUS)
                }
            }
        }.start()
        running = true
        render()
    }

    private fun pause() {
        stopTimer()
        render()
    }

    private fun reset() {
        stopTimer()
        remainingMillis = mode.minutes * 60_000L
        render()
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
        running = false
    }

    private fun render() {
        modeLabel.text = mode.label
        startPauseButton.text = if (running) "Pause" else "Start"
        cycleLabel.text = "Completed sessions: $completedSessions"
        renderTime()
    }

    private fun renderTime() {
        val totalSeconds = (remainingMillis + 999) / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        timeLabel.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }
}
