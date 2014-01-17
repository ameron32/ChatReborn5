package com.ameron32.chatreborn5.chat;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;



import com.ameron32.chatreborn5.chat.MessageTemplates.ChatMessage;
import com.ameron32.chatreborn5.chat.MessageTemplates.MessageTag;
import com.ameron32.chatreborn5.chat.MessageTemplates.RegisterName;
import com.ameron32.chatreborn5.chat.MessageTemplates.ServerChatHistory;
import com.ameron32.chatreborn5.chat.MessageTemplates.SystemMessage;
import com.ameron32.chatreborn5.chat.MessageTemplates.UpdateNames;
import com.ameron32.chatreborn5.dummy.UserContent.User;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	static public final int port = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(RegisterName.class);
		kryo.register(String[].class);
		kryo.register(UpdateNames.class);
		kryo.register(ChatMessage.class);
		kryo.register(SystemMessage.class);
		kryo.register(ServerChatHistory.class);
		kryo.register(TreeMap.class);
		kryo.register(TreeSet.class);
		kryo.register(MessageTag.class);
		kryo.register(User.class);
		kryo.register(ArrayList.class);
	}

	
	
}
