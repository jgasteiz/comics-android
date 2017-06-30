package com.jgasteiz.comics_android.interfaces

import java.util.*

interface TimersDownloadInterface {

    /**
     * Creates a new timer for checking the download progress, adds it to
     * the timers list and returns it.
     */
    fun getNewDownloadProgressTimer(id: Int): Timer

    /**
     * Cancel all existing timers.
     */
    fun cancelAllTimers()
}