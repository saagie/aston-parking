package io.saagie.astonparking.service

import io.saagie.astonparking.dao.PropositionDao
import io.saagie.astonparking.dao.ScheduleDao
import io.saagie.astonparking.domain.*
import io.saagie.astonparking.slack.SlackBot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class DrawService(
        @Autowired val userService: UserService,
        @Autowired val spotService: SpotService,
        @Autowired val emailService: EmailService,
        @Autowired val slackBot: SlackBot,
        @Autowired val propositionDao: PropositionDao,
        @Autowired val scheduleDao: ScheduleDao
) {

    @Async
    @Scheduled(cron = "0 0 10 * * MON")
    fun attribution() {
        val sortedActiveUsers = sortAndFilterUsers()
        val nextMonday = getNextMonday(LocalDate.now())
        val availableSpots = spotService.getAllSpots(State.FREE)

        val userIterator = sortedActiveUsers.iterator()
        val propositions = arrayListOf<Proposition>()
        availableSpots!!.forEach {
            if (userIterator.hasNext()) {
                val user = userIterator.next()
                propositions.addAll(generateAllProposition(it.number, user.id!!, nextMonday))

            }
        }
        propositionDao.save(propositions)
        emailService.proposition(propositions, sortedActiveUsers)
        slackBot.proposition(propositions, sortedActiveUsers, nextMonday)
    }

    fun generateAllProposition(number: Int, id: String, nextMonday: LocalDate): List<Proposition> {
        val listProps = arrayListOf<Proposition>()
        for (i in 0L..4L) {
            if (!spotAlreadyProposed(number, nextMonday.plusDays(i)) &&
                    !spotAlreadySchedule(number, nextMonday.plusDays(i))) {
                listProps.add(
                        Proposition(
                                spotNumber = number,
                                userId = id,
                                day = nextMonday.plusDays(i)
                        ))
            }
        }
        return listProps
    }

    private fun spotAlreadyProposed(number: Int, date: LocalDate?): Boolean {
        val propositions = propositionDao.findAll()
        return propositions != null && propositions.filter { it.spotNumber == number && it.day == date }.isNotEmpty()
    }

    private fun spotAlreadySchedule(number: Int, date: LocalDate): Boolean {
        val schedule = scheduleDao.findOne(date)
        return schedule != null && schedule.spots.filter { it.spotNumber == number }.isNotEmpty()

    }

    fun getNextMonday(d: LocalDate): LocalDate {
        return d.plusDays((8 - d.dayOfWeek.value).toLong())
    }

    fun sortAndFilterUsers(): List<User> {
        return userService
                .getAllActive()
                .sortedBy { it.attribution }
    }

    fun getAllPropositions(): ArrayList<Proposition>? {
        return propositionDao.findAll() as ArrayList<Proposition>?

    }

    fun acceptProposition(userId: String): Boolean {
        val propositions = propositionDao.findAll()
        val user = userService.get(userId)
        val filteredProposition = propositions.filter { it.userId == userId }
        if (filteredProposition.isNotEmpty()) {
            filteredProposition.forEach {
                var schedule = Schedule(
                        date = it.day,
                        spots = arrayListOf()
                )
                if (scheduleDao.exists(it.day)) {
                    schedule = scheduleDao.findOne(it.day)
                }
                schedule.spots.add(
                        ScheduleSpot(
                                spotNumber = it.spotNumber,
                                user = user,
                                acceptDate = LocalDateTime.now())
                )
                scheduleDao.save(schedule)
                propositionDao.delete(it.id)
                user.incrementAttribution()
            }
            userService.save(user)
            return true
        }
        return false
    }

    fun declineProposition(userId: String) {
        val propositions = propositionDao.findAll()
        val props = propositions.filter { it.userId == userId }
        propositionDao.delete(props)
        this.attribution()
    }

    fun getCurrentSchedules(): List<Schedule> {
        val currentMonday = this.getNextMonday(java.time.LocalDate.now()).minusDays(7)
        return scheduleDao.findByDateIn(listOf(currentMonday, currentMonday.plusDays(1), currentMonday.plusDays(2), currentMonday.plusDays(3), currentMonday.plusDays(4)))
    }

    fun getNextSchedules(): List<Schedule> {
        val nextMonday = this.getNextMonday(java.time.LocalDate.now())
        return scheduleDao.findByDateIn(listOf(nextMonday, nextMonday.plusDays(1), nextMonday.plusDays(2), nextMonday.plusDays(3), nextMonday.plusDays(4)))
    }
}