package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>技能发动结束的指示
 * </p>
 * 
 * @since v0.2.3 [2017-04-25]
 * @author Zdream
 * @version v0.2.3 [2017-04-25]
 */
public class ReleaseFinishInstruction extends AInstruction {
	
	/**
	 * 指示这次技能的释放的结果是成功还是失败.
	 */
	String result;
	
	/**
	 * 技能释放成功
	 */
	public static final String RESULT_SUCCESS = "success";
	
	/**
	 * 技能释放因攻击方无法行动而结束
	 */
	public static final String RESULT_MOVEABLE = "moveable";
	
	/**
	 * 技能释放失败, 可能由于发动不合法、技能违规或者免疫
	 */
	public static final String RESULT_FAIL = "fail";
	
	/**
	 * 技能释放未命中
	 */
	public static final String RESULT_MISS = "miss";
	
	/**
	 * 指示这次技能的释放失败的原因
	 */
	String reason;
	
	/**
	 * <p>设置该指示运行之后, 将其之后的所有指示删除.
	 * <p>当该指示运行时, 是告诉玩家某项攻击对防御方的攻击判定已经结束.
	 * 在 2 on 2 等环境中, 如果出现同时攻击多方但有部分未命中或者
	 * 攻击免疫时, 该指示就会被运行, 用于告知玩家出现的事情.
	 * 此时对未命中和攻击免疫的防御方的判断已经结束, 触发本指示的运行,
	 * 但是对于其它命中而且不免疫的防御方, 判定还将按流程进行下去.
	 * 因此添加这个参数用于打开开关, 用于继续流程中的判定.</p>
	 */
	boolean forward;
	
	/**
	 * @return
	 * 返回这次技能的释放的结果
	 * @see #result
	 */
	public String getResult() {
		return result;
	}
	
	/**
	 * @return
	 * 返回这次技能的释放失败的原因
	 * @see #reason
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * @return
	 *   在该指示运行结束后, 是否仍然按照原流程判定技能释放.<br>
	 *   默认 false
	 * @see #forward
	 */
	public boolean isForward() {
		return forward;
	}

	@Override
	public String name() {
		return CODE_RELEASE_FINISH;
	}
	
	@Override
	public void set(JsonObject args) {
		Object o;
		
		o = args.get("result");
		if (o != null) {
			result = o.toString();
		}
		
		o = args.get("reason");
		if (o != null) {
			reason = o.toString();
		}
		
		o = args.get("forward");
		if (o != null) {
			forward = Boolean.parseBoolean(o.toString());
		}
	}
	
	@Override
	public void restore() {
		result = RESULT_SUCCESS;
		reason = null;
		forward = false;
		super.restore();
	}

	@Override
	protected void execute() {
		sendFinishMessage();
		
		if (!forward) {
			pf.getOrderManager().removeNexts();
			pf.getOrderManager().storeReleasePackage(pack);
			// TODO 清理 om.eventList
		}
	}
	
	private void sendFinishMessage() {
		byte atno = pack.getAtStaff().getNo();
		
		// 由于不确定怪兽是否已经倒下, 不能用下面的方法询问 seat
		// byte atseat = pack.getAtStaff().getSeat();
		byte atseat = am.seatForNo(atno);
		Aperitif ap;
		if (atseat == -1) {
			ap = em.newAperitif(CODE_RELEASE_FINISH);
		} else {
			ap = em.newAperitif(CODE_RELEASE_FINISH, atseat);
		}
		
		ap.append("seat", atseat).append("no", atno).append("skillId", pack.getSkill().getId())
				.append("result", this.result);
		if (reason != null) {
			ap.append("reason", reason);
		}
		em.startCode(ap);
	}

}
