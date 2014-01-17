package com.ameron32.chatreborn5.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.chat.MessageTemplates.ChatMessage;


public class MessageFlipper extends ViewFlipper {

  private Context context;
  public MessageFlipper(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    init();
  }

  public MessageFlipper(Context context) {
    super(context);
    this.context = context;
    init();
  }

  private void init() {
    setFlipInterval(3000);
    
    setInAnimation(context, R.anim.push_up_in);
    setOutAnimation(context, R.anim.push_up_out);
  }
  
  private TextView textHolder;
  public void addMessage(final ChatMessage chatMessage) {
    textHolder = new TextView(context);
    textHolder.setText(chatMessage.name + " says: " + chatMessage.getText());
    textHolder.setBackgroundColor(Color.parseColor("#AAAAAAAA"));
    textHolder.setGravity(Gravity.CENTER);
    this.addView(textHolder);
    
    if (!isFlipping()) { 
      this.startFlipping();
    }
  }
  
  @Override
  public void showNext() {
    super.showNext();
    Log.d("MessageFlipper", "showNext");
    onShowNext();
  }
  
  private void onShowNext() {
    if (getDisplayedChild() == 0) {
      stopFlipping();
      
      // clear all extra views
      removeViews(1, getChildCount() - 1);
    }
  }
  
}
