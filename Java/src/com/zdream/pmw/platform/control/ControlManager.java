package com.zdream.pmw.platform.control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IPlatformComponent;
import com.zdream.pmw.platform.prototype.RuleConductor;

/**
 * 控制中心<br>
 * 从属于 BattlePlatform 的一个模块，作为每一个队伍 控制的源头<br>
 * 并能够对系统外部输出必要的消息<br>
 * 包括 debug, print, 控制信息<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加 BattlePlatform 的变量引用<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月31日
 * @version v0.2
 */
public class ControlManager implements IPlatformComponent {
	
	/**
	 * 指向战斗平台（环境）
	 * @since 0.2
	 */
	BattlePlatform pf;
	
	/* ************
	 *	系统输出  *
	 ************ */
	/**
	 * 详细信息输出流（字符）<br>
	 * 一般为 DEBUG 信息<br>
	 * 该流可以不设置，默认的方式是输出到某个日志文件
	 */
	private Writer logWriter;
	
	/**
	 * 一般提示信息输出流<br>
	 * 一般为 INFO 信息，即提示现在情况的输出流<br>
	 * 该流必须设置，默认的方式是输出到控制台
	 */
	private PrintStream infoOut;

	public Writer getLogWriter() {
		return logWriter;
	}

	public void setLogWriter(Writer debugWriter) {
		this.logWriter = debugWriter;
	}

	public PrintStream getInfoOut() {
		return infoOut;
	}

	public void setInfoOut(PrintStream infoOut) {
		this.infoOut = infoOut;
	}
	
	@Override
	public void logPrintf(int level, String str, Object... params) {
		debugPrint(String.format(str, params), level);
	}
	
	@Override
	public void logPrintf(int level, Throwable throwable) {
		StackTraceElement[] stack = throwable.getStackTrace();
		StringBuilder builder = new StringBuilder(stack.length * 50);
		builder.append(throwable).append('\n');
		for (StackTraceElement element : stack) {
			builder.append("\tat ").append(element).append('\n');
		}
		debugPrint(builder.toString(), level);
	}
	
	/**
	 * 设置默认的详细信息输出流<br>
	 * 输出将产生 logs 文件夹下的 .log 日志文件
	 */
	public void setDefaultDebugWriter() {
		String timeStr = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		File file = null;
		for (int i = 1; ; i++) {
			file = new File(String.format("logs/%s-%d.log", timeStr, i));
			if (!file.exists()) {
				break;
			}
		}
		
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		try {
			logWriter = new FileWriter(file);
			debugPrint("日志输出流重置", PRINT_LEVEL_INFO);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置默认的提示信息输出流<br>
	 * 输出到控制台
	 */
	public void setDefaultInfoOut() {
		infoOut = System.out;
		debugPrint("消息输出流重置", PRINT_LEVEL_INFO);
	}
	
	/**
	 * 将信息输出到 DEBUG 信息流中
	 * @param str
	 *   要输出的数据
	 * @param level
	 *   输出等级<br>
	 *   该等级定义在 IPrintLevel 中
	 */
	public void debugPrint(String str, int level) {
		String timeStr = new SimpleDateFormat("[HH:mm:ss]").format(new java.util.Date());
		String levelStr = null;
		switch (level) {
		case PRINT_LEVEL_ERROR:
			levelStr = "ERROR";
			break;
		case PRINT_LEVEL_WARN:
			levelStr = "WARN";
			break;
		case PRINT_LEVEL_INFO:
			levelStr = "INFO";
			break;
		case PRINT_LEVEL_DEBUG:
			levelStr = "DEBUG";
			break;
		case PRINT_LEVEL_VERBOSE: default:
			levelStr = "VERBOSE";
			break;
		}
		try {
			logWriter.write(String.format("%s [%s]: %s\n", timeStr, levelStr, str));
			logWriter.flush();
		} catch (IOException e) {
			System.err.println("输出日志失败");
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			logWriter.flush();
		} catch (Exception e) {} finally {
			logWriter.close();
		}
		
		if (infoOut != System.out){
			try {
				infoOut.close();
			} catch (Exception e) {}
		}
		super.finalize();
	}
	
	/* ************
	 *	调用结构  *
	 ************ */
	
	/**
	 * 指令注册表
	 */
	private Map<String, ICodeRealizer> realizers = new HashMap<String, ICodeRealizer>();
	
	/**
	 * 返回 ICodeRealizer
	 * @param key
	 * @return
	 */
	public ICodeRealizer getRealizer(String key) {
		return realizers.get(key);
	}
	
	/* ************
	 *	 控制体   *
	 ************ */
	/**
	 * 所有队伍的控制体组成的列表<br>
	 * 该数组的索引为 team 号码
	 */
	private ControlBase[] ctrls;
	
	/**
	 * 玩家所指代的队伍号码
	 */
	private byte playerTeam = -1;
	
	/**
	 * 得到对应队伍的控制体
	 * @param team
	 *   队伍号码
	 * @return
	 */
	public ControlBase getCtrl(byte team) {
		return ctrls[team];
	}
	public byte getPlayerTeam() {
		return playerTeam;
	}
	public int teamLength() {
		return ctrls.length;
	}
	private void provideMessageForEachTeam(String msg) {
		for (int i = 0; i < ctrls.length; i++) {
			ctrls[i].provide(msg);
		}
	}
	
	private ControlFactory factory;
	public ControlFactory getFactory() {
		return factory;
	}
	
	/* ************
	 *	指令中心  *
	 ************ */
	
	private IRequestSemaphore semaphore;
	
	public IRequestSemaphore getSemaphore() {
		return semaphore;
	}
	
	/**
	 * 行动请求
	 * @param team
	 *   选择向哪个队伍的控制体发送消息
	 * @param seats
	 *   该队伍需要控制的座位列表
	 */
	public void requestMove(byte team, byte[] seats) {
		semaphore.requestMove(team, seats);
	}
	
	/**
	 * 怪兽选择怪兽或入场请求
	 * @param team
	 *   队伍号
	 * @param seats
	 *   选择怪兽需要入场的位置
	 */
	public void requestEnterance(byte team, byte[] seats) {
		semaphore.requestEnterance(team, seats);
	}

	public void requestEnd(byte successCamp) {
		semaphore.requestEnd(successCamp);
	}

	public void onWaitForResponse() {
		semaphore.onWaitForResponse();
	}

	public void onCommitResponse() {
		semaphore.onCommitResponse();
	}

	/**
	 * 翻译从内部发回来的 code，指导战场的行动<br>
	 * 实现消息
	 * @param code
	 */
	public void outCode(String code) {
		String[] codes = code.split(" ");
		ICodeRealizer cr = realizers.get(codes[0]);
		if (cr != null) {
			provideMessageForEachTeam(code);
			cr.realize(codes, pf);
		} else {
			debugPrint(String.format("指令 %s 没有能够找到对应的实现者 ICodeRealizer 实现", codes[0]), 
					PRINT_LEVEL_ERROR);
		}
	}
	
	/**
	 * 注册实现者
	 * @param codes
	 * @param realizer
	 */
	private void registerRealizer(String[] codes, ICodeRealizer realizer) {
		for (int i = 0; i < codes.length; i++) {
			this.realizers.put(codes[i], realizer);
		}
	}

	/* ************
	 *	 初始化   *
	 ************ */
	/**
	 * 初始化 <code>ControlManager</code><br>
	 * 该函数为 <code>ControlManager</code> 的初始化入口函数<br>
	 * 它将完成：<br>
	 * <p>1. 控制体列表</p>
	 * @param msg
	 *   战斗信息开始消息
	 * @param referee
	 *   裁判
	 */
	public void init(Fuse msg, RuleConductor referee, BattlePlatform pf) {
		this.pf = pf;
		initControls(msg, referee);
		initRealizer(msg);
	}
	
	/**
	 * 初始化全部控制体<br>
	 * @param msg
	 * @param referee
	 */
	private void initControls(Fuse msg, RuleConductor referee) {
		
		factory = new ControlFactory(this);
		factory.setReferee(referee);
		factory.setBeginMessage(msg);
		playerTeam = factory.getPlayerTeam();
		ctrls = factory.createControls();
		
		semaphore = factory.createSemaphore();
		
	}
	
	/**
	 * 完成所有技能所要求的 <code>ICodeRealizer</code> 的注册<br>
	 * @param msg
	 */
	private void initRealizer(Fuse msg) {
		Properties pro = new Properties();
		try {
			pro.load(this.getClass().getResourceAsStream("/com/zdream/pmw/platform/control/code/realizer.properties"));
			String[] classes = pro.getProperty("realizer").split(",");
			
			for (int i = 0; i < classes.length; i++) {
				String classpath = "com.zdream.pmw.platform.control.code." + classes[i];
				try {
					Class<?> clazz = Class.forName(classpath);
					Object obj = clazz.newInstance();
					if (obj instanceof ICodeRealizer) {
						ICodeRealizer r = (ICodeRealizer) obj;
						registerRealizer(r.codes(), r);
					}
					
				} catch (ClassNotFoundException e) {
					debugPrint("cm.initRealizer(1) | classpath:" + classpath + " 加载失败: 类不存在", 
							PRINT_LEVEL_ERROR);
				} catch (InstantiationException e) {
					debugPrint("cm.initRealizer(1) | classpath:" + classpath + " 加载失败: 无法实例化", 
							PRINT_LEVEL_ERROR);
				} catch (IllegalAccessException e) {
					debugPrint("cm.initRealizer(1) | classpath:" + classpath + " 加载失败: 构造器无法抵达", 
							PRINT_LEVEL_ERROR);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			debugPrint("cm.initRealizer(1) 系统 CodeRealizer 加载失败", 
					PRINT_LEVEL_ERROR);
			System.exit(1);
		}
		
		// 注册其它 CodeRealizer 请在 realizer.properties 中写入
		debugPrint("cm.initRealizer(1) 其它 CodeRealizer 注册还未实现", 
				PRINT_LEVEL_WARN); // TODO 其它 CodeRealizer 注册还未实现
	}
	
	@Override
	public BattlePlatform getRoot() {
		return pf;
	}
	
}
