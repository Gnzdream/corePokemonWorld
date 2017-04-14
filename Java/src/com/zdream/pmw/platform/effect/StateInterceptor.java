package com.zdream.pmw.platform.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.IStateContainer;
import com.zdream.pmw.platform.attend.IStateInterceptable;
import com.zdream.pmw.platform.attend.Participant;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 状态拦截器<br>
 * <br>
 * v0.1.1 抽离 IStateInterceptable 接口<br>
 * <b>v0.2</b><br>
 *   补充战斗平台的环境属性<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>利用 {@link Aperitif} 的新特征, 允许拦截器跳过某些状态的触发</p>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.2.1
 */
public class StateInterceptor implements IStateInterceptable {
	
	/**
	 * 指向战斗平台（环境）/ 包可见性
	 * @since 0.2
	 */
	BattlePlatform pf;

	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * 即将发动的状态列表
	 */
	private List<IState> states = new ArrayList<IState>();
	
	/**
	 * 消息
	 */
	private Aperitif value;
	
	/**
	 * 消息头
	 */
	private String head;
	
	/**
	 * 迭代器
	 */
	private Iterator<IState> iterator;
	
	/* ************
	 *	  拦截    *
	 ************ */
	
	/**
	 * 启动拦截
	 * @return
	 *   返回实现消息（字符串）
	 */
	public String intercept() {
		/*
		 * 1.  建立 states 列表
		 * 2.  states 列表排序
		 * 3.  拦截
		 * TODO
		 */
		buildStates();
		sortStates();
		iterator = states.iterator();
		return nextState();
	}
	
	/**
	 * 建立 states 列表<br>
	 * 从精灵、座位、队伍、阵营、全场中寻找能够触发 head 的状态<br>
	 * 将其放入 states 列表中
	 */
	private void buildStates() {
		AttendManager am = pf.getAttendManager();
		
		byte[] seats = value.getScanSeats();
		
		// 精灵部分
		for (int i = 0; i < seats.length; i++) {
			Participant p = am.getParticipant(seats[i]);
			if (p == null) {
				continue;
			}
			pushToStateList(p, states);
		}
			
		// 座位部分
		for (int i = 0; i < seats.length; i++) {
			pushToStateList(am.getSeatStates(seats[i]), states);
		}
		
		// 阵营部分
		byte[] camps = new byte[seats.length];
		int index = 0; // camps 的索引
		for (int i = 0; i < seats.length; i++) {
			byte camp = am.campForSeat(seats[i]);
			
			// camp 是否已经查过了？
			boolean exist = false;
			for (int j = 0; j < index; j++) {
				if (camps[j] == camp) {
					exist = true;
					break;
				}
			}
			
			if (exist) {
				continue;
			}
			
			pushToStateList(am.getCampStates(camp), states);
			camps[index ++] = camp;
		}
		
		pushToStateList(am.getPlatformStates(), states);
	}
	
	/**
	 * 将 handler 中存储的状态数据进行扫描<br>
	 * 当出现可以运行 head 类型消息的状态时<br>
	 * 把该状态添加到列表 states 中<br>
	 * @param handler
	 * @param states
	 */
	private void pushToStateList(IStateContainer handler, List<IState> states) {
		List<IState> list = handler.getStates();
		if (list.isEmpty()) {
			return;
		}
		
		for (Iterator<IState> it = list.iterator(); it.hasNext();) {
			IState state = it.next();
			if (state.canExecute(head)) {
				states.add(state);
			}
		}
	}
	
	/**
	 * states 列表排序
	 */
	private void sortStates() {
		if (states.size() == 0) {
			return;
		}
		// states 列表排序
		states.sort((a, b) -> b.priority(head) - a.priority(head));
	}
	
	@Override
	public String nextState() {
		if (iterator.hasNext()) {
			IState state = iterator.next();
			if (value.hasFilter(state.name())) {
				pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
						"StateInterceptor.nextState(): 状态为 %s 被跳过", state.toString());
				return nextState();
			} else {
				pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
						"StateInterceptor.nextState(): 下一个需要过滤的状态为 %s", state.toString());
				try {
					return state.execute(value, this, pf);
				} catch (Exception e) {
					pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
							"StateInterceptor.nextState(): 过滤状态 %s 时发生错误, 数据: %s",
							state.toString(), value);
					pf.logPrintf(IPrintLevel.PRINT_LEVEL_ERROR, e);
					return nextState();
				}
			}
		} else {
			return commandLine();
		}
	}
	
	@Override
	public String getCommand() {
		return commandLine();
	}
	
	/**
	 * 返回可以执行的命令行数据<br>
	 * @return
	 *   命令行数据<br>
	 *   可以发给 CM 模块，以此来操作 AM 模块的数据
	 */
	private String commandLine() {
		return pf.getControlManager().commandLine(value);
	}
	
	/* ************
	 *	通用方法  *
	 ************ */
	/**
	 * 重置结构
	 */
	public void reset() {
		states.clear();
	}
	
	/**
	 * 重建结构
	 * @param value
	 */
	public void rebuild(Aperitif value) {
		this.reset();
		this.value = value;
		head = value.getHead();
	}

}
