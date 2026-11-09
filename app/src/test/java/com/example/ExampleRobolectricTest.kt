package com.example

import com.example.data.model.AgronomicData
import com.example.data.model.FarmPlot
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun `verify user profile and default values`() {
        val profile = UserProfile(
            userId = "test_user_123",
            name = "Amadou Ouedraogo",
            email = "amadou@example.com",
            region = "Centre (Ouagadougou)",
            preferredLanguage = "fr"
        )
        assertEquals("test_user_123", profile.userId)
        assertEquals("Amadou Ouedraogo", profile.name)
        assertEquals("Centre (Ouagadougou)", profile.region)
    }

    @Test
    fun `verify farm plot model construction`() {
        val plot = FarmPlot(
            userId = "user_456",
            name = "Champ de Maïs",
            areaHectares = 2.5,
            cropName = "Maïs",
            soilType = "Sol ferrugineux tropical",
            irrigationType = "Goutte-à-goutte"
        )
        assertEquals(2.5, plot.areaHectares, 0.001)
        assertEquals("Maïs", plot.cropName)
        assertEquals("Goutte-à-goutte", plot.irrigationType)
    }

    @Test
    fun `verify agronomic crops catalog integrity`() {
        val crops = AgronomicData.crops
        assertNotNull(crops)
        assertTrue(crops.size >= 6)
        assertTrue(crops.any { it.name == "Maïs" && it.cycleDays.contains("80") })
    }
}
