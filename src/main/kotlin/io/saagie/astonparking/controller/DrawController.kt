package io.saagie.astonparking.controller

import io.saagie.astonparking.service.DrawService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DrawController(
        @Autowired val drawService: DrawService
) {

    @PostMapping("/draw")
    fun makeADraw() {
        drawService.attribution()
    }
}