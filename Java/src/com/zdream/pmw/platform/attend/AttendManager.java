package com.zdream.pmw.platform.attend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.prototype.Pokemon;
import com.zdream.pmw.platform.attend.service.AttendantConverter;
import com.zdream.pmw.platform.attend.service.AttendantOperateHandler;
import com.zdream.pmw.platform.attend.service.ParticipantAbilityHandler;
import com.zdream.pmw.platform.attend.service.ParticipantConverter;
import com.zdream.pmw.platform.attend.service.SkillReleaseConverter;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.state.CampState;
import com.zdream.pmw.platform.effect.state.PlatformState;
import com.zdream.pmw.platform.effect.state.SeatState;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IPlatformComponent;
import com.zdream.pmw.platform.prototype.RuleConductor;
import com.zdream.pmw.platform.prototype.Team;
import com.zdream.pmw.trainer.prototype.TrainerData;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 战斗人员（怪兽）管理中心<br>
 * 从属于 BattlePlatform 的一个模块，作为每一个在场、不在场精灵的数据、状态存储中心<br>
 * 多简写为 am<br>
 * <br>
 * 战斗系统：默认<br>
 * <br>
 * 管理人员的<br>
 * <li>战斗准入号（每个怪兽都不同，且一旦分配，在本次战斗中将不会改变）
 * <li>座位号（战斗位置）
 * <li>全战场 / 队伍 / 位置的状态（比如天气等）</li>
 * <br>
 * <b>特别注意：</b><br>
 * <p>每个怪兽的数据中，有准入号 no，座位号 seat，队伍号 team，阵营号 camp<br>
 *     在战斗开始前，对每一个等待入场和不入场的所有精灵，都会分配唯一的准入号 no，值从 0 开始；<br>
 *     而座位号 seat 表示着该精灵所站的位置，值从 0 开始，而不在战场上的所有精灵数值为 -1；<br>
 *     而队伍号 team 表示每一个控制队伍的主体，从 0 开始；
 * 	   阵营号 camp，从 0 开始；</p>
 * <br>
 * <b>v0.1</b><br>
 *   修改了部分注释与输出<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>将查询实时能力的方法移到了 {@link com.zdream.pmw.platform.effect.EffectManage},
 * 这样, am 中就没有发送 {@link com.zdream.pmw.platform.effect.Aperitif} 的方法了</p>
 * <br>
 * <p><b>v0.2.2</b><br>
 * 引入了产生状态实现层的方法</p>
 * 
 * @since v0.1
 *   [2016-04-11]
 * @author Zdream
 * @version v0.2.2
 *   [2017-04-09]
 */
public class AttendManager implements IPlatformComponent {

	/* ************
	 *	  关联表  *
	 ************ */
	/**
	 * 关联表里面注明了所有精灵 no - seat - team - camp 的关系<br>
	 * <b>表的结构</b><br>
	 *   no 作为索引<br>
	 *   seat 会变动
	 */
	byte[][] unionAttendTable;
	
	/**
	 * 关联表里面注明了场地 seat - no - team - camp 的关系<br>
	 * <b>表的结构</b><br>
	 *   seat 作为索引<br>
	 *   no 会变动
	 */
	byte[][] unionSeatTable;
	
	static final int
		COLUMN_ATTEND_SEAT = 0,
		COLUMN_ATTEND_TEAM = 1,
		COLUMN_ATTEND_CAMP = 2;
	
	static final int
		COLUMN_SEAT_NO = 0,
		COLUMN_SEAT_TEAM = 1,
		COLUMN_SEAT_CAMP = 2;
	
	/**
	 * 以精灵的 no 获得精灵的 seat
	 * @param no
	 *   精灵的准入号
	 * @return
	 *   精灵的座位号<br>
	 *   当精灵不在场时返回 -1
	 */
	public byte seatForNo(byte no) {
		return unionAttendTable[COLUMN_ATTEND_SEAT][no];
	}
	
	/**
	 * 以精灵的 no 获得精灵所在的 team
	 * @param no
	 *   精灵的准入号
	 * @return
	 *   精灵所在的队伍号
	 */
	public byte teamForNo(byte no) {
		return unionAttendTable[COLUMN_ATTEND_TEAM][no];
	}
	
	/**
	 * 以精灵的 no 获得精灵所在的 camp
	 * @param no
	 *   精灵的准入号
	 * @return
	 *   精灵所在的阵营号
	 */
	public byte campForNo(byte no) {
		return unionAttendTable[COLUMN_ATTEND_CAMP][no];
	}
	
	/**
	 * 以 seat 获得对应精灵的 no
	 * @param seat
	 *   精灵的座位号
	 * @return
	 *   精灵的准入号<br>
	 *   当精灵不在场时返回 -1
	 */
	public byte noForSeat(byte seat) {
		return unionSeatTable[COLUMN_SEAT_NO][seat];
	}
	
	/**
	 * 以 seat 获得对应精灵所在的 team
	 * @param seat
	 *   精灵的座位号
	 * @return
	 *   精灵所在的队伍号
	 */
	public byte teamForSeat(byte seat) {
		return unionSeatTable[COLUMN_SEAT_TEAM][seat];
	}
	
	/**
	 * 以 seat 获得对应精灵所在的 camp
	 * @param seat
	 *   精灵的座位号
	 * @return
	 *   精灵所在的阵营号
	 */
	public byte campForSeat(byte seat) {
		return unionSeatTable[COLUMN_SEAT_CAMP][seat];
	}
	
	
	/* ************
	 *	 参与者   *
	 ************ */
	/**
	 * 全体参与者 包括不在场的（索引 no 从 0 开始）
	 */
	private Attendant[] attendants;
	
	/**
	 * 座位总数
	 */
	private Participant[] participants;
	
	/**
	 * 全体参与者总个数
	 * @return
	 */
	public int attendantLength() {
		return attendants.length;
	}
	
	/**
	 * 座位总数
	 * @return
	 */
	public int seatLength() {
		return participants.length;
	}
	
	/**
	 * 获得指定 no 对应的精灵参与者
	 * @param no
	 * @return
	 */
	public Attendant getAttendant(byte no) {
		return attendants[no];
	}
	
	/**
	 * 获得指定 seat 对应的场上精灵
	 * @param seat
	 * @return
	 */
	public Participant getParticipant(byte seat) {
		return participants[seat];
	}
	
	/**
	 * 查看指定队伍的胜负情况<br>
	 * 读操作
	 * @param team
	 *   指定队伍的号码
	 * @return
	 *   true - 如果该队伍存在可以战斗的精灵<br>
	 *   false - 如果该队伍不存在可以战斗的精灵，即该队伍已经判输
	 */
	public boolean isExistForTeam(byte team) {
		int noLength = this.attendantLength();
		
		for (byte no = 0; no < noLength; no++) {
			if (teamForNo(no) == team && getAttendant(no).getHpi() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 全场有怪兽的 seat 列表
	 * @return
	 */
	public byte[] existsSeat() {
		int length = 0; // result 的长度
		int seatLength = this.seatLength();
		for (byte seat = 0; seat < seatLength; seat++) {
			if (getParticipant(seat) != null) {
				length++;
			}
		}
		
		byte[] result = new byte[length];
		int index = 0; // result 的索引
		
		for (byte seat = 0; seat < seatLength; seat++) {
			if (getParticipant(seat) != null) {
				result[index++] = seat;
			}
		}
		
		return result;
	}
	
	/**
	 * 指定队伍中有怪兽的 seat 列表
	 * @param team
	 *   队伍号
	 * @return
	 * @since v0.2.3
	 */
	public byte[] existSeats(byte team) {
		int length = 0; // result 的长度
		int seatLength = this.seatLength();
		boolean[] exists = new boolean[seatLength];
		
		for (byte seat = 0; seat < seatLength; seat++) {
			if (participants[seat] != null && teamForSeat(seat) == team) {
				exists[seat] = true;
				length++;
			}
		}
		
		byte[] result = new byte[length];
		int index = 0; // result 的索引
		
		for (byte seat = 0; seat < seatLength; seat++) {
			if (exists[seat]) {
				result[index++] = seat;
			}
		}
		
		return result;
	}
	
	/**
	 * 获得胜利方的阵营<br>
	 * 当战场进行到一定的阶段，有且只有一个阵营还有能够战斗的精灵<br>
	 * 那么该阵营以及该阵营的所有队伍均获得胜利<br>
	 * 如果出现多个阵营有还能战斗的精灵，就无法判断胜利方<br>
	 * <br>
	 * 该方法应该在每次有精灵因为濒死而下场时调用<br>
	 * 来了解当前场上的形式<br>
	 * @return
	 *   返回胜利方的阵营（camp）号<br>
	 *   如果还没有决出最后的胜利，返回 -1<br>
	 */
	public byte successCamp() {
		byte camp = -1;
		int noLength = attendantLength();
		byte no = 0;
		
		// 第一个循环：查找第一个胜利方所在的阵营
		for (; no < noLength; no++) {
			Attendant attendant = getAttendant(no);
			if (attendant != null && attendant.getHpi() > 0) {
				camp = campForNo(no);
				break;
			}
		}
		
		// 第二个循环：查找不在该阵营的其它能够战斗的精灵
		// 如果找到这样的精灵，就说明场上有不止一个阵营的精灵能够行动
		// 即没能决出最后的胜利
		for (no++; no < noLength; no++) {
			if (campForNo(no) != camp) {
				Attendant attendant = getAttendant(no);
				if (attendant != null && attendant.getHpi() > 0) {
					camp = -1;
					break;
				}
			}
		}
		
		return camp;
	}
	
	/**
	 * 怪兽入场
	 * @throws IllegalStateException
	 *   当该座位存在怪兽时
	 */
	public void entrance(byte no, byte seat) {
		pf.logPrintf(IPrintLevel.PRINT_LEVEL_INFO, 
				"队伍 %d 的精灵 %s no = %d 入场 seat = %d", teamForNo(no), attendants[no].getNickname(), no, seat);
		
		if (participants[seat] != null) {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_ERROR, 
					"座位 seat = %d 的精灵 no = %d 还存在在场，但新精灵 no = %d 仍然上场",
					seat, participants[seat].getNo(), no);
			throw new IllegalStateException("seat = " + seat + " 的座位不为空, 无法完成入场");
		}
		participants[seat] = partConvert.convertToParticipant(getAttendant(no));
		participants[seat].setSeat(seat);
		
		unionAttendTable[COLUMN_ATTEND_SEAT][no] = seat;
		unionSeatTable[COLUMN_SEAT_NO][seat] = no;
	}
	
	/**
	 * 怪兽退场（实现部分）
	 * <p>怪兽退场时将其的所有状态全部清除. 全部在实现层实现, 不需要回到操作层.</p>
	 * @param seat
	 * @throws IllegalStateException
	 *   当该座位为空时
	 */
	public void exeunt(byte seat) {
		pf.logPrintf(IPrintLevel.PRINT_LEVEL_INFO, 
				"AM.exeunt(1) 队伍 %d 的精灵退场,seat=%d", teamForSeat(seat), seat);
		byte no = noForSeat(seat);
		
		if (participants[seat] == null) {
			pf.logPrintf(IPrintLevel.PRINT_LEVEL_ERROR, 
					"AM.exeunt(1) 座位 seat=%d 的精灵不在场，但请求退场", seat);
			throw new IllegalStateException("seat = " + seat + " 的座位为空, 无法退场");
		} else {
			// TODO 将异常状态等数据反向保存到 Attendant 中
			
			// 删状态
			participants[seat].removeAllStates(pf);
			
			participants[seat].setSeat((byte) -1);
			participants[seat] = null;
		}
		
		unionAttendTable[COLUMN_ATTEND_SEAT][no] = -1;
		unionSeatTable[COLUMN_SEAT_NO][seat] = -1;
	}

	/* ************
	 *	状态列表  *
	 ************ */
	/*
	 * 这里的状态列表记录了座位的状态、阵营的状态、全场的状态
	 */
	/**
	 * 每个 seat 上的状态列表
	 */
	private IStateContainer[] seatStates,
	/**
	 * 每个 camp 上的状态列表
	 */
		campStates;
	/**
	 * 全场状态列表<br>
	 * 比如天气、欺骗空间等
	 */
	private IStateContainer platformStates;

	public IStateContainer getSeatStates(byte seat) {
		return seatStates[seat];
	}

	public IStateContainer getCampStates(byte camp) {
		return campStates[camp];
	}

	public IStateContainer getPlatformStates() {
		return platformStates;
	}

	/* ************
	 *	调用结构  *
	 ************ */
	private BattlePlatform pf;
	
	private AttendantConverter attConvert;
	private ParticipantConverter partConvert;
	private SkillReleaseConverter skrConvert;
	private ParticipantAbilityHandler partAHandler;
	private AttendantOperateHandler attOper;
	private StateHandler stateHandler;
	
	/**
	 * Pokemon 转化到 Attendant
	 * @param pm
	 * @param data
	 * @param no
	 * @return
	 */
	public Attendant convertToAttendant(Pokemon pm, TrainerData data, byte no) {
		return attConvert.convertToAttendant(pm, data, no);
	}
	
	/**
	 * @see AttendantConverter#pokemonBaseData(short, byte)
	 * @see com.zdream.pmw.monster.data.IPokemonDataContainer#getBaseData(short, byte)
	 * @since v0.2.2
	 */
	public PokemonBaseData pokemonBaseData(short speciesID, byte form) {
		return attConvert.pokemonBaseData(speciesID, form);
	}
	
	/**
	 * <p>根据在场怪兽获得其原始静态数据</p>
	 * @param p
	 *   在场怪兽
	 * @return
	 *   对应的原始数据
	 * @see AttendantConverter#pokemonBaseData(short, byte)
	 * @since v0.2.2
	 */
	public PokemonBaseData pokemonBaseData(Participant p) {
		return pokemonBaseData(p.getSpeciesID(), p.getForm());
	}
	
	/**
	 * <p>根据参与怪兽获得其原始静态数据</p>
	 * @param att
	 *   参与怪兽
	 * @return
	 *   对应的原始数据
	 * @see AttendantConverter#pokemonBaseData(short, byte)
	 * @since v0.2.2
	 */
	public PokemonBaseData pokemonBaseData(Attendant att) {
		return pokemonBaseData(att.getSpeciesID(), att.getForm());
	}
	
	/**
	 * Attendant 转化到 Participant
	 * @param attendant
	 * @return
	 */
	public Participant convertToParticipant(Attendant attendant) {
		return partConvert.convertToParticipant(attendant);
	}
	
	/**
	 * Skill 转化到 SkillRelease
	 * @param skillID
	 * @return
	 */
	public SkillRelease getSkillRelease(short skillID) {
		return skrConvert.getSkillRelease(skillID);
	}
	
	/**
	 * 获得精灵的实时能力等级<br>
	 * 能力包括攻击、防御、特攻、特防、速度、命中、躲避 7 项<br>
	 * @param seat
	 *   指定的精灵的 seat
	 * @param item
	 *   需要查看精灵的能力项<br>
	 *   能力项在 <code>IPokemonDataType</code> 中定义
	 * @return
	 *   实时的能力等级
	 */
	public int abilityLevel(byte seat, int item) {
		return partAHandler.abilityLevel(seat, item);
	}

	/**
	 * 获得精灵的实时能力等级，忽略部分数据<br>
	 * 能力包括攻击、防御、特攻、特防、速度、命中、躲避 7 项<br>
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
	 *   实时的能力等级
	 */
	public int abilityLevel(byte seat, int item, int ignore) {
		return partAHandler.abilityLevel(seat, item, ignore);
	}
	
	/**
	 * 得到能力等级后，计算该等级对能力数据的加成倍率<br>
	 * 能力包括攻击、防御、特攻、特防、速度、命中、躲避 7 项<br>
	 * @param item
	 *   需要查看精灵的能力项<br>
	 *   能力项在 <code>IPokemonDataType</code> 中定义
	 * @param level
	 *   等级数<br>
	 *   <p>该等级是在 {@code IPokemonDataType.LEVEL_LOWER_LIMIT} 到
	 *   {@code IPokemonDataType.LEVEL_UPPER_LIMIT} 之间的整数
	 *   (包括两端, 命中、躲避除外)</p>
	 *   <p>计算命中、躲避时 {@code IPokemonDataType.LEVEL_LOWER_LIMIT * 2} 到
	 *   {@code IPokemonDataType.LEVEL_UPPER_LIMIT * 2} 之间的整数 (包括两端)</p>
	 * @return
	 *   该等级对能力数据的加成倍率
	 * @throws IllegalArgumentException
	 *   当 <code>item</code> 数值不合法时
	 */
	public float abilityLevelRate(int item, int level) throws IllegalArgumentException {
		return partAHandler.abilityLevelRate(item, level);
	}

	/**
	 * 获得精灵的技能命中的实时能力<br>
	 * 该能力仅检索命中、躲避两项能力<br>
	 * 方法将返回关于指定攻击方与指定防御方共同决定的命中参数<br>
	 * <br>
	 * <b>v0.2.1</b>
	 * <p>不再使用 150 作为基点, 改用 1.0f.</p>
	 * 
	 * @param atseat
	 *   攻击方的 seat
	 * @param dfseat
	 *   防御方的 seat
	 * @return
	 *   命中参数的基数（没有任何修正的）为 1.0f
	 */
	public float hitableLevel(byte atseat, byte dfseat) {
		return partAHandler.hitableLevel(atseat, dfseat);
	}

	/**
	 * 精灵是否有指定的属性<br>
	 * @return
	 */
	public boolean hasType(byte seat, EPokemonType type) {
		return partAHandler.hasType(seat, type);
	}
	
	/**
	 * 查看精灵的异常状态<br>
	 * 由于 <code>Participant</code> 类中储存的异常状态是 byte 码形式的<br>
	 * 它和 <code>EPokemonAbnormal</code> 枚举中的常量并不是一一对应的关系<br>
	 * 因此该方法可以帮助解读精灵的异常状态<br>
	 * @param seat
	 * @return
	 */
	public EPokemonAbnormal abnormal(byte seat) {
		return partAHandler.abnormal(seat);
	}

	/* ************
	 *	直接操作  *
	 ************ */
	/**
	 * 向 seat 对应的在场怪兽强制施加 / 清除异常状态<br>
	 * 实现层<br>
	 * 如果怪兽本身有其它的异常状态，需要将该状态移除，随后进行施加异常状态码、添加状态实例<br>
	 * @param seat
	 * @param abnormal
	 *   施加何种异常状态<br>
	 *   当该参数为 <code>EPokemonAbnormal.NONE</code> 就是强制清除异常状态
	 */
	public void forceAbnormal(byte seat, byte abnormal) {
		attOper.forceAbnormal(seat, abnormal);
	}
	
	/**
	 * 向 seat 对应的在场怪兽强制改变指定能力项的能力等级<br>
	 * 实现层<br>
	 * @param seat
	 * @param item
	 *   能力项，该数值在 <code>IPokemonDataType</code> 里面定义
	 * @param value
	 *   变化的数值
	 */
	public void changeAbilityLevel(byte seat, int item, int value) {
		attOper.changeAbilityLevel(seat, item, value);
	}
	
	/**
	 * 向 seat 对应的在场怪兽强制设置 (重置) 指定能力项的能力等级<br>
	 * 实现层<br>
	 * @param seat
	 * @param item
	 *   能力项，该数值在 <code>IPokemonDataType</code> 里面定义
	 * @param value
	 *   设置后, 该能力变成的数值
	 * @since v0.2.1
	 */
	public void setAbilityLevel(byte seat, int item, int value) {
		attOper.setAbilityLevel(seat, item, value);
	}

	/**
	 * 扣 HP
	 * @param seat
	 *   扣 HP 的主体，该精灵的 seat
	 * @param value
	 *   扣多少 HP
	 */
	public void hpFail(byte seat, int value) {
		attOper.hpChange(seat, value * -1);
	}

	/**
	 * 加 HP
	 * @param seat
	 *   加 HP 的主体，该精灵的 seat
	 * @param value
	 *   加多少 HP
	 */
	public void hpRecover(byte seat, int value) {
		attOper.hpChange(seat, value);
	}

	/**
	 * 扣 PP
	 * @param seat
	 *   扣 PP 的主体，该精灵的 seat
	 * @param skillNum
	 *   精灵第几个技能，不是技能 ID<br>
	 * @param value
	 *   扣多少 PP
	 */
	public void ppFail(byte seat, byte skillNum, byte value) {
		attOper.ppSub(seat, skillNum, value);
	}
	
	/**
	 * 向一位在场的怪兽施加状态<br>
	 * 实现层<br>
	 * @param no
	 *   怪兽的 no
	 * @param state
	 *   施加的状态
	 * @since v0.2.1
	 */
	public void forceStateForParticipant(byte no, IState state) {
		attOper.forceStateForParticipant(no, state);
	}
	
	/**
	 * 删除一位在场怪兽的指定状态<br>
	 * 实现层<br>
	 * <p>这里将怪兽的状态列表中删除<b>所有</b>符合 {@code statename} 的状态</p>
	 * @param seat
	 *   怪兽的 seat
	 * @param stateName
	 *   所要删除的状态的名称
	 * @since v0.2.1
	 */
	public void removeStateFromParticipant(byte seat, String stateName) {
		attOper.removeStateFromParticipant(seat, stateName);
	}
	
	/**
	 * @see AttendantOperateHandler#removeStateFromParticipant(byte, IState)
	 * @since v0.2.2
	 */
	public void removeStateFromParticipant(byte seat, IState state) {
		attOper.removeStateFromParticipant(seat, state);
	}
	
	/**
	 * 设置一位在场怪兽的指定状态, 为其操作状态使其某些属性变化<br>
	 * 实现层<br>
	 * <p>这里将怪兽的状态列表中删除<b>所有</b>符合 {@code statename} 的状态</p>
	 * @param seat
	 *   怪兽的 seat
	 * @param stateName
	 *   所要删除的状态的名称
	 * @param value
	 *   所要设置给状态的参数
	 * @since v0.2.1
	 */
	public void setStateFromParticipant(byte seat, String stateName, JsonObject value) {
		attOper.setStateFromParticipant(seat, stateName, value);
	}

	/**
	 * @see AttendantOperateHandler#forceStateForSeat(byte, IState)
	 * @since v0.2.2
	 */
	public void forceStateForSeat(byte seat, IState state) {
		attOper.forceStateForSeat(seat, state);
	}

	/**
	 * @see AttendantOperateHandler#removeStateFromSeat(byte, String)
	 * @since v0.2.2
	 */
	public void removeStateFromSeat(byte seat, String stateName) {
		attOper.removeStateFromSeat(seat, stateName);
	}

	/**
	 * @see AttendantOperateHandler#removeStateFromSeat(byte, IState)
	 * @since v0.2.2
	 */
	public void removeStateFromSeat(byte seat, IState state) {
		attOper.removeStateFromSeat(seat, state);
	}

	/**
	 * @see AttendantOperateHandler#setStateFromSeat(byte, String, JsonValue)
	 * @since v0.2.2
	 */
	public void setStateFromSeat(byte seat, String stateName, JsonObject value) {
		attOper.setStateFromSeat(seat, stateName, value);
	}

	/**
	 * 工厂方式创建状态类
	 * @param args
	 * @return
	 * @since v0.2.2
	 */
	public IState buildState(JsonObject args) {
		return stateHandler.buildState(args);
	}

	/**
	 * 按照施加状态的命令行数据, 完成施加状态的任务<br>
	 * @param codes
	 * @since v0.2.2
	 */
	public void forceState(String[] codes) {
		stateHandler.forceState(codes);
	}

	/* ************
	 *	 初始化   *
	 ************ */
	/**
	 * 初始化 <code>AttendManager</code><br>
	 * 该函数为 <code>AttendManager</code> 的初始化入口函数<br>
	 * 它将完成：<br>
	 * <p>1. 两个关联表的建立</p>
	 * <p>2. 参与精灵的分配</p>
	 * @param msg
	 *   战斗信息开始消息
	 * @param referee
	 *   裁判
	 */
	public void init(Fuse msg, RuleConductor referee, BattlePlatform pf) {
		this.pf = pf;
		attConvert = new AttendantConverter(this);
		partConvert = new ParticipantConverter(this);
		partAHandler = new ParticipantAbilityHandler(this);
		attOper = new AttendantOperateHandler(this);
		skrConvert = new SkillReleaseConverter(this);
		stateHandler = new StateHandler(this);
		
		msg.putDeliver("stateHandler", stateHandler);
		
		int length = countAttendants(msg.getTeams());
		initAttendantArray(msg.getTeams(), length);
		initOtherFromReferee(referee);
		initListStates(msg, referee);
		preloadSkillData(msg);
	}

	/**
	 * 计算参与精灵的总个数
	 * @param teams
	 *   初始化时包含所有队伍的队伍列表
	 * @return
	 *   所有队伍中的精灵个数总和
	 */
	private int countAttendants(List<Team> teams) {
		int length = 0;
		for (Iterator<Team> it = teams.iterator(); it.hasNext();) {
			Team team = it.next();
			length += team.getPms().length;
		}
		return length;
	}
	
	/**
	 * 参与初始化精灵的分配<br>
	 * 完成 attendants 数组和 unionAttendTable 矩阵的建立
	 * @param teams
	 *   初始化时包含所有队伍的队伍列表
	 * @param length
	 *   所有队伍中的精灵个数总和
	 */
	private void initAttendantArray(List<Team> teams, int length) {
		attendants = new Attendant[length];
		unionAttendTable = new byte[3][length];
		
		int index = 0;
		byte teamNo = 0;
		for (Iterator<Team> it = teams.iterator(); it.hasNext(); teamNo++) {
			Team team = it.next();
			Pokemon[] pms = team.getPms();
			TrainerData data = (team.getTrainer() == null) ? null : team.getTrainer().getData();
			for (int i = 0; i < pms.length; i++, index++) {
				attendants[index] = attConvert.convertToAttendant(pms[i], data, (byte)index);
				unionAttendTable[COLUMN_ATTEND_TEAM][index] = teamNo;
				unionAttendTable[COLUMN_ATTEND_SEAT][index] = -1;
				pf.logPrintf(IPrintLevel.PRINT_LEVEL_VERBOSE, 
						"am.initAttendantArray(2) 精灵%s成功被设置 no=%d", attendants[index].getNickname(), index
						);
			}
		}
	}
	
	/**
	 * 根据对战的规则，初始化其它数据<br>
	 * unionAttendTable 表中的【阵营】栏和 unionSeatTable 表的所有栏被设置<br>
	 * @param referee
	 *   对战的规则
	 */
	private void initOtherFromReferee(RuleConductor referee) {
		int length = attendantLength();
		for (int i = 0; i < length; i++) {
			unionAttendTable[COLUMN_ATTEND_CAMP][i] = 
					referee.teamToCamp(unionAttendTable[COLUMN_ATTEND_TEAM][i]);
		}
		
		length = referee.seatLength();
		participants = new Participant[length];
		unionSeatTable = new byte[3][length];
		for (byte seat = 0; seat < length; seat++) {
			unionSeatTable[COLUMN_SEAT_NO][seat] = -1;
			unionSeatTable[COLUMN_SEAT_TEAM][seat] = referee.seatToTeam(seat);
			unionSeatTable[COLUMN_SEAT_CAMP][seat] =
					referee.teamToCamp(unionSeatTable[COLUMN_SEAT_TEAM][seat]);
		}
	}
	

	
	/**
	 * 预加载战斗中可能使用的技能数据，放在技能缓存中<br>
	 * 从参加战斗的所有精灵中搜索会用到的技能，将这些技能预先读取<br>
	 * @param msg
	 */
	private void preloadSkillData(Fuse msg) {
		List<Team> teams = msg.getTeams();
		List<Short> ids = new ArrayList<Short>();
		for (Iterator<Team> it = teams.iterator(); it.hasNext();) {
			Team team = it.next();
			
			Pokemon[] pms = team.getPms();
			for (int i = 0; i < pms.length; i++) {
				Pokemon pm = pms[i];
				short[] skills = pm.getSkill();
				for (int j = 0; j < skills.length; j++) {
					if (skills[j] != 0) {
						ids.add(Short.valueOf(skills[j]));
					}
				}
			}
		}
		
		skrConvert.preloadSkills(ids);
	}
	
	/**
	 * 初始化座位、阵营、全场的状态列表
	 * @param msg
	 * @param referee
	 */
	private void initListStates(Fuse msg, RuleConductor referee) {
		int length;
		
		length = referee.seatLength();
		seatStates = new IStateContainer[length];
		for (byte seat = 0; seat < length; seat++) {
			seatStates[seat] = new StateContainer();
			seatStates[seat].pushState(new SeatState(seat), pf);
		}
		
		length = referee.campLength();
		campStates = new IStateContainer[length];
		for (byte camp = 0; camp < length; camp++) {
			campStates[camp] = new StateContainer();
			campStates[camp].pushState(new CampState(camp), pf);
		}
		
		platformStates = new StateContainer();
		platformStates.pushState(new PlatformState(), pf);
		// TODO 全场状态列表放入初始参数
		
	}
	
	@Override
	public void logPrintf(int level, String str, Object... params) {
		pf.logPrintf(level, str, params);
	}
	
	@Override
	public void logPrintf(int level, Throwable throwable) {
		pf.logPrintf(level, throwable);
	}
	
	@Override
	public BattlePlatform getRoot() {
		return pf;
	}
	
}
