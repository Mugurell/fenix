/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.tabstray

import android.content.res.Configuration
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

class TraySheetBehaviorCallback(
    private val behavior: BottomSheetBehavior<ConstraintLayout>,
    private val trayInteractor: NavigationInteractor
) : BottomSheetBehavior.BottomSheetCallback() {

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (newState == STATE_HIDDEN) {
            trayInteractor.onTabTrayDismissed()
        } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            // We only support expanded and collapsed states.
            // But why??
            behavior.state = STATE_HIDDEN
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
}

fun BottomSheetBehavior<ConstraintLayout>.setUpTrayBehavior(
    maxNumberOfTabs: Int,
    numberForExpandingTray: Int,
    navigationInteractor: DefaultNavigationInteractor,
    lifecycleScope: LifecycleCoroutineScope,
    currentOrientation: StateFlow<Int>
) {
    addBottomSheetCallback(
        TraySheetBehaviorCallback(this, navigationInteractor)
    )

    lifecycleScope.launchWhenStarted {
        currentOrientation.collect {
            state = if (it == Configuration.ORIENTATION_LANDSCAPE || maxNumberOfTabs >= numberForExpandingTray) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
}
