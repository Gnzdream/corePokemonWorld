package com.zdream.pmw.platform.effect.addition;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>附加效果判定基类</p>
 * 
 * @since v0.2.2
 * @author Zdream
 * @date 2017年3月29日
 * @version v0.2.2
 */
public abstract class AAdditionFormula extends AInstruction {

	/*
	 * 向哪方进行状态的施加<br>
	 * 是攻击方还是防御方<br>
	 * 参数 1 为攻击方，0 为防御方（默认）, 2 为双方<br>
	 *
	protected int side;
	
	/**
	 * 向防御方施加状态（默认）
	 *
	public static final int SIDE_DFSTAFF = 0;
	
	/**
	 * 向攻击方施加状态
	 *
	public static final int SIDE_ATSTAFF = 1;
	
	/**
	 * 向攻击方和防御方施加状态
	 *
	public static final int SIDE_BOTH_STAFF = 2;
	
	/**
	 * <p>target 值的给定项, 跟随系统指定</p>
	 * <p>选择该项时, 将使用 <code>pack</code> 中指定的范围, 而自己不再单独计算</p>
	 *
	public static final int SIDE_AUTO = 3;
	
	/**
	 * 是否忽略命中判定, 让该状态仍然触发
	 *
	protected boolean ignoreHit;
	
	public void setSide(int side) {
		this.side = side;
	}
	public int getSide() {
		return side;
	}
	public boolean isIgnoreHit() {
		return ignoreHit;
	}
	public void setIgnoreHit(boolean ignoreHit) {
		this.ignoreHit = ignoreHit;
	}*/
	
	/*
	 * 缓存部分
	 * 允许子类修改
	 */
	// 缓存判定的范围, 即该附加状态判定的所有怪兽位置
	protected byte[] targets;
	
	@Override
	public void set(JsonObject args) {
		/*Map<String, JsonValue> map = args.asMap();
		JsonValue v = map.get("tg");
		if (v != null) {
			String str = v.getString();
			if ("s".equals(str)) {
				side = SIDE_ATSTAFF;
			} else if ("e".equals(str)) {
				side = SIDE_DFSTAFF;
			} else if ("se".equals(str)) {
				side = SIDE_BOTH_STAFF;
			} else if ("a".equals(str)) {
				side = SIDE_AUTO;
			}
		}
		
		v = map.get("ignoreHit");
		if (v != null) {
			this.ignoreHit = (boolean) v.getValue();
		}*/
		
	}
	
	@Override
	public String canHandle() {
		return "a." + name();
	}
	
	@Override
	public void restore() {
		//side = SIDE_DFSTAFF;
		//seatLen = 0;
	}

	@Override
	public final void execute() {
		// onStart(); -> use preHandle()
		
		this.targets = getTargets();
		if (canTrigger()) {
			force();
		}
		
		// onFinish(); -> use postHandle()
	}
	
	/**
	 * <p>询问能否触发该效果</p>
	 * <p>如果不能触发该效果, 则认为技能发动失败, 后面不会进入发动</p>
	 * @return
	 */
	protected abstract boolean canTrigger();
	
	/**
	 * 正式发动效果
	 */
	protected abstract void force();
	
	/**
	 * <p>计算该状态触发所牵涉到的所有怪兽的座位号<br>
	 * 计算完成的数据将反映在 {@link #seats} 中</p>
	 */
	/*protected void calcTargets() {
		if (side == SIDE_DFSTAFF || side == SIDE_BOTH_STAFF) {
			byte[] targets;
			if (ignoreHit) {
				targets = pack.dfSeats();
			} else {
				targets = pack.getTargets();
			}
			final int length = targets.length;
			
			for (int i = 0; i < length; i++) {
				if (seats.length == seatLen) {
					seatsExpansion();
				}
				
				seats[seatLen++] = targets[i];
			}
		}
		
		if (side == SIDE_ATSTAFF || side == SIDE_BOTH_STAFF) {
			if (side == SIDE_BOTH_STAFF && seatLen == 0) {
				// 如果没有攻击到防御方, 那么就不用触发该效果了
				// do nothing
			} else {
				if (seats.length == seatLen) {
					seatsExpansion();
				}
				// 判断是否攻击命中了
				boolean hit = false;
				if (!ignoreHit) {
					hit = (pack.targetsLength() > 0);
				} else {
					hit = true;
				}
				
				if (hit) {
					seats[seatLen++] = pack.getAtStaff().getSeat();
				}
			}
		}
		
		if (side == SIDE_AUTO) {
			byte[] raw = pack.dfSeats();
			if (seats.length < raw.length) {
				seatsExpansion();
			}
			seatLen = raw.length;
			System.arraycopy(raw, 0, seats, 0, seatLen);
		}
		
		em.logPrintf(IPrintLevel.PRINT_LEVEL_DEBUG, "判断附加状态释放的目标为 %s(len=%d)\n\tat %s",
				Arrays.toString(seats), seatLen, Thread.currentThread().getStackTrace()[1].toString());
	}*/
	
	protected byte[] getTargets() {
		return (byte[]) getData(SkillReleasePackage.DATA_KEY_TARGETS);
	}

}
