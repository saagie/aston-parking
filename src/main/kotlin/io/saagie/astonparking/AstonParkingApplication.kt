package io.saagie.astonparking

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor


@SpringBootApplication(scanBasePackages = arrayOf("me.ramswaroop.jbot", "io.saagie.astonparking"))
@EnableAsync
@EnableScheduling
class AstonParkingApplication {
    @Bean
    fun asyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 4
        executor.setQueueCapacity(500)
        executor.threadNamePrefix = "AstonParking-"
        executor.initialize()
        return executor
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(AstonParkingApplication::class.java, *args)
}
