package io.saagie.astonparking.exceptions

import java.lang.IllegalArgumentException
import java.time.LocalDate

sealed class PickException(message: String): IllegalArgumentException(message) {
    data class NoScheduleError(private val date: LocalDate) : PickException("No schedule for the date $date")
    data class NoFreeSpotsError(private val date: LocalDate) : PickException("No free spot for the date $date")
    object AlreadyPickError : PickException("A spot is already reserved for you")

}