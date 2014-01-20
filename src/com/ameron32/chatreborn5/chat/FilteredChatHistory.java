package com.ameron32.chatreborn5.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ameron32.chatreborn5.chat.MessageTemplates.MessageBase;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageTag;


public class FilteredChatHistory {
  private ChatHistory masterHistory;
  public FilteredChatHistory(String name) {
    this.name = name;
    masterHistory = Global.Local.clientChatHistory.register(this);
    filteredHistory = Collections.synchronizedMap(filteredHistoryCore);
  }
  
  public String name;
  
  private final TreeMap<Long, MessageBase> filteredHistoryCore = new TreeMap<Long, MessageBase>();
  
  private final Map<Long, MessageBase> filteredHistory;
  
  
  
  
  
//FILTER METHODS
 private TreeSet<MessageTag> filterTags = new TreeSet<MessageTag>();
 
 public boolean addToFilteredHistory(MessageBase mc) {
   if (mc.hasAnyOfTags(filterTags.toArray(new MessageTag[0]))) { return false; }
   filteredHistory.put(mc.getTimeStamp(), mc);
   return true;
 }
 
 public void addToFilteredHistory(Map<Long, MessageBase> additions) {
   for (Long key : additions.keySet()) { addToFilteredHistory(additions.get(key)); }
 }
 
 public void setFilters(MessageTag...tags) {
   filterTags.clear();
   filterTags.addAll(Arrays.asList(tags));
   resetFilteredChatHistory();
 }
 
 private void resetFilteredChatHistory() {
   clear();
   addToFilteredHistory(masterHistory.getCompleteHistory());
 }
 
 public void clear() {
   filteredHistory.clear();
 }
 
 // tmp
 public Set<MessageTag> getFilters() {
   return filterTags;
 }

 
 // GETTERS / SETTERS

 public Map<Long, MessageBase> getFilteredHistory() {
   return filteredHistory;
 }
}
