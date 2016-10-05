package org.first.team2485.common;
/**
 * A common interface for creating, sending, receiving, and interpreting messages <br>
 * Allows for a unified server/client system
 */
public class Message {
	
	private static final char[] RESERVED_CHARACTORS = {'^', '*'};
	
	public enum MessageType {
		CHAT, SCOUTING_DATA, FORM_UPDATE, BET_PLACE, BET_PAYOUT, BET_CONFIRM, RAW_DATA;
	}
	
	private String reciever;
	private String sender;
	private long timeSent;
	private MessageType messageType;
	private String message;
	
	public Message(String message, String reciever, String sender, MessageType messageType) {
		this.reciever = reciever;
		this.sender = sender;
		this.timeSent = System.currentTimeMillis();
		this.messageType = messageType;
		this.message = message;
	}
	
	public Message(String messageData) {
		this.reciever = messageData.substring(0, messageData.indexOf('*'));
		messageData = messageData.substring(messageData.indexOf('*') + 1);
		this.sender = messageData.substring(0, messageData.indexOf('*'));
		messageData = messageData.substring(messageData.indexOf('*') + 1);
		this.timeSent = Long.parseLong(messageData.substring(0, messageData.indexOf('*')));
		messageData = messageData.substring(messageData.indexOf('*') + 1);
		this.messageType = MessageType.valueOf(messageData.substring(0, messageData.indexOf('*')));
		messageData = messageData.substring(messageData.indexOf('*') + 1);
		this.message = decodeEscapes(messageData);
	}
	
	private static String encodeEscapes(String target) {
		
		for (int i = 0; i < target.length(); i++) {
			for (char curChar : RESERVED_CHARACTORS) {
				if (target.charAt(i) == curChar) {
					target = target.substring(0, i) + ((char) (((int) curChar) + 127)) + target.substring(i + 1);
				}
			}
		}
		return target;
	}
	
	private static String decodeEscapes(String target) {
		
		for (int i = 0; i < target.length(); i++) {
			for (char curChar : RESERVED_CHARACTORS) {
				if (target.charAt(i) == (char) (((int) curChar) + 127)) {
					target = target.substring(0, i) + curChar + target.substring(i + 1);
				}
			}
		}
		return target;
	}
	
	public String getSendableForm() {
		
		String sendableForm = "";
		
		sendableForm += reciever;
		sendableForm += "*" + sender;
		sendableForm += "*" + timeSent;
		sendableForm += "*" + messageType;
		sendableForm += "*" + encodeEscapes(message);
		sendableForm += "^";
		
		return sendableForm;
	}
	
	public String getReciever() {
		return reciever;
	}
	
	public String getSender() {
		return sender;
	}
	
	public long getTimeSent() {
		return timeSent;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}
	
	public String getMessage() {
		return message;
	}
}
