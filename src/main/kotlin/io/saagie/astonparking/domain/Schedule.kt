package io.saagie.astonparking.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document
data class Schedule(
        @Id val date: LocalDate,
        val spots: ArrayList<ScheduleSpot>
)

data class ScheduleSpot(
        val spotNumber: Int,
        val user: User,
        val acceptDate: LocalDateTime
)