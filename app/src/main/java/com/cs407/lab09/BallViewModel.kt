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

    // Expose the ball's position as a StateFlow
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
            // Start fresh timing
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

                // Hint from starter: the sensor's x and y-axis are inverted
                val xAcc = -rawX
                val yAcc = -rawY

                // Update the ball's physics state
                currentBall.updatePositionAndVelocity(
                    xAcc = xAcc,
                    yAcc = yAcc,
                    dT = dT
                )

                // Notify the UI
                _ballPosition.update {
                    Offset(currentBall.posX, currentBall.posY)
                }
            }

            // Store timestamp for next delta calculation
            lastTimestamp = event.timestamp
        }
    }

    fun reset() {
        // Reset the ball's physics state
        ball?.reset()

        // Update the StateFlow with the reset position
        ball?.let { b ->
            _ballPosition.value = Offset(b.posX, b.posY)
        }

        // Reset timing so the next event will be treated as the first
        lastTimestamp = 0L
    }
}
