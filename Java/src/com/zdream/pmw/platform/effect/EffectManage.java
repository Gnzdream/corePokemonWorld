package com.zdream.pmw.platform.effect;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.IState;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.type.TypeRestraint;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IPlatformComponent;
import com.zdream.pmw.platform.prototype.RuleConductor;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 战斗结果、效果处理中心，行动数据处理模块<br>
 * 从属于 <code>BattlePlatform</code> 的一个模块，作为每一次行动的结果记录者<br>
 * <code>EffectValueRecorder</code> 作为一个数据包供 EffectManage 处理<br>
 * <br>
 * <b>v0.1</b><br>
 *   修改了部分注释<br>
 * <br>
 * <b>v0.1.2</b><br>
 *   将状态拦截器设置成非属性的方式，作为一个局域变量，因而支持拦截中含拦截<br>
 * <br>
 * <b>v0.2</b><br>
 *   添加 BattlePlatform 的变量引用<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>添加计算怪兽实时能力值的方法. 该方法原本在 {@code AttendManage} 中实现<p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月19日
 * @version v0.2.1
 */
public class EffectManage implements IPlatformComponent {
	
	/* ************
	 *	调用结构  *
	 ************ */
	
	/**
	 * 服务
	 */
	private SkillFormulaLoader skillLoader;
	
	/**
	 * 技能发动的释放计算平台
	 */
	private SkillReleaseBox box;
	
	/**
	 * 行动事件计算和处理平台
	 */
	private ActBox act;
	
	/**
	 * 属性克制表
	 */
	private TypeRestraint table = new TypeRestraint();
	
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

	/**
	 * 获得属性克制倍率
	 * @param atType
	 *   攻击方属性
	 * @param dfType
	 *   防御方属性
	 * @return
	 *   倍率
	 */
	public float getRestraintRate(EPokemonType atType, EPokemonType dfType) {
		return table.getRestraintRate(atType, dfType);
	}

	public byte[] defaultSkillRange(byte seat, short skillID) {
		return skillLoader.defaultSkillRange(seat, skillID);
	}

	/**
	 * 为技能选择技能计算公式
	 * @param pack
	 * @param skill
	 */
	public void loadFormula(SkillReleasePackage pack, SkillRelease skill) {
		skillLoader.loadFormula(pack, skill);
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
	public void moveAct(byte no, byte skillNum, byte originTarget) {
		box.moveAct(no, skillNum, originTarget);
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

	public void ppSubForRelease(SkillReleasePackage pack) {
		box.ppSubForRelease(pack);
	}

	public boolean judgeMoveable(SkillReleasePackage pack) {
		return box.judgeMoveable(pack);
	}

	/**
	 * 计算攻击方实时命中率（忽略能力变化）
	 * @return v0.2.1
	 *   当返回数值为负数 (-1) 则为必中
	 */
	public float calcHitrate(SkillReleasePackage pack) {
		return box.calcHitrate(pack);
	}

	/**
	 * 计算防御方实时躲避率（忽略能力变化）
	 * @return v0.2.1
	 *   当返回数值为负数 (-1) 则为必不中
	 */
	public float calcHide(SkillReleasePackage pack) {
		return box.calcHide(pack);
	}
	
	public float calcAccuracy(SkillReleasePackage pack) {
		return box.calcAccuracy(pack);
	}

	/**
	 * 判定释放技能的属性<br>
	 * @return
	 */
	public EPokemonType calcSkillType(SkillReleasePackage pack) {
		return box.calcSkillType(pack);
	}

	/**
	 * 判定克制倍率, 返回对于敌方每个属性, 我方的技能属性的克制倍率
	 * @param pack
	 * @return
	 *  数组的大小 = 对应敌方怪兽的属性多少
	 */
	public float[] calcTypeRate(SkillReleasePackage pack) {
		return box.calcTypeRate(pack);
	}
	
	/**
	 * 计算会心一击等级
	 * @param pack
	 * @return
	 *   当数值大于零, 表示会心一击等级;<br>
	 *   当数值为 0, 表示绝对回避会心;<br>
	 *   当数值为 -1, 表示绝对会心;<br>
	 */
	public byte calcCt(SkillReleasePackage pack) {
		return box.calcCt(pack);
	}

	/**
	 * 计算技能威力
	 * @param pack
	 * @return
	 */
	public int calcPower(SkillReleasePackage pack) {
		return box.calcPower(pack);
	}
	
	/**
	 * 计算最后的伤害修正 (状态等修正)<br>
	 * 这里不包括乱数修正等<br>
	 * @param index
	 */
	public float calcCorrect(SkillReleasePackage pack) {
		return box.calcCorrect(pack);
	}
	
	/**
	 * 计算最后伤害
	 * @param pack
	 * @return
	 */
	public int calcDamage(SkillReleasePackage pack) {
		return box.calcDamage(pack);
	}
	
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
	public int calcAbility(byte seat, int item) {
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
	public int calcAbility(byte seat, int item, int ignore) {
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
	public int calcAbility(byte seat, int item, int ignore, boolean alter) {
		return box.calcAbility(seat, item, ignore, alter);
	}

	/**
	 * 施加状态
	 * @param pack
	 * @param abnormal
	 *   #{@link com.zdream.pmw.platform.common.AbnormalMethods#toBytes(EPokemonAbnormal, int)}
	 */
	public void forceAbnormal(SkillReleasePackage pack, byte abnormal) {
		box.forceAbnormal(pack, abnormal);
	}
	
	/**
	 * 施加状态 (系统施加)
	 * @param pack
	 * @param abnormal
	 *   #{@link com.zdream.pmw.platform.common.AbnormalMethods#toBytes(EPokemonAbnormal, int)}
	 */
	public void forceAbnormal(byte seat, byte abnormal) {
		box.forceAbnormal(seat, abnormal);
	}

	/**
	 * 获得当前进行攻击判定的数据包
	 * @return
	 * @since v0.2.1
	 */
	public SkillReleasePackage getCurrentPackage() {
		return box.getCurrentPackage();
	}

	/**
	 * 工厂方式创建状态类
	 * @param args
	 * @return
	 * @since v0.2.1
	 */
	public IState buildState(JsonValue args) {
		return stateBuilder.buildState(args);
	}

	/**
	 * 发送施加状态拦截前消息
	 * @param args
	 *   原本存放于 skill 中的状态启动需要的静态数据
	 * @param pack
	 * @since v0.2.1
	 */
	public void sendForceStateMessage(JsonValue args, SkillReleasePackage pack) {
		stateBuilder.sendForceStateMessage(args, pack);
	}
	
	/**
	 * 获得施加状态的命令行类型消息<br>
	 * 调用方是 {@code com.zdream.pmw.platform.control.codeStateCodeRealizer}
	 * @param value
	 * @return
	 * @since v0.2.1
	 */
	public String forceStateCommandLine(JsonValue value) {
		return stateBuilder.forceStateCommandLine(value);
	}
	
	/**
	 * 按照施加状态的命令行数据, 完成施加状态的任务<br>
	 * 调用方是 {@code com.zdream.pmw.platform.control.codeStateCodeRealizer}<br>
	 * @param codes
	 * @since v0.2.1
	 */
	public void forceStateRealize(String[] codes) {
		stateBuilder.forceStateRealize(codes);
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
		box = new SkillReleaseBox(this);
		target = new TargetCounter(this);
		skillLoader = new SkillFormulaLoader(this);
		stateBuilder = new StateBuilder(this);
		act = new ActBox(this);
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
