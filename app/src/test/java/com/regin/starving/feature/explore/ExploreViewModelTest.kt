package com.regin.starving.feature.explore

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.regin.starving.core.location.Location
import com.regin.starving.core.location.LocationIsNotAvailableException
import com.regin.starving.core.location.LocationProvider
import com.regin.starving.core.location.LocationServiceIsNotAvailableException
import com.regin.starving.core.location.LocationWithZoom
import com.regin.starving.data.api.response.VenueLocation
import com.regin.starving.data.api.response.VenueResponse
import com.regin.starving.data.repository.PoiRepository
import com.regin.starving.di.testAppModule
import com.regin.starving.di.testDataModule
import com.regin.starving.domain.domainModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

@ExperimentalCoroutinesApi
class ExploreViewModelTest : KoinTest {

    private val viewModel: ExploreViewModel by inject()

    private val testDispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        startKoin {
            modules(listOf(testAppModule, testDataModule, domainModule(), exploreModule))
        }
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    @DisplayName("Load my location with known location")
    fun loadMyLocationWithKnownLocation() = runBlockingTest {
        val fakeLocation = Location(0.0, 1.0)

        declareMock<LocationProvider> {
            runBlocking { given(lastKnownLocation()).willReturn(fakeLocation) }
        }

        val values = mutableListOf<SideEffect>()
        val job = launch {
            viewModel.sideEffects.collect { values.add(it) }
        }
        viewModel.dispatchEvent(Event.LoadMyLocation)

        assertAll(
            { assertTrue(values[0] is SideEffect.FocusOnMyLocation) },
            { assertEquals((values[0] as SideEffect.FocusOnMyLocation).location, fakeLocation) }
        )
        job.cancel()
    }

    @Test
    @DisplayName("Load my location with unknown location")
    fun loadMyLocationWithUnknownLocation() = runBlockingTest {
        declareMock<LocationProvider> {
            runBlocking { given(lastKnownLocation()).willThrow(LocationIsNotAvailableException()) }
        }

        val values = mutableListOf<SideEffect>()
        val job = launch {
            viewModel.sideEffects.collect { values.add(it) }
        }
        viewModel.dispatchEvent(Event.LoadMyLocation)

        assertAll(
            { assertTrue(values[0] is SideEffect.LocationIsUnknown) }
        )

        job.cancel()
    }

    @Test
    @DisplayName("Load my location with turned off Location service")
    fun loadMyLocationWithoutLocationService() = runBlockingTest {
        declareMock<LocationProvider> {
            runBlocking {
                given(lastKnownLocation()).willThrow(
                    LocationServiceIsNotAvailableException()
                )
            }
        }

        val values = mutableListOf<SideEffect>()
        val job = launch {
            viewModel.sideEffects.collect { values.add(it) }
        }
        viewModel.dispatchEvent(Event.LoadMyLocation)

        assertAll(
            { assertTrue(values[0] is SideEffect.LocationServiceIsUnavailable) }
        )

        job.cancel()
    }

    @Test
    @DisplayName("Load pois without errors")
    fun loadPoisWithoutErrors() = runBlockingTest {
        val locationWithZoom = LocationWithZoom(
            Location(
                latitude = 5.0,
                longitude = 5.0
            ),
            zoom = 12f
        )

        val result = listOf(
            VenueResponse(
                "id", "fakeName", VenueLocation(
                    lat = 5.0,
                    lng = 5.0,
                    address = "test"
                )
            )
        )

        declareMock<PoiRepository> {
            runBlocking {
                given(loadPoi(any(), any(), any())).willReturn(result)
            }
        }

        val values = mutableListOf<ExploreViewState>()
        val job = launch {
            viewModel.viewState.collect { values.add(it) }
        }

        assertAll(
            { assertFalse(values[0].isLoading) },
            { assertTrue(values[0].pois.isEmpty()) }
        )

        viewModel.dispatchEvent(Event.LoadPoi(locationWithZoom))

        assertAll(
            { assertTrue(values[1].isLoading) },
            { assertTrue(values[1].pois.isEmpty()) }
        )

        assertAll(
            { assertFalse(values[2].isLoading) },
            { assertTrue(values[2].pois.size == 1) }
        )

        job.cancel()
    }

    @Test
    @DisplayName("Load pois with error")
    fun loadPoisWithErrors() = runBlockingTest {
        val locationWithZoom = LocationWithZoom(
            Location(
                latitude = 5.0,
                longitude = 5.0
            ),
            zoom = 12f
        )

        declareMock<PoiRepository> {
            runBlocking {
                given(loadPoi(any(), any(), any())).willThrow(RuntimeException())
            }
        }

        val values = mutableListOf<ExploreViewState>()
        val sideEffects = mutableListOf<SideEffect>()
        val job = launch {
            viewModel.viewState.collect { values.add(it) }
        }

        val jobSideEffects = launch {
            viewModel.sideEffects.collect { sideEffects.add(it) }
        }

        assertAll(
            { assertFalse(values[0].isLoading) },
            { assertTrue(values[0].pois.isEmpty()) }
        )

        viewModel.dispatchEvent(Event.LoadPoi(locationWithZoom))

        assertAll(
            { assertTrue(values[1].isLoading) },
            { assertTrue(values[1].pois.isEmpty()) }
        )

        assertAll(
            { assertFalse(values[2].isLoading) },
            { assertTrue(sideEffects[0] is SideEffect.FailedLoadingPoi) }
        )

        job.cancel()
        jobSideEffects.cancel()
    }
}