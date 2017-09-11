package io.saagie.astonparking.service

import io.saagie.astonparking.dao.PropositionDao
import io.saagie.astonparking.domain.Proposition
import io.saagie.astonparking.domain.State
import io.saagie.astonparking.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DrawService(
        @Autowired val userService: UserService,
        @Autowired val spotService: SpotService,
        @Autowired val emailService: EmailService,
        @Autowired val propositionDao: PropositionDao
) {

    fun attribution() {
        val sortedActiveUsers = sortAndFilterUsers()
        val availableSpots = spotService.getAllSpots(State.FREE)
        val nextMonday = getNextMonday(LocalDate.now())

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
    }

    fun generateAllProposition(number: Int, id: String, nextMonday: LocalDate): List<Proposition> {
        val listProps = arrayListOf<Proposition>()
        for (i in 0L..4L) {
            listProps.add(
                    Proposition(
                            spotNumber = number,
                            userId = id,
                            day = nextMonday.plusDays(i)
                    ))
        }
        return listProps
    }

    fun getNextMonday(d: LocalDate): LocalDate {
        return d.plusDays((8 - d.dayOfWeek.value).toLong())
    }

    fun sortAndFilterUsers(): List<User> {
        return userService
                .getAllActive()
                .sortedBy { it.attribution }
    }
}