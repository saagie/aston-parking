package io.saagie.astonparking.rule

import io.saagie.astonparking.dao.PropositionDao
import io.saagie.astonparking.dao.ScheduleDao
import io.saagie.astonparking.domain.User
import io.saagie.astonparking.service.UserService
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DrawRules(val userService: UserService, val propositionDao: PropositionDao, val scheduleDao: ScheduleDao) {


    /**
     * List all users for the draw according some rules :
     * sort by attribution
     * filter (no fixed spots and active)
     */
    fun sortAndFilterUsers(): List<User> {
        return userService
                .getAllActive()
                .filter { !it.hasFixedSpot }
                .sortedBy { it.attribution }
    }


    /**
     * Check if spot is already proposed for the date
     */
    fun spotAlreadyProposed(number: Int, date: LocalDate): Boolean {
        val propositions = propositionDao.findAll()
        return propositions != null && propositions.filter { it.spotNumber == number && it.day == date }.isNotEmpty()
    }

    /**
     * Check if spot is already scheduled for the date
     */
    fun spotAlreadySchedule(number: Int, date: LocalDate): Boolean {
        val schedule = scheduleDao.findByDate(date)
        return schedule != null && schedule.assignedSpots.filter { it.spotNumber == number }.isNotEmpty()

    }

    /**
     * Check if date is correct :
     * /release : before 8 o'clock the same day
     */
    fun checkIfDateIsOk(date: LocalDate): Boolean {
        val now = LocalDate.now()
        val localDateTime = LocalDateTime.now()
        when {
            now != date -> return true
            else -> return (localDateTime.hour < 8)
        }
    }
}