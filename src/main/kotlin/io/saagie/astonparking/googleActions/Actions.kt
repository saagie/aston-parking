package io.saagie.astonparking.googleActions

import com.google.actions.api.ActionRequest
import com.google.actions.api.ActionResponse
import com.google.actions.api.DialogflowApp
import com.google.actions.api.ForIntent
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import io.saagie.astonparking.service.DrawService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate



@Component
class Actions(private val drawService: DrawService) : DialogflowApp() {

    @ForIntent("today spots")
    fun todaySpots(request: ActionRequest): ActionResponse {
        LOGGER.info("today spots.")
        val responseBuilder = getResponseBuilder(request)
        with(drawService.getSchedule(LocalDate.now())) {
            if (this == null) {
                responseBuilder.add("No spots attributed today.")
            } else {
                val message = assignedSpots.sortedBy { it.spotNumber }.map { spot ->
                    "${spot.spotNumber} ${spot.username}"
                }.plus(
                    freeSpots.sorted().map { spotNumber ->
                        "$spotNumber FREE"
                    }
                ).joinToString(" ")
                responseBuilder.add(message)
            }
            responseBuilder.endConversation();
            return responseBuilder.build()
        }
    }

    @ForIntent("today free spots")
    fun todayFreeSpots(request: ActionRequest): ActionResponse {
        LOGGER.info("today free spots.")
        val responseBuilder = getResponseBuilder(request)
        with(drawService.getSchedule(LocalDate.now())) {
            if (this == null) {
                responseBuilder.add("No spots attributed today.")
            } else {
                val message = freeSpots.sorted().joinToString(" ") { spotNumber ->
                    "$spotNumber FREE"
                }
                if (message.isBlank()) {
                    responseBuilder.add("No available spots.")
                } else {
                    responseBuilder.add(message)
                }
            }
            responseBuilder.endConversation();
            return responseBuilder.build()
        }
    }

    @ForIntent("today spot")
    fun todaySpot(request: ActionRequest): ActionResponse {
        LOGGER.info("today spot.")
        val responseBuilder = getResponseBuilder(request)
        val usernameParam = request.getParameter("username") as String
        with(drawService.getSchedule(LocalDate.now())) {
            if (this == null) {
                responseBuilder.add("No spots attributed today.")
            } else {
                val message = assignedSpots.filter { it.username.toLowerCase() == usernameParam.toLowerCase() }.map { spot ->
                    "${spot.spotNumber} ${spot.username}"
                }.joinToString(" ")
                if (message.isBlank()) {
                    responseBuilder.add("You have no spot today.")
                } else {
                    responseBuilder.add(message)
                }
            }
            responseBuilder.endConversation();
            return responseBuilder.build()
        }
    }
    companion object {

        private val LOGGER = LoggerFactory.getLogger(Actions::class.java)
    }
}
