package com.zdream.pmw.platform.order;

import java.util.ArrayList;

import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>存储发送直接操作实现层的消息和战斗释放时数据</p>
 * <p>这些数据按照发出的顺序进行排序.</p>
 * 
 * @since v0.2.2
 * @author Zdream
 * @date 2017年3月28日
 * @version v0.2.2
 */
public class Recorder {
	
	/* ************
	 *	  属性    *
	 ************ */
	/*
	 * 存储发出消息
	 */
	ArrayList<String[]> msgs = new ArrayList<>(36);
	int[] msgsIdx;
	
	/*
	 * 存储技能释放的数据
	 */
	ArrayList<SkillReleasePackage> packs = new ArrayList<>();
	int[] packsIdx;
	
	/* ************
	 *	存储数据  *
	 ************ */
	
	/**
	 * 存储消息
	 * @param codes
	 */
	public void storeMessage(String[] codes) {
		msgs.add(codes);
	}
	
	/**
	 * <p>存储释放技能的数据</p>
	 * <p>存储数据前, 需要将数据中的公式类的引用去除, 这是为了以后获取时,
	 * 不会拿到这些公式类. 公式类被设计成可以重用的类型, 获取公式类实例可
	 * 能会对以后的数据计算造成影响.</p>
	 * @param pack
	 */
	public void storeReleasePackage(SkillReleasePackage pack) {
		pack.clearFormulas();
		packs.add(pack);
	}
	
	/* ************
	 *	获得数据  *
	 ************ */
	
	/**
	 * 获得某一回合中所有的消息
	 * @param round
	 *   指定的回合数
	 */
	public String[][] getMessage(int round) {
		int sIdx = msgsIdx[round];
		int eIdx;
		if (round == msgsIdx.length) {
			eIdx = msgs.size();
		} else {
			eIdx = msgsIdx[round + 1];
			if (eIdx == -1) {
				eIdx = msgs.size();
			}
		}
		
		String[][] result = new String[eIdx - sIdx][];
		for (int i = 0; i < result.length; i++) {
			result[i] = msgs.get(i + sIdx);
		}
		
		return result;
	}
	
	/**
	 * 获得某一回合中所有释放技能的数据
	 * @param round
	 *   指定的回合数
	 */
	public SkillReleasePackage[] getReleasePackage(int round) {
		int sIdx = packsIdx[round];
		int eIdx;
		if (round == packsIdx.length) {
			eIdx = packs.size();
		} else {
			eIdx = packsIdx[round + 1];
			if (eIdx == -1) {
				eIdx = packs.size();
			}
		}
		
		SkillReleasePackage[] result = new SkillReleasePackage[eIdx - sIdx];
		for (int i = 0; i < result.length; i++) {
			result[i] = packs.get(i + sIdx);
		}
		
		return result;
	}
	
	/* ************
	 *	  其它    *
	 ************ */
	
	void roundCount(int round) {
		if (round >= msgsIdx.length) {
			int length = round * 2;
			int[] ms = new int[length];
			int[] ps = new int[length];
			System.arraycopy(msgsIdx, 0, ms, 0, msgsIdx.length);
			System.arraycopy(packsIdx, 0, ps, 0, packsIdx.length);
			
			for (int i = msgsIdx.length; i < length; i++) {
				ms[i] = ms[i] = -1;
			}
			msgsIdx = ms;
			packsIdx = ps;
		}
		
		msgsIdx[round] = msgs.size();
		packsIdx[round] = packs.size();
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	OrderManager om;

	public Recorder(OrderManager om) {
		this.om = om;
		
		msgsIdx = new int[8];
		packsIdx = new int[8];
		for (int i = 1; i < 8; i++) { // 0 号元素为 0
			msgsIdx[i] = -1;
			packsIdx[i] = -1;
		}
	}

}
