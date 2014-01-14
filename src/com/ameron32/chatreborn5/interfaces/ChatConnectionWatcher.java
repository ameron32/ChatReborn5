package com.ameron32.chatreborn5.interfaces;

import com.ameron32.chatreborn5.services.ChatService;
import com.ameron32.chatreborn5.services.ChatService.ChatConnectionState;

public interface ChatConnectionWatcher {
  public void onChatConnectionStateChanged(ChatService chatService, ChatConnectionState prevState, ChatConnectionState nextState);
}
