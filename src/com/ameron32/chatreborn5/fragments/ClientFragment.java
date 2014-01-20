package com.ameron32.chatreborn5.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.adapters.ChatAdapter;
import com.ameron32.chatreborn5.chat.ChatListener;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageTag;
import com.ameron32.chatreborn5.chat.MessageTemplates.SystemMessage;
import com.ameron32.chatreborn5.interfaces.ChatConnectionWatcher;
import com.ameron32.chatreborn5.notifications.NewMessageBar;
import com.ameron32.chatreborn5.organization.ServicesOrganizer;
import com.ameron32.chatreborn5.services.ChatServer.ChatConnection;
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
  ChatAdapter   chatAdapter;
  TextView      tvTyping;
  EditText      etSearch;
  ImageButton        bSearch;
  
  private void initViews() {
    slvChatHistory = (SwipeListView) mRootView.findViewById(R.id.slvChatHistory);
    tvTyping = (TextView) mRootView.findViewById(R.id.tvIsTyping);
    etSearch = (EditText) mRootView.findViewById(R.id.etSearch);
    bSearch = (ImageButton) mRootView.findViewById(R.id.bSearch);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    init();
  }
  
  private void init() {
    chatAdapter = new ChatAdapter(getActivity());
    slvChatHistory.setAdapter(chatAdapter);
    ServicesOrganizer.chatClient.addChatClientListener(new ChatListener() {
      
      @Override
      protected void received(final SystemMessage systemMessage, final ChatConnection chatConnection) {
        super.received(systemMessage, chatConnection);
        if (systemMessage.hasAnyOfTags(MessageTag.HasStartedTypingMessage)) {
          tvTyping.post(new Runnable() {
            
            public void run() {
              tvTyping.setText(systemMessage.name + " " + systemMessage.getText());
            }
          });
        }
        if (systemMessage.hasAnyOfTags(MessageTag.HasStoppedTypingMessage)) {
          tvTyping.post(new Runnable() {
            
            public void run() {
              tvTyping.setText("");
            }
          });
        }
      }
      
      @Override
      protected void onReceivedComplete(boolean wasChatObjectReceived) {
        super.onReceivedComplete(wasChatObjectReceived);
        if (wasChatObjectReceived) mRootView.post(new Runnable() {
          
          public void run() {
            chatAdapter.notifyDataSetChanged();
          }
        });
      }
    });
    
    slvChatHistory.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    slvChatHistory.setOnItemClickListener(new ListView.OnItemClickListener() {
      
      @Override
      public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        NewMessageBar.showMessage(getActivity(), position + " clicked. [" + chatAdapter.getItem(position).getText()
            + "]");
      }
    });
    slvChatHistory.setSwipeListViewListener(new BaseSwipeListViewListener() {
      
      @Override
      public void onDismiss(int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
          chatAdapter.remove(position);
        }
        chatAdapter.notifyDataSetChanged();
      }
    });
    
    if (etSearch != null) {
      String query = etSearch.getText().toString().trim();
      chatAdapter.getFilter().filter(query);
    }
    
    if (bSearch != null) {
      bSearch.setOnClickListener(new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
          String query = etSearch.getText().toString().trim();
          if (query == null) query = "";
          chatAdapter.getFilter().filter(query);
        }
      });
    }
    
    etSearch.addTextChangedListener(new TextWatcher() {
      
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String query = etSearch.getText().toString().trim();
        if (query == null) query = "";
        chatAdapter.getFilter().filter(query);
      }
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      @Override
      public void afterTextChanged(Editable s) {}
    });
  }
  
  @Override
  public void onChatConnectionStateChanged(ChatService chatService, ChatConnectionState prevState,
      ChatConnectionState nextState) {
    // Clear the chat ONLY when ChatClient comes ONLINE
    if (chatService.getTag().equals("ChatClient")) {
      if (nextState == ChatConnectionState.ONLINE) {
        slvChatHistory.post(new Runnable() {
          
          public void run() {
            chatAdapter.clear();
            chatAdapter.notifyDataSetChanged();
          }
        });
      }
    }
  }
  
}
