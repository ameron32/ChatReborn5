package com.ameron32.chatreborn5.notifications;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

import com.michaelflisar.messagebar.MessageBar;
import com.michaelflisar.messagebar.messages.TextMessage;


public class NewMessageBar {

  public static void showMessage(Activity activity, String message) {
    final MessageBar mBar = new MessageBar(activity, true);
    mBar.show(new TextMessage(message, "OK", null));
  }
}
