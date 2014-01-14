package com.ameron32.chatreborn5.organization;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ameron32.chatreborn5.services.ChatClient;
import com.ameron32.chatreborn5.services.ChatServer;
import com.ameron32.chatreborn5.services.ChatService.MyBinder;

public class ServicesOrganizer {
  
  public static ChatClient         chatClient;
  public static ChatServer         chatServer;
  
  public static MyServerConnection mServerConnection = new MyServerConnection();
  public static MyClientConnection mClientConnection = new MyClientConnection();
  
  public static class MyServerConnection implements ServiceConnection {
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      MyBinder mBinder = (MyBinder) service;
      chatServer = (ChatServer) mBinder.getService();
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
      
    }
  }
  
  public static class MyClientConnection implements ServiceConnection {
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      MyBinder mBinder = (MyBinder) service;
      chatClient = (ChatClient) mBinder.getService();
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
      
    }
  }
}
