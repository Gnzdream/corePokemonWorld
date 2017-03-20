package com.zdream.pmw.util.gui;

import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.zdream.pmw.monster.data.dao.ISkillDao;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.monster.skill.ISkillService;
import com.zdream.pmw.monster.skill.Skill;
import com.zdream.pmw.monster.skill.SkillServiceImpl;
import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonValue;
import com.zdream.pmwdb.core.DaoGetter;
import com.zdream.pmwdb.mysql.convert.SkillBuilder;

/**
 * 设置技能的 GUI 窗口工具<br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年3月16日
 * @version v0.1
 */
public class SkillSetterFrameManager {
	
	/**
	 * 窗口
	 */
	JFrame frame;
	
	/**
	 * 服务
	 */
	ISkillService service = SkillServiceImpl.getInstance(DaoGetter.getDao(ISkillDao.class));
	
	/**
	 * 关于该设置技能数据的 GUI 窗口中解决菜单事件的监听器
	 */
	MenuCommandListener menuListener = new MenuCommandListener(this, frame);
	
	/**
	 * 关于该设置技能数据的 GUI 窗口中解决按钮事件的监听器
	 */
	ButtonCommandListener btListener = new ButtonCommandListener(this, frame);
	
	/**
	 * 列表组件的数据
	 */
	SkillListModel listModel = new SkillListModel(this, frame);
	
	/**
	 * 列表单元选择监听器
	 */
	ListChooserListener chooserListener = new ListChooserListener(this, frame);
	
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
	static final String CMD_PROPER_ID = "proper id",
			CMD_POWER_NONE = "power none",
			CMD_POWER_FLOAT = "power float",
			CMD_ACCURACY_FLOAT = "acc float",
			CMD_DESCRIPTION_CLEAN = "des. clean",
			CMD_RESET = "reset",
			CMD_SAVE = "save";
	
	/**
	 * 重要组件的名称
	 */
	private static final String CPN_INPUT_ID = "ipt-id",
			CPN_INPUT_TITLE = "ipt-title",
			CPN_SELECT_TYPE = "sel-type",
			CPN_INPUT_POWER = "ipt-power",
			CPN_SELECT_ACCURACY = "sel-accuracy",
			CPN_SELECT_CATEGORY = "sel-cate",
			CPN_SELECT_PP = "sel-pp",
			CPN_AREA_DESC = "area-des",
			CPN_LIST = "list",
			CPN_BUTTON_ID = "bt-id",
			CPN_POPMENU = "JPopupMenu";
	
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
		JComboBox<String> box;
		JTextArea area;
		JList<SkillItem> jlist;
		JScrollPane scroll;
		JPopupMenu popupMenu;
		
		label = new JLabel("id");
		label.setBounds(new Rectangle(20, 20, 60, 20));
		frame.add(label);
		label = new JLabel("title");
		label.setBounds(new Rectangle(20, 45, 60, 20));
		frame.add(label);
		label = new JLabel("type");
		label.setBounds(new Rectangle(20, 70, 60, 20));
		frame.add(label);
		label = new JLabel("power");
		label.setBounds(new Rectangle(20, 95, 60, 20));
		frame.add(label);
		label = new JLabel("accuracy");
		label.setBounds(new Rectangle(20, 120, 60, 20));
		frame.add(label);
		label = new JLabel("category");
		label.setBounds(new Rectangle(20, 145, 60, 20));
		frame.add(label);
		label = new JLabel("pp Max");
		label.setBounds(new Rectangle(20, 170, 60, 20));
		frame.add(label);
		label = new JLabel("description");
		label.setBounds(new Rectangle(20, 195, 100, 20));
		frame.add(label);
		
		text = new JTextField();
		text.setName(CPN_INPUT_ID);
		text.setBounds(new Rectangle(85, 20, 80, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text.addKeyListener(new InputNumberKeyListener(text, 5));
		text = new JTextField();
		text.setName(CPN_INPUT_TITLE);
		text.setBounds(new Rectangle(85, 45, 165, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text = new JTextField();
		text.setName(CPN_INPUT_POWER);
		text.setBounds(new Rectangle(85, 95, 70, 20));
		frame.add(text);
		cpnMap.put(text.getName(), text);
		text.addKeyListener(new InputNumberKeyListener(text, 3));
		
		box = new JComboBox<String>(getStrings(CPN_SELECT_TYPE));
		box.setName(CPN_SELECT_TYPE);
		box.setBounds(new Rectangle(85, 70, 80, 20));
		frame.add(box);
		cpnMap.put(box.getName(), box);
		box = new JComboBox<String>(getStrings(CPN_SELECT_ACCURACY));
		box.setName(CPN_SELECT_ACCURACY);
		box.setBounds(new Rectangle(85, 120, 80, 20));
		frame.add(box);
		cpnMap.put(box.getName(), box);
		box = new JComboBox<String>(getStrings(CPN_SELECT_CATEGORY));
		box.setName(CPN_SELECT_CATEGORY);
		box.setBounds(new Rectangle(85, 145, 80, 20));
		frame.add(box);
		cpnMap.put(box.getName(), box);
		box = new JComboBox<String>(getStrings(CPN_SELECT_PP));
		box.setName(CPN_SELECT_PP);
		box.setBounds(new Rectangle(85, 170, 80, 20));
		frame.add(box);
		cpnMap.put(box.getName(), box);
		
		area = new JTextArea();
		area.setName(CPN_AREA_DESC);
		area.setLineWrap(true);
		cpnMap.put(area.getName(), area);
		scroll = new JScrollPane(area);
		scroll.setBounds(new Rectangle(20, 220, 230, 70)); 
		frame.add(scroll);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		
		button = new JButton("");
		button.setActionCommand(CMD_PROPER_ID);
		button.setName(CPN_BUTTON_ID);
		button.addActionListener(btListener);
		button.setBounds(new Rectangle(170, 20, 80, 20));
		cpnMap.put(button.getName(), button);
		frame.add(button);
		button = new JButton("0");
		button.setActionCommand(CMD_POWER_NONE);
		button.addActionListener(btListener);
		button.setBounds(new Rectangle(160, 95, 45, 20));
		frame.add(button);
		button = new JButton("?");
		button.setActionCommand(CMD_POWER_FLOAT);
		button.addActionListener(btListener);
		button.setBounds(new Rectangle(205, 95, 45, 20));
		frame.add(button);
		button = new JButton("?");
		button.setActionCommand(CMD_ACCURACY_FLOAT);
		button.addActionListener(btListener);
		button.setBounds(new Rectangle(170, 120, 80, 20));
		frame.add(button);
		button = new JButton("clean");
		button.setActionCommand(CMD_DESCRIPTION_CLEAN);
		button.addActionListener(btListener);
		button.setBounds(new Rectangle(170, 195, 80, 20));
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
		
		popupMenu = new JPopupMenu(); // CPN_POPMENU
		popupMenu.setName(CPN_POPMENU);
		cpnMap.put(popupMenu.getName(), popupMenu);
		chooserListener.addMenuItem(popupMenu);
		
		jlist = new JList<SkillItem>(this.listModel);
		jlist.setName(CPN_LIST);
		jlist.addMouseListener(this.chooserListener);
		scroll = new JScrollPane(jlist);
		scroll.setBounds(new Rectangle(270, 20, 100, 270)); 
		frame.add(scroll);
		cpnMap.put(jlist.getName(), jlist);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	}
	
	/**
	 * 获得 GUI 中对应的组件
	 * @param name
	 *   组件设置的名称
	 * @return
	 */
	JComponent getComponent(String name) {
		return cpnMap.get(name);
	}
	
	/**
	 * 获得字符串数组对象<br>
	 * 一般为 JComboBox 中需要用的选项
	 * @param name
	 *   组件的名称
	 * @return
	 */
	String[] getStrings(String name) {
		String[] result = null;
		if (name.equals(CPN_SELECT_TYPE)) {
			EPokemonType[] types = EPokemonType.values();
			result = new String[types.length];
			for (int i = 0; i < types.length; i++) {
				result[i] = types[i].name();
			}
		} else if (name.equals(CPN_SELECT_ACCURACY)) {
			result = new String[16];
			result[0] = "--";
			for (int i = 1; i < result.length; i++) {
				result[i] = Integer.valueOf(105 - i * 5).toString();
			}
		} else if (name.equals(CPN_SELECT_CATEGORY)) {
			ESkillCategory[] categorys = ESkillCategory.values();
			result = new String[categorys.length];
			for (int i = 0; i < categorys.length; i++) {
				result[i] = categorys[i].name();
			}
		} else if (name.equals(CPN_SELECT_PP)){
			result = new String[8];
			for (int i = 0; i < result.length; i++) {
				result[i] = Integer.valueOf(40 - i * 5).toString();
			}
		}
		return result;
	}
	
	/**
	 * 重置组件中的数据
	 */
	@SuppressWarnings("rawtypes")
	void reset() {
		((JButton) cpnMap.get(CPN_BUTTON_ID)).setText(Integer.valueOf(this.properId()).toString());
		((JTextField) cpnMap.get(CPN_INPUT_ID)).setText(((JButton) cpnMap.get(CPN_BUTTON_ID)).getText());
		((JTextField) cpnMap.get(CPN_INPUT_TITLE)).setText("");
		((JComboBox) cpnMap.get(CPN_SELECT_TYPE)).setSelectedIndex(1);
		((JTextField) cpnMap.get(CPN_INPUT_POWER)).setText(Integer.valueOf(0).toString());
		((JComboBox) cpnMap.get(CPN_SELECT_ACCURACY)).setSelectedIndex(1);
		((JComboBox) cpnMap.get(CPN_SELECT_CATEGORY)).setSelectedIndex(0);
		((JComboBox) cpnMap.get(CPN_SELECT_PP)).setSelectedIndex(0);
		((JTextArea) cpnMap.get(CPN_AREA_DESC)).setText("");
	}
	
	/**
	 * 选取合适的 ID 号码 TODO
	 */
	private int nextId = 1;
	int properId() {
		return nextId;
	}
	
	/**
	 * 将输入的数据存入列表中
	 * @return 
	 *   返回 code<br>
	 *   其中为 0 则成功添加
	 */
	@SuppressWarnings("rawtypes")
	int saveDataToList() {
		int result = checkData(true);
		if (result != 0) {
			System.out.println("errorCode: " + result);
			return result;
		}
		
		Skill skill = new Skill();
		skill.setId(Short.parseShort(((JTextField) cpnMap.get(CPN_INPUT_ID)).getText()));
		skill.setTitle(((JTextField) cpnMap.get(CPN_INPUT_TITLE)).getText());
		skill.setType(EPokemonType.parseEnum(((JComboBox) cpnMap.get(CPN_SELECT_TYPE)).getSelectedIndex()));
		skill.setPower(Short.parseShort(((JTextField) cpnMap.get(CPN_INPUT_POWER)).getText()));
		{
			int index = ((JComboBox)cpnMap.get(CPN_SELECT_ACCURACY)).getSelectedIndex();
			skill.setAccuracy((index == 0) ? (byte)0 : (byte)(105 - index * 5));
		}
		skill.setCategory(ESkillCategory.parseEnum(
				((JComboBox) cpnMap.get(CPN_SELECT_CATEGORY)).getSelectedIndex()));
		skill.setPpMax((byte)(40 - ((JComboBox)cpnMap.get(CPN_SELECT_PP)).getSelectedIndex() * 5));
		skill.setDescription(((JTextArea) cpnMap.get(CPN_AREA_DESC)).getText());
		
		this.addList(new SkillItem(skill));
		
		// 计算下一个 properId
		this.nextId = skill.getId() + 1;
		if (nextId <= 0) {
			nextId = 1;
		}
		for (;;){
			Skill s = new Skill();
			s.setId((short) nextId);
			if (listModel.contains(new SkillItem(s))) {
				nextId ++;
				continue;
			}
			break;
		}
		((JButton) this.cpnMap.get(CPN_BUTTON_ID)).setText(Integer.toString(nextId));
		((JTextField) this.cpnMap.get(CPN_INPUT_ID)).setText(Integer.toString(nextId));
		
		return 0;
	} 
	
	/**
	 * 从存入的列表中，选择显示的数据
	 * @param index
	 *   在列表选择的元素的索引
	 */
	@SuppressWarnings("unchecked")
	void viewDataFromList(int index) {
		SkillItem item = listModel.elementAt(index);
		
		if (item == null) {
			return;
		}
		Skill skill = item.skill;
		((JTextField) cpnMap.get(CPN_INPUT_ID)).setText(Short.toString(skill.getId()));
		((JTextField) cpnMap.get(CPN_INPUT_TITLE)).setText(skill.getTitle());
		if (skill.getType() != null) {
			((JComboBox<String>) cpnMap.get(CPN_SELECT_TYPE)).setSelectedIndex(skill.getType().ordinal());
		} else {
			System.err.println("Type is null!");
		}
		
		((JTextField) cpnMap.get(CPN_INPUT_POWER)).setText(Short.toString(skill.getPower()));
		((JComboBox<String>) cpnMap.get(CPN_SELECT_ACCURACY)).setSelectedIndex(
				(skill.getAccuracy() == 0) ? 0 : (105 - skill.getAccuracy()) / 5);
		if (skill.getCategory() != null) {
			((JComboBox<String>) cpnMap.get(CPN_SELECT_CATEGORY)).setSelectedIndex(
					skill.getCategory().ordinal());
		} else {
			System.err.println("Category is null!");
		}
		((JComboBox<String>) cpnMap.get(CPN_SELECT_PP)).setSelectedIndex((40 - skill.getPpMax()) / 5);
		((JTextArea) cpnMap.get(CPN_AREA_DESC)).setText(skill.getDescription());
	}
	
	/**
	 * 将输入的数据存入列表中的指定位置，并覆盖原始数据
	 * @param index
	 *   指定的列表中的索引
	 * @return
	 *   返回 code<br>
	 *   其中为 0 则成功添加
	 */
	@SuppressWarnings("rawtypes")
	int saveDataToList(int index) {
		short id = Short.parseShort(((JTextField) cpnMap.get(CPN_INPUT_ID)).getText());
		
		int result = checkData(listModel.elementAt(index).skill.getId() != id);
		if (result != 0) {
			System.out.println("errorCode: " + result);
			return result;
		}
		
		Skill skill = new Skill();
		skill.setId(Short.parseShort(((JTextField) cpnMap.get(CPN_INPUT_ID)).getText()));
		skill.setTitle(((JTextField) cpnMap.get(CPN_INPUT_TITLE)).getText());
		skill.setType(EPokemonType.parseEnum(((JComboBox) cpnMap.get(CPN_SELECT_TYPE)).getSelectedIndex()));
		skill.setPower(Short.parseShort(((JTextField) cpnMap.get(CPN_INPUT_POWER)).getText()));
		{
			int selectIndex = ((JComboBox)cpnMap.get(CPN_SELECT_ACCURACY)).getSelectedIndex();
			skill.setAccuracy((selectIndex == 0) ? (byte)0 : (byte)(105 - selectIndex * 5));
		}
		skill.setCategory(ESkillCategory.parseEnum(
				((JComboBox) cpnMap.get(CPN_SELECT_CATEGORY)).getSelectedIndex()));
		skill.setPpMax((byte)(40 - ((JComboBox)cpnMap.get(CPN_SELECT_PP)).getSelectedIndex() * 5));
		skill.setDescription(((JTextArea) cpnMap.get(CPN_AREA_DESC)).getText());
		
		listModel.set(index, new SkillItem(skill));
		
		return 0;
	}
	
	/**
	 * 从存入的列表中，删除指定索引的元素
	 * @param index
	 *   指定的列表中的索引
	 */
	void deleteDataInList(int index) {
		nextId = listModel.elementAt(index).skill.getId();
		listModel.remove(index);
		((JButton) cpnMap.get(CPN_BUTTON_ID)).setText(Integer.valueOf(this.properId()).toString());
	}
	
	private void addList(SkillItem item) {
		listModel.addElement(item);
	}
	
	/**
	 * 保存成 Json 形式的文件
	 * @param file
	 */
	private void saveJsonFile(File file) {
		FileWriter writer = null;
		
		// 数据准备阶段
		JsonBuilder builder = new JsonBuilder(true);
		int size = listModel.getSize();
		for (int i = 0; i < size; i++) {
			builder.addEntry(listModel.get(i).skill, 
					"id", "title", "type", "power", "accuracy", "category", "ppMax", "description");
		}
		System.out.println(builder.getJson());
		
		try {
			writer = new FileWriter(file);
			writer.write(builder.getJson());
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 打开 Json 形式的文件
	 * @param file
	 */
	private void openJsonFile(File file) {
		System.out.println("openJsonFile: " + file.getName());
		
		FileReader reader = null;
		char[] ch = null;
		
		try {
			reader = new FileReader(file);
			ch = new char[(int) file.length()];
			reader.read(ch);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {}
		}
		
		if (ch != null) {
			String str = String.valueOf(ch);
			JsonBuilder builder = new JsonBuilder();
			JsonValue values = builder.parseJson(str);
			
			Iterator<JsonValue> it = values.getArray().iterator();
			for(; it.hasNext(); ) {
				JsonValue value = it.next();
				Skill skill = SkillBuilder.build(value);
				this.addList(new SkillItem(skill));
			}
		}
	}
	
	/**
	 * 检查数值
	 * @return
	 *  0: 通过检查
	 *  1: id 为空
	 *  2: id 非法
	 *  3: id 重复
	 */
	private int checkData(boolean isCheckId) {
		// 检查 ID 的合法性
		if (isCheckId) {
			String idStr = ((JTextField) cpnMap.get(CPN_INPUT_ID)).getText();
			if (idStr.equals("")) {
				return 1;
			} else {
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher isNum = pattern.matcher(idStr);
				if (!isNum.matches()) {
					return 2;
				}
				
				// 检查 ID 的唯一性
				Skill s = new Skill();
				s.setId(Short.parseShort(idStr));
				SkillItem si = new SkillItem(s);
				if (listModel.contains(si)) {
					return 3;
				}
			}
		}

		return 0;
	}
	
	public SkillSetterFrameManager() {
		frame = new JFrame("Skill Setter");
		
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
		new SkillSetterFrameManager();
	}
	
	/**
	 * 关于该设置技能数据的 GUI 窗口中解决菜单事件的监听器
	 * @since v1.0
	 * @author Zdream
	 * @email 18042046922@163.com
	 * @date: 2016年3月16日
	 */
	private class MenuCommandListener implements ActionListener {
		
		SkillSetterFrameManager manager;
		JFrame frame;
		JFileChooser fc = new JFileChooser();
		
		public MenuCommandListener(
				SkillSetterFrameManager manager, JFrame frame) {
			this.manager = manager;
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals(F_New)) {
				
			} else if (cmd.equals(F_SAVE_AS)) {
				int flag = -1;
				fc.setDialogTitle("Save Json File");
				try {
					flag = fc.showSaveDialog(frame);
				} catch (HeadlessException e1) {
					System.out.println("Save File Dialog ERROR!");
				}
				
				if (flag == JFileChooser.APPROVE_OPTION) {
					// 获得你输入要保存的文件
					File file = fc.getSelectedFile();
					// 也可以使用fileName=f.getName();
					manager.saveJsonFile(file);
				}
			} else if (cmd.equals(F_OPEN)) {
				int flag = -1;
				fc.setDialogTitle("Open Json File");
				try {
					fc.setFileFilter(new FileFilter() {
						
						@Override
						public String getDescription() {
							return "Json File";
						}
						
						@Override
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".json");
						}
					});
					flag = fc.showOpenDialog(frame);
				} catch (HeadlessException e1) {
					System.err.println("Open File Dialog ERROR!");
				}
				
				if (flag == JFileChooser.APPROVE_OPTION) {
					// 获得你输入要打开的文件
					File file = fc.getSelectedFile();
					manager.openJsonFile(file);
				}
			} else if (cmd.equals(F_SAVE_DB)) {
				int length = listModel.getSize();
				List<Skill> skills = new ArrayList<Skill>(listModel.getSize());
				for (int i = 0; i < length; i++) {
					skills.add(listModel.elementAt(i).skill);
				}
				service.addAndUpdateAll(skills);
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
		
		SkillSetterFrameManager manager;
		
		public ButtonCommandListener(
				SkillSetterFrameManager manager, JFrame frame) {
			this.manager = manager;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			
			if (cmd.equals(CMD_PROPER_ID)){
				((JTextField) manager.cpnMap.get(CPN_INPUT_ID)).setText(
						((JButton) manager.cpnMap.get(CPN_BUTTON_ID)).getText()
						);
			} else if (cmd.equals(CMD_POWER_NONE)) {
				((JTextField) manager.cpnMap.get(CPN_INPUT_POWER)).setText(Integer.valueOf(0).toString());
			} else if (cmd.equals(CMD_POWER_FLOAT)) {
				((JTextField) manager.cpnMap.get(CPN_INPUT_POWER)).setText(Integer.valueOf(1).toString());
			} else if (cmd.equals(CMD_ACCURACY_FLOAT)) {
				((JComboBox) manager.cpnMap.get(CPN_SELECT_ACCURACY)).setSelectedIndex(0);
			} else if (cmd.equals(CMD_DESCRIPTION_CLEAN)) {
				((JTextArea) manager.cpnMap.get(CPN_AREA_DESC)).setText("");
			} else if (cmd.equals(CMD_RESET)) {
				manager.reset();
			} else if (cmd.equals(CMD_SAVE)) {
				manager.saveDataToList();
			} else {
				System.out.println("?" + cmd);
			}
		}
	}
	
	private class InputNumberKeyListener implements KeyListener {
		
		JTextField field;
		int maxLength;
		
		public InputNumberKeyListener(JTextField field, int maxLength) {
			super();
			this.field = field;
			this.maxLength = maxLength;
		}

		@Override
		public void keyTyped(KeyEvent e) {
			int keyChar = e.getKeyChar();                 
            if (keyChar < KeyEvent.VK_0 || keyChar > KeyEvent.VK_9) {
            	// 添加逻辑：只能输入数字
                e.consume(); // 关键，屏蔽掉非法输入  
            } else {
            	// 长度判断 最多 maxLength
            	if (field.getText().length() >= maxLength) {
            		e.consume();
            	}
            }
		}

		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {}
		
	}
	
	private class ListChooserListener extends MouseAdapter implements ActionListener {
		
		SkillSetterFrameManager manager;
		
		/**
		 * 在列表中选择的索引数
		 */
		int index;
		
		/**
		 * 命令参数（按钮项）
		 */
		static final String CMD_SET = "set",
				CMD_VIEW = "view",
				CMD_DELETE = "delete";
		
		public void addMenuItem(JPopupMenu popupMenu) {
			JMenuItem item;
			
			item = new JMenuItem("View");
			item.setActionCommand(CMD_VIEW);
			item.addActionListener(this);
			popupMenu.add(item);
			item = new JMenuItem("Set");
			item.setActionCommand(CMD_SET);
			item.addActionListener(this);
			popupMenu.add(item);
			item = new JMenuItem("Delete");
			item.setActionCommand(CMD_DELETE);
			item.addActionListener(this);
			popupMenu.add(item);
		}

		public ListChooserListener(
				SkillSetterFrameManager manager, JFrame frame) {
			this.manager = manager;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private JList<SkillItem> getList() {
			return (JList) manager.cpnMap.get(CPN_LIST);
		}
		
		private JPopupMenu getPopupMenu() {
			return (JPopupMenu) manager.cpnMap.get(CPN_POPMENU);
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			index = getList().locationToIndex(e.getPoint());
			System.out.println("choose index: " + index);
			getList().setSelectedIndex(index); // 获取鼠标点击的项
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		// 弹出菜单
		private void maybeShowPopup(MouseEvent e) {
			JList<SkillItem> list = getList();
			if (e.isPopupTrigger() && list.getSelectedIndex() != -1) {

				// 获取选择项的值
				SkillItem selected = list.getModel().getElementAt(list.getSelectedIndex());
				System.out.println(selected.toString());
				getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals(CMD_VIEW)) {
				if (index >= 0) {
					manager.viewDataFromList(index);
				}
			} else if (cmd.equals(CMD_SET)) {
				if (index >= 0) {
					manager.saveDataToList(index);
				}
			} else if (cmd.equals(CMD_DELETE)) {
				if (index >= 0) {
					manager.deleteDataInList(index);
				}
			}
		}
	}
	
	/**
	 * 技能列表数据
	 * @since v1.0
	 * @author Zdream
	 * @email 18042046922@163.com
	 * @date: 2016年3月17日
	 */
	class SkillListModel extends DefaultListModel<SkillItem> {
		private static final long serialVersionUID = -3364699591481860183L;
		
		SkillSetterFrameManager manager;
		JFrame frame;
		
		public SkillListModel(
				SkillSetterFrameManager manager, JFrame frame) {
			super();
			this.manager = manager;
			this.frame = frame;
		}
	}
	
	
}
/**
 * 技能的封装包，支持按编号排列
 * @since v1.0
 * @author Zdream
 * @email 18042046922@163.com
 * @date: 2016年3月17日
 */
class SkillItem implements Comparable<SkillItem> {
	
	Skill skill;

	public SkillItem(Skill skill) {
		super();
		this.skill = skill;
	}

	@Override
	public int compareTo(SkillItem o) {
		return getId() - o.getId();
	}
	
	public short getId() {
		return skill.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((skill == null) ? 0 : skill.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkillItem other = (SkillItem) obj;
		return (this.getId() == other.getId());
	}

	@Override
	public String toString() {
		return getId() + ": " + skill.getTitle();
	}
}
