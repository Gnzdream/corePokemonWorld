package com.zdream.pmw.platform.effect.range;

import com.zdream.pmw.platform.attend.ESkillRange;
import com.zdream.pmw.platform.effect.EffectManage;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>基础目标过滤对象.
 * <p>在原有的攻击范围中, 过滤掉未命中和被免疫的对象.
 * </p>
 * 
 * @since v0.2.3 [2017-05-05]
 * @author Zdream
 * @version v0.2.3 [2017-05-05]
 */
public class TargetFormula implements IRangeFormula {
	
	protected SkillReleasePackage pack;
	protected EffectManage em;
	protected final static byte[] EMPTY_SEATS = new byte[]{};
	
	/**
	 * 是否忽略命中判定
	 * 默认 -1: 自动选择, 当技能范围是自身时无视命中判定;
	 *      1:  忽略命中与免疫判定
	 *      0:  实施命中与免疫判定
	 */
	protected int ignoreHit = 0;

	@Override
	public String name() {
		return "target";
	}
	
	@Override
	public void set(JsonObject args) {
		Object o = args.get("ignoreHit");
		if (o != null) {
			if (o instanceof Boolean) {
				if ((boolean) o) {
					ignoreHit = 1;
				} else {
					ignoreHit = 0;
				}
			} else {
				ignoreHit = ((Number) o).intValue();
			}
		}
	}
	
	@Override
	public void restore() {
		ignoreHit = -1;
	}

	@Override
	public final byte[] range(SkillReleasePackage pack, EffectManage em) {
		this.pack = pack;
		this.em = em;

		preHandle();
		byte[] ts = this.targets();
		postHandle();
		
		this.pack = null;
		this.em = null;
		return ts;
	}
	
	protected void preHandle() {
		// ignoreHit
		if (ignoreHit == -1) {
			if (pack.getSkill().getRange() == ESkillRange.SELF) {
				ignoreHit = 1;
			} else {
				ignoreHit = 0;
			}
		}
	}
	protected void postHandle() {}

	protected byte[] targets() {
		if (ignoreHit == 1) {
			return rangeTargets();
		}
		
		byte[] ranges = rangeTargets();
		int count = 0;
		boolean[] bs = new boolean[ranges.length];
		
		Object ohits = getData(SkillReleasePackage.DATA_KEY_HITS);
		Object oimmuses = getData(SkillReleasePackage.DATA_KEY_IMMUSES);
		boolean[] hits = null;
		boolean[] immuses = null;
		if (ohits != null) {
			hits = (boolean[]) ohits;
		}
		if (oimmuses != null) {
			immuses = (boolean[]) oimmuses;
		}
		
		for (int i = 0; i < ranges.length; i++) {
			boolean b = true;
			
			if (hits != null) {
				b &= hits[i];
			}
			if (immuses != null) {
				b &= !immuses[i];
			}
			// 多轮攻击出现的问题: 如果怪兽已经倒下退场, 不能再作为目标
			byte dfseat = ranges[i];
			if (em.getAttends().noForSeat(dfseat) == -1) {
				b = false;
			}
			
			if (b) {
				count++;
				bs[i] = true;
			}
		}
		
		byte[] targets = new byte[count];
		int index = 0;
		for (int i = 0; i < ranges.length; i++) {
			if (bs[i]) {
				targets[index++] = ranges[i];
			}
		}
		
		return targets;
	}
	
	protected byte[] rangeTargets() {
		return (byte[]) getData(SkillReleasePackage.DATA_KEY_RANGE);
	}
	
	protected Object getData(String key) {
		return pack.getData(key);
	}
	
	@Override
	public String toString() {
		return name();
	}

}
