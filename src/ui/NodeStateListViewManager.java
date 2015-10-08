/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import client.Node;
import util.Log;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author footman
 */
public class NodeStateListViewManager {
    private JTree jTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    
    public NodeStateListViewManager(JTree jTree) {
        this.jTree = jTree;
        rootNode = new DefaultMutableTreeNode("用户列表");
        treeModel = new DefaultTreeModel(rootNode);
        this.jTree.setModel(treeModel);
    }
    
    public void updateClientState(Node nodeUpdate) {
		for (int i=0;i<rootNode.getChildCount();i++){
			TreeNode treeNode = rootNode.getChildAt(i);
//            System.out.println(treeNode.toString());
			if (treeNode.toString().equals(nodeUpdate.getName())){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeNode;
				if (nodeUpdate.isOnline() && nodeUpdate.isValid()){
				    //删除”状态：在线，可信“ 那一行
                    node.remove(2);

                    node.add(new DefaultMutableTreeNode("状态：在线，可信"));
                    Log.i(this.getClass().getName(), nodeUpdate.getName() + " 已登录,状态可信.");

				}else if (nodeUpdate.isOnline() && !nodeUpdate.isValid()){
					node.remove(2);
                    node.add(new DefaultMutableTreeNode("状态：在线，不可信"));
                    Log.v(this.getClass().getName(), nodeUpdate.getName() + "状态不可信，IP地址为:" + nodeUpdate.getIP());
				}else{
					node.remove(2);
                    node.add(new DefaultMutableTreeNode("状态：离线"));
                    Log.i(this.getClass().getName(), nodeUpdate.getName() + "不在线，IP地址为:" + nodeUpdate.getIP());
				}
				this.jTree.updateUI();
				return;
			}
		}
	}
	
	public void addClientToList(Node nodeUpdate){
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodeUpdate.getName());
            treeNode.add(new DefaultMutableTreeNode("姓名：" + nodeUpdate.getName()));
            treeNode.add(new DefaultMutableTreeNode("IP：" + nodeUpdate.getIP()));
            if (nodeUpdate.isOnline() && nodeUpdate.isValid()){
                treeNode.add(new DefaultMutableTreeNode("状态：在线，可信"));
                Log.i(this.getClass().getName(), nodeUpdate.getName() + "新增Node,状态可信.");
            }else if (nodeUpdate.isOnline() && !nodeUpdate.isValid()){
            	treeNode.add(new DefaultMutableTreeNode("状态：在线，不可信"));
                Log.v(this.getClass().getName(), nodeUpdate.getName() + "新增Node，状态不可信.");
            }else{
                treeNode.add(new DefaultMutableTreeNode("状态：离线"));
                Log.i(this.getClass().getName(), nodeUpdate.getName() + "新增Node,状态 : 离线.");
            }
            rootNode.add(treeNode);
            this.jTree.updateUI();
	}
}
