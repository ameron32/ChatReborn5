package com.ameron32.chatreborn5;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageTag;
import com.ameron32.chatreborn5.interfaces.OnFragmentInteractionListener;
import com.ameron32.chatreborn5.organization.FragmentOrganizer;
import com.ameron32.chatreborn5.organization.ServicesOrganizer;
import com.ameron32.chatreborn5.services.ChatClient;
import com.ameron32.chatreborn5.services.ChatServer;
import com.ameron32.chatreborn5.services.ChatService;
import com.ameron32.chatreborn5.views.SendBar;
import com.ameron32.chatreborn5.views.TouchlessSlidingPaneLayout;

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
public class MainActivity extends FragmentActivity implements ScreenListFragment.Callbacks,
    OnFragmentInteractionListener {
  
  private TouchlessSlidingPaneLayout mSlidingLayout;
  private ActionBar                  mActionBar;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // List items should be given the
    // 'activated' state when touched.
    ((ScreenListFragment) getSupportFragmentManager().findFragmentById(R.id.screen_list)).setActivateOnItemClick(true);
    
    mActionBar = getActionBar();
    
    mSlidingLayout = (TouchlessSlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
    
    mSlidingLayout.setPanelSlideListener(new SliderListener());
    mSlidingLayout.openPane();
    
    mSlidingLayout.getViewTreeObserver().addOnGlobalLayoutListener(new FirstLayoutListener());
    
    // TODO: If exposing deep links into your app, handle intents here.
    if (iServer == null) {
      iServer = new Intent(this, ChatServer.class);
      bindService(iServer, ServicesOrganizer.mServerConnection, ContextWrapper.BIND_AUTO_CREATE);
      startService(iServer);
    }
    if (iClient == null) {
      iClient = new Intent(this, ChatClient.class);
      bindService(iClient, ServicesOrganizer.mClientConnection, ContextWrapper.BIND_AUTO_CREATE);
      startService(iClient);
    }
    
    SendBar sendBar = ((SendBar) findViewById(R.id.vSendBar));
    if (savedInstanceState == null) {
      ChatService.addWatcher(sendBar);
      
      Global.Local.clientChatHistory.setFilters(MessageTag.ServerChatter, MessageTag.HasStartedTypingMessage, MessageTag.HasStoppedTypingMessage);
      
      Global.set(MainActivity.this);
    }
  }
  
  Intent iServer, iClient;
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    unbindService(ServicesOrganizer.mServerConnection);
    unbindService(ServicesOrganizer.mClientConnection);
    
    if (isFinishing()) {
      stopService(iServer);
      stopService(iClient);
    }
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
    
    // ((ScreenDetailFragment)
    // getSupportFragmentManager().findFragmentById(R.id.content_pane)).setText(FragmentOrganizer.ITEM_MAP.get(id).title);
    getSupportFragmentManager().beginTransaction().replace(R.id.content_pane, FragmentOrganizer.ITEM_MAP.get(id).fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    
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
    if (item.getItemId() == R.id.exit) {
      requestExit();
    }
    return super.onOptionsItemSelected(item);
  }
  
  protected void requestExit() {
    final AlertDialog.Builder d = new AlertDialog.Builder(this);
    d.setMessage("Stay Connected?");

    final DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          stopService(iServer);
          stopService(iClient);

          finish();
          break;
        case DialogInterface.BUTTON_NEUTRAL:
          finish();
          break;
        case DialogInterface.BUTTON_NEGATIVE:
          // do nothing
          break;
        }
        dialog.dismiss();
      }
    };

    d.setPositiveButton("No", l);
    d.setNeutralButton("Yes", l);
    d.setNegativeButton("Cancel", l);
    d.create();
    d.show();
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
    
    // getSupportFragmentManager().findFragmentById(R.id.content_pane).setHasOptionsMenu(true);
    getSupportFragmentManager().findFragmentById(R.id.screen_list).setHasOptionsMenu(false);
  }
  
  private void panelOpened() {
    mActionBar.setHomeButtonEnabled(false);
    mActionBar.setDisplayHomeAsUpEnabled(false);
    
    if (mSlidingLayout.isSlideable()) {
      // getSupportFragmentManager().findFragmentById(R.id.content_pane).setHasOptionsMenu(false);
      getSupportFragmentManager().findFragmentById(R.id.screen_list).setHasOptionsMenu(true);
    }
    else {
      // getSupportFragmentManager().findFragmentById(R.id.content_pane).setHasOptionsMenu(true);
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
  
  // private MyServiceConnection mConnection = new MyServiceConnection();
  // private ChatServer chatServer;
  // public class MyServiceConnection implements ServiceConnection {
  //
  // @Override
  // public void onServiceConnected(ComponentName name, IBinder service) {
  // MyBinder mBinder = (MyBinder) service;
  // chatServer = (ChatServer) mBinder.getService();
  // }
  //
  // @Override
  // public void onServiceDisconnected(ComponentName name) {
  //
  // }
  // }
  
}
