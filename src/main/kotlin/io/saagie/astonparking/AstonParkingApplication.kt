package io.saagie.astonparking

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
        var executor = ThreadPoolTaskExecutor()
        executor.apply {
            corePoolSize = 2
            maxPoolSize = 4
            setQueueCapacity(500)
        }
        executor.initialize()
        return executor
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(AstonParkingApplication::class.java, *args)
}
