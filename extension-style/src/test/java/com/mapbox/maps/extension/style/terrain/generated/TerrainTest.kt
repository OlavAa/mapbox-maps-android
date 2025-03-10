// This file is generated.

package com.mapbox.maps.extension.style.terrain.generated

import com.mapbox.bindgen.Expected
import com.mapbox.bindgen.Value
import com.mapbox.maps.StylePropertyValue
import com.mapbox.maps.StylePropertyValueKind
import com.mapbox.maps.extension.style.StyleInterface
import com.mapbox.maps.extension.style.utils.TypeUtils
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TerrainTest {
  private val sourceId = "1"
  private val style = mockk<StyleInterface>(relaxUnitFun = true, relaxed = true)
  private val styleProperty = mockk<StylePropertyValue>()
  private val expected = mockk<Expected<Void, String>>(relaxUnitFun = true, relaxed = true)
  private val valueSlot = slot<Value>()

  @Before
  fun prepareTest() {
    every { style.getStyleTerrainProperty(any()) } returns styleProperty
    every { styleProperty.kind } returns StylePropertyValueKind.CONSTANT
    every { style.setStyleTerrainProperty(any(), any()) } returns expected
    every { style.setStyleTerrain(any()) } returns expected
    every { expected.error } returns null
  }

  @After
  fun cleanup() {
    unmockkAll()
  }

  @Test
  fun sourceIdTest() {
    val terrain = terrain(sourceId)
    terrain.bindTo(style)
    verify { style.setStyleTerrain(capture(valueSlot)) }
    assertTrue(valueSlot.captured.toString().contains("source=$sourceId"))
  }

  @Test
  fun exaggerationSet() {
    val terrain = terrain(sourceId) {
      exaggeration(1.0)
    }
    terrain.bindTo(style)
    verify { style.setStyleTerrain(capture(valueSlot)) }
    assertTrue(valueSlot.captured.toString().contains("exaggeration=1.0"))
  }

  @Test
  fun exaggerationSetAfterInitialization() {
    val terrain = terrain(sourceId) { }
    terrain.bindTo(style)
    terrain.exaggeration(1.0)
    verify { style.setStyleTerrainProperty("exaggeration", capture(valueSlot)) }
    assertTrue(valueSlot.captured.toString().contains("1.0"))
  }

  @Test
  fun exaggerationGet() {
    every { styleProperty.value } returns TypeUtils.wrapToValue(1.0)

    val terrain = terrain(sourceId) { }
    terrain.bindTo(style)
    assertEquals(1.0.toString(), terrain.exaggeration!!.toString())
    verify { style.getStyleTerrainProperty("exaggeration") }
  }

  // Expression Tests

  @Test
  fun exaggerationAsExpressionGetNull() {
    val terrain = terrain(sourceId) { }
    terrain.bindTo(style)
    assertEquals(null, terrain.exaggerationAsExpression)
    verify { style.getStyleTerrainProperty("exaggeration") }
  }

  @Test
  fun exaggerationAsExpressionGetFromLiteral() {
    every { styleProperty.value } returns TypeUtils.wrapToValue(1.0)
    val terrain = terrain(sourceId) { }
    terrain.bindTo(style)
    assertEquals(1.0, terrain.exaggerationAsExpression?.contents as Double, 1E-5)
    assertEquals(1.0, terrain.exaggeration!!, 1E-5)
    verify { style.getStyleTerrainProperty("exaggeration") }
  }

  @Test
  fun getTerrainTest() {
    assertNotNull(style.getTerrain(sourceId))
    verify { style.setStyleTerrain(any()) }
  }
}

// End of generated file.