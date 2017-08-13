package io.saagie.astonparking.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Spot(
        @Id val id: String,
        val number: Int,
        var state: State
)