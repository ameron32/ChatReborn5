package com.ameron32.chatreborn5.interfaces;

public interface ChatConnectionWatcher {
  public void onChatConnectionStateChanged(int prevState, int nextState);
}
