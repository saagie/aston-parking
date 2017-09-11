package io.saagie.astonparking.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.saagie.astonparking.dao.PropositionDao
import io.saagie.astonparking.domain.Proposition
import io.saagie.astonparking.domain.Spot
import io.saagie.astonparking.domain.State
import io.saagie.astonparking.domain.User
import io.saagie.astonparking.slack.SlackBot
import org.amshove.kluent.`it returns`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.mockito.Mockito
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.util.*


class DrawServiceTest {

    val allUsers = initAllUser()

    val allSpots = initAllSpots()

    val userService = mock<UserService> {
        on { getAllActive() } `it returns` allUsers
    }

    val spotService = mock<SpotService> {
        on { getAllSpots(State.FREE) } `it returns` allSpots

    }
    val emailService = mock<EmailService> {

    }
    val propositionDao = mock<PropositionDao> {

    }
    val slackBot = mock<SlackBot> {

    }

    val loterryService = DrawService(userService, spotService, emailService, slackBot, propositionDao)

    @Test
    fun should_return_the_list_of_active_users_in_the_right_order() {
        //Given
        //When
        val users = loterryService.sortAndFilterUsers()
        //Then
        users.map { u -> u.attribution } shouldEqual listOf(1, 2, 3, 4)
    }

    @Test
    fun should_return_the_next_monday() {
        //Given
        val d1 = LocalDate.parse("2017-09-04")
        val d2 = LocalDate.parse("2017-09-05")
        val d3 = LocalDate.parse("2017-09-06")
        val d4 = LocalDate.parse("2017-09-07")
        val d5 = LocalDate.parse("2017-09-08")
        val d6 = LocalDate.parse("2017-09-09")
        val d7 = LocalDate.parse("2017-09-10")
        //When
        val nextMonday1 = loterryService.getNextMonday(d1)
        val nextMonday2 = loterryService.getNextMonday(d2)
        val nextMonday3 = loterryService.getNextMonday(d3)
        val nextMonday4 = loterryService.getNextMonday(d4)
        val nextMonday5 = loterryService.getNextMonday(d5)
        val nextMonday6 = loterryService.getNextMonday(d6)
        val nextMonday7 = loterryService.getNextMonday(d7)
        //Then
        nextMonday1 `should equal` nextMonday2
        nextMonday1 `should equal` nextMonday3
        nextMonday1 `should equal` nextMonday4
        nextMonday1 `should equal` nextMonday5
        nextMonday1 `should equal` nextMonday6
        nextMonday1 `should equal` nextMonday7
        nextMonday1.dayOfWeek `should equal` DayOfWeek.MONDAY
        nextMonday1.isAfter(d1) `should equal` true
        nextMonday1.isAfter(d2) `should equal` true
        nextMonday1.isAfter(d3) `should equal` true
        nextMonday1.isAfter(d4) `should equal` true
        nextMonday1.isAfter(d5) `should equal` true
        nextMonday1.isAfter(d6) `should equal` true
        nextMonday1.isAfter(d7) `should equal` true
        nextMonday1.dayOfMonth `should equal` 11
        nextMonday1.monthValue `should equal` 9
        nextMonday1.year `should equal` 2017
    }

    @Test
    fun should_return_list_of_propositions() {
        //Given
        val nextMonday = LocalDate.parse("2017-09-11")
        val id = "ID"
        val number = 1
        //When
        val generateAllProposition = loterryService.generateAllProposition(number = number, id = id, nextMonday = nextMonday)
        //Then
        generateAllProposition.size `should equal` 5
        generateAllProposition.map { it.day } `should equal` (
                arrayListOf(
                        LocalDate.parse("2017-09-11"),
                        LocalDate.parse("2017-09-12"),
                        LocalDate.parse("2017-09-13"),
                        LocalDate.parse("2017-09-14"),
                        LocalDate.parse("2017-09-15")
                )
                )
    }

    @Test
    fun should_make_attributions() {
        //Given
        //When
        loterryService.attribution()
        //Then
        verify(propositionDao, times(1)).save(Mockito.anyListOf(Proposition::class.java))
        verify(emailService, times(1)).proposition(Mockito.anyListOf(Proposition::class.java), Mockito.anyListOf(User::class.java))
    }

    private fun initAllUser(): List<User> {
        return arrayListOf<User>(
                User(
                        id = "ID1",
                        username = "User1",
                        email = "mail1@mail.com",
                        creationDate = Date.from(Instant.now()),
                        attribution = 1,
                        enable = true,
                        activated = true),
                User(
                        id = "ID3",
                        username = "User3",
                        email = "mail3@mail.com",
                        creationDate = Date.from(Instant.now()),
                        attribution = 3,
                        enable = true,
                        activated = false),
                User(
                        id = "ID2",
                        username = "User2",
                        email = "mail2@mail.com",
                        creationDate = Date.from(Instant.now()),
                        attribution = 2,
                        enable = true,
                        activated = true),
                User(
                        id = "ID4",
                        username = "User4",
                        email = "mail4@mail.com",
                        creationDate = Date.from(Instant.now()),
                        attribution = 4,
                        enable = true,
                        activated = true)
        )
    }

    private fun initAllSpots(): List<Spot> {
        return listOf<Spot>(
                Spot(id = "SPOT0", number = 100, state = State.FREE),
                Spot(id = "SPOT1", number = 101, state = State.FREE),
                Spot(id = "SPOT2", number = 102, state = State.FREE)
        )
    }
}
