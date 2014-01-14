package com.ameron32.chatreborn5.notifications;

import android.app.Activity;
import android.content.Context;

import com.michaelflisar.messagebar.MessageBar;
import com.michaelflisar.messagebar.messages.TextMessage;

public class NewMessageBar {
  
  public static void showMessage(Context context, String message) {
    try {
      final MessageBar mBar = new MessageBar((Activity) context, true);
      mBar.show(new TextMessage(message, "OK", null));
    }
    catch (ClassCastException e) {
      e.printStackTrace();
      throw new ClassCastException("Context given to NewMessageBar was not an activity.");
    }
  }
}
