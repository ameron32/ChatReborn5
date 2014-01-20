package com.ameron32.chatreborn5.adapters;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ameron32.chatreborn5.R;
import com.ameron32.chatreborn5.chat.Global;
import com.ameron32.chatreborn5.chat.MessageTemplates.ChatMessage;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageBase;
import com.ameron32.chatreborn5.chat.MessageTemplates.SystemMessage;

public class UnreadMessageAdapter extends BaseAdapter {
  
  // TODO replace with a collection of real data
  private final Map<Long, MessageBase> DATA;
  
  private LayoutInflater                      mInflater;
  private Context                             context;
  
  public UnreadMessageAdapter(Context context) {
    // Cache the LayoutInflate to avoid asking for a new one each time.
    mInflater = LayoutInflater.from(context);
    this.context = context;
    DATA = Global.Local.clientChatHistory.getFilteredChatHistory("unread").getFilteredHistory();
  }
  
  /**
   * @see android.widget.ListAdapter#getCount()
   */
  public int getCount() {
    return DATA.size();
  }
  
  /**
   * @see android.widget.ListAdapter#getItem(int)
   */
  public MessageBase getItem(int position) {
    return DATA.get(getKeyAt(position));
  }
  
  private long getKeyAt(int position) {
    int counter = 0;
    for (Map.Entry<Long, MessageBase> entry : DATA.entrySet()) {
      if (position == counter) return entry.getKey();
      counter++;
    }
    return -1l;
  }
  
  /**
   * Use the array index as a unique id.
   * 
   * @see android.widget.ListAdapter#getItemId(int)
   */
  public long getItemId(int position) {
    return getItem(position).getTimeStamp();
  }
  
  /**
   * Make a view to hold each row.
   * 
   * @see android.widget.ListAdapter#getView(int, android.view.View,
   *      android.view.ViewGroup)
   */
  public View getView(final int position, View convertView, final ViewGroup parent) {
    // A ViewHolder keeps references to children views to avoid unneccessary
    // calls to findViewById() on each row.
    ViewHolder holder;
  
    item = getItem(position);
    
    // When convertView is not null, we can reuse it directly, there is no need
    // to reinflate it. We only inflate a new View when the convertView supplied
    // by ListView is null.
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.list_item_unreadmessage, parent, false);
      
      // Creates a ViewHolder and store references to the two children views
      // we want to bind data to.
      holder = new ViewHolder();
      // TODO store references to your views
      holder.title = (TextView) convertView.findViewById(R.id.name);
      holder.subtitle = (TextView) convertView.findViewById(R.id.notes);
      holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
      
      convertView.setTag(holder);
    }
    else {
      // Get the ViewHolder back to get fast access to the TextView
      // and the ImageView.
      holder = (ViewHolder) convertView.getTag();
    }
    
    // TODO Bind your data efficiently with the holder.
    holder.title.setText("");
      String subtitle = item.getText();
      if (subtitle.length() > 15) {
        subtitle = subtitle.substring(0, 14) + "...";
      }
    holder.subtitle.setText("");
   
    if (item instanceof ChatMessage) {
      holder.thumbnail.setImageResource(R.drawable.ic_contact_picture_2);  
    }
    if (item instanceof SystemMessage) {
      holder.thumbnail.setImageResource(R.drawable.ic_action_cancel);
    }
    
    item = null;
    return convertView;
  }
  MessageBase item;
  
  static class ViewHolder {
    
    // TODO define members for each view in the item layout
    TextView  title;
    TextView  subtitle;
    ImageView thumbnail;
  }
  
}
