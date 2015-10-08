/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.*;

/**
 *
 * @author footman
 */
public abstract class Handler {
    public final Message obtainMessage(int what){
        Message message = new Message();
        message.what = what;
        return message;
    }
    
    public final Message obtainMessage(int what, Object obj){
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        return message;
    }
    
    public abstract void handleMessage(Message msg);
    
    public class Message {
        public Object obj;
        public int what;
        
        public void sendToTarget(){
            EventQueue.invokeLater(new MessageRunnable(this));
        }
    }
    
    private class MessageRunnable implements Runnable {
        private Message message;
        
        public MessageRunnable(Message message){
            this.message = message;
        }
        @Override
        public void run() {
             Handler.this.handleMessage(message);
        }
    }
}
