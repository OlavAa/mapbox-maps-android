package com.mapbox.maps.testapp.examples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.common.Logger
import com.mapbox.maps.*
import com.mapbox.maps.extension.observable.getResourceEventData
import com.mapbox.maps.extension.observable.subscribeResourceRequest
import com.mapbox.maps.extension.observable.unsubscribeResourceRequest
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadErrorListener
import com.mapbox.maps.plugin.delegates.listeners.eventdata.MapLoadErrorType
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.testapp.R
import kotlinx.android.synthetic.main.activity_debug.*

/**
 * Example of enabling and visualizing some debug information for a map.
 */
class DebugModeActivity : AppCompatActivity() {

  private lateinit var mapboxMap: MapboxMap
  private lateinit var scaleBarPlugin: ScaleBarPlugin
  private val debugOptions: MutableList<MapDebugOptions> = mutableListOf()
  private val extensionObservable = object : Observer() {
    override fun notify(event: Event) {
      val data = event.getResourceEventData()
      Logger.i(
        TAG,
        "extensionObservable DataSource: ${data.dataSource}\nRequest: ${data.request}\nResponse: ${data.response}\nCancelled: ${data.cancelled}"
      )
    }
  }

  private val observable = object : Observer() {
    override fun notify(event: Event) {
      Logger.i(
        TAG,
        "Type: ${event.type}\nValue: ${event.data.contents}"
      )
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_debug)

    mapboxMap = mapView.getMapboxMap()
    mapboxMap.subscribe(observable, listOf("resource-request"))
    // Using the extension method
    mapboxMap.subscribeResourceRequest(extensionObservable)
    mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)
    mapView.compass.opacity = 0.5f
    scaleBarPlugin = mapView.scalebar
    scaleBarPlugin.enabled = false
    scaleBarPlugin.textColor = ContextCompat.getColor(this@DebugModeActivity, R.color.primary)
    displayOnSecondDisplayButton.setOnClickListener {
      scaleBarPlugin.enabled = true

      mapboxMap.setDebug(debugOptions, false)
      debugOptions.clear()

      if (checkboxTileBorders.isChecked) {
        debugOptions.add(MapDebugOptions.TILE_BORDERS)
      }
      if (checkboxCollision.isChecked) {
        debugOptions.add(MapDebugOptions.COLLISION)
      }
      if (checkboxDepthBuffer.isChecked) {
        debugOptions.add(MapDebugOptions.DEPTH_BUFFER)
      }
      if (checkboxOverdraw.isChecked) {
        debugOptions.add(MapDebugOptions.OVERDRAW)
      }
      if (checkboxParseStatus.isChecked) {
        debugOptions.add(MapDebugOptions.PARSE_STATUS)
      }
      if (checkboxStencilClip.isChecked) {
        debugOptions.add(MapDebugOptions.STENCIL_CLIP)
      }
      if (checkboxTimestamps.isChecked) {
        debugOptions.add(MapDebugOptions.TIMESTAMPS)
      }
      mapboxMap.setDebug(debugOptions, true)
    }
    registerListeners(mapboxMap)
  }

  private fun registerListeners(mapboxMap: MapboxMap) {
    mapboxMap.addOnStyleLoadedListener() {
      Logger.i(TAG, "OnStyleLoadedListener")
    }
    mapboxMap.addOnStyleDataLoadedListener {
      Logger.i(TAG, "OnStyleDataLoadedListener: $it")
    }
    mapboxMap.addOnStyleImageMissingListener {
      Logger.i(TAG, "OnStyleImageMissingListener: $it")
    }
    mapboxMap.addOnStyleImageUnusedListener {
      Logger.i(TAG, "OnStyleImageUnusedListener: $it")
    }
    mapboxMap.addOnMapIdleListener {
      Logger.i(TAG, "OnMapIdleListener")
    }
    mapboxMap.addOnMapLoadErrorListener(object : OnMapLoadErrorListener {
      override fun onMapLoadError(mapLoadErrorType: MapLoadErrorType, description: String) {
        Logger.i(TAG, "OnMapLoadErrorListener: $mapLoadErrorType, $description")
      }
    })
    mapboxMap.addOnMapLoadedListener {
      Logger.i(TAG, "OnMapLoadedListener")
    }
    mapboxMap.addOnCameraChangeListener {
      Logger.i(TAG, "OnCameraChangeListener")
    }
    mapboxMap.addOnRenderFrameStartedListener {
      Logger.i(TAG, "OnRenderFrameStartedListener")
    }
    mapboxMap.addOnRenderFrameFinishedListener { renderMode, needsRepaint, placementChanged ->
      Logger.i(
        TAG,
        "OnRenderFrameFinishedListener: $renderMode, $needsRepaint, $placementChanged"
      )
    }
    mapboxMap.addOnSourceAddedListener {
      Logger.i(
        TAG,
        "OnSourceAddedListener: $it"
      )
    }
    mapboxMap.addOnSourceDataLoadedListener { id, type, loaded, tileID ->
      Logger.i(
        TAG,
        "OnSourceDataLoadedListener: $id, $type, $loaded, $tileID"
      )
    }
    mapboxMap.addOnSourceRemovedListener {
      Logger.i(
        TAG,
        "OnSourceRemovedListener: $it"
      )
    }
  }

  override fun onStart() {
    super.onStart()
    mapView.onStart()
    mapView.setOnFpsChangedListener {
      runOnUiThread {
        fpsView.text = "${it.toInt()} FPS"
      }
    }
  }

  override fun onStop() {
    super.onStop()
    mapView.onStop()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapboxMap.unsubscribe(observable)
    mapboxMap.unsubscribeResourceRequest(extensionObservable)
    mapView.onDestroy()
  }

  companion object {
    const val TAG = "DebugModeActivity"
  }
}