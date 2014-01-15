package com.ameron32.chatreborn5.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.adapters.ChatAdapter;
import com.ameron32.chatreborn5.chat.ChatListener;
import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageTag;
import com.ameron32.chatreborn5.interfaces.ChatConnectionWatcher;
import com.ameron32.chatreborn5.notifications.NewMessageBar;
import com.ameron32.chatreborn5.organization.ServicesOrganizer;
import com.ameron32.chatreborn5.services.ChatService;
import com.ameron32.chatreborn5.services.ChatService.ChatConnectionState;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

public class ClientFragment extends CoreFragment implements ChatConnectionWatcher {
  
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
    chatAdapter = new ChatAdapter(getActivity(), Global.Local.getFilteredClientChatHistory());
    slvChatHistory.setAdapter(chatAdapter);
    ServicesOrganizer.chatClient.addChatClientListener(new ChatListener() {

      @Override
      protected void onReceivedComplete(boolean wasChatObjectReceived) {
        super.onReceivedComplete(wasChatObjectReceived);
        if (wasChatObjectReceived) mRootView.post(new Runnable() { public void run() {
          chatAdapter.notifyDataSetChanged();
        }});
      }
    });
    
    slvChatHistory.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    slvChatHistory.setOnItemClickListener(new ListView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> arg0, 
          View v, int position, long id) {
        NewMessageBar.showMessage(getActivity(), 
            position + " clicked. [" + chatAdapter.getItem(position).getText() + "]");
      }
    });
    slvChatHistory.setSwipeListViewListener(
     new BaseSwipeListViewListener() {
      @Override
      public void onDismiss(int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
          chatAdapter.remove(position);
        }
        chatAdapter.notifyDataSetChanged();
      }
    });
  }

  @Override
  public void onChatConnectionStateChanged(ChatService chatService, ChatConnectionState prevState,
      ChatConnectionState nextState) {
    // Clear the chat ONLY when ChatClient comes ONLINE
    if (chatService.getTag().equals("ChatClient")) {
      if (nextState == ChatConnectionState.ONLINE) {
        slvChatHistory.post(new Runnable() { public void run() {
          chatAdapter.clear();
          chatAdapter.notifyDataSetChanged();
        }});
      }
    }
  }
  
  

}
