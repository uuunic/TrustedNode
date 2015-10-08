/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import dataHandler.audio.AudioHandler;

import javax.swing.*;

/**
 *
 * @author footman
 */
public class JOptionPaneDialog {
    private static final String ErrorTitle = "错误";
    private static final String AudioTitle = "语音请求";
    private static final String ReqAudioMessage = "语音通信，同意请点击【确定】，拒绝请点击【取消】!";
    
    public static void showErrorJOptionPane(){
        String messageString = "未接入网络或网络设置错误，请检查设备是否接入网络，是否设置了正确的IP地址……";
        Object[] message = {messageString};
        JOptionPane.showMessageDialog(null, message, ErrorTitle, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showReqAudioJOptionPane(final AudioHandler audioHandler){
        String messageString = audioHandler.sourceIP + "邀请您进行" + ReqAudioMessage;
        Object[] message = {messageString};
        int res = JOptionPane.showConfirmDialog(null, message, AudioTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch (res){
            case JOptionPane.OK_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
                return false;
            case JOptionPane.CLOSED_OPTION:
                return false;
            default:
                return false;
        }
    }
}
