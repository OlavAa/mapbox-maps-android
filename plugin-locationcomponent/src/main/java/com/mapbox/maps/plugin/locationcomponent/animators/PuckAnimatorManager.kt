package com.mapbox.maps.plugin.locationcomponent.animators

import android.animation.ValueAnimator
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.locationcomponent.LocationLayerRenderer
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings
import com.mapbox.maps.plugin.locationcomponent.utils.MathUtils

internal class PuckAnimatorManager(
  indicatorPositionChangedListener: OnIndicatorPositionChangedListener,
  indicatorBearingChangedListener: OnIndicatorBearingChangedListener
) {

  private var bearingAnimator = PuckBearingAnimator(indicatorBearingChangedListener)
  private var positionAnimator = PuckPositionAnimator(indicatorPositionChangedListener)
  private var pulsingAnimator = PuckPulsingAnimator()

  @VisibleForTesting(otherwise = PRIVATE)
  constructor(
    indicatorPositionChangedListener: OnIndicatorPositionChangedListener,
    indicatorBearingChangedListener: OnIndicatorBearingChangedListener,
    bearingAnimator: PuckBearingAnimator,
    positionAnimator: PuckPositionAnimator,
    pulsingAnimator: PuckPulsingAnimator
  ) : this(indicatorPositionChangedListener, indicatorBearingChangedListener) {
    this.bearingAnimator = bearingAnimator
    this.positionAnimator = positionAnimator
    this.pulsingAnimator = pulsingAnimator
  }

  fun setLocationLayerRenderer(renderer: LocationLayerRenderer) {
    bearingAnimator.setLocationLayerRenderer(renderer)
    positionAnimator.setLocationLayerRenderer(renderer)
    pulsingAnimator.setLocationLayerRenderer(renderer)
  }

  fun setUpdateListeners(
    onLocationUpdated: ((Point) -> Unit),
    onBearingUpdated: ((Double) -> Unit)
  ) {
    positionAnimator.setUpdateListener(onLocationUpdated)
    bearingAnimator.setUpdateListener(onBearingUpdated)
  }

  fun onStart() {
    if (pulsingAnimator.enabled) {
      pulsingAnimator.animateInfinite()
    }
  }

  fun onStop() {
    bearingAnimator.cancelRunning()
    positionAnimator.cancelRunning()
    pulsingAnimator.cancelRunning()
  }

  fun animateBearing(
    vararg targets: Double,
    options: (ValueAnimator.() -> Unit)?
  ) {
    val optimized = DoubleArray(targets.size)
    targets.toTypedArray().apply {
      for (i in 0 until size) {
        optimized[i] = if (i == 0)
          MathUtils.normalize(get(i))
        else
          MathUtils.shortestRotation(MathUtils.normalize(get(i)), optimized[i - 1])
      }
    }
    bearingAnimator.animate(*optimized.toTypedArray(), options = options)
  }

  fun animatePosition(
    vararg targets: Point,
    options: (ValueAnimator.() -> Unit)?
  ) {
    positionAnimator.animate(*targets, options = options)
  }

  fun applyPulsingAnimationSettings(settings: LocationComponentSettings) {
    pulsingAnimator.apply {
      enabled = settings.pulsingEnabled
      maxRadius = settings.pulsingMaxRadius.toDouble()
      pulsingColor = settings.pulsingColor
      if (settings.pulsingEnabled) {
        animateInfinite()
      } else {
        cancelRunning()
      }
    }
  }

  fun updateBearingAnimator(block: ValueAnimator.() -> Unit) {
    bearingAnimator.updateOptions(block)
  }

  fun updatePositionAnimator(block: ValueAnimator.() -> Unit) {
    positionAnimator.updateOptions(block)
  }
}