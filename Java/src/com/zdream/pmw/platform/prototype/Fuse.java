package com.zdream.pmw.platform.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zdream.pmw.monster.prototype.Pokemon;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.control.ControlManager;
import com.zdream.pmw.trainer.prototype.Trainer;

/**
 * 战场的入口, 翻译名为导火线<br>
 * 用于获取请求触发战斗的数据提交、管理，以及启动战斗系统<br>
 * <br>
 * <b>v0.2</b><br>
 * 	 删除单例模式、战场线程，转而设置成单线程操作<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>将 {@code Gate} 与 {@code BattleBeginMessage} 两个类合并之后, 并命名为 {@code Fuse}</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月30日
 * @version v0.2.1
 */
public class Fuse implements IBattleRule {
	/*
	 * 接受参数：
	 * 1. 精灵的组队情况，和精灵的数据
	 * 2. 对战的规则
	 * 3. 双方训练师实例（野怪为空）
	 * 4. 附加参数（如：初始天气、初始场地）
	 * 
	 * 如果是打野的话，仅需要提交己方训练师和精灵状况即可
	 * 但是需要在附加参数中指明战斗的位置，这样可以引导系统选择对方的怪兽
	 */
	
	/**
	 * 停用单例模式
	 */
	public Fuse(int rule) {
		super();
		this.rule = rule;
	}
	
	/* ************
	 *	  规则    *
	 ************ */
	/**
	 * 对战的规则
	 */
	private int rule;

	public int getRule() {
		return rule;
	}

	public void setRule(int rule) {
		this.rule = rule;
	}
	
	/**
	 * 战斗附加参数
	 */
	private Map<String, String> condition = new HashMap<String, String>();
	
	/**
	 * 添加战斗开始的附加参数
	 * @param key
	 *   附加参数 key
	 * @param param
	 *   附加参数值
	 */
	public void putCondition(String key, String param) {
		condition.put(key, param);
	}
	
	public Map<String, String> getCondition() {
		return condition;
	}

	/* ************
	 *	 参与者   *
	 ************ */
	/**
	 * 队伍训练师以及作战精灵储存列表
	 */
	private List<Team> teams = new ArrayList<Team>();
	/**
	 * 指请求回调接口列表
	 */
	private List<IRequestCallback> callbacks = new ArrayList<>();
	/**
	 * 指消息回调接口列表
	 */
	private List<IMessageCallback[]> msgbacks = new ArrayList<>();
	
	/**
	 * 在队伍列表中添加队伍, 这里为非玩家控制的队伍添加
	 * @param trainer
	 *   指代的训练师实例<br>
	 *   如果遇到没有训练师实例的时候，比如在野外遇到野生精灵，那野生精灵方训练师可以为空
	 * @param pms
	 *   出场的精灵的队伍
	 */
	public void putTeams(Trainer trainer, Pokemon[] pms) {
		putTeams(trainer, pms, null);
	}
	
	/**
	 * 在队伍列表中添加队伍, 和对应的回调接口, 这里为玩家控制的队伍添加
	 * @param trainer
	 *   指代的训练师实例<br>
	 *   如果遇到没有训练师实例的时候，比如在野外遇到野生精灵，那野生精灵方训练师可以为空
	 * @param pms
	 *   出场的精灵的队伍
	 * @param callback
	 *   请求回调接口
	 */
	public void putTeams(Trainer trainer, Pokemon[] pms, IRequestCallback callback) {
		putTeams(trainer, pms, callback, null);
	}

	/**
	 * 在队伍列表中添加队伍, 和对应的回调接口, 这里为玩家控制的队伍添加
	 * @param trainer
	 *   指代的训练师实例<br>
	 *   如果遇到没有训练师实例的时候，比如在野外遇到野生精灵，那野生精灵方训练师可以为空
	 * @param pms
	 *   出场的精灵的队伍
	 * @param callback
	 *   请求回调接口
	 * @param msgbacks
	 *   消息回调接口, 列表
	 */
	public void putTeams(
			Trainer trainer,
			Pokemon[] pms,
			IRequestCallback callback,
			IMessageCallback[] msgbacks) {
		callbacks.add(callback);
		this.msgbacks.add(msgbacks);
		
		/*
		 * pms 数组中是否有空元素的标志
		 */
		boolean isEmpty = false;
		for (int i = 0; i < pms.length; i++) {
			if (pms[i] == null) {
				isEmpty = true;
				break;
			}
		}
		
		if (isEmpty) {
			int count = 0;
			for (int i = 0; i < pms.length; i++) {
				if (pms[i] != null) {
					count ++;
				}
			}
			
			Pokemon[] newpms = new Pokemon[count];
			int index = 0;
			for (int i = 0; i < pms.length; i++) {
				if (pms[i] != null) {
					newpms[index ++] = pms[i];
				}
			}
			teams.add(new Team(trainer, newpms));
			
		} else {
			teams.add(new Team(trainer, pms));
		}
	}

	public List<Team> getTeams() {
		return teams;
	}
	
	public List<IRequestCallback> getCallbacks() {
		return callbacks;
	}
	
	public List<IMessageCallback[]> getMessagebacks() {
		return msgbacks;
	}
	
	/* ************
	 *	启动战斗  *
	 ************ */
	
	/**
	 * 初始化所有与战斗相关的数据结构
	 * @param msg
	 * @return 
	 * 	BattlePlatform 的实例, since v0.2
	 */
	public BattlePlatform initPlatform() {
		BattlePlatform pf = new BattlePlatform();
		RuleConductor referee = pf.getReferee();
		referee.setRule(getRule());
		
		ControlManager cm = pf.getControlManager();
		cm.setDefaultDebugWriter();
		cm.setDefaultInfoOut();
		cm.init(this, referee, pf);
		
		AttendManager am = pf.getAttendManager();
		am.init(this, referee, pf);
		
		pf.getOrderManager().init(this, referee, pf);
		
		pf.getEffectManage().init(this, referee, pf);
		
		return pf;
	}
	
}
