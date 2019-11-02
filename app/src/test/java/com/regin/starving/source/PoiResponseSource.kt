package com.regin.starving.source

import com.regin.starving.data.api.response.PoiResponse
import com.regin.starving.data.api.response.Response
import com.regin.starving.data.api.response.VenueLocation
import com.regin.starving.data.api.response.VenueResponse
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

class PoiResponseSucessSource : ArgumentsProvider {

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments>? {
        return Stream.of(Arguments.of(createData()))
    }

    private fun createData(): PoiResponse {
        val venues = listOf(
            VenueResponse(
                "id", "fakeName", VenueLocation(
                    lat = 5.0,
                    lng = 5.0,
                    address = "test"
                )
            )
        )

        return PoiResponse(Response(venues))
    }
}