package com.ameron32.chatreborn5.services;

import java.io.IOException;

import android.content.Intent;
import android.os.IBinder;

import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.Network;
import com.ameron32.chatreborn5.chat.MessageTemplates.*;
import com.ameron32.chatreborn5.chat.ChatListener;
import com.ameron32.chatreborn5.services.ChatService;
import com.ameron32.chatreborn5.services.ChatServer.ChatConnection;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

public class ChatClient extends ChatService {
	public static final String TAG = "ChatClient";
	public String getTag() { return TAG; }
  
  private Client client;
	public Client getClient() {
		return client;
	}
	
	private void init() {
	  register();
	  
		client.start();
//		Global.set();
		
		Network.register(client);

		client.addListener(chatListener);
		
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
      protected void onPostExecute() {
        // all UIs should be notified that now connected
      }
    }.execute();
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
//			request.setIsHistoryRequest(true);
			request.attachTags(MessageTag.ClientHistoryRequest);
			client.sendTCP(request);
		}
		
		@Override
		protected void received(final ServerChatHistory serverChatHistory, final ChatConnection chatConnection) {
			super.received(serverChatHistory, chatConnection);
		  Global.Local.unpackServerHistory(serverChatHistory.getHistoryBundle());
		}
		
		@Override
		protected void received(final SystemMessage systemMessage, final ChatConnection chatConnection) {
			Global.Local.addToHistory(systemMessage);

				notifyMessage(systemMessage.name + "[" + systemMessage.getText() + "]");

		}
		
		@Override
		protected void received(final ChatMessage chatMessage, final ChatConnection chatConnection) {
			Global.Local.addToHistory(chatMessage);
				notifyMessage(chatMessage.name + " says: " + chatMessage.getText());
		}
		
		@Override
		protected void received(final UpdateNames updateNames, final ChatConnection chatConnection) {
			Global.Local.groupUsers = updateNames.names;
			notifyMessage("Users Changed");
		}
		
		@Override
		protected void disconnected(final ChatConnection chatConnection) {
			Global.Local.clearChatHistory();
		}
	};
	
//	private boolean isConnected = false;
//	public boolean getIsConnected() {
//		return isConnected;
//	}
//	public void setIsConnected(boolean state) {
//		isConnected = state;
//	}
//	
//	private boolean isPrepared = false;
//	public boolean getIsPrepared() {
//		return isPrepared;
//	}
//	public void setIsPrepared(boolean state) {
//		isPrepared = state;
//	}
	private void connect(String host) {
		if (getState() == ChatConnectionState.OFFLINE) {
			changeState(ChatConnectionState.PREPARING);
		  Global.Local.hostname = host;
			client = new Client();
			init();
		}
	}
	
	private void disconnect() {
		if (getState() == ChatConnectionState.ONLINE) {
		  changeState(ChatConnectionState.DISCONNECTING);
		  client.stop();
		  changeState(ChatConnectionState.DISABLING);
			client.close();
			client = null;
			changeState(ChatConnectionState.OFFLINE);
		}
	}
	
	// --------------------------------------
	// SERVICE Calls
	// --------------------------------------
	
//	private boolean isStarted = false;
//	public boolean getIsStarted() {
//		return isStarted;
//	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		com.esotericsoftware.minlog.Log
			.set(com.esotericsoftware.minlog.Log.LEVEL_DEBUG);
		if (getState() == ChatConnectionState.OFFLINE) {
			if (intent != null)	connect(intent.getStringExtra("host"));
		}
		return super.onStartCommand(intent, flags, startId);
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

}
