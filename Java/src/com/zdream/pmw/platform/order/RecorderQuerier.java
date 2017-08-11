package com.zdream.pmw.platform.order;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonArray;

/**
 * 用于查询 recorder 的数据
 * 
 * @since v0.2.3 [2017-05-08]
 * @author Zdream
 * @version v0.2.3 [2017-05-08]
 */
public class RecorderQuerier {
	
	OrderManager om;
	Recorder recorder;
	
	public RecorderQuerier(OrderManager om) {
		this.om = om;
		this.recorder = om.recorder;
	}
	
	/**
	 * 查询 recorder 的数据
	 * @param args
	 * @return
	 */
	public JsonArray query(JsonArray args) {
		int size = args.size();
		if (size == 0) {
			return null;
		}
		
		return null;
	}
	
	/**
	 * 根据攻击方查询最近释放的技能, 仅查询一条数据
	 * @param atno
	 *   攻击方 no
	 * @return
	 *   skillId 数据, 当未查询到返回 -1
	 */
	public short querySkillIdByAtno(final byte atno) {
		for (ListIterator<SkillReleasePackage> it = recorder.packs.listIterator(recorder.packs.size());
				it.hasPrevious();) {
			SkillReleasePackage p = it.previous();
			
			if (p.getAtno() == atno) {
				short skillId = (short) p.getData("skillId");
				return skillId;
			}
		}
		
		return -1;
	}
	
	/**
	 * 查询一个技能数据包
	 * @param func
	 * @param checker
	 *   对于查询的那个数据包, 进行审核, 如果不通过, 返回 null
	 * @return
	 */
	public SkillReleasePackage query(Predicate<SkillReleasePackage> func,
			Predicate<SkillReleasePackage> checker) {
		SkillReleasePackage test = null;
		for (ListIterator<SkillReleasePackage> it = recorder.packs.listIterator(recorder.packs.size());
				it.hasPrevious();) {
			SkillReleasePackage p = it.previous();
			
			if (func.test(p)) {
				test = p;
				break;
			}
		}
		
		if (test != null && checker != null) {
			return (checker.test(test)) ? test : null;
		}
		return null;
	}
	
	/**
	 * 根据攻击方查询最近释放的技能, 按时间的倒序排序
	 * @param atno
	 *   攻击方 no
	 * @return
	 *   查询数据集合的迭代器
	 */
	public PackageIterator queryByAtno(final byte atno) {
		List<Integer> list = new ArrayList<>();

		for (ListIterator<SkillReleasePackage> it = recorder.packs.listIterator(recorder.packs.size());
				it.hasPrevious();) {
			SkillReleasePackage p = it.previous();
			
			if (p.getAtno() == atno) {
				list.add(it.nextIndex());
			}
		}
		
		PackageIterator pi = new PackageIterator();
		pi.idxs = new int[list.size()];
		for (int i = 0; i < pi.idxs.length; i++) {
			pi.idxs[i] = list.get(i).intValue();
		}
		
		return pi;
	}
	
	class PackageIterator implements Iterator<SkillReleasePackage> {
		int[] idxs;
		int thiz = 0;
		@Override
		public boolean hasNext() {
			return thiz < idxs.length;
		}
		@Override
		public SkillReleasePackage next() {
			return recorder.packs.get(idxs[thiz]);
		}
	}

}
