package com.ameron32.chatreborn5;

import com.ameron32.chatreborn5.fragments.BlankFragment;
import com.ameron32.chatreborn5.organization.FragmentOrganizer;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import android.view.MenuItem;

/**
 * An activity representing the represents a a list of Screens and its details
 * in a Sliding Pane.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ScreenListFragment} and the item details (if present) is a
 * {@link ScreenDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ScreenListFragment.Callbacks} interface to listen for item selections.
 */
public class MainActivity extends FragmentActivity 
  implements ScreenListFragment.Callbacks, BlankFragment.OnFragmentInteractionListener {
  
  private SlidingPaneLayout mSlidingLayout;
  private ActionBar         mActionBar;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // List items should be given the
    // 'activated' state when touched.
    ((ScreenListFragment) getSupportFragmentManager().findFragmentById(R.id.screen_list)).setActivateOnItemClick(true);
    
    mActionBar = getActionBar();
    
    mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
    
    mSlidingLayout.setPanelSlideListener(new SliderListener());
    mSlidingLayout.openPane();
    
    mSlidingLayout.getViewTreeObserver().addOnGlobalLayoutListener(new FirstLayoutListener());
    
    // TODO: If exposing deep links into your app, handle intents here.
  }
  
  /**
   * Callback method from {@link ScreenListFragment.Callbacks} indicating that
   * the item with the given ID was selected.
   */
  @Override
  public void onItemSelected(String id) {
    
    // Show the detail view in this activity by
    // adding or replacing the detail fragment using a
    // fragment transaction.
    
//    ((ScreenDetailFragment) getSupportFragmentManager().findFragmentById(R.id.content_pane)).setText(FragmentOrganizer.ITEM_MAP.get(id).title);
    getSupportFragmentManager().beginTransaction()
      .replace(R.id.content_pane, FragmentOrganizer.ITEM_MAP.get(id).fragment)
      .addToBackStack(null)
      .commit();
    
    mSlidingLayout.closePane();
    
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    /*
     * The action bar up action should open the slider if it is currently
     * closed, as the left pane contains content one level up in the navigation
     * hierarchy.
     */
    if (item.getItemId() == android.R.id.home && !mSlidingLayout.isOpen()) {
      mSlidingLayout.openPane();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  /**
   * This panel slide listener updates the action bar accordingly for each panel
   * state.
   */
  private class SliderListener extends SlidingPaneLayout.SimplePanelSlideListener {
    
    @Override
    public void onPanelOpened(View panel) {
      panelOpened();
    }
    
    @Override
    public void onPanelClosed(View panel) {
      panelClosed();
    }
    
    @Override
    public void onPanelSlide(View view, float v) {}
  }
  
  private void panelClosed() {
    mActionBar.setDisplayHomeAsUpEnabled(true);
    mActionBar.setHomeButtonEnabled(true);
    
//    getSupportFragmentManager().findFragmentById(R.id.content_pane).setHasOptionsMenu(true);
    getSupportFragmentManager().findFragmentById(R.id.screen_list).setHasOptionsMenu(false);
    
  }
  
  private void panelOpened() {
    mActionBar.setHomeButtonEnabled(false);
    mActionBar.setDisplayHomeAsUpEnabled(false);
    
    if (mSlidingLayout.isSlideable()) {
//      getSupportFragmentManager().findFragmentById(R.id.content_pane).setHasOptionsMenu(false);
      getSupportFragmentManager().findFragmentById(R.id.screen_list).setHasOptionsMenu(true);
    }
    else {
//      getSupportFragmentManager().findFragmentById(R.id.content_pane).setHasOptionsMenu(true);
      getSupportFragmentManager().findFragmentById(R.id.screen_list).setHasOptionsMenu(false);
    }
  }
  
  /**
   * This global layout listener is used to fire an event after first layout
   * occurs and then it is removed. This gives us a chance to configure parts of
   * the UI that adapt based on available space after they have had the
   * opportunity to measure and layout.
   */
  @SuppressLint("NewApi")
  private class FirstLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    
    @Override
    public void onGlobalLayout() {
      
      if (mSlidingLayout.isSlideable() && !mSlidingLayout.isOpen()) {
        panelClosed();
      }
      else {
        panelOpened();
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        mSlidingLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
      else {
        mSlidingLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
      }
    }
  }

  @Override
  public void onFragmentInteraction(String thingThatHappened) {
    // TODO Auto-generated method stub
    
  }
}
