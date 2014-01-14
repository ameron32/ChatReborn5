package com.ameron32.chatreborn5.interfaces;

import com.ameron32.chatreborn5.services.ChatService;
import com.ameron32.chatreborn5.services.ChatService.ChatConnectionState;

public interface ChatConnectionWatcher {
  
  /**
   * Warning! In most cases, this method will originate outside the main thread.
   * Posting to the main thread may need to occur within the method body.
   * 
   * @param chatService
   * @param prevState
   * @param nextState
   */
  public void onChatConnectionStateChanged(ChatService chatService, ChatConnectionState prevState, ChatConnectionState nextState);
}
