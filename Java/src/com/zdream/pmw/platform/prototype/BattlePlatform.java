package com.zdream.pmw.platform.prototype;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.order.OrderManager;

/**
 * 战场实例类<br>
 * <br>
 * <b>v0.2</b><br>
 * 	删除单例模式、战场线程，转而设置成单线程操作。<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月31日
 * @version v0.2
 */
public class BattlePlatform implements IPlatformComponent {
	
	/**
	 * 决定停用单例模式
	 */
	public BattlePlatform() {
		super();
		// no for private
	}
	
	@Override
	public BattlePlatform getRoot() {
		return this;
	}
	
	/**
	 * 启动战斗线程
	 */
	public void start() {
		logPrintf(IPrintLevel.PRINT_LEVEL_INFO, "战斗开始");
		int count = 0;
		while (!isFinish()) {
			try {
				orderManager.actNextEvent();
			} catch (Exception e) {
				logPrintf(IPrintLevel.PRINT_LEVEL_ERROR, e);
			}
			count ++;
			if (count > 48) {
				break;
			}
		}
		logPrintf(IPrintLevel.PRINT_LEVEL_INFO, "战斗结束");
	}

	/* ************
	 *	  裁判    *
	 ************ */
	
	/**
	 * 裁判
	 */
	private RuleConductor referee = new RuleConductor();
	
	public RuleConductor getReferee() {
		return referee;
	}

	/* ************
	 *	 Attend   *
	 ************ */
	/**
	 * 战斗人员（怪兽）管理中心
	 */
	private AttendManager attendManager = new AttendManager();

	public AttendManager getAttendManager() {
		return attendManager;
	}

	/* ************
	 *	 Control  *
	 ************ */
	/**
	 * 控制中心
	 */
	private ControlManager controlManager = new ControlManager();
	
	public ControlManager getControlManager() {
		return controlManager;
	}
	
	@Override
	public void logPrintf(int level, String str, Object... params) {
		controlManager.logPrintf(level, str, params);
	}

	@Override
	public void logPrintf(int level, Throwable throwable) {
		controlManager.logPrintf(level, throwable);
	}
	
	/**
	 * 启动战场指令，随后附加传递战场指令<br>
	 * 拦截前消息
	 * @param value
	 */
	public void readyCode(Aperitif value) {
		effectManage.startCode(value);
	}
	
	/**
	 * 战斗结束的标志
	 */
	private boolean finish = false;
	
	public boolean isFinish() {
		return finish;
	}
	
	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	/* ************
	 *	  Order   *
	 ************ */
	/**
	 * 事件触发、顺序中心
	 */
	private OrderManager orderManager = new OrderManager();

	public OrderManager getOrderManager() {
		return orderManager;
	}

	/* ************
	 *	 Effect   *
	 ************ */
	/**
	 * 效果计算中心
	 */
	private EffectManage effectManage = new EffectManage();
	
	public EffectManage getEffectManage() {
		return effectManage;
	}
	
}
