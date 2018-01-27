package io.saagie.astonparking.controller

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.saagie.astonparking.domain.Proposition
import io.saagie.astonparking.domain.Schedule
import io.saagie.astonparking.domain.User
import io.saagie.astonparking.service.DrawService
import io.saagie.astonparking.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DashboardController(val userService: UserService, val drawService: DrawService) {

    @GetMapping("/dashboard")
    fun dashboard(): Dashboard {
        return Dashboard(
                users=userService.getAll().sortedBy { it.attribution },
                schedule = drawService.getCurrentSchedules(),
                proposition = drawService.getAllPropositions()?.groupBy { it.spotNumber }
        )
    }


    data class Dashboard(
            val users: List<User>,
            val schedule: List<Schedule>,
            val proposition: Map<Int, List<Proposition>>?

    )
}