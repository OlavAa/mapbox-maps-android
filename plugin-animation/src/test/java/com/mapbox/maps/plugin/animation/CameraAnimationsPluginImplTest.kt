package com.mapbox.maps.plugin.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper.getMainLooper
import androidx.core.animation.addListener
import com.mapbox.common.ShadowLogger
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CameraState
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions.Companion.cameraAnimatorOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.animator.CameraBearingAnimator
import com.mapbox.maps.plugin.animation.animator.CameraCenterAnimator
import com.mapbox.maps.plugin.animation.animator.CameraPitchAnimator
import com.mapbox.maps.plugin.delegates.MapCameraManagerDelegate
import com.mapbox.maps.plugin.delegates.MapDelegateProvider
import com.mapbox.maps.plugin.delegates.MapTransformDelegate
import io.mockk.*
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLog
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowLogger::class])
@LooperMode(LooperMode.Mode.PAUSED)
class CameraAnimationsPluginImplTest {

  private lateinit var cameraAnimationsPluginImpl: CameraAnimationsPluginImpl
  private lateinit var mapTransformDelegate: MapTransformDelegate
  private lateinit var mapCameraManagerDelegate: MapCameraManagerDelegate
  private lateinit var cameraAnimatorsFactory: CameraAnimatorsFactory
  private lateinit var bearingAnimator: CameraBearingAnimator
  private lateinit var centerAnimator: CameraCenterAnimator

  @Before
  fun setUp() {
    ShadowLog.stream = System.out
    cameraAnimatorsFactory = mockk(relaxed = true)
    bearingAnimator = mockk(relaxed = true)
    centerAnimator = mockk(relaxed = true)
    every { cameraAnimatorsFactory.getEaseTo(any()) } returns arrayOf(
      bearingAnimator,
      centerAnimator
    )
    every { cameraAnimatorsFactory.getMoveBy(any()) } returns arrayOf(
      bearingAnimator,
      centerAnimator
    )
    every { cameraAnimatorsFactory.getPitchBy(any()) } returns arrayOf(
      bearingAnimator,
      centerAnimator
    )
    every { cameraAnimatorsFactory.getRotateBy(any(), any()) } returns arrayOf(
      bearingAnimator,
      centerAnimator
    )
    every { cameraAnimatorsFactory.getScaleBy(any(), any()) } returns arrayOf(
      bearingAnimator,
      centerAnimator
    )

    val delegateProvider = mockk<MapDelegateProvider>(relaxed = true)
    mapCameraManagerDelegate = mockk(relaxed = true)
    mapTransformDelegate = mockk(relaxed = true)
    mockkObject(CameraTransform)
    every { delegateProvider.mapCameraManagerDelegate } returns mapCameraManagerDelegate
    every { delegateProvider.mapTransformDelegate } returns mapTransformDelegate
    every { CameraTransform.normalizeAngleRadians(any(), any()) } answers { secondArg() }
    cameraAnimationsPluginImpl = CameraAnimationsPluginImpl().apply {
      onDelegateProvider(delegateProvider)
    }
  }

  @Test
  fun registerAnimators() {
    val animators = arrayOf(
      centerAnimator, bearingAnimator
    )
    cameraAnimationsPluginImpl.registerAnimators(*animators)
    animators.forEach {
      verify { it.addInternalListener(any()) }
    }
  }

  @Test
  fun unregisterAnimators() {
    val animators = arrayOf(
      centerAnimator, bearingAnimator
    )
    cameraAnimationsPluginImpl.registerAnimators(*animators)
    cameraAnimationsPluginImpl.unregisterAnimators(*animators)
    animators.forEach {
      verify {
        it.addInternalListener(any())
        it.removeInternalListener()
      }
    }
  }

  @Test
  fun unregisterAllAnimators() {
    val animators = arrayOf(
      centerAnimator, bearingAnimator
    )
    cameraAnimationsPluginImpl.registerAnimators(*animators)
    cameraAnimationsPluginImpl.unregisterAllAnimators()
    animators.forEach {
      verify {
        it.addInternalListener(any())
        it.removeInternalListener()
      }
    }
  }

  @Test
  fun startRegisteredAnimation() {
    val bearingAnimator = CameraBearingAnimator(
      cameraAnimatorOptions(10.0) {
        startValue(0.0)
      }
    )
    val animators = arrayOf(
      bearingAnimator
    )
    cameraAnimationsPluginImpl.registerAnimators(*animators)
    AnimatorSet().apply {
      duration = DURATION
      playTogether(*animators)
      start()
    }
    verify { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) }
  }

  @Test
  fun startUnregisteredAnimation() {
    cameraAnimationsPluginImpl.unregisterAllAnimators()
    val bearingAnimator = CameraBearingAnimator(
      cameraAnimatorOptions(10.0) {
        startValue(0.0)
      }
    )
    val animators = arrayOf(
      bearingAnimator
    )
    AnimatorSet().apply {
      duration = DURATION
      playTogether(*animators)
      start()
    }
    verify(exactly = 0) { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) }
  }

  @Test
  fun testEaseToRegister() {
    cameraAnimationsPluginImpl.cameraAnimationsFactory = cameraAnimatorsFactory
    cameraAnimationsPluginImpl.easeTo(cameraOptions, mapAnimationOptions { duration(DURATION) })
    verify {
      centerAnimator.addInternalListener(any())
      bearingAnimator.addInternalListener(any())
    }
  }

  @Test
  fun testMoveToRegister() {
    cameraAnimationsPluginImpl.cameraAnimationsFactory = cameraAnimatorsFactory
    cameraAnimationsPluginImpl.moveBy(ScreenCoordinate(VALUE, VALUE), mapAnimationOptions { duration(DURATION) })
    verify {
      centerAnimator.addInternalListener(any())
      bearingAnimator.addInternalListener(any())
    }
  }

  @Test
  fun testScaleByRegister() {
    cameraAnimationsPluginImpl.cameraAnimationsFactory = cameraAnimatorsFactory
    cameraAnimationsPluginImpl.scaleBy(VALUE, ScreenCoordinate(VALUE, VALUE), mapAnimationOptions { duration(DURATION) })
    verify {
      centerAnimator.addInternalListener(any())
      bearingAnimator.addInternalListener(any())
    }
  }

  @Test
  fun testRotateByRegister() {
    cameraAnimationsPluginImpl.cameraAnimationsFactory = cameraAnimatorsFactory
    cameraAnimationsPluginImpl.rotateBy(
      ScreenCoordinate(VALUE, VALUE),
      ScreenCoordinate(VALUE, VALUE),
      mapAnimationOptions { duration(DURATION) }
    )
    verify {
      centerAnimator.addInternalListener(any())
      bearingAnimator.addInternalListener(any())
    }
  }

  @Test
  fun testPitchByRegister() {
    cameraAnimationsPluginImpl.cameraAnimationsFactory = cameraAnimatorsFactory
    cameraAnimationsPluginImpl.pitchBy(VALUE, mapAnimationOptions { duration(DURATION) })
    verify {
      centerAnimator.addInternalListener(any())
      bearingAnimator.addInternalListener(any())
    }
  }

  @Test
  fun startSubsequentAnimationsWithTheSameType1() {
    var cameraPosition = CameraOptions.Builder().build()
    every { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) } answers {
      cameraPosition = firstArg()
    }

    val targetFirst = 10.0
    val bearingAnimatorFirst = createBearingAnimator(targetFirst, 2, 5)
    val listenerFirst = CameraAnimatorListener()
    bearingAnimatorFirst.addListener(listenerFirst)

    val targetSecond = 12.0
    val bearingAnimatorSecond = createBearingAnimator(targetSecond, 4, 5)
    val listenerSecond = CameraAnimatorListener()
    bearingAnimatorSecond.addListener(listenerSecond)

    cameraAnimationsPluginImpl.registerAnimators(bearingAnimatorFirst, bearingAnimatorSecond)

    shadowOf(getMainLooper()).pause()
    bearingAnimatorFirst.start()
    bearingAnimatorSecond.start()

    shadowOf(getMainLooper()).idle()

    assertTrue(listenerFirst.started)
    assertTrue(listenerSecond.started)
    assertTrue(listenerFirst.canceled)
    assertTrue(listenerSecond.ended)
    assertTrue(listenerSecond.ended)

    assertEquals(targetSecond, cameraPosition.bearing)
  }

  @Test
  fun startSubsequentAnimationsWithTheSameType2() {
    var cameraPosition = CameraOptions.Builder().build()
    every { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) } answers {
      cameraPosition = firstArg()
    }

    val targetFirst = 10.0
    val bearingAnimatorFirst = createBearingAnimator(targetFirst, 2, 3)
    val listenerFirst = CameraAnimatorListener()
    bearingAnimatorFirst.addListener(listenerFirst)

    val targetSecond = 12.0
    val bearingAnimatorSecond = createBearingAnimator(targetSecond, 7, 3)
    val listenerSecond = CameraAnimatorListener()
    bearingAnimatorSecond.addListener(listenerSecond)

    cameraAnimationsPluginImpl.registerAnimators(bearingAnimatorFirst, bearingAnimatorSecond)

    shadowOf(getMainLooper()).pause()
    bearingAnimatorFirst.start()
    bearingAnimatorSecond.start()

    shadowOf(getMainLooper()).idle()

    assertTrue(listenerFirst.started)
    assertTrue(listenerSecond.started)
    assertFalse(listenerFirst.canceled)
    assertFalse(listenerSecond.canceled)
    assertTrue(listenerSecond.ended)
    assertTrue(listenerSecond.ended)

    assertEquals(targetSecond, cameraPosition.bearing)
  }

  @Test
  fun testEaseToSingleDurationZero() {

    var cameraPosition = CameraOptions.Builder().build()
    every { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) } answers {
      cameraPosition = firstArg()
    }

    val targetPitch = 5.0
    val cameraOptions = CameraOptions.Builder().pitch(targetPitch).build()
    val expectedValues = mutableSetOf(0.0, targetPitch)
    val updatedValues = mutableListOf<Double>()

    cameraAnimationsPluginImpl.addCameraPitchChangeListener { updatedValue ->
      updatedValues.add(
        updatedValue
      )
    }

    shadowOf(getMainLooper()).pause()

    cameraAnimationsPluginImpl.easeTo(cameraOptions, mapAnimationOptions { duration(0) })

    shadowOf(getMainLooper()).idle()

    assertEquals(targetPitch, cameraPosition.pitch)
    assertArrayEquals(expectedValues.toDoubleArray(), updatedValues.toDoubleArray(), EPS)
  }

  @Test
  fun testEaseToSingleDurationShort() {

    var cameraPosition = CameraOptions.Builder().build()
    every { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) } answers {
      cameraPosition = firstArg()
    }

    val targetPitch = 5.0
    val cameraOptions = CameraOptions.Builder().pitch(targetPitch).build()
    val expectedValues = mutableSetOf(0.0, targetPitch)
    val updatedValues = mutableListOf<Double>()

    cameraAnimationsPluginImpl.addCameraPitchChangeListener { updatedValue ->
      updatedValues.add(
        updatedValue
      )
    }

    shadowOf(getMainLooper()).pause()

    cameraAnimationsPluginImpl.easeTo(cameraOptions, mapAnimationOptions { duration(1L) })

    shadowOf(getMainLooper()).idle()

    assertEquals(targetPitch, cameraPosition.pitch)
    assertArrayEquals(expectedValues.toDoubleArray(), updatedValues.toDoubleArray(), EPS)
  }

  @Test
  fun testEaseToSequenceDurationZero() {
    var cameraPosition = CameraState(
      Point.fromLngLat(90.0, 90.0),
      EdgeInsets(0.0, 0.0, 0.0, 0.0),
      3.0,
      90.0,
      0.0
    )
    every { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) } answers {
      cameraPosition = (firstArg() as CameraOptions).toCameraState()
    }

    every { mapCameraManagerDelegate.cameraState } answers { cameraPosition }

    val targetPitchFirst = 5.0
    val targetPitchSecond = 10.0
    val targetPitchThird = 15.0
    val cameraOptions1 = CameraOptions.Builder().pitch(targetPitchFirst).build()
    val cameraOptions2 = CameraOptions.Builder().pitch(targetPitchSecond).build()
    val cameraOptions3 = CameraOptions.Builder().pitch(targetPitchThird).build()
    val expectedValues = mutableSetOf(0.0, targetPitchFirst, targetPitchSecond, targetPitchThird)
    val updatedValues = mutableListOf<Double>()

    cameraAnimationsPluginImpl.addCameraPitchChangeListener { updatedValue ->
      updatedValues.add(
        updatedValue
      )
    }

    shadowOf(getMainLooper()).pause()

    val handler = Handler(getMainLooper())
    cameraAnimationsPluginImpl.easeTo(cameraOptions1, mapAnimationOptions { duration(0) })

    handler.postDelayed({ cameraAnimationsPluginImpl.easeTo(cameraOptions2, mapAnimationOptions { duration(0) }) }, 1)
    handler.postDelayed({ cameraAnimationsPluginImpl.easeTo(cameraOptions3, mapAnimationOptions { duration(0) }) }, 2)

    shadowOf(getMainLooper()).idleFor(Duration.ofMillis(2))

    assertEquals(targetPitchThird, cameraPosition.pitch, EPS)
    assertArrayEquals(expectedValues.toDoubleArray(), updatedValues.toDoubleArray(), EPS)
  }

  @Test
  fun testEaseToSequenceQuickDuration() {
    var cameraPosition = CameraState(
      Point.fromLngLat(90.0, 90.0),
      EdgeInsets(0.0, 0.0, 0.0, 0.0),
      3.0,
      90.0,
      0.0
    )
    every { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) } answers {
      cameraPosition = (firstArg() as CameraOptions).toCameraState()
    }

    every { mapCameraManagerDelegate.cameraState } answers { cameraPosition }

    val targetPitchFirst = 5.0
    val targetPitchSecond = 10.0
    val targetPitchThird = 15.0
    val cameraOptions1 = CameraOptions.Builder().pitch(targetPitchFirst).build()
    val cameraOptions2 = CameraOptions.Builder().pitch(targetPitchSecond).build()
    val cameraOptions3 = CameraOptions.Builder().pitch(targetPitchThird).build()
    val expectedValues = mutableSetOf(0.0, targetPitchFirst, targetPitchSecond, targetPitchThird)
    val updatedValues = mutableListOf<Double>()

    cameraAnimationsPluginImpl.addCameraPitchChangeListener { updatedValue ->
      updatedValues.add(
        updatedValue
      )
    }

    shadowOf(getMainLooper()).pause()

    val handler = Handler(getMainLooper())
    cameraAnimationsPluginImpl.easeTo(cameraOptions1, mapAnimationOptions { duration(1) })
    handler.postDelayed({ cameraAnimationsPluginImpl.easeTo(cameraOptions2, mapAnimationOptions { duration(1) }) }, 2)
    handler.postDelayed({ cameraAnimationsPluginImpl.easeTo(cameraOptions3, mapAnimationOptions { duration(1) }) }, 8)

    shadowOf(getMainLooper()).idleFor(Duration.ofMillis(10))

    assertEquals(targetPitchThird, cameraPosition.pitch, EPS)
    assertArrayEquals(expectedValues.toDoubleArray(), updatedValues.toDoubleArray(), EPS)
  }

  @Test
  fun testDelayedAnimatorsFinalStateAndCallbackResult() {
    var cameraPosition = CameraOptions.Builder().build()
    every { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) } answers {
      cameraPosition = firstArg()
    }

    val targetPitchOne = 10.0
    val pitchAnimatorOne = createPitchAnimator(targetPitchOne, 0, 1000L)
    val pitchListenerOne = CameraAnimatorListener()
    pitchAnimatorOne.addListener(pitchListenerOne)

    val targetPitchTwo = 20.0
    val pitchAnimatorTwo = createPitchAnimator(targetPitchTwo, 500, 1000L)
    val pitchListenerTwo = CameraAnimatorListener()
    pitchAnimatorTwo.addListener(pitchListenerTwo)

    val targetPitchThree = 30.0
    val pitchAnimatorThree = createPitchAnimator(targetPitchThree, 750, 1000L)
    val pitchListenerThree = CameraAnimatorListener()
    pitchAnimatorThree.addListener(pitchListenerThree)

    var currentPitch = 0.0
    cameraAnimationsPluginImpl.addCameraPitchChangeListener { updatedValue ->
      currentPitch = updatedValue
    }

    cameraAnimationsPluginImpl.registerAnimators(
      pitchAnimatorOne,
      pitchAnimatorTwo,
      pitchAnimatorThree
    )

    shadowOf(getMainLooper()).pause()

    pitchAnimatorOne.start()
    pitchAnimatorTwo.start()
    pitchAnimatorThree.start()

    shadowOf(getMainLooper()).idle()

    assertTrue(pitchListenerOne.canceled)
    assertTrue(pitchListenerTwo.canceled)
    assertFalse(pitchListenerThree.canceled)
    assertTrue(pitchListenerThree.ended)
    assertEquals(targetPitchThree, currentPitch, EPS)
    assertEquals(targetPitchThree, cameraPosition.pitch ?: -1.0, EPS)
  }

  @Test
  fun testUpdateFrequency() {
    val bearingDuration = 37L
    val bearingAnimator = createBearingAnimator(10.0, 2, bearingDuration)

    cameraAnimationsPluginImpl.registerAnimators(bearingAnimator)
    shadowOf(getMainLooper()).pause()

    bearingAnimator.start()
    shadowOf(getMainLooper()).idle()

    // Adding value 2 because of first call after Animator.start() and last in the onEnd() or onCancel()
    val countUpdates = (bearingDuration + 2).toInt()
    verify(exactly = countUpdates) { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) }
  }

  @Test
  fun testAnimatorListenersCallsCount() {

    val bearingDuration = 17L
    val bearingAnimator = createBearingAnimator(10.0, 2, bearingDuration)
    val bearingListener = CameraAnimatorListener()
    val bearingUpdateListener = CameraUpdateAnimatorListener()
    bearingAnimator.addListener(bearingListener)
    bearingAnimator.addUpdateListener(bearingUpdateListener)

    cameraAnimationsPluginImpl.registerAnimators(bearingAnimator)
    shadowOf(getMainLooper()).pause()

    bearingAnimator.start()
    shadowOf(getMainLooper()).idle()

    assertEquals(1, bearingListener.endedCount)
    assertEquals(1, bearingListener.startedCount)

    // Adding +1 because of count of interpolated intervals (which is equal to bearing duration) and start value
    assertEquals(bearingDuration + 1, bearingUpdateListener.updateCount)
  }

  @Test
  fun executeBearingAnimator() {
    var cameraPosition = CameraOptions.Builder().build()
    every { mapCameraManagerDelegate.setCamera(any<CameraOptions>()) } answers {
      cameraPosition = firstArg()
    }

    val targetBearing = 12.0
    val bearingAnimator = CameraBearingAnimator(
      cameraAnimatorOptions(targetBearing) {
        startValue(0.0)
      }
    ) {
      duration = DURATION
    }

    cameraAnimationsPluginImpl.registerAnimators(bearingAnimator)
    shadowOf(getMainLooper()).pause()

    bearingAnimator.start()

    shadowOf(getMainLooper()).idle()

    assertEquals(targetBearing, cameraPosition.bearing)
  }

  @Test
  fun testSetUserAnimationProgressSubsequentAnimators() {
    val bearingAnimatorFirst = createBearingAnimator(10.0, 2, 5)
    val bearingAnimatorSecond = createBearingAnimator(12.0, 8, 5)

    cameraAnimationsPluginImpl.registerAnimators(bearingAnimatorFirst, bearingAnimatorSecond)

    shadowOf(getMainLooper()).pause()

    bearingAnimatorFirst.start()
    bearingAnimatorSecond.start()

    shadowOf(getMainLooper()).idle()

    verifyOrder {
      mapTransformDelegate.setUserAnimationInProgress(true)
      mapTransformDelegate.setUserAnimationInProgress(false)
      mapTransformDelegate.setUserAnimationInProgress(true)
      mapTransformDelegate.setUserAnimationInProgress(false)
    }
  }

  @Test
  fun testSetUserAnimationProgressOverlappedAnimators() {
    val bearingAnimatorFirst = createBearingAnimator(10.0, 2, 5)
    val bearingAnimatorSecond = createBearingAnimator(12.0, 4, 5)

    cameraAnimationsPluginImpl.registerAnimators(bearingAnimatorFirst, bearingAnimatorSecond)

    shadowOf(getMainLooper()).pause()

    bearingAnimatorFirst.start()
    bearingAnimatorSecond.start()

    shadowOf(getMainLooper()).idle()

    verifyOrder {
      mapTransformDelegate.setUserAnimationInProgress(true)
      mapTransformDelegate.setUserAnimationInProgress(true)
      mapTransformDelegate.setUserAnimationInProgress(false)
    }
  }

  @Test
  fun testPlayAnimatorsTogether() {

    val pitch = createPitchAnimator(15.0, 0, 5)
    val pitchListener = CameraAnimatorListener()
    pitch.addListener(pitchListener)

    val bearing = createBearingAnimator(10.0, 0, 5)
    bearing.addListener(
      onStart = {
        assertEquals(pitchListener.started, true)
        assertEquals(pitchListener.ended, false)
        assertEquals(pitchListener.canceled, false)
      }
    )

    shadowOf(getMainLooper()).pause()

    cameraAnimationsPluginImpl.playAnimatorsTogether(pitch, bearing)

    shadowOf(getMainLooper()).idle()
  }

  @Test
  fun testPlayAnimatorsSequentially() {

    val pitch = createPitchAnimator(15.0, 0, 5)
    val pitchListener = CameraAnimatorListener()
    pitch.addListener(pitchListener)

    val bearing = createBearingAnimator(10.0, 0, 5)
    bearing.addListener(
      onStart = {
        assertEquals(pitchListener.started, true)
        assertEquals(pitchListener.ended, true)
      }
    )

    shadowOf(getMainLooper()).pause()

    cameraAnimationsPluginImpl.playAnimatorsSequentially(pitch, bearing)

    shadowOf(getMainLooper()).idle()
  }

  @Test
  fun testAnimatorListenerParameterEnd() {
    val listener = CameraAnimatorListener()
    shadowOf(getMainLooper()).pause()
    cameraAnimationsPluginImpl.easeTo(
      cameraOptions,
      mapAnimationOptions {
        duration(100L)
        animatorListener(listener)
      }
    )
    shadowOf(getMainLooper()).idle()
    assertEquals(true, listener.started)
    assertEquals(false, listener.canceled)
    assertEquals(true, listener.ended)
  }

  @Test
  fun testAnimatorListenerParameterCancel() {
    val listener = CameraAnimatorListener()

    shadowOf(getMainLooper()).pause()

    val handler = Handler(getMainLooper())
    cameraAnimationsPluginImpl.easeTo(
      cameraOptions,
      mapAnimationOptions {
        duration(10L)
        animatorListener(listener)
      }
    )
    handler.postDelayed(
      {
        cameraAnimationsPluginImpl.cancelAllAnimators()
      },
      5L
    )

    shadowOf(getMainLooper()).idleFor(Duration.ofMillis(20L))

    assertEquals(true, listener.started)
    assertEquals(true, listener.canceled)
    // end is triggered after cancel in any case
    assertEquals(true, listener.ended)
  }

  @Test
  fun testCancelAllExceptProtected() {

    val listenerOne = CameraAnimatorListener()
    val listenerTwo = CameraAnimatorListener()
    val listenerThree = CameraAnimatorListener()

    val animatorOne = cameraAnimationsPluginImpl.createBearingAnimator(
      cameraAnimatorOptions(2.0) {
        owner("Owner_1")
      }
    ) {
      duration = 200L
      addListener(listenerOne)
    }
    val animatorTwo = cameraAnimationsPluginImpl.createZoomAnimator(
      cameraAnimatorOptions(3.0) {
        owner("Owner_2")
      }
    ) {
      duration = 200L
      addListener(listenerTwo)
    }
    val animatorThree = cameraAnimationsPluginImpl.createPitchAnimator(
      cameraAnimatorOptions(4.0) {
        owner("Owner_3")
      }
    ) {
      duration = 200L
      addListener(listenerThree)
    }

    val handler = Handler(getMainLooper())

    shadowOf(getMainLooper()).pause()
    cameraAnimationsPluginImpl.registerAnimators(animatorOne, animatorTwo, animatorThree)
    animatorOne.start()
    animatorTwo.start()
    animatorThree.start()
    handler.postDelayed(
      {
        cameraAnimationsPluginImpl.cancelAllAnimators(listOf("Owner_1", "Owner_3"))
      },
      5L
    )
    shadowOf(getMainLooper()).idleFor(Duration.ofMillis(20L))

    assertEquals(false, listenerOne.canceled)
    assertEquals(true, listenerTwo.canceled)
    assertEquals(false, listenerThree.canceled)
  }

  @Test
  fun testAnimatorListenerParameterCancelAnotherAnimation() {
    val listenerOne = CameraAnimatorListener()
    val listenerTwo = CameraAnimatorListener()

    shadowOf(getMainLooper()).pause()

    val handler = Handler(getMainLooper())
    cameraAnimationsPluginImpl.easeTo(
      cameraOptions,
      mapAnimationOptions {
        duration(10L)
        animatorListener(listenerOne)
      }
    )
    handler.postDelayed(
      {
        cameraAnimationsPluginImpl.easeTo(
          cameraOptions,
          mapAnimationOptions {
            duration(10L)
            animatorListener(listenerTwo)
          }
        )
      },
      5L
    )

    shadowOf(getMainLooper()).idleFor(Duration.ofMillis(20L))

    assertEquals(true, listenerOne.started)
    assertEquals(true, listenerOne.canceled)
    // end is triggered after cancel in any case
    assertEquals(true, listenerOne.ended)

    assertEquals(true, listenerTwo.started)
    assertEquals(false, listenerTwo.canceled)
    assertEquals(true, listenerTwo.ended)
  }

  @Test
  fun testLifecycleListener() {
    val listenerOne = LifecycleListener()
    shadowOf(getMainLooper()).pause()
    cameraAnimationsPluginImpl.addCameraAnimationsLifecycleListener(listenerOne)
    val bearingAnimatorOne = cameraAnimationsPluginImpl.createBearingAnimator(
      cameraAnimatorOptions(60.0) {
        startValue(10.0)
      }
    ) {
      duration = 10L
    }
    cameraAnimationsPluginImpl.registerAnimators(bearingAnimatorOne)
    bearingAnimatorOne.start()
    shadowOf(getMainLooper()).idle()

    assertEquals(true, listenerOne.starting)
    assertEquals(false, listenerOne.cancelling)
    assertEquals(true, listenerOne.ending)
    assertEquals(false, listenerOne.interrupting)

    shadowOf(getMainLooper()).pause()

    val bearingAnimatorTwo = cameraAnimationsPluginImpl.createBearingAnimator(
      cameraAnimatorOptions(90.0) {
        startValue(10.0)
      }
    ) {
      duration = 50L
    }
    val listenerTwo = LifecycleListener()
    cameraAnimationsPluginImpl.removeCameraAnimationsLifecycleListener(listenerOne)
    cameraAnimationsPluginImpl.addCameraAnimationsLifecycleListener(listenerTwo)
    cameraAnimationsPluginImpl.registerAnimators(bearingAnimatorTwo)
    bearingAnimatorOne.start()
    bearingAnimatorTwo.start()

    shadowOf(getMainLooper()).idle()

    assertEquals(false, listenerOne.cancelling)
    assertEquals(true, listenerTwo.starting)
    assertEquals(true, listenerTwo.cancelling)
    assertEquals(true, listenerTwo.ending)
    assertEquals(true, listenerTwo.interrupting)
  }

  @Test
  fun easeToTestNonZeroDurationWithListener() {
    val options = mapAnimationOptions {
      duration(1000)
      animatorListener(object : AnimatorListenerAdapter() {})
    }
    cameraAnimationsPluginImpl.easeTo(cameraOptions, options)
    assert(cameraAnimationsPluginImpl.highLevelListener != null)
  }

  @Test
  fun easeToTestZeroDurationWithListener() {
    val options = mapAnimationOptions {
      duration(0)
      animatorListener(object : AnimatorListenerAdapter() {})
    }
    cameraAnimationsPluginImpl.easeTo(cameraOptions, options)
    assert(cameraAnimationsPluginImpl.highLevelListener == null)
  }

  @Test
  fun registerOnlyCameraAnimatorsTest() {
    val pitch = createPitchAnimator(15.0, 0, 5)
    val bearing = createBearingAnimator(10.0, 0, 5)
    val animator = ValueAnimator.ofFloat(0.0f, 10.0f)
    cameraAnimationsPluginImpl.playAnimatorsSequentially(pitch, bearing, animator)
    assert(cameraAnimationsPluginImpl.animators.size == 2)
    cameraAnimationsPluginImpl.unregisterAllAnimators()
    cameraAnimationsPluginImpl.playAnimatorsTogether(pitch, bearing, animator)
    assert(cameraAnimationsPluginImpl.animators.size == 2)
  }

  @Test
  fun cancelStartedHighLevelAnimation() {
    val listener = CameraAnimatorListener()
    shadowOf(getMainLooper()).pause()
    val cancelable = cameraAnimationsPluginImpl.flyTo(
      CameraOptions.Builder()
        .center(Point.fromLngLat(VALUE, VALUE))
        .bearing(VALUE)
        .build(),
      mapAnimationOptions {
        duration(50L)
        animatorListener(listener)
      }
    )
    cancelable.cancel()
    shadowOf(getMainLooper()).idle()
    // expecting 1 (and not 2) because we register 1 high-level animator listener
    assertEquals(1, listener.startedCount)
    assertEquals(1, listener.canceledCount)
  }

  @Test
  fun anchorTest() {
    shadowOf(getMainLooper()).pause()
    val anchor = ScreenCoordinate(7.0, 7.0)
    val anchorAnimator = cameraAnimationsPluginImpl.createAnchorAnimator(
      cameraAnimatorOptions(anchor) {
        startValue(anchor)
      }
    ) {
      duration = 10L
    }
    cameraAnimationsPluginImpl.registerAnimators(anchorAnimator)
    anchorAnimator.start()
    shadowOf(getMainLooper()).idle()
    assertEquals(anchor, cameraAnimationsPluginImpl.anchor)
    var counter = 0
    cameraAnimationsPluginImpl.addCameraAnchorChangeListener { counter++ }
    cameraAnimationsPluginImpl.anchor = null
    assertEquals(null, cameraAnimationsPluginImpl.anchor)
    assertEquals(1, counter)
  }

  @Test
  fun nestedHighLevelAnimationListeners() {
    val listener = CameraAnimatorListener()
    shadowOf(getMainLooper()).pause()
    cameraAnimationsPluginImpl.flyTo(
      CameraOptions.Builder()
        .center(Point.fromLngLat(VALUE, VALUE))
        .bearing(VALUE)
        .build(),
      mapAnimationOptions {
        duration(50L)
        animatorListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            cameraAnimationsPluginImpl.flyTo(
              CameraOptions.Builder()
                .center(Point.fromLngLat(VALUE, VALUE))
                .bearing(VALUE)
                .build(),
              mapAnimationOptions {
                duration(50L)
                animatorListener(listener)
              }
            )
          }
        })
      }
    )
    shadowOf(getMainLooper()).idle()
    assertEquals(1, listener.startedCount)
    assertEquals(1, listener.endedCount)
  }

  class LifecycleListener : CameraAnimationsLifecycleListener {
    var starting = false
    var interrupting = false
    var cancelling = false
    var ending = false

    override fun onAnimatorStarting(
      type: CameraAnimatorType,
      animator: ValueAnimator,
      owner: String?
    ) {
      starting = true
    }

    override fun onAnimatorInterrupting(
      type: CameraAnimatorType,
      runningAnimator: ValueAnimator,
      runningAnimatorOwner: String?,
      newAnimator: ValueAnimator,
      newAnimatorOwner: String?
    ) {
      interrupting = true
    }

    override fun onAnimatorEnding(
      type: CameraAnimatorType,
      animator: ValueAnimator,
      owner: String?
    ) {
      ending = true
    }

    override fun onAnimatorCancelling(
      type: CameraAnimatorType,
      animator: ValueAnimator,
      owner: String?
    ) {
      cancelling = true
    }
  }

  class CameraUpdateAnimatorListener : ValueAnimator.AnimatorUpdateListener {

    var updateCount = 0L

    override fun onAnimationUpdate(animation: ValueAnimator) {
      updateCount += 1
    }
  }

  class CameraAnimatorListener : Animator.AnimatorListener {

    var started = false
    var startedCount = 0
    var ended = false
    var endedCount = 0
    var canceled = false
    var canceledCount = 0

    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {
      endedCount += 1
      ended = true
    }

    override fun onAnimationCancel(animation: Animator?) {
      canceledCount += 1
      canceled = true
    }

    override fun onAnimationStart(animation: Animator?) {
      startedCount += 1
      started = true
    }
  }

  private fun createBearingAnimator(target: Double, animatorDelay: Long, animatorDuration: Long) =
    CameraBearingAnimator(
      cameraAnimatorOptions(target) {
        startValue(0.0)
      }
    ) {
      startDelay = animatorDelay
      duration = animatorDuration
    }

  private fun createPitchAnimator(target: Double, animatorDelay: Long, animatorDuration: Long) =
    CameraPitchAnimator(
      cameraAnimatorOptions(target) {
        startValue(0.0)
      }
    ) {
      startDelay = animatorDelay
      duration = animatorDuration
    }

  private fun CameraOptions.toCameraState(): CameraState {
    return CameraState(
      center ?: cameraOptions.center!!,
      padding ?: cameraOptions.padding!!,
      zoom ?: cameraOptions.zoom!!,
      bearing ?: cameraOptions.bearing!!,
      pitch ?: cameraOptions.pitch!!
    )
  }

  companion object {
    private const val DURATION = 3000L
    const val VALUE = 10.0
    val cameraOptions: CameraOptions = CameraOptions.Builder()
      .anchor(ScreenCoordinate(VALUE, VALUE))
      .bearing(VALUE)
      .center(Point.fromLngLat(VALUE, VALUE))
      .pitch(VALUE)
      .padding(EdgeInsets(VALUE, VALUE, VALUE, VALUE))
      .zoom(VALUE)
      .build()

    const val EPS = 0.000001
  }
}