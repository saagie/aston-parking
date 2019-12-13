package io.saagie.astonparking.googleActions

import com.google.actions.api.ActionRequest
import com.google.actions.api.ActionResponse
import com.google.actions.api.DialogflowApp
import com.google.actions.api.ForIntent
import com.google.actions.api.response.helperintent.SignIn
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import io.saagie.astonparking.exceptions.PickException
import io.saagie.astonparking.security.BasicAuthConfig
import io.saagie.astonparking.service.DrawService
import io.saagie.astonparking.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate





@Component
class Actions(private val drawService: DrawService, private val userService: UserService, private val basicAuthConfig: BasicAuthConfig) : DialogflowApp() {

    @ForIntent("today spot")
    fun todaySpot(request: ActionRequest): ActionResponse {
        LOGGER.info("today spot.")
        val responseBuilder = getResponseBuilder(request)
        if (request.user?.userVerificationStatus != "VERIFIED") {
            responseBuilder.add("Vous n'êtes pas autorise a reserver une place")
            responseBuilder.endConversation()
        }

        if (request.user != null && userIsSignedIn(request)) {
            val mail = getUserProfile(request.user!!.idToken).email
            val user = userService.getByMail(mail)
            if (user == null) {
                responseBuilder.add("L'email qui vous utilisez n'est pas lie a votre compte aston parking. " +
                    "S'il vous plait, utilisez la commande slack ap-link pour lier votre email")
                responseBuilder.endConversation()
            } else {
                with(drawService.getSchedule(LocalDate.now())) {
                    if (this == null) {
                        responseBuilder.add("Pas de places attribuees aujourd'hui")
                        responseBuilder.endConversation()
                    } else {
                        val message = assignedSpots.filter { it.userId == user.id }.joinToString(" ") { spot ->
                            "Votre place est la place ${spot.spotNumber}"
                        }
                        if (message.isBlank()) {
                            responseBuilder.add("Vous n'avez pas de place aujourd'hui")
                            responseBuilder.add("Voulez vous reserver une place?")
                        } else {
                            responseBuilder.add(message)
                            responseBuilder.endConversation()
                        }
                    }
                    return responseBuilder.build()
                }
            }
        } else {
            responseBuilder.add(
                SignIn()
                    .setContext("You are signed in"))
            return responseBuilder.build()
        }
        responseBuilder.endConversation()
        return responseBuilder.build()
    }

    @ForIntent("pick today")
    fun pickToday(request: ActionRequest): ActionResponse {
        LOGGER.debug("pick today.")
        val responseBuilder = getResponseBuilder(request)

        if (request.user?.userVerificationStatus != "VERIFIED") {
            responseBuilder.add("Vous n'etes pas autorises a prendre une place")
        }

        if (request.user != null && userIsSignedIn(request)) {
            val mail = getUserProfile(request.user!!.idToken).email
            val user = userService.getByMail(mail)
            if (user == null) {
                responseBuilder.add("L'email qui vous utilisez n'est pas lie a votre compte aston parking. " +
                    "S'il vous plait, utilisez la commande slack ap-link pour lier votre email")
            } else {
                try {
                    val spot = drawService.pick(user.id!!, LocalDate.now())
                    responseBuilder.add("Vous avez la place $spot pour aujourd'hui.")
                } catch (e: Exception) {
                    val message = when (e) {
                        is PickException.NoScheduleError -> "Pas de places attribuees aujourd'hui"
                        is PickException.NoFreeSpotsError -> "Pas de places disponibles aujourd'hui"
                        is PickException.AlreadyPickError -> "Une place est deja reserve pour vous aujourd'hui"
                        else -> "Un erreur s'est produite."
                    }
                    responseBuilder.add(message)
                }
            }
        } else {
            responseBuilder.add(
                SignIn()
                    .setContext("You are signed in"))
            return responseBuilder.build()
        }
        responseBuilder.endConversation()
        return responseBuilder.build()
    }

    private fun userIsSignedIn(request: ActionRequest): Boolean {
        val idToken = request.user?.idToken
        LOGGER.info(String.format("Id token: %s", idToken))
        return !(idToken == null || idToken.isEmpty())
    }

    private fun getUserProfile(idToken: String): GoogleIdToken.Payload {
        return TokenDecoder(basicAuthConfig.clientId).decodeIdToken(idToken)
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(Actions::class.java)
    }
}
