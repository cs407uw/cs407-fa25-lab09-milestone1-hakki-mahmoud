package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BallViewModel : ViewModel() {

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    // scales m/s^2 from the gravity sensor into pixels/s^2
    private val ACCEL_SCALE = 80f

    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    /**
     * Called by the UI when the game field's size is known.
     */
    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            ball = Ball(
                backgroundWidth = fieldWidth,
                backgroundHeight = fieldHeight,
                ballSize = ballSizePx
            ).also { b ->
                _ballPosition.value = Offset(b.posX, b.posY)
            }
            lastTimestamp = 0L
        }
    }

    /**
     * Called by the SensorEventListener in the UI.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            if (lastTimestamp != 0L) {
                // Time delta in seconds (timestamps are in nanoseconds)
                val NS2S = 1.0f / 1_000_000_000.0f
                val dT = (event.timestamp - lastTimestamp) * NS2S

                val rawX = event.values[0]
                val rawY = event.values[1]

                // X stays inverted, Y is NOT inverted so positive rawY => down on screen
                val xAcc = -rawX * ACCEL_SCALE
                val yAcc =  rawY * ACCEL_SCALE

                currentBall.updatePositionAndVelocity(
                    xAcc = xAcc,
                    yAcc = yAcc,
                    dT = dT
                )

                _ballPosition.update {
                    Offset(currentBall.posX, currentBall.posY)
                }
            }
            lastTimestamp = event.timestamp
        }
    }

    fun reset() {
        ball?.reset()
        ball?.let { b ->
            _ballPosition.value = Offset(b.posX, b.posY)
        }
        lastTimestamp = 0L
    }
}
