package io.saagie.astonparking.controller

import io.saagie.astonparking.service.DrawService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class DrawController(
        @Autowired val drawService: DrawService
) {

    @PostMapping("/draw")
    @ResponseStatus(HttpStatus.OK)
    fun makeADraw() {
        drawService.attribution()
    }
}