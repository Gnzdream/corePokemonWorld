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
 * 
 * <p><b>v0.2.1</b><br>
 * 将 {@code Gate} 与 {@code BattleBeginMessage} 两个类合并之后, 并命名为 {@code Fuse}</p>
 * 
 * <p><b>v0.2.2</b><br>
 * 增加了系统运行方式的选项,<br>
 * <code>passive = false</code> 设置系统以主动模式运行,<br>
 * <code>passive = true</code> 设置系统以被动模式 (客户端模式) 运行.</p>
 * 
 * @since v0.1
 *   [2016-03-30]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-09]
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
	 *	  参数    *
	 ************ */
	
	/*
	 * v0.2.2
	 * 设置该生成的战斗平台是主动计算结果 (服务端) 还是被动监听消息 (客户端)
	 */
	boolean passive;
	
	/*
	 * v0.2.2
	 * 需要在建立战场环境前传递的数据
	 * 这些数据会在战场成功建立之后删除
	 */
	Map<String, Object> deliver;
	
	/**
	 * <p>设置生成战斗平台的方式</p>
	 * <p>如果为 {@code true}, 该战斗平台将从您指定的接收源接收效果, 而不是自己计算结果.
	 * 这类效果如同服务 - 客户端模型中的客户端;<br>
	 * 如果为 {@code false}, 该战斗平台将自己计算结果,
	 * 如果有其它的客户端接入的话, 会发送消息给它们.
	 * 这类效果如同服务 - 客户端模型中的服务端;</p>
	 * @param passive
	 *   战斗平台主被动方式
	 * @since v0.2.2
	 */
	public void setPassive(boolean passive) {
		this.passive = passive;
	}
	
	/**
	 * 获得当前战斗平台是否是被动的. 默认为 {@code false}.
	 * @return
	 *   战斗平台主被动方式
	 * @since v0.2.2
	 */
	public boolean isPassive() {
		return passive;
	}
	
	public void putDeliver(String key, Object value) {
		deliver.put(key, value);
	}
	
	public Object getDeliver(String key) {
		return deliver.get(key);
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
		deliver = new HashMap<>();
		
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
		
		deliver = null;
		
		return pf;
	}
	
}
