package ca.impulsedev.feedme.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class ViewUtils {
    public static void show(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void show(View[] views, View visibleView) {
        for (View view : views) {
            show(view, false);
        }
        show(visibleView, true);
    }

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

    public static void animateShow(View view, boolean visible) {
        animateShow(view, visible, view.getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime));
    }
}