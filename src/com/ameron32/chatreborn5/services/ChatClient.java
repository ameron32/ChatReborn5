package com.ameron32.chatreborn5.services;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.IBinder;

import com.ameron32.chatreborn5.chat.ChatHistory;
import com.ameron32.chatreborn5.chat.ChatListener;
import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.MessageTemplates.ChatMessage;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageBase;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageTag;
import com.ameron32.chatreborn5.chat.MessageTemplates.RegisterName;
import com.ameron32.chatreborn5.chat.MessageTemplates.ServerChatHistory;
import com.ameron32.chatreborn5.chat.MessageTemplates.SystemMessage;
import com.ameron32.chatreborn5.chat.MessageTemplates.UpdateNames;
import com.ameron32.chatreborn5.chat.Network;
import com.ameron32.chatreborn5.dummy.UserContent;
import com.ameron32.chatreborn5.services.ChatServer.ChatConnection;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

public class ChatClient extends ChatService {
  
  public static final String TAG = "ChatClient";
  
  public String getTag() {
    return TAG;
  }
  
  private Client client;
  
  public Client getClient() {
    return client;
  }
  
  private final ChatListener chatListener = new ChatListener() {
                                            
    @Override
    protected void connected() {
      final RegisterName registerName = new RegisterName();
      registerName.name = Global.Local.username;
      client.sendTCP(registerName);
      
      final SystemMessage request = new SystemMessage();
      request.name = Global.Local.username;
      request.setText("history request");
      request.attachTags(MessageTag.ClientHistoryRequest);
      client.sendTCP(request);
    }
    
    @Override
    protected void received(final ServerChatHistory serverChatHistory,
        final ChatConnection chatConnection) {
      super.received(serverChatHistory, chatConnection);
      Global.Local.unpackServerHistory(serverChatHistory.getHistoryBundle());
    }
    
    @Override
    protected void received(final SystemMessage systemMessage,
        final ChatConnection chatConnection) {
      Global.Local.addToHistory(systemMessage);
      
      // send notification UNLESS message is filtered
      if (!ChatHistory.getPrimaryFilteredChatHistory().wouldMessageBeFiltered(systemMessage))
        notifyMessage(systemMessage.name + "[" + systemMessage.getText() + "]");
      
    }
    
    @Override
    protected void received(final ChatMessage chatMessage,
        final ChatConnection chatConnection) {
      Global.Local.addToHistory(chatMessage);
      notifyMessage(chatMessage.name + " says: " + chatMessage.getText());
    }
    
    @Override
    protected void received(final UpdateNames updateNames,
        final ChatConnection chatConnection) {
      UserContent.addItems(updateNames.getUsers());
      notifyMessage("Users Changed");
    }
    
    @Override
    protected void disconnected(final ChatConnection chatConnection) {
      Global.Local.clearChatHistory();
    }
  };
  
  public void disconnect() {
    if (getState() != ChatConnectionState.OFFLINE) {
      if (getState() == ChatConnectionState.ONLINE) {
        changeState(ChatConnectionState.DISCONNECTING);
        client.stop();
      }
      if (getState() == ChatConnectionState.DISCONNECTING) {
        changeState(ChatConnectionState.DISABLING);
        client.close();
        client = null;
      }
      changeState(ChatConnectionState.OFFLINE);
    }
  }
  
  // --------------------------------------
  // SERVICE Calls
  // --------------------------------------
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
    return super.onStartCommand(intent, flags, startId);
  }
  
  public void connect() {
    if (getState() == ChatConnectionState.OFFLINE) {
      changeState(ChatConnectionState.PREPARING);
      // Global.Local.hostname = host;
      client = new Client();
      
      register();
      
      client.start();
      // Global.set();
      
      Network.register(client);
      
      client.addListener(chatListener);
      if (unaddedListenerQueue.size() > 0) {
        for (ChatListener queuedListener : unaddedListenerQueue) { client.addListener(queuedListener); }
      }
      
      changeState(ChatConnectionState.PREPARED);
      
      new SimpleAsyncTask() {
        
        @Override
        protected void doInBackground() {
          try {
            Log.error("CLIENT CONNECTING TO SERVER");
            client.connect(5000, Global.Local.hostname, Network.port);
            changeState(ChatConnectionState.ONLINE);
          }
          catch (IOException e) {
            e.printStackTrace();
          }
        }

        @Override
        protected void onPostExecute() {}
      }.execute();
    }
  }
  
  @Override
  public void onDestroy() {
    if (getState() == ChatConnectionState.ONLINE) {
      disconnect();
    }
    super.onDestroy();
    clearNotification(getSTOP_NOTIFICATION_ID());
  }
  
  private IBinder mBinder = new MyClientBinder();
  
  @Override
  public IBinder onBind(Intent intent) {
    super.onBind(intent);
    return mBinder;
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    setSTART_NOTIFICATION_ID("ClientStart");
    setSTOP_NOTIFICATION_ID("ClientStop");
    
    setMyBinder(new MyClientBinder());
  }
  
  public class MyClientBinder extends MyBinder {
    public ChatClient getService() {
      return ChatClient.this;
    }
  }
  
  
  
  
  
  private String username = Global.Local.username;
  public void sendMessage(final String msg) {
    final ChatMessage chatMessage = new ChatMessage();
    chatMessage.name = username;
    chatMessage.setText(msg);
    sendMessage(chatMessage);
  }
  
  public void sendSystemMessage(final String msg) {
    final SystemMessage systemMessage = new SystemMessage();
    systemMessage.name = username;
    systemMessage.setText(msg);
    sendMessage(systemMessage);
  }
  
  public void sendMessage(final MessageBase mc) {
    SimpleAsyncTask task = new SimpleAsyncTask() {
      
      @Override
      protected void onPostExecute() {
        // TODO message sent notification
      }
      
      @Override
      protected void doInBackground() {
        client.sendTCP(mc);
      }
    };
    task.execute();
  }
  
  public List<ChatListener> unaddedListenerQueue = new LinkedList<ChatListener>();
  public void addChatClientListener(ChatListener listener) {
    if (client != null) {
      client.addListener(listener);
    }
    else {
      if (!unaddedListenerQueue.contains(listener)) unaddedListenerQueue.add(listener);
    }
  }
  
}
