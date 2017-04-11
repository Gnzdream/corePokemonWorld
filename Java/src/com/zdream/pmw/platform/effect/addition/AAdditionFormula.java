package com.zdream.pmw.platform.effect.addition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>附加效果判定基类</p>
 * 
 * @since v0.2.2
 * @author Zdream
 * @date 2017年3月29日
 * @version v0.2.2
 */
public abstract class AAdditionFormula implements IAdditionFormula {
	
	protected SkillReleasePackage pack;

	@Override
	public final void addition(SkillReleasePackage pack) {
		this.pack = pack;
		onStart();
		
		if (canTrigger()) {
			force();
		}
		
		onFinish();
		this.pack = null;
	}
	
	protected void onStart() {}
	
	protected void onFinish() {}
	
	protected abstract boolean canTrigger();
	
	protected abstract void force();
	
	/**
	 * 目标对象位置列表, 即防御方目标列表<br>
	 * 会剔除没有命中的目标<br>
	 * @return
	 *   能够施加附加效果的防御方目标列表
	 */
	protected byte[] targetSeats() {
		List<Byte> list = new ArrayList<Byte>();
		int length = pack.dfStaffLength();
		for (int i = 0; i < length; i++) {
			if (pack.getSkill().getSkill().getCategory() == ESkillCategory.STATUS) {
				if (pack.isHitable(i)) {
					list.add(pack.getDfStaff(i).getSeat());
				}
			} else {
				if (pack.isHitable(i) && pack.getRate(i) != 0) {
					list.add(pack.getDfStaff(i).getSeat());
				}
			}
		}
		
		byte[] result = new byte[list.size()];
		int index = 0;
		for (Iterator<Byte> it = list.iterator(); it.hasNext();) {
			Byte b = it.next();
			result[index++] = b;
		}
		return result;
	}

}
