package com.ameron32.chatreborn5.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.chat.ChatAdapter;
import com.ameron32.chatreborn5.chat.ChatListener;
import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.organization.ServicesOrganizer;
import com.fortysevendeg.swipelistview.SwipeListView;

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
  
  SwipeListView slvChatHistory;
  ChatAdapter chatAdapter;
  private void initViews() {
    slvChatHistory = (SwipeListView) mRootView.findViewById(R.id.slvChatHistory);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    init();
  }

  private void init() {
    chatAdapter = new ChatAdapter(getActivity(), Global.Local.clientChatHistory.getFilteredHistory());
    slvChatHistory.setAdapter(chatAdapter);
    ServicesOrganizer.chatClient.addChatClientListener(listener);
  }
  
  private ChatListener listener = new ChatListener() {

    @Override
    protected void onReceivedComplete(boolean wasChatObjectReceived) {
      super.onReceivedComplete(wasChatObjectReceived);
      if (wasChatObjectReceived) mRootView.post(new Runnable() { public void run() {
        chatAdapter.notifyDataSetChanged();
      }});
    }
    
  };

}
