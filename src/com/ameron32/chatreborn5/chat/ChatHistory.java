package com.ameron32.chatreborn5.chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import android.util.Log;

import com.ameron32.chatreborn5.chat.MessageTemplates.MessageBase;

public class ChatHistory {
  private static final Map<String, FilteredChatHistory> filteredHistoryStack = new HashMap<String, FilteredChatHistory>();
  public ChatHistory register(final FilteredChatHistory filteredChatHistory) { 
    filteredHistoryStack.put(filteredChatHistory.name, filteredChatHistory);
    return this;
  }

	private final TreeMap<Long, MessageBase> completeHistoryCore = new TreeMap<Long, MessageBase>();
//	private final TreeMap<Long, MessageBase> filteredHistoryCore = new TreeMap<Long, MessageBase>();
//	private final TreeMap<Long, MessageBase> unreadFilteredHistoryCore = new TreeMap<Long, MessageBase>();
	
	private final Map<Long, MessageBase> completeHistory;
//	private final Map<Long, MessageBase> filteredHistory;
//	private final Map<Long, MessageBase> unreadFilteredHistory;
	
	public ChatHistory() {
		completeHistory = Collections.synchronizedMap(completeHistoryCore);
//		filteredHistory = Collections.synchronizedMap(filteredHistoryCore);
//		unreadFilteredHistory = Collections.synchronizedMap(unreadFilteredHistoryCore);
		// filterTags.add(MessageTag.ServerChatter);
	}
	
	/**
	 * root of all new messages
	 * @param mc
	 */
	public void addToHistory(MessageBase mc) {
		Log.d("ChatHistory", mc.toString());
    completeHistory.put(mc.getTimeStamp(), mc);
    addToFilteredHistory(mc);
	}
	
	public void addToHistory(TreeMap<Long, MessageBase> additions) {
		for (Long key : additions.keySet()) {
			addToHistory(additions.get(key));
		}
	}
	
	public void unpackServerHistory(TreeMap<Long, MessageBase> historyBundle) {
		clearChatHistory();
		addToHistory(historyBundle);
	}
	
	public void clearChatHistory() {
		completeHistory.clear();
		clearFilteredHistoryStack();
	}
	
	
	private void addToFilteredHistory(MessageBase messageClass) {
	  for(Map.Entry<String, FilteredChatHistory> fch : filteredHistoryStack.entrySet()) { fch.getValue().addToFilteredHistory(messageClass); }
	}
	
	private void clearFilteredHistoryStack() {
	  for(Map.Entry<String, FilteredChatHistory> fch : filteredHistoryStack.entrySet()) { fch.getValue().clear(); }
	}
	
	
	// FILTER METHODS
//	private TreeSet<MessageTag> filterTags = new TreeSet<MessageTag>();
//	
//	private boolean addToFilteredHistory(MessageBase mc) {
//		if (mc.hasAnyOfTags(filterTags.toArray(new MessageTag[0]))) {
//			return false;
//		}
//		filteredHistory.put(mc.getTimeStamp(), mc);
//		return true;
//	}
//	
//	private void addToFilteredHistory(Map<Long, MessageBase> additions) {
//		for (Long key : additions.keySet()) {
//			addToFilteredHistory(additions.get(key));
//		}
//	}
//	
//	public void setFilters(MessageTag...tags) {
//		filterTags.clear();
//		filterTags.addAll(Arrays.asList(tags));
//		resetFilteredChatHistory();
//		// need to notifyDataSetChanged
//	}
//	
//	private void resetFilteredChatHistory() {
//		filteredHistory.clear();
//		
//		addToFilteredHistory(completeHistory);
//	}
//	
//	// tmp
//	public Set<MessageTag> getFilters() {
//	  return filterTags;
//	}
//
//	
//	// GETTERS / SETTERS
	public Map<Long, MessageBase> getCompleteHistory() {
		return completeHistory;
	}
//
//	public Map<Long, MessageBase> getFilteredHistory() {
//		return filteredHistory;
//	}
	
	
	
	public FilteredChatHistory getFilteredChatHistory(String name) {
	  for (Map.Entry<String, FilteredChatHistory> fch : filteredHistoryStack.entrySet()) { 
	    if(fch.getValue().name.equals(name)) return fch.getValue(); 
	  }
	  return null;
	}
	
	public static FilteredChatHistory getPrimaryFilteredChatHistory() {
	  return filteredHistoryStack.get("standard");
	}
	
}
