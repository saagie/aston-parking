package io.saagie.astonparking.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "google.actions")
class BasicAuthConfig {

    var username: String = ""
    var password: String = ""
    var clientId: String = ""
}
