package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.Behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jahnplay on 11/10/2016.
 */

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {


    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    // ...

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }

    // ...
}