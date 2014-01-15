package com.ameron32.chatreborn5.views;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TouchlessSlidingPaneLayout extends SlidingPaneLayout {

  public TouchlessSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    // TODO Auto-generated constructor stub
  }
  
  public TouchlessSlidingPaneLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    // TODO Auto-generated constructor stub
  }
  
  public TouchlessSlidingPaneLayout(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {

    return false;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent arg0) {

    return false;
  }
  
  
}
