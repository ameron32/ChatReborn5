package com.ameron32.chatreborn5.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ameron32.chatreborn5.R;

public class ClientFragment extends CoreFragment {
  
  public ClientFragment() {
    super();
  }
  
  public static ClientFragment newInstance(int fragmentId) {
    ClientFragment fragment = new ClientFragment();
    Bundle args = new Bundle();
    args.putInt(id, fragmentId);
    fragment.setArguments(args);
    return fragment;
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d("ClientFragment", "onCreateView");
    // Inflate the layout for this fragment
    mRootView = inflater.inflate(R.layout.chat_client, container, false);

    initViews();
    return mRootView;
  }
  
  private void initViews() {}
  

}
