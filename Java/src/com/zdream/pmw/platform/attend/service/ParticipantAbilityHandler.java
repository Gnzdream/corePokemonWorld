package com.zdream.pmw.platform.attend.service;

import com.zdream.pmw.core.tools.AbnormalMethods;
import com.zdream.pmw.monster.prototype.EPokemonAbnormal;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.monster.prototype.IPokemonDataType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Participant;

/**
 * 查询精灵实时数据的服务接口实现类<br>
 * <br>
 * v0.1.1 将接口移到 AM 模块下<br>
 * v0.1.2 实现了发送拦截前消息来修正计算得到的精灵能力值的相关方法<br>
 * <br>
 * <b>v0.2</b><br>
 *   取消其接口, 让其直接被 am 所管理<br>
 *   将类名从 <code>ParticipantAbilityServiceImpl</code> 修改至 <code>ParticipantAbilityHandler</code><br>
 * 
 * @since v0.1.2
 * @author Zdream
 * @date 2016年4月19日
 * @version v0.2
 */
public class ParticipantAbilityHandler implements IPokemonDataType {
	
	private AttendManager am;
	
	public ParticipantAbilityHandler(AttendManager am) {
		this.am = am;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */
	
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
		return abilityLevel(seat, item, 0);
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
		int lv = am.getParticipant(seat).getAbilityLevel(item);
		switch (ignore) {
		case 0:
			return lv;
		case 1:
			return (lv > 0) ? 0 : lv;
		case 2:
			return (lv < 0) ? 0 : lv;
		}
		return 0;
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
		if (level == 0) {
			return 1.0f;
		}
		
		switch (item) {
		case AT: case DF: case SA: case SD: case SP:
			if (level > 0) {
				return (level + 2) / 2.0f;
			} else {
				return 2.0f / (level * -1 + 2);
			}
			
		case TG: case HD:
			if (level > 0) {
				return (level + 3) / 3.0f;
			} else {
				return 3.0f / (level * -1 + 3);
			}

		default:
			throw new IllegalArgumentException("Item " + item + " is wrong.");
		}
	}
	
	/**
	 * 获得精灵的技能命中的实时能力<br>
	 * 该能力仅检索命中、躲避两项能力<br>
	 * 方法将返回关于指定攻击方与指定防御方共同决定的命中参数<br>
	 * @param atseat
	 *   攻击方的 seat
	 * @param dfseat
	 *   防御方的 seat
	 * @return
	 *   命中参数的基数（没有任何修正的）为 150
	 */
	public float hitableLevel(byte atseat, byte dfseat) {
		byte hitLevel = getParticipant(atseat).getAbilityLevel(TG),
				hideLevel = getParticipant(dfseat).getAbilityLevel(HD);
		
		byte level = (byte) (hitLevel - hideLevel);
		if (level < LEVEL_LOWER_LIMIT) {
			level = LEVEL_LOWER_LIMIT;
		} else if (level > LEVEL_UPPER_LIMIT) {
			level = LEVEL_UPPER_LIMIT;
		}
		
		return levelRate(TG, level);
	}
	
	/**
	 * 精灵是否有指定的属性<br>
	 * @return
	 */
	public boolean hasType(byte seat, EPokemonType type) {
		final EPokemonType[] types = getParticipant(seat).getTypes();
		
		for (int i = 0; i < types.length; i++) {
			if (types[i].equals(type)) {
				return true;
			}
		}
		
		return false;
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
		byte code = getParticipant(seat).getAttendant().getAbnormal();
		
		return AbnormalMethods.toAbnormal(code);
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 获得能力修正的参数
	 * @param item
	 * @param level
	 *   能力等级
	 * @return
	 */
	private float levelRate(int item, byte level) {
		switch (item) {
		case AT: case DF: case SA: case SD: case SP: {
			if (level == 0) {
				return 1;
			} else if (level > 0) {
				return (2 + level) / 2.0f;
			} else {
				return 2.0f / (2 - level);
			}
		}
		default:{
			if (level == 0) {
				return 1;
			} else if (level > 0) {
				return (3 + level) / 3.0f;
			} else {
				return 3.0f / (3 - level);
			}
		}
		}
	}
	
	/* ************
	 *	工具方法  *
	 ************ */
	private Participant getParticipant(byte seat) {
		return am.getParticipant(seat);
	}
}
