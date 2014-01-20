package com.ameron32.chatreborn5.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.Network;
import com.ameron32.chatreborn5.chat.Utils;
import com.ameron32.chatreborn5.chat.MessageTemplates.*;
import com.ameron32.chatreborn5.chat.ChatListener;
import com.ameron32.chatreborn5.dummy.UserContent.User;
import com.ameron32.chatreborn5.services.ChatService;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class ChatServer extends ChatService {
  
  public static final String TAG = "ChatServer";
  
  public String getTag() {
    return TAG;
  }
  
  public static class ChatConnection extends Connection {
    
    public String name;
  }
  
  private Server             server;
  
  private final ChatListener serverListener = new ChatListener() {
                                              
                                              @Override
                                              protected void received(final SystemMessage systemMessage,
                                                  final ChatConnection chatConnection) {
                                                String message = null;
                                                // SystemMessage sourceSMessage
                                                // = systemMessage;
                                                message = systemMessage.getText();
                                                if (message == null) return;
                                                message = message.trim();
                                                if (message.length() == 0) return;
                                                
                                                systemMessage.setServerRelayed();
                                                
                                                if (systemMessage.hasAnyOfTags(MessageTag.ClientHistoryRequest)) {
                                                  sendHistory(chatConnection.getID());
                                                  
                                                  final SystemMessage historyRequestPlaceholder = new SystemMessage();
                                                  final String name = systemMessage.name;
                                                  historyRequestPlaceholder.name = name;
                                                  historyRequestPlaceholder.setText(name
                                                      + ": requested server chat history");
                                                  historyRequestPlaceholder.attachTags(MessageTag.ServerChatter);
                                                  historyRequestPlaceholder.setServerRelayed();
                                                  Global.Server.addToHistory(historyRequestPlaceholder);
                                                  // do not store the original
                                                  // ChatHistory requests. OMG
                                                  // break everything!
                                                  return;
                                                }
                                                
                                                // SystemMessage sMessage = new
                                                // SystemMessage();
                                                // sMessage.name =
                                                // systemMessage.name;
                                                // sMessage.setText(message);
                                                systemMessage.setServerRelayed();
                                                Global.Server.addToHistory(systemMessage);
                                                getServer().sendToAllTCP(systemMessage);
                                              }
                                              
                                              @Override
                                              protected void received(final ChatMessage chatMessage,
                                                  final ChatConnection chatConnection) {
                                                String message = null;
                                                message = chatMessage.getText();
                                                if (message == null) return;
                                                message = message.trim();
                                                if (message.length() == 0) return;
                                                
                                                // final ChatMessage mMessage =
                                                // new ChatMessage();
                                                // mMessage.name =
                                                // chatMessage.name;
                                                // mMessage.setText(message);
                                                chatMessage.setServerRelayed();
                                                Global.Server.addToHistory(chatMessage);
                                                getServer().sendToAllTCP(chatMessage);
                                              }
                                              
                                              @Override
                                              protected void received(final RegisterName registerName,
                                                  final ChatConnection chatConnection) {
                                                // confirm connection is clear
                                                if (chatConnection.name != null) return;
                                                
                                                String name = null;
                                                name = registerName.name;
                                                if (name == null) return;
                                                name = name.trim();
                                                if (name.length() == 0) return;
                                                chatConnection.name = name;
                                                
                                                // create a new server
                                                // notification
                                                final SystemMessage systemMessage = new SystemMessage();
                                                systemMessage.name = serverName;
                                                systemMessage.setServerRelayed();
                                                systemMessage.setText(name + " connected.");
                                                systemMessage.attachTags(MessageTag.ServerUpdate);
                                                Global.Server.addToHistory(systemMessage);
                                                getServer().sendToAllTCP(systemMessage);
                                                
                                                updateNames();
                                              }
                                              
                                              @Override
                                              protected void disconnected(final ChatConnection chatConnection) {
                                                if (chatConnection.name != null) {
                                                  // Announce to everyone that
                                                  // someone
                                                  // (with a registered name)
                                                  // has left.
                                                  final SystemMessage systemMessage = new SystemMessage();
                                                  systemMessage.name = "Server:[" + Utils.getIPAddress(true) + ":"
                                                      + Network.port + "]";
                                                  systemMessage.setText(chatConnection.name + " disconnected.");
                                                  systemMessage.attachTags(MessageTag.ServerUpdate);
                                                  systemMessage.setServerRelayed();
                                                  Global.Server.addToHistory(systemMessage);
                                                  getServer().sendToAllTCP(systemMessage);
                                                  
                                                  updateNames();
                                                }
                                              }
                                            };
  
  private void updateNames() {
    // Collect the names for each connection.
    final Connection[] connections = getServer().getConnections();
    
    final UpdateNames updateNames = new UpdateNames();
    
    for (int i = connections.length - 1; i >= 0; i--) {
      final ChatConnection connection = (ChatConnection) connections[i];
      updateNames.addUser(new User(connection.name, "id: " + connection.getID(), R.drawable.ic_contact_picture_2));
    }
    
    getServer().sendToAllTCP(updateNames);
  }
  
  private static final String serverName = "Server:[" + Utils.getIPAddress(true) + ":" + Network.port + "]";
  
  private void sendHistory(int connectionId) {
    TreeMap<Long, MessageBase> sch = Global.Server.getServerChatHistory();
    ServerChatHistory schB = new ServerChatHistory();
    schB.name = serverName;
    schB.loadHistory(sch);
    getServer().sendToTCP(connectionId, schB);
  }
  
  // --------------------------------------
  // SERVICE Calls
  // --------------------------------------
  
  // private boolean isPrepared = false;
  // public boolean getIsPrepared() {
  // return isPrepared;
  // }
  // private boolean isRunning = false;
  // public boolean getIsRunning() {
  // return isRunning;
  // }
  // public void setIsRunning(boolean state) {
  // isRunning = state;
  // }
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
    // if (intent != null) startServer();
    return super.onStartCommand(intent, flags, startId);
  }
  
  public void startServer() {
    if (getState() != ChatConnectionState.ONLINE) {
      if (getState() == ChatConnectionState.OFFLINE) {
      changeState(ChatConnectionState.PREPARING);
      
      register();
      
      setServer(new Server() {
        
        protected Connection newConnection() {
          return new ChatConnection();
        }
      });
      
      Network.register(server);
      
      getServer().addListener(serverListener);
      
      changeState(ChatConnectionState.PREPARED);
      }
      
      if (getState() == ChatConnectionState.PREPARED) {
        // restore chat log somehow
        
        new SimpleAsyncTask() {
          
          boolean successful = false;
          
          @Override
          protected void doInBackground() {
            try {
              Log.error("SERVER STARTING");
              server.bind(Network.port);
              server.start();
              successful = true;
            }
            catch (IOException e) {
              e.printStackTrace();
            }
          }
          
          @Override
          protected void onPostExecute() {
            if (successful) changeState(ChatConnectionState.ONLINE);
          }
        }.execute();
      }
    }
  }
  
  @Override
  public void onDestroy() {
    stopServerIn(3000);
    super.onDestroy();
    clearNotification(getSTOP_NOTIFICATION_ID());
  }
  
  public void stopServerIn(int millis) {
    AsyncTask<Integer, Integer, String> stopServer = new AsyncTask<Integer, Integer, String>() {
      
      @Override
      protected String doInBackground(Integer... params) {
        if (getState() != ChatConnectionState.OFFLINE) {
          int totalTimeInMillis = params[0];
          int updates = (totalTimeInMillis < 10000) ? 3 : 10;
          int timePerUpdate = totalTimeInMillis / updates;
          changeState(ChatConnectionState.DISCONNECTING);
          for (int u = 0; u < updates; u++) {
            sendChatMessage("Server shutting down in " + ((totalTimeInMillis - (timePerUpdate * u)) / 1000)
                + " seconds!");
            try {
              Thread.sleep(timePerUpdate);
            }
            catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          
          // store server log somehow
          
          getServer().stop();
          getServer().close();
          
          changeState(ChatConnectionState.OFFLINE);
        }
        setServer(null);
        return null;
      }
      
      @Override
      protected void onPostExecute(String result) {
        super.onPostExecute(result);
      }
    };
    stopServer.execute(millis);
    
  }
  
  // --------------------------------------
  // BINDER
  // --------------------------------------
  
  @Override
  public IBinder onBind(Intent intent) {
    super.onBind(intent);
    return mBinder;
  }
  
  private IBinder mBinder = new MyServerBinder();
  
  @Override
  public void onCreate() {
    super.onCreate();
    setSTART_NOTIFICATION_ID("ServerStart");
    setSTOP_NOTIFICATION_ID("ServerStop");
    
    setMyBinder(new MyServerBinder());
  }
  
  public class MyServerBinder extends MyBinder {
    
    public ChatServer getService() {
      return ChatServer.this;
    }
  }
  
  // --------------------------------------
  // GETTER/SETTER
  // --------------------------------------
  
  public Server getServer() {
    return server;
  }
  
  private void setServer(Server server) {
    this.server = server;
  }
  
  // --------------------------------------
  // HELPER METHODS
  // --------------------------------------
  
  private void sendChatMessage(String msg) {
    final ChatMessage chatMessage = new ChatMessage();
    chatMessage.name = serverName;
    chatMessage.setText(msg);
    chatMessage.setServerRelayed();
    getServer().sendToAllTCP(chatMessage);
  }
}
