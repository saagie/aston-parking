package io.saagie.astonparking.googleActions


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import java.io.IOException
import java.security.GeneralSecurityException

class TokenDecoder(private val clientId: String) {

    @Throws(GeneralSecurityException::class, IOException::class)
    fun decodeIdToken(idTokenString: String): GoogleIdToken.Payload {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(listOf(clientId))
            .build()

        val idToken = verifier.verify(idTokenString)
        return idToken.payload
    }
}