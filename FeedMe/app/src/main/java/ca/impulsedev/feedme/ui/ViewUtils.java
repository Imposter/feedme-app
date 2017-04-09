package ca.impulsedev.feedme.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Utility class for visibility status of Android Views and animation
 */
public class ViewUtils {
    /**
     * Sets visibility status of a target view
     * @param view Target view
     * @param visible Whether view is to be visible or not
     */
    public static void show(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Hides all views except for the visible view
     * @param views Set of views
     * @param visibleView View to set as visible
     */
    public static void show(View[] views, View visibleView) {
        for (View view : views) {
            show(view, false);
        }
        show(visibleView, true);
    }

    /**
     * Sets visibility status of a target view with animation
     * @param view Target view
     * @param visible Whether the view is to be visible or not
     * @param animTime Duration of animation (Milliseconds)
     */
    public static void animateShow(final View view, final boolean visible, int animTime) {
        show(view, visible);
        view.animate().setDuration(animTime).alpha(
                visible ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Sets visibility status of a target view with animation
     * @param view Target view
     * @param visible Whether the view is to be visible or not
     */
    public static void animateShow(View view, boolean visible) {
        animateShow(view, visible, view.getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime));
    }
}