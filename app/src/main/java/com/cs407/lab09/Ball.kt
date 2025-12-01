package com.cs407.lab09

/**
 * Represents a ball that can move. (No Android UI imports!)
 *
 * Constructor parameters:
 * - backgroundWidth: the width of the background, of type Float
 * - backgroundHeight: the height of the background, of type Float
 * - ballSize: the width/height of the ball, of type Float
 */
class Ball(
    private val backgroundWidth: Float,
    private val backgroundHeight: Float,
    private val ballSize: Float
) {
    // Position (in pixels, screen coordinates: x→right, y→down)
    var posX = 0f
    var posY = 0f

    // Velocity (pixels / second)
    var velocityX = 0f
    var velocityY = 0f

    // Acceleration from previous step (pixels / s^2, derived from gravity)
    private var accX = 0f
    private var accY = 0f

    private var isFirstUpdate = true

    init {
        // Start in the center with zero motion
        reset()
    }

    /**
     * Updates the ball's position and velocity based on the given acceleration and time step.
     *
     * xAcc, yAcc: current acceleration components (a1) for x and y axes.
     * dT: time delta between t0 and t1 in seconds.
     *
     * Uses Equations (1) and (2) from the handout:
     *   v1 = v0 + 1/2 (a1 + a0) * Δt
     *   l  = v0 * Δt + 1/6 * Δt^2 * (3a0 + a1)
     */
    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
        // First call: just initialize acceleration so we have a0 for the next step
        if (isFirstUpdate) {
            isFirstUpdate = false
            accX = xAcc
            accY = yAcc
            return
        }

        val dt = dT

        // ---- X axis ----
        val v0x = velocityX
        val a0x = accX
        val a1x = xAcc

        val v1x = v0x + 0.5f * (a1x + a0x) * dt
        val dx = v0x * dt + (1f / 6f) * (dt * dt) * (3f * a0x + a1x)

        // ---- Y axis ----
        val v0y = velocityY
        val a0y = accY
        val a1y = yAcc

        val v1y = v0y + 0.5f * (a1y + a0y) * dt
        val dy = v0y * dt + (1f / 6f) * (dt * dt) * (3f * a0y + a1y)

        // Apply displacement
        posX += dx
        posY += dy

        // Store new velocity & acceleration
        velocityX = v1x
        velocityY = v1y
        accX = a1x
        accY = a1y

        // Keep the ball within bounds
        checkBoundaries()
    }

    /**
     * Ensures the ball does not move outside the boundaries.
     * When it collides, velocity and acceleration perpendicular to the
     * boundary should be set to 0.
     */
    fun checkBoundaries() {
        // Left boundary
        if (posX < 0f) {
            posX = 0f
            velocityX = 0f
            accX = 0f
        }

        // Right boundary
        val maxX = backgroundWidth - ballSize
        if (posX > maxX) {
            posX = maxX
            velocityX = 0f
            accX = 0f
        }

        // Top boundary
        if (posY < 0f) {
            posY = 0f
            velocityY = 0f
            accY = 0f
        }

        // Bottom boundary
        val maxY = backgroundHeight - ballSize
        if (posY > maxY) {
            posY = maxY
            velocityY = 0f
            accY = 0f
        }
    }

    /**
     * Resets the ball to the center of the screen with zero
     * velocity and acceleration.
     */
    fun reset() {
        posX = (backgroundWidth - ballSize) / 2f
        posY = (backgroundHeight - ballSize) / 2f
        velocityX = 0f
        velocityY = 0f
        accX = 0f
        accY = 0f
        isFirstUpdate = true
    }
}
