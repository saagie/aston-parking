package io.saagie.astonparking.rule

import com.nhaarman.mockito_kotlin.mock
import io.saagie.astonparking.dao.PropositionDao
import io.saagie.astonparking.dao.RequestDao
import io.saagie.astonparking.dao.ScheduleDao
import io.saagie.astonparking.domain.Request
import io.saagie.astonparking.domain.Schedule
import io.saagie.astonparking.domain.State
import io.saagie.astonparking.service.DrawServiceTest
import io.saagie.astonparking.service.EmailService
import io.saagie.astonparking.service.SpotService
import io.saagie.astonparking.service.UserService
import io.saagie.astonparking.slack.SlackBot
import org.amshove.kluent.`it returns`
import org.amshove.kluent.any
import org.amshove.kluent.shouldEqual
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import java.time.LocalDate
import java.time.LocalDateTime

class DrawRulesTest{

    private val allUsers= DrawServiceTest().allUsers
    private val allPropositions = DrawServiceTest().allPropositions


    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T

    val userService = mock<UserService> {
        on { getAllActive() } `it returns` allUsers
        on { get(allUsers.first().id!!) } `it returns` allUsers.first()
    }

    val propositionDao = mock<PropositionDao> {
        on { findAll() } `it returns` allPropositions
    }
    val scheduleDao = mock<ScheduleDao> {
        on { exists(any()) } `it returns` true
        on { findByDate(any()) } `it returns` Schedule(date = LocalDate.now(), assignedSpots = arrayListOf(), userSelected = arrayListOf(), freeSpots = arrayListOf())
    }


    val drawRules = DrawRules(userService, propositionDao, scheduleDao)

     @Test
    fun should_return_the_list_of_active_users_in_the_right_order() {
        //Given
        //When
        val users = drawRules.sortAndFilterUsers()
        //Then
        users.map { u -> u.attribution } shouldEqual listOf(0, 2, 3, 4)
    }
}