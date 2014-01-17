package com.ameron32.chatreborn5.chat;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;

import com.ameron32.chatreborn5.chat.MessageTemplates.MessageBase;

public class Global {
	/**
	 * WARNING, THIS CLASS IS NOT THREAD-SAFE. THAT'S PROBABLY WHY ON RARE OCCASSIONS
	 * THE APPLICATION WILL CRASH WHEN THE TREEMAPS ARE ALTERED FROM MULTIPLE THREADS.
	 */

	/**
	 * 
	 */
	public static void set(Context context) {
//		Local.username = "user" + (new java.util.Random().nextInt(90) + 10);
//		Local.hostname = "localhost";
	  Global.activity = (Activity) context;
	}
	private static Activity activity;
	
	public static class Server {
		private static final TreeMap<Long, MessageBase> serverChatHistory 
			= new TreeMap<Long, MessageBase>();
		public static void addToHistory(MessageBase mc) {
			serverChatHistory.put(mc.getTimeStamp(), mc);
		}
		public static TreeMap<Long, MessageBase> getServerChatHistory() {
			return new TreeMap<Long, MessageBase>(serverChatHistory);
		}
		public static final ArrayList<String> connectedUsers 
			= new ArrayList<String>();
		public static void removeUser(String s) {
			for (String user : connectedUsers) {
				if (user.equalsIgnoreCase(s)) connectedUsers.remove(user);
			}
		}
	}

	public static class Local {
		public static String username = "User"
				+ (new java.util.Random().nextInt(90) + 10);
		public static String hostname = "localhost";

//		public static String[] groupUsers = { "" };
		
//		private static final TreeMap<Long, MessageClass> clientChatHistory = new TreeMap<Long, MessageClass>();

		public static Map<Long, MessageBase> getFilteredClientChatHistory() {
			return clientChatHistory.getFilteredHistory();
		}

//		private static final TreeMap<Long, MessageClass> clientChatHistoryFiltered = new TreeMap<Long, MessageClass>();

		public static ChatHistory clientChatHistory = new ChatHistory();
		
		public static void addToHistory(final TreeMap<Long, MessageBase> additions) {
		  activity.runOnUiThread(new Runnable() { public void run() {
		    clientChatHistory.addToHistory(additions);
		  }});
		}

		public static void addToHistory(final MessageBase mc) {
		  activity.runOnUiThread(new Runnable() { public void run() {
		    clientChatHistory.addToHistory(mc);
		  }});
		}

		public static void unpackServerHistory(final TreeMap<Long, MessageBase> historyBundle) {
		  activity.runOnUiThread(new Runnable() { public void run() {
		    clientChatHistory.unpackServerHistory(historyBundle);
		  }});
		}

		public static void clearChatHistory() {
			activity.runOnUiThread(new Runnable() { public void run() {
			  clientChatHistory.clearChatHistory();
			}});
		}
	}
	
}
