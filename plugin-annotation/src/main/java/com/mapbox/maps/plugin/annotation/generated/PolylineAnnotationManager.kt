// This file is generated.

package com.mapbox.maps.plugin.annotation.generated

import android.view.View
import com.mapbox.geojson.*
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.get
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.*
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationManagerImpl
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.AnnotationType
import com.mapbox.maps.plugin.delegates.MapDelegateProvider
import java.util.concurrent.atomic.AtomicLong

/**
 * The polylineAnnotation manager allows to add polylineAnnotations to a map.
 */
class PolylineAnnotationManager(
  mapView: View,
  delegateProvider: MapDelegateProvider,
  annotationConfig: AnnotationConfig? = null
) :
  AnnotationManagerImpl<LineString, PolylineAnnotation, PolylineAnnotationOptions, OnPolylineAnnotationDragListener, OnPolylineAnnotationClickListener, OnPolylineAnnotationLongClickListener, OnPolylineAnnotationInteractionListener, LineLayer>(
    mapView, delegateProvider, annotationConfig
  ) {
  private val id = ID_GENERATOR.incrementAndGet()
  override val layerId = annotationConfig?.layerId ?: "mapbox-android-polylineAnnotation-layer-$id"
  override val sourceId = annotationConfig?.sourceId ?: "mapbox-android-polylineAnnotation-source-$id"

  init {
    delegateProvider.getStyle {
      style = it
      initLayerAndSource()
    }
  }

  override fun initializeDataDrivenPropertyMap() {
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_JOIN] = false
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_SORT_KEY] = false
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_BLUR] = false
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_COLOR] = false
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_GAP_WIDTH] = false
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_OFFSET] = false
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_OPACITY] = false
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_PATTERN] = false
    dataDrivenPropertyUsageMap[PolylineAnnotationOptions.PROPERTY_LINE_WIDTH] = false
  }

  override fun setDataDrivenPropertyIsUsed(property: String) {
    when (property) {
      PolylineAnnotationOptions.PROPERTY_LINE_JOIN -> layer?.lineJoin(get(PolylineAnnotationOptions.PROPERTY_LINE_JOIN))
      PolylineAnnotationOptions.PROPERTY_LINE_SORT_KEY -> layer?.lineSortKey(get(PolylineAnnotationOptions.PROPERTY_LINE_SORT_KEY))
      PolylineAnnotationOptions.PROPERTY_LINE_BLUR -> layer?.lineBlur(get(PolylineAnnotationOptions.PROPERTY_LINE_BLUR))
      PolylineAnnotationOptions.PROPERTY_LINE_COLOR -> layer?.lineColor(get(PolylineAnnotationOptions.PROPERTY_LINE_COLOR))
      PolylineAnnotationOptions.PROPERTY_LINE_GAP_WIDTH -> layer?.lineGapWidth(get(PolylineAnnotationOptions.PROPERTY_LINE_GAP_WIDTH))
      PolylineAnnotationOptions.PROPERTY_LINE_OFFSET -> layer?.lineOffset(get(PolylineAnnotationOptions.PROPERTY_LINE_OFFSET))
      PolylineAnnotationOptions.PROPERTY_LINE_OPACITY -> layer?.lineOpacity(get(PolylineAnnotationOptions.PROPERTY_LINE_OPACITY))
      PolylineAnnotationOptions.PROPERTY_LINE_PATTERN -> layer?.linePattern(get(PolylineAnnotationOptions.PROPERTY_LINE_PATTERN))
      PolylineAnnotationOptions.PROPERTY_LINE_WIDTH -> layer?.lineWidth(get(PolylineAnnotationOptions.PROPERTY_LINE_WIDTH))
    }
  }

  /**
   * Create a list of polylineAnnotations on the map.
   *
   * PolylineAnnotations are going to be created only for features with a matching geometry.
   *
   * All supported properties are:
   * PolylineAnnotationOptions.PROPERTY_LINE_JOIN - LineJoin   * PolylineAnnotationOptions.PROPERTY_LINE_SORT_KEY - Double   * PolylineAnnotationOptions.PROPERTY_LINE_BLUR - Double   * PolylineAnnotationOptions.PROPERTY_LINE_COLOR - String   * PolylineAnnotationOptions.PROPERTY_LINE_GAP_WIDTH - Double   * PolylineAnnotationOptions.PROPERTY_LINE_OFFSET - Double   * PolylineAnnotationOptions.PROPERTY_LINE_OPACITY - Double   * PolylineAnnotationOptions.PROPERTY_LINE_PATTERN - String   * PolylineAnnotationOptions.PROPERTY_LINE_WIDTH - Double   * Learn more about above properties in the )[The online documentation](https://www.mapbox.com/mapbox-gl-js/style-spec/).
   *
   * Out of spec properties:
   * "is-draggable" - Boolean, true if the polylineAnnotation should be draggable, false otherwise
   *
   * @param json the GeoJSON defining the list of polylineAnnotations to build
   * @return the list of built polylineAnnotations
   */
  fun create(json: String): List<PolylineAnnotation> {
    return create(FeatureCollection.fromJson(json))
  }

  /**
   * Create a list of polylineAnnotations on the map.
   *
   * PolylineAnnotations are going to be created only for features with a matching geometry.
   *
   * All supported properties are:
   * PolylineAnnotationOptions.PROPERTY_LINE_JOIN - LineJoin   * PolylineAnnotationOptions.PROPERTY_LINE_SORT_KEY - Double   * PolylineAnnotationOptions.PROPERTY_LINE_BLUR - Double   * PolylineAnnotationOptions.PROPERTY_LINE_COLOR - String   * PolylineAnnotationOptions.PROPERTY_LINE_GAP_WIDTH - Double   * PolylineAnnotationOptions.PROPERTY_LINE_OFFSET - Double   * PolylineAnnotationOptions.PROPERTY_LINE_OPACITY - Double   * PolylineAnnotationOptions.PROPERTY_LINE_PATTERN - String   * PolylineAnnotationOptions.PROPERTY_LINE_WIDTH - Double   * Learn more about above properties in the )[The online documentation](https://www.mapbox.com/mapbox-gl-js/style-spec/).
   *
   * Out of spec properties:
   * "is-draggable" - Boolean, true if the polylineAnnotation should be draggable, false otherwise
   *
   * @param featureCollection the featureCollection defining the list of polylineAnnotations to build
   * @return the list of built polylineAnnotations
   */
  fun create(featureCollection: FeatureCollection): List<PolylineAnnotation> {
    featureCollection.features()?.let { features ->
      val options = features.mapNotNull {
        PolylineAnnotationOptions.fromFeature(it)
      }
      return create(options)
    }
    return listOf()
  }

  /**
   * Get the key of the id of the annotation.
   *
   * @return the key of the id of the annotation
   */
  override fun getAnnotationIdKey(): String {
    return PolylineAnnotation.ID_KEY
  }

  // Property accessors
  /**
   * The LineCap property
   *
   * The display of line endings.
   */
  var lineCap: LineCap?
    /**
     * Get the LineCap property
     *
     * @return property wrapper value around LineCap
     */
    get(): LineCap? {
      return layer?.lineCap
    }
    /**
     * Set the LineCap property
     * @param value property wrapper value around LineCap
     */
    set(value) {
      value?.let {
        layer?.lineCap(it)
      }
    }

  /**
   * The LineMiterLimit property
   *
   * Used to automatically convert miter joins to bevel joins for sharp angles.
   */
  var lineMiterLimit: Double?
    /**
     * Get the LineMiterLimit property
     *
     * @return property wrapper value around Double
     */
    get(): Double? {
      return layer?.lineMiterLimit
    }
    /**
     * Set the LineMiterLimit property
     * @param value property wrapper value around Double
     */
    set(value) {
      value?.let {
        layer?.lineMiterLimit(it)
      }
    }

  /**
   * The LineRoundLimit property
   *
   * Used to automatically convert round joins to miter joins for shallow angles.
   */
  var lineRoundLimit: Double?
    /**
     * Get the LineRoundLimit property
     *
     * @return property wrapper value around Double
     */
    get(): Double? {
      return layer?.lineRoundLimit
    }
    /**
     * Set the LineRoundLimit property
     * @param value property wrapper value around Double
     */
    set(value) {
      value?.let {
        layer?.lineRoundLimit(it)
      }
    }

  /**
   * The LineDasharray property
   *
   * Specifies the lengths of the alternating dashes and gaps that form the dash pattern. The lengths are later scaled by the line width. To convert a dash length to density-independent pixels, multiply the length by the current line width. Note that GeoJSON sources with `lineMetrics: true` specified won't render dashed lines to the expected scale. Also note that zoom-dependent expressions will be evaluated only at integer zoom levels.
   */
  var lineDasharray: List<Double>?
    /**
     * Get the LineDasharray property
     *
     * @return property wrapper value around List<Double>
     */
    get(): List<Double>? {
      return layer?.lineDasharray
    }
    /**
     * Set the LineDasharray property
     * @param value property wrapper value around List<Double>
     */
    set(value) {
      value?.let {
        layer?.lineDasharray(it)
      }
    }

  /**
   * The LineTranslate property
   *
   * The geometry's offset. Values are [x, y] where negatives indicate left and up, respectively.
   */
  var lineTranslate: List<Double>?
    /**
     * Get the LineTranslate property
     *
     * @return property wrapper value around List<Double>
     */
    get(): List<Double>? {
      return layer?.lineTranslate
    }
    /**
     * Set the LineTranslate property
     * @param value property wrapper value around List<Double>
     */
    set(value) {
      value?.let {
        layer?.lineTranslate(it)
      }
    }

  /**
   * The LineTranslateAnchor property
   *
   * Controls the frame of reference for {@link PropertyFactory#lineTranslate}.
   */
  var lineTranslateAnchor: LineTranslateAnchor?
    /**
     * Get the LineTranslateAnchor property
     *
     * @return property wrapper value around LineTranslateAnchor
     */
    get(): LineTranslateAnchor? {
      return layer?.lineTranslateAnchor
    }
    /**
     * Set the LineTranslateAnchor property
     * @param value property wrapper value around LineTranslateAnchor
     */
    set(value) {
      value?.let {
        layer?.lineTranslateAnchor(it)
      }
    }

  /**
   * Create the layer for managed annotations
   *
   * @return the layer created
   */
  override fun createLayer(): LineLayer {
    return lineLayer(layerId, sourceId) {}
  }

  /**
   * The filter on the managed polylineAnnotations.
   *
   * @param expression expression
   */
  override var layerFilter: Expression?
    /**
     * Get filter of the managed polylineAnnotations.
     *
     * @return expression
     */
    get() = layer?.filter
    /**
     * Set filter on the managed polylineAnnotations.
     *
     * @param expression expression
     */
    set(value) {
      value?.let { layer?.filter(it) }
    }

  /**
   * Static variables and methods.
   */
  companion object {
    /** The generator for id */
    var ID_GENERATOR = AtomicLong(0)
  }
}

/**
 * Extension function to create a PolylineAnnotationManager instance.
 */
fun AnnotationPlugin.createPolylineAnnotationManager(
  mapView: View,
  annotationConfig: AnnotationConfig? = null
): PolylineAnnotationManager {
  return createAnnotationManager(mapView, AnnotationType.PolylineAnnotation, annotationConfig) as PolylineAnnotationManager
}