/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

import java.util.Date;

/**
 *
 * @author Dan
 */
public class Message {
    int messageId;
    private String position, senderName, text;
    Date entryDate;

    public Message(int messageId, String senderName, String text, Date entryDate) {
        this.messageId = messageId;
        this.senderName = senderName;
        this.text = text;
        this.entryDate = entryDate;
    }

    public Message(int messageId, String position, String senderName, String text, Date entryDate) {
        this.messageId = messageId;
        this.position = position;
        this.senderName = senderName;
        this.text = text;
        this.entryDate = entryDate;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }
    
    
}
