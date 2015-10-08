/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import dataHandler.audio.AudioHandler;

import javax.swing.*;
import java.util.Calendar;

/**
 *
 * @author footman
 */
public class AudioInfoManager {
	private final JTextArea listView;

	public AudioInfoManager(JTextArea listView){
		this.listView = listView;
	}
	
	public void addNewAudioInfo(AudioHandler audioHandler){
		String message;
		switch(audioHandler.keyword){
		case REQAUDIO:
			message = "邀请语音通信";
			break;
		case ACCAUDIO:
			message = "接受语音通信";
			break;
		case REFAUDIO:
			message = "拒绝语音通信";
			break;
		case ENDAUDIO:
			message = "结束语音通信";
			break;
		default:
			message = "";	
		}
                listView.append(getCurrentTime() + "  " + audioHandler.sourceName + "对" + audioHandler.destName + "：\n");
                listView.append(message + "\n");
	}
	
	private String getCurrentTime(){
		Calendar t = Calendar.getInstance();
		int year = t.get(Calendar.YEAR);
		int month = t.get(Calendar.MONTH) + 1;
		int date = t.get(Calendar.DATE);
		int hour = t.get(Calendar.HOUR_OF_DAY);
		int minute = t.get(Calendar.MINUTE);
		return year + "年" + month + "月" + date + "日" + hour + "时" + minute + "分";
	}
}
