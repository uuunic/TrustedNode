package ui;

import Packet.PacketThreadManager;
import accessPackage.RSAPackage0;
import accessPackage.RSAPackage1;
import accessPackage.RSAPackage2;
import accessPackage.RSAPackage3;
import client.Node;
import com.sun.deploy.security.MozillaJSSDSASignature;
import dataHandler.audio.AudioHandler;
import dataHandler.audio.AudioSpectrum;
import dataHandler.text.MessageHandler;
import sun.reflect.generics.tree.Tree;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author firefix
 * @version 1.1
 */
public class TrustedNode extends javax.swing.JFrame {
    public static final boolean DEBUG = true;
    private static final String TAG = "TrustAccessServer";

    //单机演示界面相关提示
    private static final String DEMO_CLIENT_NAME = "服务器单机演示模式";
    private static final String DEMO_CLIENT_IP = "不合法的IP地址";

    // 使用Handler的相关变量
    public static final int HANDLER_UPDATE_CLIENT_STATE = 20;
    public static final int HANDLER_ADD_CLIENT = 21;
    public static final int HANDLER_ERROR_WIFI = 22;
    public static final int HANDLER_ERROR_NULL_CURRENT_CLIENT = 23;
    public static final int HANDLER_ERROR_CURRENT_CLIENT_OFFLINE = 24;
    public static final int HANDLER_ERROR_NULL_NEIGHBOR_ONLINE = 25;
    public static final int HANDLER_ADD_TEXT = 26;
    public static final int HANDLER_FORWARD_TEXT = 27;
    public static final int HANDLER_REQ_AUDIO_SEND = 28;
    public static final int HANDLER_ACC_AUDIO_SEND = 29;
    public static final int HANDLER_REF_AUDIO_SEND = 30;
    public static final int HANDLER_END_AUDIO_SEND = 31;
    public static final int HANDLER_REQ_AUDIO_RECE = 32;
    public static final int HANDLER_ACC_AUDIO_RECE = 33;
    public static final int HANDLER_REF_AUDIO_RECE = 34;
    public static final int HANDLER_END_AUDIO_RECE = 35;
    public static final int HANDLER_REQ_AUDIO_FORWARD = 36;
    public static final int HANDLER_ERROR_AUDIO_ONLINE = 37;
    public static final int HANDLER_GET_AUDIO_PACKAGE = 38;
    public static final int HANDLER_SEND_AUDIO_PACKAGE = 39;

    public static final int HANDLER_ROUTE_TEXT = 101;
    public static final int HANDLER_ROUTE_ERROR_TEXT = 102;
    public static final int HANDLER_ROUTE_AUDIO = 103;
    public static final int HANDLER_ROUTE_ERROR_AUDIO = 104;

    public static final int HANDLER_RECEIVE_RSA_0_PACKAGE = 105;
    public static final int HANDLER_RECEIVE_RSA_1_PACKAGE = 106;
    public static final int HANDLER_RECEIVE_RSA_2_PACKAGE = 107;
    public static final int HANDLER_RECEIVE_RSA_3_PACKAGE = 108;
    public static final int HANDLER_RECEIVE_RSA_4_PACKAGE = 109;

    public static final int HANDLER_SEND_RSA_0_PACKAGE = 110;
    public static final int HANDLER_SEND_RSA_1_PACKAGE = 111;
    public static final int HANDLER_SEND_RSA_2_PACKAGE = 112;
    public static final int HANDLER_SEND_RSA_3_PACKAGE = 113;
    public static final int HANDLER_SEND_RSA_4_PACKAGE = 114;


    public static final int HANDLER_TEST_BLS = 41;
    public static final int HANDLER_TEST_HESS = 42;
    public static final int HANDLER_TEST_JOUX = 43;
    public static final int HANDLER_TEST_PATERSON = 44;
    public static final int HANDLER_TEST_YUANLI = 45;
    public static final int HANDLER_TEST_ZHANGKIM = 46;
    public static final int HANDLER_TEST_ZSS = 47;
    public static final int HANDLER_TEST_BH = 50;
    public static final int HANDLER_TEST_BH_INIT = 51;
    public static final int HANDLER_TEST_BH_CLIENT_START_AUTH = 52;
    public static final int HANDLER_TEST_BH_SERVER_SEND_FOR_ID = 53;
    public static final int HANDLER_TEST_BH_CLIENT_SIG_TO_SERVER = 54;
    public static final int HANDLER_TEST_BH_SERVER_AUTH_CLIENT = 55;
    public static final int HANDLER_TEST_BH_SERVER_SEND_SIG_TO_STA = 56;
    public static final int HANDLER_TEST_BH_CLIENT_AUTH_SERVER = 57;
    public static final int HANDLER_TEST_BH_CLIENT_SEND_TSTA_TO_AP = 58;
    public static final int HANDLER_TEST_BH_SERVER_GENERATE_KEY = 59;
    public static final int HANDLER_TEST_BH_CLIENT_GENERATE_KEY = 60;
    public static final int HANDLER_TEST_BH_GENERATE_KEY_RESULT = 61;
    public static final int HANDLER_TEST_BH_TIME = 62;
    public static final int HANDLER_TEST_BH_UNINIT = 63;

    public static final int HANDLER_TEST_BH_CLIENT_AUTH_SERVER_RESULT = 64;

    public static final int HANDLER_FAST_BH_1 = 65;
    public static final int HANDLER_FAST_BH_3 = 66;


    public final Handler infoHandler = new Handler() {

        @Override

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HANDLER_UPDATE_CLIENT_STATE:
                    Node nodeUpdate = (Node) msg.obj;
                    // 更新用户的在线信息
                    nodestateListViewManager.updateClientState(nodeUpdate);
                    break;
                case HANDLER_ADD_CLIENT:
                    Node nodeAdd = (Node) msg.obj;
                    // 增加新用户
                    if (DEBUG) {
                        System.out.println(TAG + "增加新用户:"
                                + nodeAdd.getName());
                    }
                    nodestateListViewManager.addClientToList(nodeAdd);
                    break;
                case HANDLER_ERROR_WIFI:
//				JOptionPaneDialog.showErrorJOptionPane();
                    jLabel_systemInfo.setText("Network中断，请检查网络是否连接");
                    break;
                case HANDLER_ERROR_NULL_CURRENT_CLIENT:
                    jLabel_systemInfo.setText("请选择一个合法的通信用户");
                    break;
                case HANDLER_ERROR_CURRENT_CLIENT_OFFLINE:
                    jLabel_systemInfo.setText("当前通信用户处于离线状态或不可信状态");
                    break;
                case HANDLER_ERROR_NULL_NEIGHBOR_ONLINE:
                    jLabel_systemInfo.setText("无路由可与当前用户通信");
                    break;
                case HANDLER_ROUTE_TEXT:
                    MessageHandler messageHandlerRoute = (MessageHandler) msg.obj;
                    messageListViewManager.addNewMessage(messageHandlerRoute.text,
                            messageHandlerRoute.sourceName,
                            messageHandlerRoute.destName + " (中转)");
                    break;
                case HANDLER_ROUTE_ERROR_TEXT:

                    jLabel_systemInfo.setText("路由中断，文字桥接已经断路！");
                    break;
                case HANDLER_ADD_TEXT:
                    MessageHandler messageHandlerAdd = (MessageHandler) msg.obj;
                    messageListViewManager.addNewMessage(messageHandlerAdd.text,
                            messageHandlerAdd.sourceName,
                            messageHandlerAdd.destName);
                    break;
                case HANDLER_FORWARD_TEXT:
                    MessageHandler messageHandlerForward = (MessageHandler) msg.obj;
                    messageListViewManager.addNewMessage(
                            messageHandlerForward.text,
                            messageHandlerForward.sourceName,
                            messageHandlerForward.destName);
                    break;

                case HANDLER_ROUTE_AUDIO:
                    AudioHandler ah = (AudioHandler) msg.obj;
                    jLabel_systemInfo.setText("语音中转进行中：" + ah.sourceName + "->" + ah.destName);
                    break;
                case HANDLER_ROUTE_ERROR_AUDIO:
                    isAudioCommu = false;
                    jLabel_systemInfo.setText("路由中断，语音桥接已经断路！");
                    break;
                case HANDLER_REQ_AUDIO_SEND:
                    AudioHandler audioHandlerSENDREQ = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerSENDREQ);
                    break;
                case HANDLER_ACC_AUDIO_SEND:
                    isAudioCommu = true;
                    AudioHandler audioHandlerSENDACC = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerSENDACC);
                    break;
                case HANDLER_REF_AUDIO_SEND:
                    AudioHandler audioHandlerSENDREF = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerSENDREF);
                    break;
                case HANDLER_END_AUDIO_SEND:
                    isAudioCommu = false;
                    AudioHandler audioHandlerSENDEND = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerSENDEND);
                    break;
                case HANDLER_REQ_AUDIO_RECE:
                    AudioHandler audioHandlerRECEREQ = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerRECEREQ);
                    if (JOptionPaneDialog
                            .showReqAudioJOptionPane(audioHandlerRECEREQ)) {
                        packetThreadManager.accAudio(audioHandlerRECEREQ);
                    } else {
                        packetThreadManager.refAudio(audioHandlerRECEREQ);
                    }
                    break;
                case HANDLER_ACC_AUDIO_RECE:
                    isAudioCommu = true;
                    AudioHandler audioHandlerRECEACC = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerRECEACC);
                    break;
                case HANDLER_REF_AUDIO_RECE:
                    AudioHandler audioHandlerRECEREF = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerRECEREF);
                    break;
                case HANDLER_END_AUDIO_RECE:
                    isAudioCommu = false;
                    AudioHandler audioHandlerRECEEND = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerRECEEND);
                    break;
                case HANDLER_REQ_AUDIO_FORWARD:
                    AudioHandler audioHandlerFORWARDREQ = (AudioHandler) msg.obj;
                    audioInfoManager.addNewAudioInfo(audioHandlerFORWARDREQ);
                    break;
                case HANDLER_ERROR_AUDIO_ONLINE:
                    isAudioCommu = false;
                    jLabel_systemInfo.setText("通信链接中断，语音通信已停止");
                case HANDLER_GET_AUDIO_PACKAGE:
                    byte[] audioGet = (byte[]) msg.obj;
                    if (audioGet != null)
                        getAudioSpectrum.putDataAndDraw(audioGet);
                    break;
                case HANDLER_SEND_AUDIO_PACKAGE:
                    byte[] audioSend = (byte[]) msg.obj;
                    if (audioSend != null)
                        sendAudioSpectrum.putDataAndDraw(audioSend);
                    break;

                case HANDLER_RECEIVE_RSA_0_PACKAGE:
                    RSAPackage0 p0 = (RSAPackage0) msg.obj;
                    ShowAccessLog.showReceiveRSA0Package(jTextArea_log, p0);
                    break;

                case HANDLER_RECEIVE_RSA_1_PACKAGE:
                    RSAPackage1 p1 = (RSAPackage1) msg.obj;
                    ShowAccessLog.showReceiveRSA1Package(jTextArea_log, p1);
                    break;

                case HANDLER_RECEIVE_RSA_2_PACKAGE:
                    RSAPackage2 p2 = (RSAPackage2) msg.obj;
                    ShowAccessLog.showReceiveRSA2Package(jTextArea_log, p2);

                    break;
                case HANDLER_RECEIVE_RSA_3_PACKAGE:
                    RSAPackage3 p3 = (RSAPackage3) msg.obj;
                    ShowAccessLog.showReceiveRSA3Package(jTextArea_log, p3);
                    break;
                case HANDLER_SEND_RSA_0_PACKAGE:
                    RSAPackage0 p0_send = (RSAPackage0) msg.obj;
                    ShowAccessLog.showSendRSA0Package(jTextArea_log, p0_send);
                    break;

                case HANDLER_SEND_RSA_1_PACKAGE:
                    RSAPackage1 p1_send = (RSAPackage1) msg.obj;
                    ShowAccessLog.showSendRSA1Package(jTextArea_log, p1_send);
                    break;
                case HANDLER_SEND_RSA_2_PACKAGE:
                    RSAPackage2 p2_send = (RSAPackage2) msg.obj;
                    ShowAccessLog.showSendRSA2Package(jTextArea_log, p2_send);
                    break;
                case HANDLER_SEND_RSA_3_PACKAGE:
                    RSAPackage3 p3_send = (RSAPackage3) msg.obj;
                    ShowAccessLog.showSendRSA3Package(jTextArea_log, p3_send);
                    break;
                default:
                    break;
            }
        }
    };
    // 拓扑图

    // 用户列表相关组件
    private NodeStateListViewManager nodestateListViewManager;

    // 文字通信页面
    private MessageListViewManager messageListViewManager;

    // 语音通信页面
    private AudioInfoManager audioInfoManager;
    private AudioSpectrum getAudioSpectrum;
    private AudioSpectrum sendAudioSpectrum;
    private boolean isAudioCommu;

    //PBC测试管理对象声明
    private boolean isTestRunning = false;

    //网络通信相关对象声明
    private boolean isDemoMode = true;
    private PacketThreadManager packetThreadManager;

    /**
     * Creates new form AdHocGPSServer
     */
    public TrustedNode() {
        initComponents();
        this.setTitle(TAG);
        getAudioSpectrum = new AudioSpectrum();
        sendAudioSpectrum = new AudioSpectrum();
        jPanel_audio_spectrum.setLayout(new BoxLayout(jPanel_audio_spectrum,
                BoxLayout.X_AXIS));
        jPanel_audio_spectrum.add(sendAudioSpectrum);
        jPanel_audio_spectrum.add(Box.createHorizontalStrut(2));
        jPanel_audio_spectrum.add(getAudioSpectrum);
        nodestateListViewManager = new NodeStateListViewManager(this.jTree_clientlist);
        messageListViewManager = new MessageListViewManager(
                this.jTextArea_commu);
        audioInfoManager = new AudioInfoManager(this.jTextArea_audio);
        isAudioCommu = false;
        packetThreadManager = new PacketThreadManager(infoHandler);
        Node localClient = packetThreadManager.initLocalNet();
        if (localClient != null) {
            this.isDemoMode = false;
            jTextField_localClientName.setText(localClient.getName());
            jTextField_localClientIP.setText(localClient.getIP());
            packetThreadManager.onCreatThread();
            packetThreadManager.onStartThread();
        } else {
            //IP地址不正确，只可进行单机演示
            this.isDemoMode = true;
            jTextField_localClientName.setText(TrustedNode.DEMO_CLIENT_NAME);
            jTextField_localClientIP.setText(TrustedNode.DEMO_CLIENT_IP);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_ClientInfo = new javax.swing.JPanel();
        jPanel_currentClient = new javax.swing.JPanel();
        jLabel_currentClientIP = new javax.swing.JLabel();
        jLabel_currentClientName = new javax.swing.JLabel();
        jLabel_currentClientTitle = new javax.swing.JLabel();
        jTextField_currentClientIP = new javax.swing.JTextField();
        jTextField_currentClientName = new javax.swing.JTextField();
        jPanel_localClient = new javax.swing.JPanel();
        jLabel_localClientIP = new javax.swing.JLabel();
        jLabel_localClientTitle = new javax.swing.JLabel();
        jTextField_localClientIP = new javax.swing.JTextField();
        jLabel_localClientName = new javax.swing.JLabel();
        jTextField_localClientName = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel_topology = new javax.swing.JPanel();
        jPanel_topology_example = new javax.swing.JPanel();
        jLabel_example_local = new javax.swing.JLabel();
        jLabel_example_online = new javax.swing.JLabel();
        jLabel_example_offline = new javax.swing.JLabel();
        jTextField_example_local = new javax.swing.JTextField();
        jTextField_example_online = new javax.swing.JTextField();
        jTextField_example_offline = new javax.swing.JTextField();
        jLabel_topology_example = new javax.swing.JLabel();
        jPanel_commu = new javax.swing.JPanel();
        jButton_commu = new javax.swing.JButton();
        jTextField_commu = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea_commu = new javax.swing.JTextArea();
        jPanel_audio = new javax.swing.JPanel();
        jPanel_audio_button = new javax.swing.JPanel();
        jButton_audio_endAudio = new javax.swing.JButton();
        jButton_audio_reqAudio = new javax.swing.JButton();
        jPanel_audio_spectrum = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea_audio = new javax.swing.JTextArea();
        jPanel_log = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea_log = new javax.swing.JTextArea();
        jButton_bls = new javax.swing.JButton();
        jButton_hess = new javax.swing.JButton();
        jButton_joux = new javax.swing.JButton();
        jButton_paterson = new javax.swing.JButton();
        jButton_yuanli = new javax.swing.JButton();
        jButton_zhangkim = new javax.swing.JButton();
        jButton_zss = new javax.swing.JButton();
        jButton_bh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree_clientlist = new javax.swing.JTree();
        jLabel_systemInfo = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel_currentClientIP.setText("用户IP：");

        jLabel_currentClientName.setText("用户名：");

        jLabel_currentClientTitle.setFont(new java.awt.Font("华文琥珀", 0, 14)); // NOI18N
        jLabel_currentClientTitle
                .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_currentClientTitle.setText("当前通信用户");

        jTextField_currentClientIP.setEnabled(false);

        jTextField_currentClientName.setEnabled(false);

        javax.swing.GroupLayout jPanel_currentClientLayout = new javax.swing.GroupLayout(
                jPanel_currentClient);
        jPanel_currentClient.setLayout(jPanel_currentClientLayout);
        jPanel_currentClientLayout
                .setHorizontalGroup(jPanel_currentClientLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_currentClientLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel_currentClientLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                jLabel_currentClientTitle,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                162,
                                                                Short.MAX_VALUE)
                                                        .addComponent(
                                                                jLabel_currentClientIP)
                                                        .addComponent(
                                                                jTextField_currentClientIP,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                162,
                                                                Short.MAX_VALUE)
                                                        .addComponent(
                                                                jLabel_currentClientName)
                                                        .addComponent(
                                                                jTextField_currentClientName,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                162,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));
        jPanel_currentClientLayout
                .setVerticalGroup(jPanel_currentClientLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_currentClientLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel_currentClientTitle)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel_currentClientIP)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                jTextField_currentClientIP,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel_currentClientName)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(
                                                jTextField_currentClientName,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        jLabel_localClientIP.setText("PC端IP：");

        jLabel_localClientTitle.setFont(new java.awt.Font("华文琥珀", 0, 14)); // NOI18N
        jLabel_localClientTitle
                .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_localClientTitle.setText("用户登录信息");

        jTextField_localClientIP.setEnabled(false);

        jLabel_localClientName.setText("用户名：");

        jTextField_localClientName.setEnabled(false);

        javax.swing.GroupLayout jPanel_localClientLayout = new javax.swing.GroupLayout(
                jPanel_localClient);
        jPanel_localClient.setLayout(jPanel_localClientLayout);
        jPanel_localClientLayout
                .setHorizontalGroup(jPanel_localClientLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_localClientLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel_localClientLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                jLabel_localClientIP)
                                                        .addComponent(
                                                                jLabel_localClientTitle,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(
                                                                jTextField_localClientIP)
                                                        .addComponent(
                                                                jLabel_localClientName)
                                                        .addComponent(
                                                                jTextField_localClientName))
                                        .addContainerGap()));
        jPanel_localClientLayout
                .setVerticalGroup(jPanel_localClientLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_localClientLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel_localClientTitle)
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel_localClientIP)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(
                                                jTextField_localClientIP,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel_localClientName)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(
                                                jTextField_localClientName,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        javax.swing.GroupLayout jPanel_ClientInfoLayout = new javax.swing.GroupLayout(
                jPanel_ClientInfo);
        jPanel_ClientInfo.setLayout(jPanel_ClientInfoLayout);
        jPanel_ClientInfoLayout
                .setHorizontalGroup(jPanel_ClientInfoLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_ClientInfoLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel_ClientInfoLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel_ClientInfoLayout
                                                                        .createSequentialGroup()
                                                                        .addGap(
                                                                                0,
                                                                                0,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                jPanel_currentClient,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(
                                                                jPanel_localClient,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(
                                                                jSeparator1,
                                                                javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addContainerGap()));
        jPanel_ClientInfoLayout
                .setVerticalGroup(jPanel_ClientInfoLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_ClientInfoLayout
                                        .createSequentialGroup()
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(
                                                jPanel_currentClient,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(
                                                jSeparator1,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                10,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(
                                                jPanel_localClient,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)));

        jPanel_topology.setBackground(new java.awt.Color(0, 0, 0));

        jPanel_topology_example.setBackground(new java.awt.Color(0, 0, 0));
        jPanel_topology_example
                .setForeground(new java.awt.Color(240, 240, 240));
        jPanel_topology_example.setOpaque(false);

        jLabel_example_local.setForeground(new java.awt.Color(240, 240, 240));
        jLabel_example_local.setText("本地节点：");

        jLabel_example_online.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_example_online.setText("在线节点：");

        jLabel_example_offline.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_example_offline.setText("离线节点：");

        jTextField_example_local.setEditable(false);
        jTextField_example_local
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jTextField_example_localActionPerformed(evt);
                    }
                });

        jTextField_example_online.setBackground(new java.awt.Color(0, 255, 0));

        jTextField_example_offline.setBackground(new java.awt.Color(255, 0, 0));

        jLabel_topology_example.setFont(new java.awt.Font("宋体", 0, 14)); // NOI18N
        jLabel_topology_example
                .setForeground(new java.awt.Color(255, 255, 255));
        jLabel_topology_example.setText("图例：");

        javax.swing.GroupLayout jPanel_topology_exampleLayout = new javax.swing.GroupLayout(
                jPanel_topology_example);
        jPanel_topology_example.setLayout(jPanel_topology_exampleLayout);
        jPanel_topology_exampleLayout
                .setHorizontalGroup(jPanel_topology_exampleLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_topology_exampleLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel_topology_exampleLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel_topology_exampleLayout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel_example_local)
                                                                        .addGap(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .addComponent(
                                                                                jTextField_example_local,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                20,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(
                                                                jLabel_topology_example)
                                                        .addGroup(
                                                                jPanel_topology_exampleLayout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel_example_online)
                                                                        .addGap(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .addComponent(
                                                                                jTextField_example_online,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                20,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(
                                                                jPanel_topology_exampleLayout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel_example_offline)
                                                                        .addGap(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .addComponent(
                                                                                jTextField_example_offline,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                20,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));
        jPanel_topology_exampleLayout
                .setVerticalGroup(jPanel_topology_exampleLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_topology_exampleLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel_topology_example)
                                        .addGap(18, 18, 18)
                                        .addGroup(
                                                jPanel_topology_exampleLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(
                                                                jLabel_example_local)
                                                        .addComponent(
                                                                jTextField_example_local,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                20,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(
                                                jPanel_topology_exampleLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                jLabel_example_online)
                                                        .addComponent(
                                                                jTextField_example_online,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                20,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(
                                                jPanel_topology_exampleLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                jLabel_example_offline)
                                                        .addComponent(
                                                                jTextField_example_offline,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                20,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)));

        javax.swing.GroupLayout jPanel_topologyLayout = new javax.swing.GroupLayout(
                jPanel_topology);
        jPanel_topology.setLayout(jPanel_topologyLayout);
        jPanel_topologyLayout.setHorizontalGroup(jPanel_topologyLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        jPanel_topologyLayout.createSequentialGroup()
                                .addContainerGap(333, Short.MAX_VALUE)
                                .addComponent(jPanel_topology_example,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));
        jPanel_topologyLayout.setVerticalGroup(jPanel_topologyLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        jPanel_topologyLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                jPanel_topology_example,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(193, Short.MAX_VALUE)));

     //   jTabbedPane.addTab("拓扑图", jPanel_topology);

        jButton_commu.setText("发送");
        jButton_commu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_commuActionPerformed(evt);
            }
        });

        jTextField_commu.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField_commu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_commuActionPerformed(evt);
            }
        });
        jTextField_commu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField_commuKeyReleased(evt);
            }
        });

        jTextArea_commu.setEditable(false);
        jTextArea_commu.setColumns(20);
        jTextArea_commu.setRows(5);
        jScrollPane3.setViewportView(jTextArea_commu);

        javax.swing.GroupLayout jPanel_commuLayout = new javax.swing.GroupLayout(
                jPanel_commu);
        jPanel_commu.setLayout(jPanel_commuLayout);
        jPanel_commuLayout
                .setHorizontalGroup(jPanel_commuLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                jPanel_commuLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel_commuLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel_commuLayout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jTextField_commu)
                                                                        .addGap(
                                                                                18,
                                                                                18,
                                                                                18)
                                                                        .addComponent(
                                                                                jButton_commu))
                                                        .addComponent(
                                                                jScrollPane3,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                441,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));
        jPanel_commuLayout
                .setVerticalGroup(jPanel_commuLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                jPanel_commuLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(
                                                jScrollPane3,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                300, Short.MAX_VALUE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                                jPanel_commuLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(
                                                                jButton_commu)
                                                        .addComponent(
                                                                jTextField_commu,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap()));

        jTabbedPane.addTab("文字通信", jPanel_commu);

        jButton_audio_endAudio.setText("结束语音通信");
        jButton_audio_endAudio
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton_audio_endAudioActionPerformed(evt);
                    }
                });

        jButton_audio_reqAudio.setText("邀请语音通信");
        jButton_audio_reqAudio
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton_audio_reqAudioActionPerformed(evt);
                    }
                });

        javax.swing.GroupLayout jPanel_audio_buttonLayout = new javax.swing.GroupLayout(
                jPanel_audio_button);
        jPanel_audio_button.setLayout(jPanel_audio_buttonLayout);
        jPanel_audio_buttonLayout
                .setHorizontalGroup(jPanel_audio_buttonLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                jPanel_audio_buttonLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jButton_audio_reqAudio)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                211, Short.MAX_VALUE)
                                        .addComponent(jButton_audio_endAudio)
                                        .addContainerGap()));
        jPanel_audio_buttonLayout
                .setVerticalGroup(jPanel_audio_buttonLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                jPanel_audio_buttonLayout
                                        .createSequentialGroup()
                                        .addContainerGap(
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addGroup(
                                                jPanel_audio_buttonLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(
                                                                jButton_audio_endAudio)
                                                        .addComponent(
                                                                jButton_audio_reqAudio))
                                        .addContainerGap()));

        javax.swing.GroupLayout jPanel_audio_spectrumLayout = new javax.swing.GroupLayout(
                jPanel_audio_spectrum);
        jPanel_audio_spectrum.setLayout(jPanel_audio_spectrumLayout);
        jPanel_audio_spectrumLayout
                .setHorizontalGroup(jPanel_audio_spectrumLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE));
        jPanel_audio_spectrumLayout
                .setVerticalGroup(jPanel_audio_spectrumLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 136, Short.MAX_VALUE));

        jTextArea_audio.setColumns(20);
        jTextArea_audio.setEditable(false);
        jTextArea_audio.setRows(3);
        jScrollPane4.setViewportView(jTextArea_audio);

        javax.swing.GroupLayout jPanel_audioLayout = new javax.swing.GroupLayout(
                jPanel_audio);
        jPanel_audio.setLayout(jPanel_audioLayout);
        jPanel_audioLayout
                .setHorizontalGroup(jPanel_audioLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_audioLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel_audioLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                jPanel_audio_button,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(
                                                                jPanel_audio_spectrum,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                        .addComponent(
                                                                jScrollPane4,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                441,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));
        jPanel_audioLayout.setVerticalGroup(jPanel_audioLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        jPanel_audioLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                jPanel_audio_spectrum,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18).addComponent(jScrollPane4,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                133, Short.MAX_VALUE).addGap(5, 5, 5)
                                .addComponent(jPanel_audio_button,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        41,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));

        jTabbedPane.addTab("语音通信", jPanel_audio);

        jTextArea_log.setEditable(false);
        jTextArea_log.setColumns(20);
        jTextArea_log.setRows(5);
        jScrollPane2.setViewportView(jTextArea_log);

        jButton_bls.setText("BLS");

        jButton_hess.setText("HESS");
        jButton_hess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_hessActionPerformed(evt);
            }
        });

        jButton_joux.setText("JOUX");
        jButton_joux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_jouxActionPerformed(evt);
            }
        });

        jButton_paterson.setText("PS");
        jButton_paterson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_patersonActionPerformed(evt);
            }
        });

        jButton_yuanli.setText("YL");
        jButton_yuanli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_yuanliActionPerformed(evt);
            }
        });

        jButton_zhangkim.setText("ZK");
        jButton_zhangkim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_zhangkimActionPerformed(evt);
            }
        });

        jButton_zss.setText("ZSS");
        jButton_zss.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_zssActionPerformed(evt);
            }
        });

        jButton_bh.setText("BH");
        jButton_bh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_bhActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_logLayout = new javax.swing.GroupLayout(
                jPanel_log);
        jPanel_log.setLayout(jPanel_logLayout);
        jPanel_logLayout
                .setHorizontalGroup(jPanel_logLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_logLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel_logLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                jScrollPane2,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                441,
                                                                Short.MAX_VALUE)
                                                        .addGroup(
                                                                jPanel_logLayout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jButton_bls)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jButton_hess)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jButton_joux)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jButton_paterson)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jButton_yuanli)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jButton_zhangkim)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jButton_zss)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jButton_bh)
                                                                        .addGap(
                                                                                0,
                                                                                0,
                                                                                Short.MAX_VALUE)))
                                        .addContainerGap()));
        jPanel_logLayout
                .setVerticalGroup(jPanel_logLayout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel_logLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(
                                                jScrollPane2,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                304, Short.MAX_VALUE)
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                jPanel_logLayout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(
                                                                jButton_bls)
                                                        .addComponent(
                                                                jButton_hess)
                                                        .addComponent(
                                                                jButton_joux)
                                                        .addComponent(
                                                                jButton_paterson)
                                                        .addComponent(
                                                                jButton_yuanli)
                                                        .addComponent(
                                                                jButton_zhangkim)
                                                        .addComponent(
                                                                jButton_zss)
                                                        .addComponent(
                                                                jButton_bh))
                                        .addContainerGap()));

        jTabbedPane.addTab("系统输出", jPanel_log);
        jTree_clientlist
                .addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                    public void valueChanged(
                            javax.swing.event.TreeSelectionEvent evt) {
                        jTree_clientlistValueChanged(evt);

                    }
                });
        jScrollPane1.setViewportView(jTree_clientlist);

        jLabel_systemInfo.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
        jLabel_systemInfo.setForeground(new java.awt.Color(255, 0, 51));

        jLabel2.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
        jLabel2.setText("系统提示：");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
                getContentPane());
        getContentPane().setLayout(layout);
        layout
                .setHorizontalGroup(layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                false)
                                                        .addGroup(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jScrollPane1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                216,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jTabbedPane,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                466,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel2)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                jLabel_systemInfo,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)))
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(
                                                jPanel_ClientInfo,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap()));
        layout
                .setVerticalGroup(layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                        .addComponent(
                                                                                                jScrollPane1)
                                                                                        .addComponent(
                                                                                                jTabbedPane))
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addGroup(
                                                                                layout
                                                                                        .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                        .addComponent(
                                                                                                jLabel2,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                        .addComponent(
                                                                                                jLabel_systemInfo,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)))
                                                        .addGroup(
                                                                layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jPanel_ClientInfo,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(
                                                                                0,
                                                                                0,
                                                                                Short.MAX_VALUE)))
                                        .addGap(0, 10, 10)));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_audio_reqAudioActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (!isDemoMode) {
            if (!isAudioCommu) {
                packetThreadManager.reqAudio();
            }
        }
    }

    private void jButton_audio_endAudioActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (!isDemoMode) {
            if (isAudioCommu) {
                packetThreadManager.endAudio();
            }
        }
    }

    // 进行加密的判断和发送
    private void jButton_commuActionPerformed(java.awt.event.ActionEvent evt) {
        if (!isDemoMode) {
            String text = this.jTextField_commu.getText().toString();
            if (!text.equals("")) {
                if (packetThreadManager.sendTextMessage(text)) {
                    jTextField_commu.setText("");
                }
            }
        }
    }// GEN-LAST:event_jButton_commuActionPerformed

    private void jTextField_example_localActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField_example_localActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField_example_localActionPerformed

    private void jTree_clientlistValueChanged(
            javax.swing.event.TreeSelectionEvent evt) {// GEN-FIRST:event_jTree_clientlistValueChanged
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) this.jTree_clientlist
                .getLastSelectedPathComponent();
      //  String nodeIP = treeNode.getChildAt(1).toString().split("：")[1].trim(); //比较脏的方法获取的IP
        String treeString = treeNode.toString();
        if (packetThreadManager.setCurrentNodeByName(treeString)) {
            Node node = packetThreadManager.getCurrentNode();
            jTextField_currentClientIP.setText(node.getIP());
            jTextField_currentClientName.setText(node.getName());
        }
    }// GEN-LAST:event_jTree_clientlistValueChanged

    private void jTextField_commuKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jTextField_commuKeyReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField_commuKeyReleased

    private void jTextField_commuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField_commuActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_jTextField_commuActionPerformed


    private void jButton_hessActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton_hessActionPerformed
        if (this.isTestRunning == true) return;
        this.isTestRunning = true;
    }

    private void jButton_jouxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton_jouxActionPerformed
        if (this.isTestRunning == true) return;
        this.isTestRunning = true;
    }

    private void jButton_patersonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton_patersonActionPerformed
        if (this.isTestRunning == true) return;
        this.isTestRunning = true;
    }

    private void jButton_yuanliActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton_yuanliActionPerformed
        if (this.isTestRunning == true) return;
        this.isTestRunning = true;
    }

    private void jButton_zhangkimActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton_zhangkimActionPerformed
        if (this.isTestRunning == true) return;
        this.isTestRunning = true;
    }

    private void jButton_zssActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton_zssActionPerformed
        if (this.isTestRunning == true) return;
        this.isTestRunning = true;
    }

    private void jButton_bhActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton_bhActionPerformed
        if (this.isTestRunning == true) return;
        this.isTestRunning = true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
		/*
		 * Set the Nimbus look and feel
		 */
        // <editor-fold defaultstate="collapsed"
        // desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase
		 * /tutorial/uiswing/lookandfeel/plaf.html
		 */

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(
                    TrustedNode.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(
                    TrustedNode.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(
                    TrustedNode.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(
                    TrustedNode.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

		/*
		 * Create and display the form
		 */

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                new TrustedNode().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_audio_endAudio;
    private javax.swing.JButton jButton_audio_reqAudio;
    private javax.swing.JButton jButton_bh;
    private javax.swing.JButton jButton_bls;
    private javax.swing.JButton jButton_commu;
    private javax.swing.JButton jButton_hess;
    private javax.swing.JButton jButton_joux;
    private javax.swing.JButton jButton_paterson;
    private javax.swing.JButton jButton_yuanli;
    private javax.swing.JButton jButton_zhangkim;
    private javax.swing.JButton jButton_zss;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel_currentClientIP;
    private javax.swing.JLabel jLabel_currentClientName;
    private javax.swing.JLabel jLabel_currentClientTitle;
    private javax.swing.JLabel jLabel_example_local;
    private javax.swing.JLabel jLabel_example_offline;
    private javax.swing.JLabel jLabel_example_online;
    private javax.swing.JLabel jLabel_localClientIP;
    private javax.swing.JLabel jLabel_localClientName;
    private javax.swing.JLabel jLabel_localClientTitle;
    private javax.swing.JLabel jLabel_systemInfo;
    private javax.swing.JLabel jLabel_topology_example;
    private javax.swing.JPanel jPanel_ClientInfo;
    private javax.swing.JPanel jPanel_audio;
    private javax.swing.JPanel jPanel_audio_button;
    private javax.swing.JPanel jPanel_audio_spectrum;
    private javax.swing.JPanel jPanel_commu;
    private javax.swing.JPanel jPanel_currentClient;
    private javax.swing.JPanel jPanel_localClient;
    private javax.swing.JPanel jPanel_log;
    private javax.swing.JPanel jPanel_topology;
    private javax.swing.JPanel jPanel_topology_example;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextArea jTextArea_audio;
    private javax.swing.JTextArea jTextArea_commu;
    private javax.swing.JTextArea jTextArea_log;
    private javax.swing.JTextField jTextField_commu;
    private javax.swing.JTextField jTextField_currentClientIP;
    private javax.swing.JTextField jTextField_currentClientName;
    private javax.swing.JTextField jTextField_example_local;
    private javax.swing.JTextField jTextField_example_offline;
    private javax.swing.JTextField jTextField_example_online;
    private javax.swing.JTextField jTextField_localClientIP;
    private javax.swing.JTextField jTextField_localClientName;
    private javax.swing.JTree jTree_clientlist;
// End of variables declaration//GEN-END:variables
}
