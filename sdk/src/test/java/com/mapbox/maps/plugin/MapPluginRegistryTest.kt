package com.mapbox.maps.plugin

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import com.mapbox.common.ShadowLogger
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.StyleInterface
import com.mapbox.maps.loader.MapboxMapStaticInitializer
import com.mapbox.maps.plugin.delegates.MapDelegateProvider
import com.mapbox.maps.plugin.gestures.GesturesPlugin
import io.mockk.*
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowLogger::class])
class MapPluginRegistryTest {

  private val delegateProvider = mockk<MapDelegateProvider>()
  private lateinit var mapPluginRegistry: MapPluginRegistry
  private val mapInitOptions = mockk<MapInitOptions>()
  private val mapOptions = mockk<MapOptions>()
  private val resourceOptions = mockk<ResourceOptions>()

  @Before
  fun setUp() {
    mockkStatic(MapboxMapStaticInitializer::class)
    every { MapboxMapStaticInitializer.loadMapboxMapNativeLib() } just Runs
    mapPluginRegistry = MapPluginRegistry(delegateProvider)
    every { mapInitOptions.mapOptions } returns mapOptions
    every { mapInitOptions.resourceOptions } returns resourceOptions
    every { mapInitOptions.attrs } returns null
  }

  @Test
  fun createPlugin() {
    val mapView = mockk<MapView>(relaxUnitFun = true)
    val clazz = MapPlugin::class.java
    val plugin = mockk<MapPlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    val createdPlugin = mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    Assert.assertEquals(plugin, createdPlugin)
    verify { createdPlugin.onDelegateProvider(delegateProvider) }
    verify { createdPlugin.initialize() }
  }

  @Test
  fun createPlugin_duplicate() {
    val mapView = mockk<MapView>(relaxUnitFun = true)
    val clazz = MapPlugin::class.java
    mockkStatic("com.mapbox.maps.UtilsKt")
    val plugin = mockk<MapPlugin>(relaxUnitFun = true)
    every { clazz.instantiate() } returns plugin
    val createdPlugin = mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)

    val plugin2 = mockk<MapPlugin>(relaxUnitFun = true)
    every { clazz.instantiate() } returns plugin2
    val createdPlugin2 = mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)

    Assert.assertEquals(plugin, createdPlugin)
    Assert.assertEquals(plugin, createdPlugin2)

    verify(exactly = 1) { plugin.onDelegateProvider(delegateProvider) }
    verify(exactly = 2) { plugin.initialize() }
  }

  @Test
  fun createPlugin_startedBefore() {
    val mapView = mockk<MapView>()
    val clazz = TestLifecyclePlugin::class.java
    val plugin = mockk<TestLifecyclePlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    mapPluginRegistry.onStart()
    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    verify { plugin.onStart() }
  }

  @Test
  fun createPlugin_viewPlugin() {
    val mapView = mockk<MapView>(relaxed = true)
    val clazz = ViewPlugin::class.java
    val plugin = mockk<ViewPlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin
    val view = mockk<View>()
    every { mapInitOptions.context } returns mockk()
    every { mapOptions.pixelRatio } returns 1f
    every { plugin.bind(mapView, any(), any()) } returns view
    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    verify { plugin.bind(mapView, any(), any()) }
    verify { mapView.addView(view) }
    verify { plugin.onPluginView(view) }
  }

  @Test
  fun createPlugin_gesturePlugin() {
    val mapView = mockk<MapView>(relaxed = true)
    val clazz = GesturesPlugin::class.java
    val plugin = mockk<GesturesPlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin
    every { plugin.bind(any(), any(), any()) } returns mockk()
    every { mapInitOptions.context } returns mockk()
    every { mapOptions.pixelRatio } returns 1f
    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    verify { plugin.bind(any(), any(), any()) }

    verify { plugin.onDelegateProvider(any()) }
    verify { plugin.initialize() }
  }

  @Test
  fun getPlugin() {
    val mapView = mockk<MapView>()
    val clazz = MapPlugin::class.java
    val plugin = mockk<MapPlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    Assert.assertEquals(plugin, mapPluginRegistry.getPlugin(clazz))
  }

  @Test
  fun getPlugin_null() {
    val clazz = MapPlugin::class.java
    val plugin = mockk<MapPlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    Assert.assertNull(mapPluginRegistry.getPlugin(clazz))
  }

  @Test
  fun onStart() {
    val mapView = mockk<MapView>()
    val clazz = TestLifecyclePlugin::class.java
    val plugin = mockk<TestLifecyclePlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    mapPluginRegistry.onStart()
    verify { plugin.onStart() }
  }

  @Test
  fun onStop() {
    val mapView = mockk<MapView>()
    val clazz = TestLifecyclePlugin::class.java
    val plugin = mockk<TestLifecyclePlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    mapPluginRegistry.onStart()
    mapPluginRegistry.onStop()
    verify { plugin.onStop() }
  }

  @Test
  fun onCameraMove() {
    val mapView = mockk<MapView>(relaxUnitFun = true)
    val clazz = MapCameraPlugin::class.java
    val plugin = mockk<MapCameraPlugin>(relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin
    val cameraState = mockk<CameraState>()

    val center = Point.fromLngLat(1.0, 2.0)
    every { cameraState.center } returns center
    val zoom = 5.0
    every { cameraState.zoom } returns zoom
    val bearing = 15.0
    every { cameraState.bearing } returns bearing
    val pitch = 30.0
    every { cameraState.pitch } returns pitch
    val insets = EdgeInsets(1.0, 2.0, 3.0, 4.0)
    every { cameraState.padding } returns insets

    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    mapPluginRegistry.onCameraMove(cameraState)
    verify {
      plugin.onCameraMove(
        center.latitude(),
        center.longitude(),
        zoom,
        pitch,
        bearing,
        arrayOf(insets.left, insets.top, insets.right, insets.bottom)
      )
    }
  }

  @Test
  fun onTouch() {
    val mapView = mockk<MapView>(relaxUnitFun = true)
    val context = mockk<Context>(relaxUnitFun = true)
    val displayMetrics = DisplayMetrics().also { it.density = 1f }
    val motionEvent = mockk<MotionEvent>(relaxUnitFun = true)
    val clazz = GesturesPlugin::class.java
    val plugin = mockk<GesturesPlugin>(relaxed = true, relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    every { mapView.context.applicationContext } returns context
    every { mapInitOptions.context } returns context
    every { mapOptions.pixelRatio } returns 1f
    every { mapView.resources.displayMetrics } returns displayMetrics
    every { plugin.onTouchEvent(motionEvent) } returns true

    assertFalse(mapPluginRegistry.onTouch(motionEvent))
    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    assertTrue(mapPluginRegistry.onTouch(motionEvent))

    verify {
      plugin.onTouchEvent(motionEvent)
    }
  }

  @Test
  fun onGenericMotionEvent() {
    val mapView = mockk<MapView>(relaxUnitFun = true)
    val attributeSet = mockk<AttributeSet>(relaxUnitFun = true)
    val context = mockk<Context>(relaxUnitFun = true)
    val displayMetrics = DisplayMetrics().also { it.density = 1f }
    val motionEvent = mockk<MotionEvent>(relaxUnitFun = true)
    val clazz = GesturesPlugin::class.java
    val plugin = mockk<GesturesPlugin>(relaxed = true, relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    every { mapView.context.applicationContext } returns context
    every { mapInitOptions.context } returns context
    every { mapOptions.pixelRatio } returns 1f
    every { mapView.resources.displayMetrics } returns displayMetrics
    every { plugin.onGenericMotionEvent(motionEvent) } returns true

    assertFalse(mapPluginRegistry.onGenericMotionEvent(motionEvent))
    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    assertTrue(mapPluginRegistry.onGenericMotionEvent(motionEvent))

    verify {
      plugin.onGenericMotionEvent(motionEvent)
    }
  }

  @Test
  fun onSizeChanged() {
    val mapView = mockk<MapView>(relaxUnitFun = true)
    val attributeSet = mockk<AttributeSet>(relaxUnitFun = true)
    val context = mockk<Context>(relaxUnitFun = true)
    val displayMetrics = DisplayMetrics().also { it.density = 1f }
    val clazz = GesturesPlugin::class.java
    val plugin = mockk<GesturesPlugin>(relaxed = true, relaxUnitFun = true)
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    every { mapView.context.applicationContext } returns context
    every { mapInitOptions.context } returns context
    every { mapOptions.pixelRatio } returns 1f
    every { mapView.resources.displayMetrics } returns displayMetrics

    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)
    mapPluginRegistry.onSizeChanged(1, 2)

    verify {
      plugin.onSizeChanged(1, 2)
    }
  }

  @Test
  fun onStyleChanged() {
    val mapView = mockk<MapView>(relaxed = true)
    val clazz = TestStyleObserverPlugin::class.java
    val plugin = mockk<TestStyleObserverPlugin>(relaxUnitFun = true)
    val style = mockk<Style>()
    mockkStatic("com.mapbox.maps.UtilsKt")
    every { clazz.instantiate() } returns plugin

    mapPluginRegistry.createPlugin(mapView, mapInitOptions, clazz)

    mapPluginRegistry.onStyleChanged(style)

    verify {
      plugin.onStyleChanged(style)
    }
  }

  class TestLifecyclePlugin : MapPlugin, LifecyclePlugin {
    override fun cleanup() {
    }

    override fun initialize() {
    }

    override fun onDelegateProvider(delegateProvider: MapDelegateProvider) {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }
  }

  class TestStyleObserverPlugin : MapPlugin, MapStyleObserverPlugin {
    override fun initialize() {
    }

    override fun cleanup() {
    }

    override fun onDelegateProvider(delegateProvider: MapDelegateProvider) {
    }

    override fun onStyleChanged(styleDelegate: StyleInterface) {
    }
  }
}