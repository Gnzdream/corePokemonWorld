package com.zdream.pmw.platform.effect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.attend.StateHandler;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IPlatformComponent;
import com.zdream.pmw.platform.prototype.RuleConductor;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 战斗结果、效果处理中心，行动数据处理模块<br>
 * 从属于 <code>BattlePlatform</code> 的一个模块，作为每一次行动的结果记录者<br>
 * <code>EffectValueRecorder</code> 作为一个数据包供 EffectManage 处理<br>
 * 
 * <p>当整个系统运行主动模式时, 系统将自己计算伤害、附加效果等数据, 此时
 * {@code EffectManage} 为处理中心;<br>
 * 当系统运行被动模式时, 系统的处理方式更像一个监听器, 监听远端服务器的数据结果,
 * 因此这时的计算伤害、附加效果等数据是在对应的服务器端, 而本地的
 * {@code EffectManage} 将不进行计算.</p>
 * 
 * <b>v0.1</b><br>
 *   修改了部分注释<br>
 * <br>
 * <b>v0.1.2</b><br>
 *   将状态拦截器设置成非属性的方式，作为一个局域变量，因而支持拦截中含拦截<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加 BattlePlatform 的变量引用<br>
 * 
 * <p><b>v0.2.1</b><br>
 * 添加计算怪兽实时能力值的方法. 该方法原本在 {@code AttendManage} 中实现<p>
 * 
 * <p><b>v0.2.2</b><br>
 * 在运行被动模式 (客户端模式) 时, {@code EffectManage} 将不会初始化
 * 任何与计算相关的对象.<p>
 * 
 * @since v0.1
 *   [2016-04-19]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-09]
 */
public class EffectManage implements IPlatformComponent {
	
	/* ************
	 *	调用结构  *
	 ************ */
	
	/**
	 * 服务
	 */
	FormulaLoader formualLoader;
	
	/**
	 * 技能发动的释放计算平台
	 */
	private SkillReleaseBox box;
	
	/**
	 * 行动事件计算和处理平台
	 */
	private ActBox act;
	
	/**
	 * 目标计算
	 */
	private TargetCounter target;
	
	/**
	 * 状态生成器
	 */
	private StateBuilder stateBuilder;
	
	/**
	 * 获得默认的攻击目标<br>
	 * 得到的默认目标指技能能够波及的范围，无论该座位上有没有精灵<br>
	 * @param seat
	 *   技能发动精灵的 seat
	 * @param skillID
	 * @return
	 */
	public byte[] defaultTargets(byte seat, SkillRelease skill) {
		return target.defaultTargets(seat, skill);
	}

	public int hashCode() {
		return target.hashCode();
	}

	public boolean equals(Object obj) {
		return target.equals(obj);
	}

	public String toString() {
		return target.toString();
	}

	@SuppressWarnings("deprecation")
	public byte[] defaultSkillRange(byte seat, short skillID) {
		return formualLoader.defaultSkillRange(seat, skillID);
	}

	/**
	 * 技能行动释放
	 * @param no
	 *   攻击方的 no
	 * @param skillNum
	 *   选择的技能号码（索引）
	 * @param originTarget
	 *   原始目标数据<br>
	 * 例如训练师下达命令中，选择的对象，可能为 -1
	 */
	public void moveAct(byte no, int skillNum, byte originTarget) {
		box.moveAct(no, skillNum, originTarget);
	}
	
	/**
	 * 技能行动释放, 并加入选项参数, 来修改该技能的计算方式
	 * @see SkillReleaseBox#moveAct(byte, byte, byte, JsonValue)
	 */
	public void moveAct(byte no, int skillNum, byte originTarget, JsonObject param) {
		box.moveAct(no, skillNum, originTarget, param);
	}

	/**
	 * 怪兽交换
	 * @param noOut
	 *   下场的怪兽, 必须是有效的参数
	 * @param noIn
	 *   上场的怪兽<br>
	 *   满足以下情况时, 该参数可以为 -1:<br>
	 *   <li>不确定上场怪兽, 需要让 <b>玩家或 AI</b> 进行判断由哪只怪兽上场时</li>
	 *   因此如果类似吼叫技能等，让怪兽上场不是玩家决定的, 在这里必须确定上场怪兽<br>
	 */
	public void exchangeAct(byte noOut, byte noIn) {
		act.exchangeAct(noOut, noIn);
	}
	
	/**
	 * 怪兽上场
	 * @param seat
	 *   座位号
	 * @param noIn
	 *   上场的怪兽, 必须是有效的参数
	 * @since v0.2.1
	 */
	public void enteranceAct(byte seat, byte noIn) {
		act.enteranceAct(seat, noIn);
	}

	/*
	public boolean judgeMoveable(SkillReleasePackage pack) {
		return box.judgeMoveable(pack);
	}

	/**
	 * 计算攻击方实时命中率（忽略能力变化）
	 * @return v0.2.1
	 *   当返回数值为负数 (-1) 则为必中
	 *
	public float calcHitrate(SkillReleasePackage pack) {
		return box.calcHitrate(pack);
	}

	/**
	 * 计算防御方实时躲避率（忽略能力变化）
	 * @return v0.2.1
	 *   当返回数值为负数 (-1) 则为必不中
	 *
	public float calcHide(SkillReleasePackage pack) {
		return box.calcHide(pack);
	}
	
	public float calcAccuracy(SkillReleasePackage pack) {
		return box.calcAccuracy(pack);
	}

	/**
	 * 判定释放技能的属性<br>
	 * @return
	 *
	public EPokemonType calcSkillType(SkillReleasePackage pack) {
		return box.calcSkillType(pack);
	}

	/**
	 * 判定克制倍率, 返回对于敌方每个属性, 我方的技能属性的克制倍率
	 * @param pack
	 * @return
	 *  数组的大小 = 对应敌方怪兽的属性多少
	 *
	public float[] calcTypeRate(SkillReleasePackage pack) {
		return box.calcTypeRate(pack);
	}
	
	/**
	 * 计算会心一击等级
	 * @param pack
	 * @return
	 *   当数值大于等于零, 表示会心一击等级;<br>
	 *   当数值为 -2, 表示绝对回避会心;<br>
	 *   当数值为 -1, 表示绝对会心;<br>
	 *
	public byte calcCt(SkillReleasePackage pack) {
		return box.calcCt(pack);
	}

	/**
	 * 计算技能威力
	 * @param pack
	 * @return
	 *
	public int calcPower(SkillReleasePackage pack) {
		return box.calcPower(pack);
	}
	
	/**
	 * 计算最后的伤害修正 (状态等修正)<br>
	 * 这里不包括乱数修正等<br>
	 * @param index
	 *
	public float calcCorrect(SkillReleasePackage pack) {
		return box.calcCorrect(pack);
	}
	
	/**
	 * 计算最后伤害
	 * @param pack
	 * @return
	 *
	public int calcDamage(SkillReleasePackage pack) {
		return box.calcDamage(pack);
	}*/
	
	/**
	 * 获得精灵的实时能力<br>
	 * 能力包括攻击、防御、特攻、特防、速度、命中、躲避 7 项<br>
	 * 其中命中、躲避 2 项不允许使用<br>
	 * @param seat
	 *   指定的精灵的 seat
	 * @param item
	 *   需要查看精灵的能力项<br>
	 *   能力项在 <code>IPokemonDataType</code> 中定义
	 * @return
	 *   实时的能力值<br>
	 */
	public float calcAbility(byte seat, int item) {
		return box.calcAbility(seat, item);
	}

	/**
	 * 获得精灵的实时能力，无视部分能力等级的计算<br>
	 * 能力包括攻击、防御、特攻、特防、速度 5 项<br>
	 * 命中、躲避 2 项不允许使用<br>
	 * @param seat
	 *   指定的精灵的 seat
	 * @param item
	 *   需要查看精灵的能力项<br>
	 *   能力项在 <code>IPokemonDataType</code> 中定义
	 * @param ignore
	 *   忽略的能力等级<br>
	 *   当参数为 0 时，不忽略任何能力等级，其做法与 realTimeAbility(2) 相同<br>
	 *   当参数为 1 时，忽略提高的能力等级，当该精灵能力等级大于 0 时忽略能力等级计算<br>
	 *   当参数为 2 时，忽略降低的能力等级，当该精灵能力等级小于 0 时忽略能力等级计算<br>
	 * @return
	 *   实时的能力值<br>
	 */
	public float calcAbility(byte seat, int item, int ignore) {
		return box.calcAbility(seat, item, ignore);
	}
	
	/**
	 * 获得精灵的实时能力，无视部分能力等级的计算<br>
	 * 能力包括攻击、防御、特攻、特防、速度 5 项<br>
	 * 命中、躲避 2 项不允许使用<br>
	 * @param seat
	 *   指定的精灵的 seat
	 * @param item
	 *   需要查看精灵的能力项<br>
	 *   能力项在 <code>IPokemonDataType</code> 中定义
	 * @param ignore
	 *   忽略的能力等级<br>
	 *   当参数为 0 时，不忽略任何能力等级，其做法与 realTimeAbility(2) 相同<br>
	 *   当参数为 1 时，忽略提高的能力等级，当该精灵能力等级大于 0 时忽略能力等级计算<br>
	 *   当参数为 2 时，忽略降低的能力等级，当该精灵能力等级小于 0 时忽略能力等级计算<br>
	 *   当参数为 3 时，忽略所有能力等级变化<br>
	 * @param alter
	 *   是否添加其它状态等造成的影响, 默认为 true<br>
	 *   比如在混乱状态时, 是不计算装备、其它状态对能力值的影响的<br>
	 * @return
	 *   实时的能力值<br>
	 */
	public float calcAbility(byte seat, int item, int ignore, boolean alter) {
		return box.calcAbility(seat, item, ignore, alter);
	}

	/**
	 * 施加状态 (技能施加)
	 * @param atseat
	 *   攻击方
	 * @param dfseat
	 *   防御方, 就是确定要施加状态的一方
	 * @param abnormal
	 *   #{@link com.zdream.pmw.core.tools.AbnormalMethods#toBytes(EPokemonAbnormal, int)}
	 */
	public void forceAbnormal(byte atseat, byte dfseat, byte abnormal) {
		act.forceAbnormal(atseat, dfseat, abnormal);
	}
	
	/**
	 * 施加状态 (系统施加)
	 * @param pack
	 * @param abnormal
	 *   #{@link com.zdream.pmw.core.tools.AbnormalMethods#toBytes(EPokemonAbnormal, int)}
	 */
	public void forceAbnormal(byte seat, byte abnormal) {
		act.forceAbnormal(seat, abnormal);
	}

	/**
	 * 获得当前进行攻击判定的数据包
	 * @return
	 * @since v0.2.1
	 */
	@Deprecated
	public SkillReleasePackage getCurrentPackage() {
		return box.getCurrentPackage();
	}

	/**
	 * <p>发送施加状态拦截前消息, 控制层</p>
	 * <p>注: 原本有将 {@code SkillReleasePackage} 作为参数的重载方法已经被弃用.</p>
	 * @see StateBuilder#sendForceStateMessage(String, byte, byte, short, Map)
	 * @since v0.2.2
	 */
	public void sendForceStateMessage(String stateName,
			byte atseat,
			byte dfseat,
			short skillID,
			JsonObject param) {
		stateBuilder.sendForceStateMessage(stateName, atseat, dfseat, skillID, param);
	}

	/**
	 * 获得施加状态的命令行类型消息<br>
	 * 调用方是 {@code com.zdream.pmw.platform.control.codeStateCodeRealizer}
	 * @param value
	 * @return
	 * @since v0.2.1
	 */
	public String forceStateCommandLine(Aperitif value) {
		return stateBuilder.forceStateCommandLine(value);
	}

	/**
	 * 发送清除状态的拦截前消息<br>
	 * <p>清除指定怪兽的状态</p>
	 * @param stateName
	 *   状态名
	 * @param no
	 *   指定怪兽的 no 号
	 * @since v0.2.1
	 */
	public void sendRemoveParticipantStateMessage(final String stateName, final byte no) {
		stateBuilder.sendRemoveParticipantStateMessage(stateName, no);
	}
	
	/**
	 * @see StateBuilder#sendRemoveSeatStateMessage(String, byte)
	 * @since v0.2.2
	 */
	public void sendRemoveSeatStateMessage(final String stateName, final byte seat) {
		stateBuilder.sendRemoveSeatStateMessage(stateName, seat);
	}
	
	/* ************
	 *	 拦截器   *
	 ************ */

	/**
	 * 获得新的开胃酒 (拦截前) 消息
	 * @param head
	 *   消息头
	 * @param scanSeats
	 *   需要扫描对应谁的座位, 用这些座位相关的状态来组成拦截器栈
	 * @return
	 * @version v0.2.1
	 */
	public Aperitif newAperitif(String head, byte... scanSeats) {
		Aperitif ap = new Aperitif();
		ap.restore(head, scanSeats);
		return ap;
	}

	/**
	 * 翻译启动信息 code<br>
	 * 开胃酒 (拦截前) 消息
	 * @return
	 */
	public void startCode(Aperitif value) {
		StateInterceptor interceptor = new StateInterceptor();
		interceptor.pf = pf;
		interceptor.rebuild(value);
		
		String cmd = interceptor.intercept();
		logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
				"em.startCode(1) 拦截前消息: %s", value.toString());
		
		if (cmd != null) {
			logPrintf(IPrintLevel.PRINT_LEVEL_INFO,
					"em.startCode(1) 执行: %s", cmd);
			getRoot().getControlManager().outCode(cmd);
		}
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	Properties conf;
	
	private void loadProperties() {
		conf = new Properties();
		InputStream in = getClass().getResourceAsStream("/com/zdream/pmw/platform/effect/conf.properties");
		try {
			conf.load(in);
            in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得配置项数据
	 * @param key
	 * @return
	 * @since v0.2.3
	 */
	public String getConfig(String key) {
		return conf.getProperty(key);
	}
	
	/**
	 * 获得配置项数据
	 * @param key
	 * @param def
	 *   默认值
	 * @return
	 * @since v0.2.3
	 */
	public String getConfig(String key, String def) {
		return conf.getProperty(key, def);
	}
	
	/**
	 * 指向战斗平台（环境）
	 * @since 0.2
	 */
	BattlePlatform pf;
	
	/**
	 * 初始化 <code>EffectManage</code><br>
	 * @param msg
	 *   战斗信息开始消息
	 * @param referee
	 *   裁判
	 * @param pf
	 */
	public void init(Fuse msg, RuleConductor referee, BattlePlatform pf) {
		this.pf = pf;
		
		if (!msg.isPassive()) {
			box = new SkillReleaseBox(this);
			target = new TargetCounter(this);
			formualLoader = new FormulaLoader(this);
			stateBuilder = new StateBuilder(this, (StateHandler) msg.getDeliver("stateHandler"));
			act = new ActBox(this);
			
			loadProperties();
		}
	}
	
	@Override
	public BattlePlatform getRoot() {
		return pf;
	}
	
	public AttendManager getAttends() {
		return pf.getAttendManager();
	}
	
	@Override
	public void logPrintf(int level, String str, Object... params) {
		pf.logPrintf(level, str, params);
	}
	
	@Override
	public void logPrintf(int level, Throwable throwable) {
		pf.logPrintf(level, throwable);
	}

}
