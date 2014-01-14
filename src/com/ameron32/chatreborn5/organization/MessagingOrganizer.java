package com.ameron32.chatreborn5.organization;

import java.util.ArrayList;
import java.util.List;

import com.ameron32.chatreborn5.chat.ChatListener;

public class MessagingOrganizer {
  
  private static final List<ChatListener>         listeners = new ArrayList<ChatListener>();
  private static final List<ChatListenersMonitor> monitors  = new ArrayList<ChatListenersMonitor>();
  
  public static void addListener(ChatListener listener) {
    listeners.add(listener);
    for (ChatListenersMonitor monitor : monitors) {
      monitor.onChatListenerAdded();
    }
  }
  
  public static void clearListeners() {
    listeners.clear();
    for (ChatListenersMonitor monitor : monitors) {
      monitor.onChatListenersCleared();
    }
  }
  
  public static List<ChatListener> getListeners() {
    return listeners;
  }
  
  public static void addMonitor(ChatListenersMonitor monitor) {
    monitors.add(monitor);
  }
  
  public static void clearMonitors() {
    monitors.clear();
  }
  
  public interface ChatListenersMonitor {
    
    public void onChatListenerAdded();
    
    public void onChatListenersCleared();
  }
}
