/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.tabstray

import android.content.res.Configuration
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_SETTLING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import io.mockk.Called
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import mozilla.components.support.test.robolectric.createAddedTestFragment
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mozilla.fenix.helpers.FenixRobolectricTestRunner

@RunWith(FenixRobolectricTestRunner::class)
class TraySheetBehaviorCallbackTest {

    @Test
    fun `WHEN state is hidden THEN invoke interactor`() {
        val interactor = mockk<NavigationInteractor>(relaxed = true)
        val callback = TraySheetBehaviorCallback(mockk(), interactor)

        callback.onStateChanged(mockk(), STATE_HIDDEN)

        verify { interactor.onTabTrayDismissed() }
    }

    @Test
    fun `WHEN state is half-expanded THEN close the tray`() {
        val behavior = mockk<BottomSheetBehavior<ConstraintLayout>>(relaxed = true)
        val callback = TraySheetBehaviorCallback(behavior, mockk())

        callback.onStateChanged(mockk(), STATE_HALF_EXPANDED)

        verify { behavior.state = STATE_HIDDEN }
    }

    @Test
    fun `WHEN other states are invoked THEN do nothing`() {
        val behavior = mockk<BottomSheetBehavior<ConstraintLayout>>(relaxed = true)
        val interactor = mockk<NavigationInteractor>(relaxed = true)
        val callback = TraySheetBehaviorCallback(behavior, interactor)

        callback.onStateChanged(mockk(), STATE_COLLAPSED)
        callback.onStateChanged(mockk(), STATE_DRAGGING)
        callback.onStateChanged(mockk(), STATE_SETTLING)
        callback.onStateChanged(mockk(), STATE_EXPANDED)

        verify { behavior wasNot Called }
        verify { interactor wasNot Called }
    }

    @Test
    fun `GIVEN more tabs opened than the expanding limit WHEN orientation is portrait THEN the behavior is set as expanded`() {
        val behavior = spyk(BottomSheetBehavior<ConstraintLayout>())
        val interactor = mockk<DefaultNavigationInteractor>()
        val testFragment = createAddedTestFragment { Fragment() }
        val orientationFlow = MutableStateFlow(Configuration.ORIENTATION_PORTRAIT)

        behavior.setUpTrayBehavior(
            maxNumberOfTabs = 5,
            numberForExpandingTray = 4,
            navigationInteractor = interactor,
            lifecycleScope = testFragment.lifecycleScope,
            orientationFlow
        )

        assert(behavior.state == STATE_EXPANDED)
    }

    @Test
    fun `GIVEN the number of tabs opened is exactly the expanding limit WHEN orientation is portrait THEN the behavior is set as expanded`() {
        val behavior = spyk(BottomSheetBehavior<ConstraintLayout>())
        val interactor = mockk<DefaultNavigationInteractor>()
        val testFragment = createAddedTestFragment { Fragment() }
        val orientationFlow = MutableStateFlow(Configuration.ORIENTATION_PORTRAIT)

        behavior.setUpTrayBehavior(
            maxNumberOfTabs = 5,
            numberForExpandingTray = 5,
            navigationInteractor = interactor,
            lifecycleScope = testFragment.lifecycleScope,
            orientationFlow
        )

        assert(behavior.state == STATE_EXPANDED)
    }

    @Test
    fun `GIVEN fewer tabs opened than the expanding limit WHEN orientation is portrait THEN the behavior is set as collapsed`() {
        val behavior = spyk(BottomSheetBehavior<ConstraintLayout>())
        val interactor = mockk<DefaultNavigationInteractor>()
        val testFragment = createAddedTestFragment { Fragment() }
        val orientationFlow = MutableStateFlow(Configuration.ORIENTATION_PORTRAIT)

        behavior.setUpTrayBehavior(
            maxNumberOfTabs = 4,
            numberForExpandingTray = 5,
            navigationInteractor = interactor,
            lifecycleScope = testFragment.lifecycleScope,
            orientationFlow
        )

        assert(behavior.state == STATE_COLLAPSED)
    }

    @Test
    fun `GIVEN more tabs opened than the expanding limit WHEN orientation is landscape THEN the behavior is set as expanded`() {
        val behavior = spyk(BottomSheetBehavior<ConstraintLayout>())
        val interactor = mockk<DefaultNavigationInteractor>()
        val testFragment = createAddedTestFragment { Fragment() }
        val orientationFlow = MutableStateFlow(Configuration.ORIENTATION_LANDSCAPE)

        behavior.setUpTrayBehavior(
            maxNumberOfTabs = 5,
            numberForExpandingTray = 4,
            navigationInteractor = interactor,
            lifecycleScope = testFragment.lifecycleScope,
            orientationFlow
        )

        assert(behavior.state == STATE_EXPANDED)
    }

    @Test
    fun `GIVEN the number of tabs opened is exactly the expanding limit WHEN orientation is landscape THEN the behavior is set as expanded`() {
        val behavior = spyk(BottomSheetBehavior<ConstraintLayout>())
        val interactor = mockk<DefaultNavigationInteractor>()
        val testFragment = createAddedTestFragment { Fragment() }
        val orientationFlow = MutableStateFlow(Configuration.ORIENTATION_LANDSCAPE)

        behavior.setUpTrayBehavior(
            maxNumberOfTabs = 5,
            numberForExpandingTray = 5,
            navigationInteractor = interactor,
            lifecycleScope = testFragment.lifecycleScope,
            orientationFlow
        )

        assert(behavior.state == STATE_EXPANDED)
    }

    @Test
    fun `GIVEN fewer tabs opened than the expanding limit WHEN orientation is landscape THEN the behavior is set as expanded`() {
        val behavior = spyk(BottomSheetBehavior<ConstraintLayout>())
        val interactor = mockk<DefaultNavigationInteractor>()
        val testFragment = createAddedTestFragment { Fragment() }
        val orientationFlow = MutableStateFlow(Configuration.ORIENTATION_LANDSCAPE)

        behavior.setUpTrayBehavior(
            maxNumberOfTabs = 4,
            numberForExpandingTray = 5,
            navigationInteractor = interactor,
            lifecycleScope = testFragment.lifecycleScope,
            orientationFlow
        )

        assert(behavior.state == STATE_EXPANDED)
    }

    @Test
    fun `WHEN entering in landscape THEN the tabs tray expandedOffset is set to 0`() {
        val behavior = spyk(BottomSheetBehavior<ConstraintLayout>())
        // expandedOffset is only used if isFitToContents == false
        behavior.isFitToContents = false
        val interactor = mockk<DefaultNavigationInteractor>()
        val testFragment = createAddedTestFragment { Fragment() }
        val orientationFlow = MutableStateFlow(Configuration.ORIENTATION_LANDSCAPE)

        behavior.setUpTrayBehavior(
            maxNumberOfTabs = 4,
            numberForExpandingTray = 5,
            navigationInteractor = interactor,
            lifecycleScope = testFragment.lifecycleScope,
            orientationFlow
        )

        assertEquals(0, behavior.expandedOffset)
    }

    @Test
    fun `WHEN entering in portrait THEN the tabs tray expandedOffset is set bigger than 0`() {
        val behavior = spyk(BottomSheetBehavior<ConstraintLayout>())
        // expandedOffset is only used if isFitToContents == false
        behavior.isFitToContents = false
        val interactor = mockk<DefaultNavigationInteractor>()
        val testFragment = createAddedTestFragment { Fragment() }
        val orientationFlow = MutableStateFlow(Configuration.ORIENTATION_PORTRAIT)

        behavior.setUpTrayBehavior(
            maxNumberOfTabs = 4,
            numberForExpandingTray = 5,
            navigationInteractor = interactor,
            lifecycleScope = testFragment.lifecycleScope,
            orientationFlow
        )

        assertEquals(EXPANDED_OFFSET_IN_PORTRAIT_DP, behavior.expandedOffset)
    }
}
