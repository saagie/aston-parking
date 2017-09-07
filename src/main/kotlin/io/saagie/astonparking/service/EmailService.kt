package io.saagie.astonparking.service

import io.saagie.astonparking.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context


@Service
class EmailService(
        @Autowired val mailSender: JavaMailSender,
        @Autowired val templateEngine: TemplateEngine) {

    @Value("\${sendEmail:true}")
    val sendEmail = true

    @Value("\${url}")
    val url = ""


    @Async
    fun profileCreated(user: User) {
        val context = Context()
        context.setVariable("user", user)
        context.setVariable("url", url)

        val messagePreparator = MimeMessagePreparator { mimeMessage ->
            val messageHelper = MimeMessageHelper(mimeMessage)
            messageHelper.setTo(user.email)
            messageHelper.setSubject("Account created")
            messageHelper.setText(templateEngine.process("accountCreated", context), true)
        }

        send(messagePreparator)
    }

    @Async
    fun profileStatusChange(user: User) {
        val context = Context()
        context.setVariable("user", user)
        context.setVariable("url", url)

        val messagePreparator = MimeMessagePreparator { mimeMessage ->
            val messageHelper = MimeMessageHelper(mimeMessage)
            messageHelper.setTo(user.email)
            messageHelper.setSubject("Status Change")
            messageHelper.setText(templateEngine.process("statusChange", context), true)
        }

        send(messagePreparator)
    }

    private fun send(messagePreparator: MimeMessagePreparator) {
        try {
            if (sendEmail) {
                mailSender.send(messagePreparator)
            }
        } catch (e: Exception) {

        }
    }
}