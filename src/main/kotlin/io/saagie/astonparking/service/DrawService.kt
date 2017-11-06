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
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class DrawService(
        @Autowired val userService: UserService,
        @Autowired val spotService: SpotService,
        @Autowired val emailService: EmailService,
        @Autowired val slackBot: SlackBot,
        @Autowired val propositionDao: PropositionDao,
        @Autowired val scheduleDao: ScheduleDao
) {

    @Scheduled(cron = "0 0 10 * * MON")
    fun scheduleAttribution() {
        userService.resetAllSelectedAttribution()
        propositionDao.deleteAll()
        this.attribution()
    }

    @Async
    fun attribution() {
        val sortedActiveUsers = sortAndFilterUsers().filter { it.alreadySelected == false }
        val nextMonday = getNextMonday(LocalDate.now())
        val availableSpots = spotService.getAllSpots(State.FREE)

        val userIterator = sortedActiveUsers.iterator()
        val propositions = arrayListOf<Proposition>()
        availableSpots!!.forEach {
            if (userIterator.hasNext()) {
                val user = userIterator.next()
                propositions.addAll(generateAllProposition(it.number, user.id!!, nextMonday))
                user.alreadySelected = true
                userService.save(user)
            }
        }
        propositionDao.save(propositions)
        emailService.proposition(propositions, sortedActiveUsers)
        slackBot.proposition(propositions, sortedActiveUsers, nextMonday)
    }

    fun generateAllProposition(number: Int, userId: String, nextMonday: LocalDate): List<Proposition> {
        val listProps = arrayListOf<Proposition>()
        for (i in 0L..4L) {
            if (!spotAlreadyProposed(number, nextMonday.plusDays(i)) &&
                    !spotAlreadySchedule(number, nextMonday.plusDays(i))) {
                listProps.add(
                        Proposition(
                                spotNumber = number,
                                userId = userId,
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
        return schedule != null && schedule.assignedSpots.filter { it.spotNumber == number }.isNotEmpty()

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
            acceptAllPropositions(filteredProposition, user)
            return true
        }
        return false
    }

    @Async
    fun acceptAllPropositions(filteredProposition: List<Proposition>, user: User) {
        filteredProposition.forEach {
            var schedule = Schedule(
                    date = it.day,
                    assignedSpots = arrayListOf(),
                    freeSpots = arrayListOf(),
                    userSelected = arrayListOf()
            )
            if (scheduleDao.exists(it.day)) {
                schedule = scheduleDao.findOne(it.day)
            }
            schedule.userSelected.add(user.id!!)
            schedule.assignedSpots.add(
                    ScheduleSpot(
                            spotNumber = it.spotNumber,
                            userId = user.id!!,
                            username = user.username,
                            acceptDate = LocalDateTime.now())
            )
            scheduleDao.save(schedule)
            propositionDao.delete(it.id)
            user.incrementAttribution()
        }
        userService.save(user)
    }

    @Async
    fun declineProposition(userId: String) {
        val propositions = propositionDao.findAll()
        val props = propositions.filter { it.userId == userId }
        propositionDao.delete(props)
        this.attribution()
    }

    fun getCurrentSchedules(): List<Schedule> {
        return getSchedules(this.getNextMonday(java.time.LocalDate.now()).minusDays(7))
    }

    fun getNextSchedules(): List<Schedule> {
        return getSchedules(this.getNextMonday(java.time.LocalDate.now()))
    }

    fun getSchedules(date: LocalDate): List<Schedule> {
        return scheduleDao.findByDateIn(listOf(date, date.plusDays(1), date.plusDays(2), date.plusDays(3), date.plusDays(4)))
    }

    @Async
    fun release(userId: String, text: String) {
        val date = extractDate(text)
        val user = userService.get(userId)
        val schedule = scheduleDao.findByDate(date)
        val spotToBeDeleted = schedule.assignedSpots.filter { it.userId == userId }
        schedule.assignedSpots.removeAll(spotToBeDeleted)
        schedule.freeSpots.add(spotToBeDeleted.first().spotNumber)
        scheduleDao.save(schedule)
        user.attribution = user.attribution - 1
        userService.save(user)
    }

    private fun extractDate(text: String): LocalDate {
        val now = LocalDate.now()
        val date = LocalDate.parse(text + "/${now.year}", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        if (now.isAfter(date)) {
            throw IllegalArgumentException("Date can't be before today")
        }
        return date
    }

    fun pick(userId: String, text: String): Int {
        val date = extractDate(text)
        val user = userService.get(userId)
        val schedule = scheduleDao.findByDate(date)
        if (schedule.freeSpots.isEmpty())
            throw IllegalArgumentException("No free spot for the date ${text}")
        val freeSpot = schedule.freeSpots.first()
        schedule.freeSpots.removeAt(0)
        schedule.userSelected.add(user.id!!)
        schedule.assignedSpots.add(
                ScheduleSpot(
                        spotNumber = freeSpot,
                        userId = user.id,
                        username = user.username,
                        acceptDate = LocalDateTime.now())
        )
        scheduleDao.save(schedule)
        user.attribution = user.attribution + 1
        userService.save(user)
        return freeSpot
    }
}