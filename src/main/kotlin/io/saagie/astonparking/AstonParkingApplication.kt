package io.saagie.astonparking

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication(scanBasePackages = arrayOf("me.ramswaroop.jbot", "io.saagie.astonparking"))
class AstonParkingApplication

fun main(args: Array<String>) {
    SpringApplication.run(AstonParkingApplication::class.java, *args)
}
