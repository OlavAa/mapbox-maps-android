// This file is generated.

package com.mapbox.maps.plugin.annotation.generated

import android.view.View
import com.mapbox.geojson.*
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.get
import com.mapbox.maps.extension.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.properties.generated.*
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationManagerImpl
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.AnnotationType
import com.mapbox.maps.plugin.delegates.MapDelegateProvider
import java.util.concurrent.atomic.AtomicLong

/**
 * The circleAnnotation manager allows to add circleAnnotations to a map.
 */
class CircleAnnotationManager(
  mapView: View,
  delegateProvider: MapDelegateProvider,
  annotationConfig: AnnotationConfig? = null
) :
  AnnotationManagerImpl<Point, CircleAnnotation, CircleAnnotationOptions, OnCircleAnnotationDragListener, OnCircleAnnotationClickListener, OnCircleAnnotationLongClickListener, OnCircleAnnotationInteractionListener, CircleLayer>(
    mapView, delegateProvider, annotationConfig
  ) {
  private val id = ID_GENERATOR.incrementAndGet()
  override val layerId = annotationConfig?.layerId ?: "mapbox-android-circleAnnotation-layer-$id"
  override val sourceId = annotationConfig?.sourceId ?: "mapbox-android-circleAnnotation-source-$id"

  init {
    delegateProvider.getStyle {
      style = it
      initLayerAndSource()
    }
  }

  override fun initializeDataDrivenPropertyMap() {
    dataDrivenPropertyUsageMap[CircleAnnotationOptions.PROPERTY_CIRCLE_SORT_KEY] = false
    dataDrivenPropertyUsageMap[CircleAnnotationOptions.PROPERTY_CIRCLE_BLUR] = false
    dataDrivenPropertyUsageMap[CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR] = false
    dataDrivenPropertyUsageMap[CircleAnnotationOptions.PROPERTY_CIRCLE_OPACITY] = false
    dataDrivenPropertyUsageMap[CircleAnnotationOptions.PROPERTY_CIRCLE_RADIUS] = false
    dataDrivenPropertyUsageMap[CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR] = false
    dataDrivenPropertyUsageMap[CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_OPACITY] = false
    dataDrivenPropertyUsageMap[CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_WIDTH] = false
  }

  override fun setDataDrivenPropertyIsUsed(property: String) {
    when (property) {
      CircleAnnotationOptions.PROPERTY_CIRCLE_SORT_KEY -> layer?.circleSortKey(get(CircleAnnotationOptions.PROPERTY_CIRCLE_SORT_KEY))
      CircleAnnotationOptions.PROPERTY_CIRCLE_BLUR -> layer?.circleBlur(get(CircleAnnotationOptions.PROPERTY_CIRCLE_BLUR))
      CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR -> layer?.circleColor(get(CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR))
      CircleAnnotationOptions.PROPERTY_CIRCLE_OPACITY -> layer?.circleOpacity(get(CircleAnnotationOptions.PROPERTY_CIRCLE_OPACITY))
      CircleAnnotationOptions.PROPERTY_CIRCLE_RADIUS -> layer?.circleRadius(get(CircleAnnotationOptions.PROPERTY_CIRCLE_RADIUS))
      CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR -> layer?.circleStrokeColor(get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR))
      CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_OPACITY -> layer?.circleStrokeOpacity(get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_OPACITY))
      CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_WIDTH -> layer?.circleStrokeWidth(get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_WIDTH))
    }
  }

  /**
   * Create a list of circleAnnotations on the map.
   *
   * CircleAnnotations are going to be created only for features with a matching geometry.
   *
   * All supported properties are:
   * CircleAnnotationOptions.PROPERTY_CIRCLE_SORT_KEY - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_BLUR - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR - String   * CircleAnnotationOptions.PROPERTY_CIRCLE_OPACITY - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_RADIUS - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR - String   * CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_OPACITY - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_WIDTH - Double   * Learn more about above properties in the )[The online documentation](https://www.mapbox.com/mapbox-gl-js/style-spec/).
   *
   * Out of spec properties:
   * "is-draggable" - Boolean, true if the circleAnnotation should be draggable, false otherwise
   *
   * @param json the GeoJSON defining the list of circleAnnotations to build
   * @return the list of built circleAnnotations
   */
  fun create(json: String): List<CircleAnnotation> {
    return create(FeatureCollection.fromJson(json))
  }

  /**
   * Create a list of circleAnnotations on the map.
   *
   * CircleAnnotations are going to be created only for features with a matching geometry.
   *
   * All supported properties are:
   * CircleAnnotationOptions.PROPERTY_CIRCLE_SORT_KEY - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_BLUR - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR - String   * CircleAnnotationOptions.PROPERTY_CIRCLE_OPACITY - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_RADIUS - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR - String   * CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_OPACITY - Double   * CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_WIDTH - Double   * Learn more about above properties in the )[The online documentation](https://www.mapbox.com/mapbox-gl-js/style-spec/).
   *
   * Out of spec properties:
   * "is-draggable" - Boolean, true if the circleAnnotation should be draggable, false otherwise
   *
   * @param featureCollection the featureCollection defining the list of circleAnnotations to build
   * @return the list of built circleAnnotations
   */
  fun create(featureCollection: FeatureCollection): List<CircleAnnotation> {
    featureCollection.features()?.let { features ->
      val options = features.mapNotNull {
        CircleAnnotationOptions.fromFeature(it)
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
    return CircleAnnotation.ID_KEY
  }

  // Property accessors
  /**
   * The CirclePitchAlignment property
   *
   * Orientation of circle when map is pitched.
   */
  var circlePitchAlignment: CirclePitchAlignment?
    /**
     * Get the CirclePitchAlignment property
     *
     * @return property wrapper value around CirclePitchAlignment
     */
    get(): CirclePitchAlignment? {
      return layer?.circlePitchAlignment
    }
    /**
     * Set the CirclePitchAlignment property
     * @param value property wrapper value around CirclePitchAlignment
     */
    set(value) {
      value?.let {
        layer?.circlePitchAlignment(it)
      }
    }

  /**
   * The CirclePitchScale property
   *
   * Controls the scaling behavior of the circle when the map is pitched.
   */
  var circlePitchScale: CirclePitchScale?
    /**
     * Get the CirclePitchScale property
     *
     * @return property wrapper value around CirclePitchScale
     */
    get(): CirclePitchScale? {
      return layer?.circlePitchScale
    }
    /**
     * Set the CirclePitchScale property
     * @param value property wrapper value around CirclePitchScale
     */
    set(value) {
      value?.let {
        layer?.circlePitchScale(it)
      }
    }

  /**
   * The CircleTranslate property
   *
   * The geometry's offset. Values are [x, y] where negatives indicate left and up, respectively.
   */
  var circleTranslate: List<Double>?
    /**
     * Get the CircleTranslate property
     *
     * @return property wrapper value around List<Double>
     */
    get(): List<Double>? {
      return layer?.circleTranslate
    }
    /**
     * Set the CircleTranslate property
     * @param value property wrapper value around List<Double>
     */
    set(value) {
      value?.let {
        layer?.circleTranslate(it)
      }
    }

  /**
   * The CircleTranslateAnchor property
   *
   * Controls the frame of reference for {@link PropertyFactory#circleTranslate}.
   */
  var circleTranslateAnchor: CircleTranslateAnchor?
    /**
     * Get the CircleTranslateAnchor property
     *
     * @return property wrapper value around CircleTranslateAnchor
     */
    get(): CircleTranslateAnchor? {
      return layer?.circleTranslateAnchor
    }
    /**
     * Set the CircleTranslateAnchor property
     * @param value property wrapper value around CircleTranslateAnchor
     */
    set(value) {
      value?.let {
        layer?.circleTranslateAnchor(it)
      }
    }

  /**
   * Create the layer for managed annotations
   *
   * @return the layer created
   */
  override fun createLayer(): CircleLayer {
    return circleLayer(layerId, sourceId) {}
  }

  /**
   * The filter on the managed circleAnnotations.
   *
   * @param expression expression
   */
  override var layerFilter: Expression?
    /**
     * Get filter of the managed circleAnnotations.
     *
     * @return expression
     */
    get() = layer?.filter
    /**
     * Set filter on the managed circleAnnotations.
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
 * Extension function to create a CircleAnnotationManager instance.
 */
fun AnnotationPlugin.createCircleAnnotationManager(
  mapView: View,
  annotationConfig: AnnotationConfig? = null
): CircleAnnotationManager {
  return createAnnotationManager(mapView, AnnotationType.CircleAnnotation, annotationConfig) as CircleAnnotationManager
}