package com.zdream.pmw.platform.effect.instruction;

import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.platform.effect.moveable.IMoveableFormula;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>判断怪兽能否行动的指示.
 * <p>在该指示事件中, 将创建 {@link IMoveableFormula}, 并执行
 * {@link IMoveableFormula}. 如果发现无法行动, 则立即结束该技能的触发.
 * <p>原来判断的特殊情况是混乱效果 (confusion).
 * 当怪兽的混乱效果触发时, 阻止了原来的行动, 但判断能够继续下去.
 * 我们的处理是让其在 {@code ConfirmSkillInstruction} 中进行判断,
 * 而不是在这里做判断.
 * </p>
 * 
 * @since v0.2.3 [2017-04-25]
 * @author Zdream
 * @version v0.2.3 [2017-04-25]
 */
public class MoveableInstruction extends AInstruction {
	
	IMoveableFormula moveable;
	JsonObject param;
	
	public IMoveableFormula getMoveable() {
		return moveable;
	}

	@Override
	public String name() {
		return CODE_JUDGE_MOVEABLE;
	}
	
	@Override
	public void set(JsonObject args) {
		param = args;
	}
	
	@Override
	public void restore() {
		this.param = null;
		super.restore();
	}

	@Override
	protected void execute() {
		if (param != null)
			moveable = (IMoveableFormula) loadFormula(param);
		else
			moveable = defaultMoveableFormula();
		
		putInfo("Moveable formula: " + moveable.toString());
		
		boolean result = moveable.canMove(pack, em);
		if (result) {
			putData(SkillReleasePackage.DATA_KEY_MOVEABLE,
					SkillReleasePackage.MOVEABLE_TRUE);
			onMoveSuccess();
		} else {
			putData(SkillReleasePackage.DATA_KEY_MOVEABLE,
					SkillReleasePackage.MOVEABLE_FALSE);
			onMoveFail();
		}

	}
	
	private void onMoveSuccess() {
		// TODO 向下执行: 数据初始化、扣 PP、检查是否发动失败、判断属性免疫和命中
	}
	
	private void onMoveFail() {
		JsonObject jo = new JsonObject();
		jo.put("result", ReleaseFinishInstruction.RESULT_FAIL);
		jo.put("reason", moveable.reasonOf());
		
		AInstruction ins = (AInstruction) loadFormula("i.finish");
		ins.setPackage(pack);
		ins.set(jo);
		putNextEvent(ins);
	}

}
