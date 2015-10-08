/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandler.text;

/**
 *
 * @author footman
 */
public class MessageHandler {
	public String text;
	public String sourceName;
        public String sourceIP;
	public String destName;
        public String destIP;
	
	public MessageHandler(String text, String sourceName, String sourceIP, String destName, String destIP){
		this.text = text;
		this.sourceName = sourceName;
                this.sourceIP = sourceIP;
		this.destName = destName;
                this.destIP = destIP;
	}
}
