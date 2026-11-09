package com.example

import com.example.data.model.AgronomicData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `verify agronomic crops catalog contains staple cereals and legumes`() {
        val crops = AgronomicData.crops
        assertTrue(crops.any { it.name == "Maïs" })
        assertTrue(crops.any { it.name == "Sorgho" })
        assertTrue(crops.any { it.name == "Niébé (Haricot)" })
        assertTrue(crops.any { it.name == "Tomate" })
        assertTrue(crops.any { it.name == "Oignon" })
    }

    @Test
    fun `verify livestock catalog contains poultry and ruminants`() {
        val livestock = AgronomicData.livestock
        assertTrue(livestock.any { it.species.contains("Poulets") })
        assertTrue(livestock.any { it.species.contains("Moutons") })
        assertTrue(livestock.any { it.species.contains("Bovins") })
    }

    @Test
    fun `verify market reference prices are defined with positive values`() {
        val prices = AgronomicData.marketPrices
        assertFalse(prices.isEmpty())
        assertTrue(prices.all { it.priceFcfa > 0 })
        assertTrue(prices.any { it.market.contains("Sankaryare") })
    }
}
