package io.saagie.astonparking.googleActions

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.stream.Collectors
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Handles request received via HTTP POST and delegates it to your Actions app. See: [Request
 * handling in Google App
 * Engine](https://cloud.google.com/appengine/docs/standard/java/how-requests-are-handled).
 */
@RestController
class ActionsServlet(private val actionsApp: Actions) : HttpServlet() {
    
    @Throws(IOException::class)
    @PostMapping("/actions")
    override fun doPost(req: HttpServletRequest, res: HttpServletResponse) {
        val body = req.reader.lines().collect(Collectors.joining())

        try {
            val jsonResponse = actionsApp.handleRequest(body, getHeadersMap(req)).get()
            res.contentType = "application/json"
            writeResponse(res, jsonResponse)
        } catch (e: InterruptedException) {
            handleError(res, e)
        } catch (e: ExecutionException) {
            handleError(res, e)
        }

    }

    private fun writeResponse(res: HttpServletResponse, asJson: String) {
        try {
            res.writer.write(asJson)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun handleError(res: HttpServletResponse, throwable: Throwable) {
        try {
            throwable.printStackTrace()
            LOG.error("Error in App.handleRequest ", throwable)
            res.writer.write("Error handling the intent - " + throwable.message)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun getHeadersMap(request: HttpServletRequest): Map<String, String> {
        val map = HashMap<String, String>()

        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val key = headerNames.nextElement() as String
            val value = request.getHeader(key)
            map[key] = value
        }
        return map
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ActionsServlet::class.java)
    }

}