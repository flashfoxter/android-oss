package com.kickstarter.libs;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.kickstarter.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func0;

public final class SwipeRefresher {
  /**
   *
   * @param activity Activity to bind lifecycle events for.
   * @param layout Layout to subscribe to for refresh events, send signals when no longer refreshing.
   * @param refreshAction Action to call when a refresh event is emitted, likely a viewModel input.
   * @param isRefreshing Observable that emits events when the refreshing status changes.
   */
  public SwipeRefresher(@NonNull final BaseActivity<? extends ViewModel> activity,
    @NonNull final SwipeRefreshLayout layout,
    @NonNull final Action0 refreshAction,
    @NonNull final Func0<Observable<Boolean>> isRefreshing) {

    // Iterate through colors in loading spinner while waiting for refresh
    layout.setColorSchemeResources(R.color.green, R.color.green_darken_10, R.color.green_darken_20, R.color.green_darken_10);

    // Emits when user has signaled to refresh layout
    RxSwipeRefreshLayout.refreshes(layout)
      .compose(activity.bindToLifecycle())
      .subscribe(__ -> refreshAction.call());

    // Emits when the refreshing status changes. Hides loading spinner when feed is no longer refreshing.
    isRefreshing.call()
      .filter(refreshing -> !refreshing)
      .compose(activity.bindToLifecycle())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(layout::setRefreshing);
  }
}