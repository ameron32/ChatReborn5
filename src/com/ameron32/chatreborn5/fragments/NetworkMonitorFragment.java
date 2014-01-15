package com.ameron32.chatreborn5.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.interfaces.ChatConnectionWatcher;
import com.ameron32.chatreborn5.organization.ServicesOrganizer;
import com.ameron32.chatreborn5.services.ChatClient;
import com.ameron32.chatreborn5.services.ChatServer;
import com.ameron32.chatreborn5.services.ChatService;
import com.ameron32.chatreborn5.services.ChatService.ChatConnectionState;


public class NetworkMonitorFragment extends CoreFragment implements ChatConnectionWatcher {
  
  public NetworkMonitorFragment() {
    super();
    ChatService.addWatcher(this);
  }
  
  public static NetworkMonitorFragment newInstance(int fragmentId) {
    NetworkMonitorFragment fragment = new NetworkMonitorFragment();
    Bundle args = new Bundle();
    args.putInt(id, fragmentId);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d("NetworkMonitorFragment", "onCreateView");
    // Inflate the layout for this fragment
    mRootView = inflater.inflate(R.layout.network_monitor, container, false);

    initViews();
    return mRootView;
  }
  
  ToggleButton tbClient;
  ToggleButton tbServer;
  TextView tvCStatus; String sCStatus;
  TextView tvSStatus; String sSStatus;
  ListView lvUsers;
  
  private void initViews() {
    tbClient = (ToggleButton) mRootView.findViewById(R.id.tbClient);
    tvCStatus = (TextView) mRootView.findViewById(R.id.tvCConnectionStatus);
    lvUsers = (ListView) mRootView.findViewById(R.id.lvUsers);
    
    tbServer = (ToggleButton) mRootView.findViewById(R.id.tbServer);
    tvSStatus = (TextView) mRootView.findViewById(R.id.tvSConnectionStatus);
  }

  Intent iClient, iServer;
  @Override
  public void onResume() {
    super.onResume();
    
    iClient = new Intent(getActivity(), ChatClient.class);
    iServer = new Intent(getActivity(), ChatServer.class);
    
    tbClient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          ServicesOrganizer.chatClient.connect();
        }
        else {
          ServicesOrganizer.chatClient.disconnect();
        }
      }
    });
    
    tbServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          ServicesOrganizer.chatServer.startServer();
        }
        else {
          ServicesOrganizer.chatServer.stopServerIn(10000);
        }
      }
    });
    
    populateViews();
  }
  
  private void populateViews() {
    tvCStatus.setText(ServicesOrganizer.chatClient.getState().name());
    tvSStatus.setText(ServicesOrganizer.chatServer.getState().name());
  }

  
  
  @Override
  public void onPause() {
    super.onPause();

  }
  
  

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Log.d("NetworkMonitorFragment", "onSaveInstanceState");
  }

  @Override
  public void onChatConnectionStateChanged(
      final ChatService chatService, 
      final ChatConnectionState prevState,
      final ChatConnectionState nextState) {
    Log.d("NetworkMonitorFragment", "onChatConnectionStateChanged:" + chatService.getClass().getSimpleName() + ": from " + prevState.name() + " to " + nextState.name());
    if (chatService.getTag().equals("ChatClient")) {
      tvCStatus.post(new Runnable() { public void run() { 
        sCStatus = nextState.name();
        tvCStatus.setText(sCStatus); } });
    }
    if (chatService.getTag().equals("ChatServer")) {
      tvSStatus.post(new Runnable() { public void run() {
        sSStatus = nextState.name();
        tvSStatus.setText(sSStatus); } });
    }
  }
  
  
}
