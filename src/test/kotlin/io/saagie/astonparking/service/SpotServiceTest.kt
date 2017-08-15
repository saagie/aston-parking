package io.saagie.astonparking.service

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.saagie.astonparking.dao.SpotDao
import io.saagie.astonparking.domain.Spot
import io.saagie.astonparking.domain.State
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.lang.IllegalArgumentException
import kotlin.test.fail

class SpotServiceTest {

    val allSpots = initAllSpots()

    val spotDao: SpotDao = mock<SpotDao> {
        on { findAll() }.doReturn(allSpots)
        on { findByState(State.FIXED) }.doReturn(allSpots.filter { it.state == State.FIXED })
        on { findByState(State.FREE) }.doReturn(allSpots.filter { it.state == State.FREE })
    }

    val spotService = SpotService(spotDao)

    @Test
    fun should_return_all_spot_when_no_state_filter() {
        // Given
        // When
        val returnedAllSpots = spotService.getAllSpots(null)
        // Then
        verify(spotDao).findAll()
        verify(spotDao, times(0)).findByState(State.FREE)
        verify(spotDao, times(0)).findByState(State.FIXED)
        returnedAllSpots `should equal` allSpots
    }

    @Test
    fun should_return_an_exception_when_an_invalid_state_is_set() {
        // Given
        // When
        try {
            spotService.getAllSpots("UNKNOWN STATE")
            fail("Should return an exception if the state is unknown")
        } catch (e: IllegalArgumentException) {
            e.message `should be` "State is unknown"
        }
        // Then
        verify(spotDao, times(0)).findAll()
        verify(spotDao, times(0)).findByState(State.FREE)
        verify(spotDao, times(0)).findByState(State.FIXED)
    }

    @Test
    fun should_return_filtered_spot_when_no_state_is_set() {
        // Given
        // When
        val returnedAllSpotsWithFixed = spotService.getAllSpots(State.FIXED.name)
        val returnedAllSpotsWithFree = spotService.getAllSpots(State.FREE.name)
        // Then
        verify(spotDao, times(0)).findAll()
        verify(spotDao, times(1)).findByState(State.FREE)
        verify(spotDao, times(1)).findByState(State.FIXED)
        returnedAllSpotsWithFixed `should equal` allSpots.filter { it.state == State.FIXED }
        returnedAllSpotsWithFree `should equal` allSpots.filter { it.state == State.FREE }
    }


    private fun initAllSpots(): List<Spot> {
        return arrayListOf<Spot>(
                Spot(null, 100, State.FIXED),
                Spot(null, 101, State.FIXED),
                Spot(null, 102, State.FIXED),
                Spot(null, 103, State.FREE)
        )
    }
}
