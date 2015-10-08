/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandler.audio;


import Packet.DatagramPackage;

/**
 *
 * @author footman
 */
public class AudioHandler {
	public String sourceName;
	public String sourceIP;
	public String destName;
	public String destIP;
	public DatagramPackage.KeyWord keyword;
	
	public AudioHandler(DatagramPackage.KeyWord keyword, String sourceName, String sourceIP,
			String destName, String destIP){
		this.keyword = keyword;
		this.sourceName = sourceName;
		this.sourceIP = sourceIP;
		this.destName = destName;
		this.destIP = destIP;
	}
}
