package io.saagie.astonparking.controller

import io.saagie.astonparking.domain.Spot
import io.saagie.astonparking.domain.State
import io.saagie.astonparking.service.SpotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class SpotController(@Autowired val spotService: SpotService) {

    @GetMapping("/spot")
    fun getAllSpots(@RequestParam(name = "state", required = false) state: String?): MutableIterable<Spot>? {
        return spotService.getAllSpots(state)
    }

    @GetMapping("/spot/{number}")
    fun getSpot(@PathVariable(name = "number", required = true) number: Int): Spot? {
        return spotService.getSpot(number)
    }

    @PutMapping("/spot/{number}/{state}")
    fun updateSpot(@PathVariable(name = "number", required = true) number: Int,
                   @PathVariable(name = "state", required = true) state: State): Spot? {
        return spotService.updateSpot(number, state)
    }
}