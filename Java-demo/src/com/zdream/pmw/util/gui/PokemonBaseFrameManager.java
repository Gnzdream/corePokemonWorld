package com.zdream.pmw.util.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

/**
 * 设置精灵基本信息的 GUI 窗口工具<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月24日
 * @version v0.1
 */
public class PokemonBaseFrameManager {

	/**
	 * 窗口
	 */
	JFrame frame;
	
	/**
	 * 关于该设置技能数据的 GUI 窗口中解决菜单事件的监听器
	 */
	MenuCommandListener menuListener = new MenuCommandListener(this);
	
	/**
	 * 关于该设置技能数据的 GUI 窗口中解决按钮事件的监听器
	 */
	ButtonCommandListener btListener = new ButtonCommandListener(this);
	
	/**
	 * 组件的 Map
	 */
	Map<String, JComponent> cpnMap = new HashMap<String, JComponent>();
	
	/**
	 * 命令参数（菜单项）
	 */
	static final String F_New = "F-New",
			F_OPEN = "F-Open",
			F_SAVE_AS = "F-Save as",
			F_SAVE_DB = "F-Save to db";

	/**
	 * 命令参数（按钮项）
	 */
	static final String CMD_RESET_FORM = "form=0",
			CMD_RESET = "reset",
			CMD_SAVE = "save";
	
	/**
	 * 重要组件的名称
	 */
	private static final String CPN_INPUT_ID = "ipt-id",
			CPN_INPUT_FORM = "ipt-form", 
			CPN_INPUT_NAME = "ipt-name", 
			CPN_INPUT_HP = "ipt-hp",
			CPN_INPUT_AT = "ipt-at", 
			CPN_INPUT_DF = "ipt-df", 
			CPN_INPUT_SP = "ipt-sp", 
			CPN_INPUT_SA = "ipt-sa",
			CPN_INPUT_SD = "ipt-sd", 
			CPN_INPUT_WT = "ipt-wt";

	/**
	 * 设置窗口的菜单
	 */
	private void addMenus() {
		JMenuBar bar = new JMenuBar();
		JMenu menu = null;
		JMenuItem[] items = null;
		
		// File
		menu = new JMenu("File");
		bar.add(menu);
		items = new JMenuItem[]{new JMenuItem("New"), new JMenuItem("Open"),
				new JMenuItem("Save As..."), new JMenuItem("Save To DB")};
		for (int i = 0; i < items.length; i++) {
			JMenuItem item = items[i];
			switch (i) {
			case 0:
				item.setActionCommand(F_New);
				break;
			case 1:
				item.setActionCommand(F_OPEN);
				break;
			case 2:
				item.setActionCommand(F_SAVE_AS);
				break;
			case 3:
				item.setActionCommand(F_SAVE_DB);
				break;

			default:
				break;
			}
			item.addActionListener(this.menuListener);
			menu.add(item);
		}
		
		frame.setJMenuBar(bar);
	}

	/**
	 * 设置窗口的组件
	 */
	private void addComponents() {
		JLabel label;
		JTextField text;
		JButton button;
		
		label = new JLabel("ID");
		label.setBounds(new Rectangle(20, 20, 60, 20));
		frame.add(label);
		label = new JLabel("Form");
		label.setBounds(new Rectangle(20, 45, 60, 20));
		frame.add(label);
		label = new JLabel("Name");
		label.setBounds(new Rectangle(20, 70, 60, 20));
		frame.add(label);
		label = new JLabel("Type");
		label.setBounds(new Rectangle(20, 95, 60, 20));
		frame.add(label);
		label = new JLabel("HP");
		label.setBounds(new Rectangle(30, 120, 74, 20));
		frame.add(label);
		label = new JLabel("AT");
		label.setBounds(new Rectangle(108, 120, 74, 20));
		frame.add(label);
		label = new JLabel("DF");
		label.setBounds(new Rectangle(186, 120, 74, 20));
		frame.add(label);
		label = new JLabel("SP");
		label.setBounds(new Rectangle(30, 170, 74, 20));
		frame.add(label);
		label = new JLabel("SA");
		label.setBounds(new Rectangle(108, 170, 74, 20));
		frame.add(label);
		label = new JLabel("SD");
		label.setBounds(new Rectangle(186, 170, 74, 20));
		frame.add(label);
		label = new JLabel("Ability");
		label.setBounds(new Rectangle(20, 228, 60, 20));
		frame.add(label);
		label = new JLabel("WT");
		label.setBounds(new Rectangle(20, 270, 60, 20));
		frame.add(label);
		
		text = new JTextField();
		text.setName(CPN_INPUT_ID);
		text.setBounds(new Rectangle(85, 20, 80, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_FORM);
		text.setBounds(new Rectangle(85, 45, 80, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_NAME);
		text.setBounds(new Rectangle(85, 70, 165, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_HP);
		text.setBounds(new Rectangle(20, 140, 74, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_AT);
		text.setBounds(new Rectangle(98, 140, 74, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_DF);
		text.setBounds(new Rectangle(176, 140, 74, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_SP);
		text.setBounds(new Rectangle(20, 190, 74, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_SA);
		text.setBounds(new Rectangle(98, 190, 74, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_SD);
		text.setBounds(new Rectangle(176, 190, 74, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_WT);
		text.setBounds(new Rectangle(85, 270, 165, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);

		button = new JButton("0");
		button.setActionCommand(CMD_RESET_FORM);
		button.addActionListener(btListener);
		button.setBounds(new Rectangle(170, 45, 80, 20));
		frame.add(button);
		button = new JButton("Reset");
		button.setActionCommand(CMD_RESET);
		button.addActionListener(btListener);
		button.setBounds(new Rectangle(85, 300, 80, 20));
		frame.add(button);
		button = new JButton("Save");
		button.setActionCommand(CMD_SAVE);
		button.addActionListener(btListener);
		button.setBounds(new Rectangle(170, 300, 80, 20));
		frame.add(button);
	}
	
	/**
	 * 重置组件中的数据
	 */
	private void reset() {
		
	}
	
	/**
	 * 将组件的数据存储到列表中
	 */
	private void saveToList() {
		
	}
	
	public PokemonBaseFrameManager() {
		frame = new JFrame("Pokemon Base Data Setter");
		
		// 设置窗口的属性
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		
		addMenus();
		addComponents();
		reset();
		
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new PokemonBaseFrameManager();
	}
	
	
	private class MenuCommandListener implements ActionListener {
		
		@SuppressWarnings("unused")
		PokemonBaseFrameManager manager;
		@SuppressWarnings("unused")
		JFileChooser fc = new JFileChooser();
		
		public MenuCommandListener(
				PokemonBaseFrameManager manager) {
			this.manager = manager;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals(F_New)) {
				
			} else if (cmd.equals(F_SAVE_AS)) {
				
			} else if (cmd.equals(F_OPEN)) {
				
			} else if (cmd.equals(F_SAVE_DB)) {
				
			}
			
			System.out.println(cmd);
		}
	}
	
	/**
	 * 关于该设置技能数据的 GUI 窗口中解决按钮事件的监听器
	 * @since v1.0
	 * @author Zdream
	 * @email 18042046922@163.com
	 * @date: 2016年3月17日
	 */
	private class ButtonCommandListener implements ActionListener {
		
		PokemonBaseFrameManager manager;
		
		public ButtonCommandListener(
				PokemonBaseFrameManager manager) {
			this.manager = manager;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			
			if (cmd.equals(CMD_RESET_FORM)) {
				((JTextField) manager.cpnMap.get(CPN_INPUT_FORM)).setText("0");
			} else if (cmd.equals(CMD_RESET)) {
				manager.reset();
			} else if (cmd.equals(CMD_SAVE)) {
				manager.saveToList();
			} else {
				System.out.println("?" + cmd);
			}
		}
	}
} 
