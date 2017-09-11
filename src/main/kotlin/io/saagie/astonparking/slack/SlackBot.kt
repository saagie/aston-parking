package io.saagie.astonparking.slack


import io.saagie.astonparking.domain.Proposition
import io.saagie.astonparking.domain.User
import me.ramswaroop.jbot.core.slack.Bot
import me.ramswaroop.jbot.core.slack.Controller
import me.ramswaroop.jbot.core.slack.EventType
import me.ramswaroop.jbot.core.slack.models.Event
import me.ramswaroop.jbot.core.slack.models.Message
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.WebSocketSession
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Component
class SlackBot : Bot() {

    @Value("\${slackBotToken}")
    private val slackToken: String? = null

    @Value("\${slackWebhookUrl}")
    private val slackWebhookUrl: String? = null

    override fun getSlackToken(): String? {
        return slackToken
    }

    override fun getSlackBot(): Bot {
        return this
    }

    val logger = LoggerFactory.getLogger("SlackBot")


    @Controller(events = arrayOf(EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE))
    fun onReceiveDM(session: WebSocketSession, event: Event) {
        reply(session, event, Message(
                """Hi, I am ${slackService.currentUser.name}
                    |You can interact with me using commands.
                    |To have all available commands, type : /command
                """.trimMargin()))
    }

    fun proposition(propositions: ArrayList<Proposition>, sortedActiveUsers: List<User>, nextMonday: LocalDate) {

        val message = Message("*******************\n")
        message.text += ":game_die: Draw is done for the week started the ${nextMonday.format(DateTimeFormatter.ISO_DATE)} \n"
        message.text += generateTextForSlack(propositions, sortedActiveUsers)
        message.text += "\nYou can see attributions for the current and the next week by using the command /attribution\n"
        message.text += "*******************"
        val restTemplate = RestTemplate()
        try {
            restTemplate.postForEntity<String>(slackWebhookUrl, message, String::class.java)
        } catch (e: RestClientException) {
            logger.error("Error posting to Slack Incoming Webhook: ", e)
        }
    }

    fun generateTextForSlack(propositions: ArrayList<Proposition>, sortedActiveUsers: List<User>): String {
        val spotPerUser = propositions.associateBy(
                keySelector = { prop -> prop.spotNumber },
                valueTransform = { prop ->
                    sortedActiveUsers.filter { it.id == prop.userId }.first()
                })

        var message = ""
        spotPerUser.forEach(
                {
                    message += ":parking: *${it.key}* :arrow_right: <@${it.value.id}|${it.value.username}>\n"
                }
        )
        return message
    }


}