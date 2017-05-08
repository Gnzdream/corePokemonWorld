package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.random.RanValue;

/**
 * <p>多段攻击的指示
 * <p>该类原本的名称是 double.
 * </p>
 * 
 * @since v0.2.3 [2017-05-07]
 * @author Zdream
 * @version v0.2.3 [2017-05-07]
 */
public class MultiStrikeInstruction extends AInstruction {
	
	JsonObject args;
	
	/* ************
	 *	  属性    *
	 ************ */
	
	/**
	 * 连续攻击的次数
	 */
	int round;
	public int getRound() {
		return round;
	}
	
	int mode;
	/**
	 * <p>普通 2-5 次连续攻击. (默认值)</p>
	 * </p>这类攻击时, 攻击 2 下和 3 下都有 1/3 几率触发,
	 * 攻击 4 下和 5 下都有 1/6 几率触发.</p>
	 */
	public static final int MODE_FLOAT = 0;
	
	/**
	 * <p>固定连续攻击次数</p>
	 * <p>param 设置了连续攻击的次数</p>
	 */
	public static final int MODE_FIXED = 1;

	@Override
	public String name() {
		return "mltstrk";
	}
	
	@Override
	public void set(JsonObject args) {
		this.args = args;
	}
	
	@Override
	public void restore() {
		this.args = null;
		this.mode = MODE_FLOAT;
		this.round = -1;
	}

	@Override
	protected void execute() {
		init();
		calcRound();
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	/**
	 * 初始化参数
	 */
	private void init() {
		Object o;
		
		if (args == null) {
			return;
		}
		
		// mode
		if ((o = args.get("m")) != null) {
			if (o instanceof Number) {
				int i = ((Number) o).intValue();
				this.mode = i;
			} else {
				String s = o.toString();
				switch (s) {
				case "f": case "float":
					this.mode = MODE_FLOAT;
					break;
				case "x": case "fixed":
					this.mode = MODE_FIXED;
					break;
				}
			}
		}
		
		// round
		// 在 fixed 模式下, 连击的次数是固定的, 这个值由 args 给出
		if ((o = args.get("r")) != null) {
			if (o instanceof Number) {
				round = ((Number) o).intValue();
			}
		}
	}
	
	/**
	 * 计算攻击发动的次数
	 */
	private void calcRound() {
		switch (mode) {
		case MODE_FLOAT:
			calcFloatRound();
			break;

		case MODE_FIXED:
			if (round <= 0) {
				throw new IllegalArgumentException(
					"round: " + round + " 在固定连续攻击次数时是非法的");
			}
			break;
		default:
			break;
		}
		
		// 发布数据
		putData(SkillReleasePackage.DATA_KEY_MULTI_MAX, round);
	}
	
	/**
	 * 计算 float 模式下, 攻击发动的次数
	 */
	private void calcFloatRound() {
		byte atseat = pack.getAtStaff().getSeat();
		int r = RanValue.random(0, 6);
		switch (r) {
		case 0: case 1:
			round = 2;
			break;
		case 2: case 3:
			round = 3;
			break;
		case 4:
			round = 4;
			break;
		case 5:
			round = 5;
			break;
		}
		
		Aperitif ap = em.newAperitif(Aperitif.CODE_MULTI_STRIKE_COUNT, atseat)
				.append("seat", atseat).append("mode", mode).append("round", round);
		em.startCode(ap);
		round = (int) ap.get("round");
	}

}
