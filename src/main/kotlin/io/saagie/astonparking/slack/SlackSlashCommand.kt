package io.saagie.astonparking.slack

import io.saagie.astonparking.service.UserService
import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class SlackSlashCommand(@Autowired val userService: UserService) {

    @Value("\${url}")
    private val url: String? = null

    @RequestMapping(value = "/slack/command",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveHelpCommand(@RequestParam("token") token: String,
                             @RequestParam("team_id") teamId: String,
                             @RequestParam("team_domain") teamDomain: String,
                             @RequestParam("channel_id") channelId: String,
                             @RequestParam("channel_name") channelName: String,
                             @RequestParam("user_id") userId: String,
                             @RequestParam("user_name") userName: String,
                             @RequestParam("command") command: String,
                             @RequestParam("text") text: String,
                             @RequestParam("response_url") responseUrl: String): RichMessage {
        val richMessage = RichMessage("---Aston Parking : Available commands--- ")
        val attachments = arrayOf(
                Attachment().apply {
                    setText("/command : this list of all available commands")
                },
                Attachment().apply {
                    setText("/register : to register you as a new member of Aston Parking")
                },
                Attachment().apply {
                    setText("/profile : to display your AstonParking profile")
                },
                Attachment().apply {
                    setText("/disable : to disable your AstonParking profile")
                },
                Attachment().apply {
                    setText("/enable : to enable your AstonParking profile")
                }
        )
        richMessage.attachments = attachments


        return richMessage.encodedMessage()
    }

    @RequestMapping(value = "/slack/register",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveRegisterCommand(@RequestParam("token") token: String,
                                 @RequestParam("team_id") teamId: String,
                                 @RequestParam("team_domain") teamDomain: String,
                                 @RequestParam("channel_id") channelId: String,
                                 @RequestParam("channel_name") channelName: String,
                                 @RequestParam("user_id") userId: String,
                                 @RequestParam("user_name") userName: String,
                                 @RequestParam("command") command: String,
                                 @RequestParam("text") text: String,
                                 @RequestParam("response_url") responseUrl: String): RichMessage {
        val registerUser = userService.registerUser(userName, userId)
        val richMessage = RichMessage("Register : ${userName}")
        val attachments = arrayOfNulls<Attachment>(1)
        attachments[0] = Attachment()
        attachments[0]!!.setText("Welcome on Aston Parking ${userName}. Please log in on the website : ${url} to complete your registration.")
        if (!registerUser) {
            attachments[0]!!.setText("You are already registred on AstonParking. Use /profile to see your profile.")
        }
        richMessage.attachments = attachments


        return richMessage.encodedMessage()
    }

    @RequestMapping(value = "/slack/profile/active",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveActiveProfileCommand(@RequestParam("token") token: String,
                                      @RequestParam("team_id") teamId: String,
                                      @RequestParam("team_domain") teamDomain: String,
                                      @RequestParam("channel_id") channelId: String,
                                      @RequestParam("channel_name") channelName: String,
                                      @RequestParam("user_id") userId: String,
                                      @RequestParam("user_name") userName: String,
                                      @RequestParam("command") command: String,
                                      @RequestParam("text") text: String,
                                      @RequestParam("response_url") responseUrl: String): RichMessage {
        val registerUser = userService.changeStatus(userId, true)
        val richMessage = RichMessage("Activate Profile : ${userName}")
        val attachments = arrayOfNulls<Attachment>(1)
        attachments[0] = Attachment()
        attachments[0]!!.setText("Your profile is now active.")
        richMessage.attachments = attachments

        return richMessage.encodedMessage()
    }

    @RequestMapping(value = "/slack/profile/inactive",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveInactiveProfileCommand(@RequestParam("token") token: String,
                                        @RequestParam("team_id") teamId: String,
                                        @RequestParam("team_domain") teamDomain: String,
                                        @RequestParam("channel_id") channelId: String,
                                        @RequestParam("channel_name") channelName: String,
                                        @RequestParam("user_id") userId: String,
                                        @RequestParam("user_name") userName: String,
                                        @RequestParam("command") command: String,
                                        @RequestParam("text") text: String,
                                        @RequestParam("response_url") responseUrl: String): RichMessage {
        val registerUser = userService.changeStatus(userId, false)
        val richMessage = RichMessage("Desactivate Profile : ${userName}")
        val attachments = arrayOfNulls<Attachment>(1)
        attachments[0] = Attachment()
        attachments[0]!!.setText("Your profile is now inactive.")
        richMessage.attachments = attachments

        return richMessage.encodedMessage()
    }

    @RequestMapping(value = "/slack/profile",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveProfileCommand(@RequestParam("token") token: String,
                                @RequestParam("team_id") teamId: String,
                                @RequestParam("team_domain") teamDomain: String,
                                @RequestParam("channel_id") channelId: String,
                                @RequestParam("channel_name") channelName: String,
                                @RequestParam("user_id") userId: String,
                                @RequestParam("user_name") userName: String,
                                @RequestParam("command") command: String,
                                @RequestParam("text") text: String,
                                @RequestParam("response_url") responseUrl: String): RichMessage {


        try {
            val user = userService.get(userId)
            val richMessage = RichMessage("Profile : ${user.username}")
            if (!user.activated) {
                richMessage.text = richMessage.text + """
                    - :warning: *PROFILE NOT ACTIVATED* :warning: -
                    Please log in AstonParking to activate your profile (${url})
                    """.trimIndent()
            }
            val attachments = arrayOf(
                    Attachment().apply {
                        setText("Username : ${user.username}")
                    },
                    Attachment().apply {
                        setText("Email : ${user.email}")
                    },
                    Attachment().apply {
                        setText("Attribution : ${user.attribution}")
                    },
                    Attachment().apply {
                        setText("Karma : ${user.karma}")
                    },
                    Attachment().apply {
                        setText("Demande : ${user.demande}")
                    },
                    Attachment().apply {
                        setText("Status :  ${user.status()}")
                    }
            )
            richMessage.attachments = attachments

            return richMessage.encodedMessage()
        } catch (e: Exception) {
            val richMessage = RichMessage("The profile : ${userName} doesn't exist. Type /register to create your profile.")
            return richMessage.encodedMessage()
        }

    }
}