package com.zdream.pmw.platform.prototype;

/**
 * 对战规则的解析<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月31日
 * @version v0.1
 */
public class RuleConductor implements IBattleRule{
	
	/**
	 * 规则
	 */
	private int rule;
	
	public int getRule() {
		return rule;
	}

	public void setRule(int rule) {
		this.rule = rule;
	}

	/**
	 * 根据规则得到理应有的队伍个数
	 * @return
	 */
	public int teamLength() {
		switch (rule) {
		case RULE_DEBUG:
			return 2;

		default:
			return 2;
		}
	}
	
	/**
	 * 根据规则得到理应有的阵营个数
	 * @return
	 */
	public int campLength() {
		return 2;
	}
	
	/**
	 * 根据队伍号，获得该队伍所在的阵营号
	 * @param team
	 * @return
	 */
	public byte teamToCamp(byte team) {
		return team;
	}
	
	/**
	 * 根据规则得到理应在战场上的座位个数
	 * @return
	 */
	public int seatLength() {
		return 2;
	}
	
	/**
	 * 根据座位号，获得该精灵所在的队伍号
	 * @param seat
	 * @return
	 */
	public byte seatToTeam(byte seat) {
		return seat;
	}
	
}
