// This file is generated.

package com.mapbox.maps.plugin.annotation.generated

import android.graphics.Color
import android.graphics.PointF
import android.view.View
import com.mapbox.android.gestures.MoveDistancesObject
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.bindgen.Expected
import com.mapbox.bindgen.ExpectedFactory
import com.mapbox.bindgen.Value
import com.mapbox.common.ValueConverter
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.QueriedFeature
import com.mapbox.maps.QueryFeaturesCallback
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.extension.style.StyleInterface
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.addLayerBelow
import com.mapbox.maps.extension.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.plugin.annotation.*
import com.mapbox.maps.plugin.delegates.*
import com.mapbox.maps.plugin.gestures.GesturesPlugin
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.OnMoveListener
import io.mockk.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowValueConverter::class, ShadowLogger::class])
class CircleAnnotationManagerTest {
  private val delegateProvider: MapDelegateProvider = mockk()
  private val style: StyleInterface = mockk()
  private val mapCameraManagerDelegate: MapCameraManagerDelegate = mockk()
  private val mapFeatureQueryDelegate: MapFeatureQueryDelegate = mockk()
  private val gesturesPlugin: GesturesPlugin = mockk()
  private val layer: CircleLayer = mockk()
  private val source: GeoJsonSource = mockk()
  private val mapView: View = mockk()
  private val queriedFeatures = mockk<Expected<List<QueriedFeature>, String>>()
  private val queriedFeature = mockk<QueriedFeature>()
  private val feature = mockk<Feature>()
  private val queriedFeatureList = listOf(queriedFeature)
  private val querySlot = slot<QueryFeaturesCallback>()

  private lateinit var manager: CircleAnnotationManager
  @Before
  fun setUp() {
    mockkStatic("com.mapbox.maps.extension.style.layers.LayerKt")
    mockkStatic("com.mapbox.maps.extension.style.sources.SourceKt")
    mockkStatic(ValueConverter::class)
    every { ValueConverter.fromJson(any()) } returns ExpectedFactory.createValue(
      Value(1)
    )
    val captureCallback = slot<(StyleInterface) -> Unit>()
    every { delegateProvider.getStyle(capture(captureCallback)) } answers {
      captureCallback.captured.invoke(style)
    }
    val styleStateDelegate = mockk<MapStyleStateDelegate>()
    every { delegateProvider.styleStateDelegate } returns styleStateDelegate
    every { styleStateDelegate.isFullyLoaded() } returns true
    every { style.addSource(any()) } just Runs
    every { style.addLayer(any()) } just Runs
    every { style.addLayerBelow(any(), any()) } just Runs
    every { style.getSource(any()) } returns null
    every { style.styleSourceExists(any()) } returns false
    every { style.styleLayerExists(any()) } returns false
    every { style.removeStyleLayer(any()) } returns mockk()
    every { style.removeStyleSource(any()) } returns mockk()
    every { style.pixelRatio } returns 1.0f
    every { style.getStyleImage(any()) } returns null
    every { gesturesPlugin.addOnMapClickListener(any()) } just Runs
    every { gesturesPlugin.addOnMapLongClickListener(any()) } just Runs
    every { gesturesPlugin.addOnMoveListener(any()) } just Runs
    every { gesturesPlugin.removeOnMoveListener(any()) } just Runs
    every { gesturesPlugin.removeOnMapClickListener(any()) } just Runs
    every { gesturesPlugin.removeOnMapLongClickListener(any()) } just Runs
    every { delegateProvider.mapPluginProviderDelegate.getPlugin(any<Class<GesturesPlugin>>()) } returns gesturesPlugin
    every { delegateProvider.mapCameraManagerDelegate } returns mapCameraManagerDelegate
    every { delegateProvider.mapFeatureQueryDelegate } returns mapFeatureQueryDelegate
    every { mapCameraManagerDelegate.coordinateForPixel(any()) } returns Point.fromLngLat(0.0, 0.0)
    every { mapCameraManagerDelegate.pixelForCoordinate(any()) } returns ScreenCoordinate(1.0, 1.0)
    every { mapView.scrollX } returns 0
    every { mapView.scrollY } returns 0
    every { layer.layerId } returns "layer0"
    every { source.sourceId } returns "source0"
    every { source.featureCollection(any()) } answers { source }

    every { queriedFeature.feature } returns feature
    every { queriedFeatures.value } returns queriedFeatureList
    every { feature.getProperty(any()).asLong } returns 0L
    every {
      mapFeatureQueryDelegate.queryRenderedFeatures(
        any<ScreenCoordinate>(),
        any(),
        capture(querySlot)
      )
    } answers {
      querySlot.captured.run(queriedFeatures)
    }

    manager = CircleAnnotationManager(mapView, delegateProvider)
    manager.layer = layer
    manager.source = source
    every { layer.circleSortKey(any<Expression>()) } answers { layer }
    every { layer.circleBlur(any<Expression>()) } answers { layer }
    every { layer.circleColor(any<Expression>()) } answers { layer }
    every { layer.circleOpacity(any<Expression>()) } answers { layer }
    every { layer.circleRadius(any<Expression>()) } answers { layer }
    every { layer.circleStrokeColor(any<Expression>()) } answers { layer }
    every { layer.circleStrokeOpacity(any<Expression>()) } answers { layer }
    every { layer.circleStrokeWidth(any<Expression>()) } answers { layer }
  }

  @Test
  fun initialize() {
    verify { gesturesPlugin.addOnMapClickListener(any()) }
    verify { gesturesPlugin.addOnMapLongClickListener(any()) }
    verify { gesturesPlugin.addOnMoveListener(any()) }
    assertEquals(CircleAnnotation.ID_KEY, manager.getAnnotationIdKey())
    verify { style.addLayer(any()) }
    every { style.styleLayerExists("test_layer") } returns true

    manager = CircleAnnotationManager(mapView, delegateProvider, AnnotationConfig("test_layer"))
    verify { style.addLayerBelow(any(), "test_layer") }

    manager.addClickListener(mockk())
    manager.addDragListener(mockk())
    manager.addLongClickListener(mockk())
    assertEquals(1, manager.dragListeners.size)
    assertEquals(1, manager.clickListeners.size)
    assertEquals(1, manager.longClickListeners.size)
    every { style.styleSourceExists(any()) } returns true
    every { style.styleLayerExists(any()) } returns true
    manager.onDestroy()
    verify { style.removeStyleLayer(any()) }
    verify { style.removeStyleSource(any()) }
    verify { gesturesPlugin.removeOnMapClickListener(any()) }
    verify { gesturesPlugin.removeOnMapLongClickListener(any()) }
    verify { gesturesPlugin.removeOnMoveListener(any()) }
    assertTrue(manager.dragListeners.isEmpty())
    assertTrue(manager.clickListeners.isEmpty())
    assertTrue(manager.longClickListeners.isEmpty())
  }

  @Test
  fun createWithGeoJsonOptions() {
    manager = CircleAnnotationManager(
      mapView, delegateProvider,
      AnnotationConfig(
        annotationSourceOptions = AnnotationSourceOptions(
          10, 20L, true, 10.0
        )
      )
    )

    val sourceString = manager.source.toString()
    assertTrue(sourceString.contains("maxzoom = 10"))
    assertTrue(sourceString.contains("lineMetrics = true"))
    assertTrue(sourceString.contains("buffer = 20"))
    assertTrue(sourceString.contains("tolerance = 10.0"))
  }

  @Test
  fun create() {
    val annotation = manager.create(
      CircleAnnotationOptions()
        .withPoint(Point.fromLngLat(0.0, 0.0))
    )
    assertEquals(annotation, manager.annotations[0])
  }

  @Test
  fun createFromFeature() {
    val featureCollection =
      FeatureCollection.fromFeature(Feature.fromGeometry(Point.fromLngLat(0.0, 0.0)))
    val annotations = manager.create(featureCollection.toJson())
    assertEquals(annotations.first(), manager.annotations[0])
    val annotations1 = manager.create(featureCollection)
    assertEquals(annotations1.first(), manager.annotations[1])
  }

  @Test
  fun createList() {
    val list = listOf(
      CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0)),
      CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0))
    )
    val annotations = manager.create(list)
    assertEquals(annotations[0], manager.annotations[0])
    assertEquals(annotations[1], manager.annotations[1])
  }

  @Test
  fun update() {
    val annotation = manager.create(CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0)))
    assertEquals(annotation, manager.annotations[0])
    annotation.point = Point.fromLngLat(1.0, 1.0)
    manager.update(annotation)
    assertEquals(annotation, manager.annotations[0])
  }

  @Test
  fun updateList() {
    val list = listOf(
      CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0)),
      CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0))
    )
    val annotations = manager.create(list)
    assertEquals(annotations[0], manager.annotations[0])
    assertEquals(annotations[1], manager.annotations[1])
    annotations[0].point = Point.fromLngLat(1.0, 1.0)
    annotations[1].point = Point.fromLngLat(1.0, 1.0)
    manager.update(annotations)
    assertEquals(annotations[0], manager.annotations[0])
    assertEquals(annotations[1], manager.annotations[1])
  }

  @Test
  fun delete() {
    val annotation = manager.create(
      CircleAnnotationOptions()
        .withPoint(Point.fromLngLat(0.0, 0.0))
    )
    assertEquals(annotation, manager.annotations[0])
    manager.delete(annotation)
    assertTrue(manager.annotations.isEmpty())
  }

  @Test
  fun deleteList() {
    val list = listOf(
      CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0)),
      CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0))
    )
    val annotations = manager.create(list)
    assertEquals(annotations[0], manager.annotations[0])
    assertEquals(annotations[1], manager.annotations[1])

    manager.delete(annotations)
    assertTrue(manager.annotations.isEmpty())
  }

  @Test
  fun deleteAll() {
    val list = listOf(
      CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0)),
      CircleAnnotationOptions().withPoint(Point.fromLngLat(0.0, 0.0))
    )
    val annotations = manager.create(list)
    assertEquals(annotations[0], manager.annotations[0])
    assertEquals(annotations[1], manager.annotations[1])

    manager.deleteAll()
    assertTrue(manager.annotations.isEmpty())
  }

  @Test
  fun click() {
    val captureSlot = slot<OnMapClickListener>()
    every { gesturesPlugin.addOnMapClickListener(capture(captureSlot)) } just Runs
    val manager = CircleAnnotationManager(mapView, delegateProvider)
    val annotation = manager.create(
      CircleAnnotationOptions()
        .withPoint(Point.fromLngLat(0.0, 0.0))
    )
    assertEquals(annotation, manager.annotations[0])

    val listener = mockk<OnCircleAnnotationClickListener>()
    every { listener.onAnnotationClick(any()) } returns false
    manager.addClickListener(listener)

    val interactionListener = mockk<OnCircleAnnotationInteractionListener>()
    every { interactionListener.onSelectAnnotation(any()) } just Runs
    every { interactionListener.onDeselectAnnotation(any()) } just Runs
    manager.addInteractionListener(interactionListener)

    captureSlot.captured.onMapClick(Point.fromLngLat(0.0, 0.0))
    verify { listener.onAnnotationClick(annotation) }
    verify { interactionListener.onSelectAnnotation(annotation) }
    captureSlot.captured.onMapClick(Point.fromLngLat(0.0, 0.0))
    verify { interactionListener.onDeselectAnnotation(annotation) }

    manager.removeClickListener(listener)
    assertTrue(manager.clickListeners.isEmpty())
    manager.removeInteractionListener(interactionListener)
    assertTrue(manager.interactionListener.isEmpty())
  }

  @Test
  fun longClick() {
    val captureSlot = slot<OnMapLongClickListener>()
    every { gesturesPlugin.addOnMapLongClickListener(capture(captureSlot)) } just Runs
    val manager = CircleAnnotationManager(mapView, delegateProvider)

    val annotation = manager.create(
      CircleAnnotationOptions()
        .withPoint(Point.fromLngLat(0.0, 0.0))
    )
    assertEquals(annotation, manager.annotations[0])

    val listener = mockk<OnCircleAnnotationLongClickListener>()
    every { listener.onAnnotationLongClick(any()) } returns false
    manager.addLongClickListener(listener)
    captureSlot.captured.onMapLongClick(Point.fromLngLat(0.0, 0.0))
    verify { listener.onAnnotationLongClick(annotation) }

    manager.removeLongClickListener(listener)
    assertTrue(manager.longClickListeners.isEmpty())
  }

  @Test
  fun drag() {
    val captureSlot = slot<OnMoveListener>()
    every { gesturesPlugin.addOnMoveListener(capture(captureSlot)) } just Runs
    val manager = CircleAnnotationManager(mapView, delegateProvider)
    manager.onSizeChanged(100, 100)
    val annotation = manager.create(
      CircleAnnotationOptions()
        .withPoint(Point.fromLngLat(0.0, 0.0))
    )
    assertEquals(annotation, manager.annotations[0])

    every { feature.getProperty(any()).asLong } returns 0L

    val listener = mockk<OnCircleAnnotationDragListener>()
    every { listener.onAnnotationDragStarted(any()) } just Runs
    every { listener.onAnnotationDragFinished(any()) } just Runs
    every { listener.onAnnotationDrag(any()) } just Runs
    manager.addDragListener(listener)

    annotation.isDraggable = true
    val moveGestureDetector = mockk<MoveGestureDetector>()
    val pointF = PointF(0f, 0f)
    every { moveGestureDetector.pointersCount } returns 1
    every { moveGestureDetector.focalPoint } returns pointF

    captureSlot.captured.onMoveBegin(moveGestureDetector)
    verify { listener.onAnnotationDragStarted(annotation) }

    val moveDistancesObject = mockk<MoveDistancesObject>()
    every { moveDistancesObject.currentX } returns 1f
    every { moveDistancesObject.currentY } returns 1f
    every { moveDistancesObject.distanceXSinceLast } returns 1f
    every { moveDistancesObject.distanceYSinceLast } returns 1f
    every { moveGestureDetector.getMoveObject(any()) } returns moveDistancesObject
    captureSlot.captured.onMove(moveGestureDetector)
    verify { listener.onAnnotationDrag(annotation) }

    captureSlot.captured.onMoveEnd(moveGestureDetector)
    verify { listener.onAnnotationDragFinished(annotation) }

    manager.removeDragListener(listener)
    assertTrue(manager.dragListeners.isEmpty())
  }

  @Test
  fun testCircleSortKeyLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleSortKey(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_SORT_KEY)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleSortKey(1.0)
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleSortKey(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_SORT_KEY)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleSortKey(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_SORT_KEY)) }
  }

  @Test
  fun testCircleBlurLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleBlur(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_BLUR)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleBlur(0.0)
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleBlur(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_BLUR)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleBlur(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_BLUR)) }
  }

  @Test
  fun testCircleColorIntLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleColor(Color.YELLOW)
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR)) }
  }

  @Test
  fun testCircleColorLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleColor("rgba(0, 0, 0, 1)")
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_COLOR)) }
  }

  @Test
  fun testCircleOpacityLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleOpacity(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_OPACITY)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleOpacity(1.0)
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleOpacity(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_OPACITY)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleOpacity(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_OPACITY)) }
  }

  @Test
  fun testCircleRadiusLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleRadius(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_RADIUS)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleRadius(5.0)
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleRadius(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_RADIUS)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleRadius(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_RADIUS)) }
  }

  @Test
  fun testCircleStrokeColorIntLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleStrokeColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleStrokeColor(Color.YELLOW)
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleStrokeColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleStrokeColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR)) }
  }

  @Test
  fun testCircleStrokeColorLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleStrokeColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleStrokeColor("rgba(0, 0, 0, 1)")
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleStrokeColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleStrokeColor(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_COLOR)) }
  }

  @Test
  fun testCircleStrokeOpacityLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleStrokeOpacity(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_OPACITY)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleStrokeOpacity(1.0)
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleStrokeOpacity(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_OPACITY)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleStrokeOpacity(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_OPACITY)) }
  }

  @Test
  fun testCircleStrokeWidthLayerProperty() {
    every { style.styleSourceExists(any()) } returns true
    verify(exactly = 0) { manager.layer?.circleStrokeWidth(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_WIDTH)) }
    val options = CircleAnnotationOptions()
      .withPoint(Point.fromLngLat(0.0, 0.0))
      .withCircleStrokeWidth(0.0)
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleStrokeWidth(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_WIDTH)) }
    manager.create(options)
    verify(exactly = 1) { manager.layer?.circleStrokeWidth(Expression.get(CircleAnnotationOptions.PROPERTY_CIRCLE_STROKE_WIDTH)) }
  }
}