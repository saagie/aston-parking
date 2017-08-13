package io.saagie.astonparking.service

import io.saagie.astonparking.dao.SpotDao
import io.saagie.astonparking.domain.Spot
import io.saagie.astonparking.domain.State
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SpotService(@Autowired val spotDao: SpotDao) {

    fun getAllSpots(state: String?): MutableIterable<Spot>? {
        when (state) {
            State.FIXED.name, State.FREE.name -> return spotDao.findByState(State.valueOf(state))
            else -> return spotDao.findAll()
        }
    }

    fun getSpot(number: Int): Spot? {
        return spotDao.findByNumber(number)
    }

    fun updateSpot(number: Int, state: State): Spot? {
        val spot = spotDao.findByNumber(number)
        spot?.state = state
        spotDao.save(spot)
        return spot
    }
}