package com.ameron32.chatreborn5.adapters;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.MessageTemplates;
import com.ameron32.chatreborn5.chat.MessageTemplates.ChatMessage;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageBase;
import com.ameron32.chatreborn5.chat.MessageTemplates.SystemMessage;
import com.ameron32.chatreborn5.notifications.NewMessageBar;

public class ChatAdapter extends BaseAdapter {
  
  private final Context          context;
  private ViewHolder             holder;
  private final LayoutInflater   inflater;
  
  private Map<Long, MessageBase> mData = Global.Local.clientChatHistory.getFilteredChatHistory("standard").getFilteredHistory();
  
  public ChatAdapter(Context context) {
    super();
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.context = context;
  }
  
  public int getCount() {
    return mData.size();
  }
  
  public MessageBase getItem(int position) {
    return mData.get(getKeyAt(position));
  }
  
  public long getItemId(int arg0) {
    return arg0;
  }
  
  private long getKeyAt(int position) {
    int counter = 0;
    for (Map.Entry<Long, MessageBase> entry : mData.entrySet()) {
      if (position == counter) return entry.getKey();
      counter++;
    }
    return -1l;
  }
  
  MessageBase item;
  @Override
  public View getView(final int position, View convertView, final ViewGroup parent) {
    item = getItem(position);
    if (item instanceof SystemMessage) {
      convertView = inflater.inflate(R.layout.chat_sysmsg_ui, parent, false);
      holder = new ViewHolder();
      holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
      holder.tvUsr = (TextView) convertView.findViewById(R.id.tvUsr);
      holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
      
      convertView.setTag(holder);
    }
    if (item instanceof ChatMessage) {
      // check for continued message
      boolean useDefaultInflater = true;
      if (position != 0) {
        final MessageBase previousItem = getItem(position - 1);
        if (previousItem instanceof ChatMessage && previousItem.name.equals(item.name)) {
          
          convertView = inflater.inflate(R.layout.chat_bubble_continue, parent, false);
          holder = new ViewHolder();
          holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
          // holder.tvUsr = (TextView)
          // convertView.findViewById(R.id.tvUsr);
          holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
          
          convertView.setTag(holder);
          useDefaultInflater = false;
        }
      }
      
      if (useDefaultInflater) {
        convertView = inflater.inflate(R.layout.chat_bubble_ui, parent, false);
        holder = new ViewHolder();
        holder.tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
        holder.tvUsr = (TextView) convertView.findViewById(R.id.tvUsr);
        holder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
        
        convertView.setTag(holder);
      }
      
      int count = item.getTagCount();
      if (count > 0) {
        holder.rlTag = (RelativeLayout) convertView.findViewById(R.id.rlTagCount);
        holder.rlTag.setVisibility(RelativeLayout.VISIBLE);
        holder.tvTagCount = (TextView) convertView.findViewById(R.id.tvTagCount);
        holder.tvTagCount.setText(String.valueOf(count));
        
      }
    }
    
    // both share slide rear
    holder.bComment = (ImageButton) convertView.findViewById(R.id.bAddComment);
    holder.bTag = (ImageButton) convertView.findViewById(R.id.bAddTags);
    holder.bEdit = (ImageButton) convertView.findViewById(R.id.bEditMessage);
    holder.bHide = (ImageButton) convertView.findViewById(R.id.bHideMessage);
    holder.bDelete = (ImageButton) convertView.findViewById(R.id.bDeleteMessage);
    
    // tmp OnClickListener
    final View.OnClickListener tmp = new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        NewMessageBar.showMessage(context, "Not yet implemented.");
      }
    };
    
    // cheating to allow SystemMessage to avoid these buttons
    if (item instanceof ChatMessage) {
      holder.bComment.setOnClickListener(tmp);
      holder.bTag.setOnClickListener(tmp);
      holder.bEdit.setOnClickListener(tmp);
      holder.bHide.setOnClickListener(tmp);
      holder.bDelete.setOnClickListener(tmp);
    }
    
    Long timeStamp = item.getTimeStamp();
    holder.tvTime.setText(new SimpleDateFormat("h:mma", Locale.US).format(timeStamp));
    if (holder.tvUsr != null) holder.tvUsr.setText(item.name);
    
    holder.tvMsg.setText(item.getText());
    // holder.tvMsg.setTypeface(Loader.fonts.get(Fonts.neutonregular));
    
    item = null;
    return convertView;
  }
  
  public static class ViewHolder {
    
    TextView tvTime, tvUsr, tvMsg;
    ImageButton bComment, bTag, bEdit, bHide, bDelete;
    
    RelativeLayout rlComment, rlTag, rlEdit;
    TextView       tvCommentCount, tvTagCount, tvEditCount;
    
    public void clear() {
      rlComment = null;
      rlTag = null;
      rlEdit = null;
      tvCommentCount = null;
      tvTagCount = null;
      tvEditCount = null;
    }
  }
  
  public void clear() {
    mData.clear();
  }
  
  public void addAll(TreeMap<Long, MessageBase> history) {
    mData.putAll(history);
  }
  
  public void remove(int position) {
    mData.remove(getKeyAt(position));
  }
  
}
