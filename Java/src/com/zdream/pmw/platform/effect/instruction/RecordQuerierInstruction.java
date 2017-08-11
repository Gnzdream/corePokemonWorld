package com.zdream.pmw.platform.effect.instruction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.effect.AInstruction;
import com.zdream.pmw.platform.effect.SkillReleasePackage;
import com.zdream.pmw.util.json.JsonObject;

/**
 * <p>用于查询记录, 并向 pack 中提交数据
 * 
 * @since v0.2.3 [2017-05-09]
 * @author Zdream
 * @version v0.2.3 [2017-05-09]
 */
public class RecordQuerierInstruction extends AInstruction {
	
	/**
	 * <p>当没有查找到数据, (true:) 选择直接停止技能释放,
	 * 向外部发送 '技能释放失败' 的消息
	 * 
	 * <p>如果在查询技能, 默认情况下,
	 * 查询到 0 (混乱攻击自己), 165 (挣扎), -1 (查询失败)
	 * 将视为查询失败, 将视检测该参数选择是否直接停止技能判断流程.
	 * 
	 * <p>默认 true.</p>
	 */
	boolean failWhenFindNothing = true;
	
	/**
	 * <p>在查找单条数据时:
	 * <li>(true:) 如果发现当前数据不合法时再查找下一条, 直到不存在下一条数据,
	 * 视检测 {#failWhenFindNothing} 选择是否直接停止技能判断流程;
	 * <li>(false:) 直接视检测 {#failWhenFindNothing} 选择是否直接停止技能判断流程;</li>
	 * <p>默认 true.</p>
	 */
	boolean ever = true;

	/**
	 * 查询条件, 既定数据在下面静态数据中定义
	 */
	String in;
	/**
	 * 输出的数据
	 */
	String[] out;
	/**
	 * 作为放入 package 的 key 名称
	 */
	String keys;
	/**
	 * set 传入的参数
	 */
	JsonObject args;
	
	@Override
	public void set(JsonObject args) {
		Object o;
		
		if ((o = args.get("failWhenFindNothing")) != null) {
			failWhenFindNothing = (boolean) o;
		}
		
		if ((o = args.get("ever")) != null) {
			ever = (boolean) o;
		}
		
		if ((o = args.get("in")) != null) {
			in = o.toString();
		}
		
		if ((o = args.get("out")) != null) {
			out = o.toString().split(",");
		}
		
		if ((o = args.get("keys")) != null) {
			keys = o.toString();
		}
		this.args = args;
		
		super.set(args);
	}
	
	@Override
	public void restore() {
		failWhenFindNothing = true;
		in = null;
		out = null;
	}

	@Override
	public String name() {
		return "record-querier";
	}
	
	@Override
	public String canHandle() {
		return "recqr";
	}

	@Override
	protected void execute() {
		if (in == null || out == null) {
			throw new NullPointerException("'in':" + in + " or 'out':" + out + " can not be null.");
		}
		
		switch (in) {
		case "atno": {
			if (out[0].equals("skillId")) {
				queryRecentSkill();
				break;
			}
			if (out[0].equals("skillNum")) {
				queryRecentSkillNum();
				break;
			}
		} break;
		case "self_target_physical": {
			Predicate<SkillReleasePackage> checker = (raw) -> {
				/*Object o;
				if ((o = args.get("ban_skill")) != null) {
					/*short skillId = raw.getSkillId();
					if (o instanceof JsonArray) {
						
					}*
				}*/
				
				
				return true;
			};
			
			SkillReleasePackage p = pf.getOrderManager().query((raw) -> {
				byte atseat = pack.getAtStaff().getSeat();
				byte[] targets = raw.getRanges();
				boolean exist = false;
				for (int i = 0; i < targets.length; i++) {
					if (targets[i] == atseat) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					return false;
				}
				return am.getSkillRelease(raw.getSkillId()).getCategory()
						== ESkillCategory.PHYSICS;
			}, checker);
			if (p != null) {
				
			}
			
		} break;

		default:
			break;
		}

	}
	
	/**
	 * <p>查询目标最近释放的技能.
	 * <p>默认按照用 range 公式得到的目标来查询最近的技能, 放到 package 中,
	 * 默认 key = "recent_skill_id";
	 * <p>这里只支持单目标的查询. 如果目标不为一个, 则抛出异常.</p>
	 * @throws IllegalStateException
	 *   当查到的目标不为一个时;
	 * @throws NullPointerException
	 *   无法查询到目标时. 请将该流程的 Instruction 在判断目标的 Instruction 之后执行.
	 */
	private void queryRecentSkill() {
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_RANGE);
		if (targets == null) {
			throw new NullPointerException("targets is null.");
		}
		if (targets.length != 1) {
			throw new IllegalStateException("targets` length != 1");
		}
		
		byte atno = am.noForSeat(targets[0]);
		
		LOOP: {
			Iterator<SkillReleasePackage> it = pf.getOrderManager().queryByAtno(atno);
			for (; it.hasNext();) {
				SkillReleasePackage p = it.next();
				short skillId = (short) p.getData(SkillReleasePackage.DATA_KEY_SKILL_ID);
				
				switch (skillId) {
				case -1: case 0: case 165:
					// 确定释放失败
					if (ever) {
						continue;
					} else {
						onFail();
						break LOOP;
					}
				default:
					{
						if (keys != null) {
							putData(keys, skillId);
						} else {
							putData("recent_skill_num", skillId);
						}
						break LOOP;
					}
				}
			}
			onFail();
		}
	}
	
	/**
	 * <p>查询目标最近释放的技能.
	 * <p>默认按照用 range 公式得到的目标来查询最近的技能, 放到 package 中,
	 * 默认 key = "recent_skill_id";
	 * <p>这里只支持单目标的查询. 如果目标不为一个, 则抛出异常.</p>
	 * @throws IllegalStateException
	 *   当查到的目标不为一个时;
	 * @throws NullPointerException
	 *   无法查询到目标时. 请将该流程的 Instruction 在判断目标的 Instruction 之后执行.
	 */
	@SuppressWarnings("unused")
	private void query() {
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_RANGE);
		if (targets == null) {
			throw new NullPointerException("targets is null.");
		}
		if (targets.length != 1) {
			throw new IllegalStateException("targets` length != 1");
		}
		
		byte atno = am.noForSeat(targets[0]);
		
		LOOP: {
			Iterator<SkillReleasePackage> it = pf.getOrderManager().queryByAtno(atno);
			for (; it.hasNext();) {
				SkillReleasePackage p = it.next();
				short skillId = (short) p.getData(SkillReleasePackage.DATA_KEY_SKILL_ID);
				
				switch (skillId) {
				case -1: case 0: case 165:
					// 确定释放失败
					if (ever) {
						continue;
					} else {
						onFail();
						break LOOP;
					}
				default:
					{
						if (keys != null) {
							putData(keys, skillId);
						} else {
							putData("recent_skill_num", skillId);
						}
						break LOOP;
					}
				}
			}
			onFail();
		}
	}
	
	/**
	 * <p>查询目标最近释放的技能, 侦测目标使用其技能列表中的第几个技能.
	 * 如果该目标使用某个技能, 最后使用的技能为 0 (混乱攻击自己) 或 165 (挣扎),
	 * 则根据 {@code ever} 来判断下一步该如何行动.
	 * <p>默认按照用 range 公式得到的目标来查询最近的技能, 放到 package 中,
	 * 默认 key = "recent_skill_num";
	 * <p>这里只支持单目标的查询. 如果目标不为一个, 则抛出异常.</p>
	 * @throws IllegalStateException
	 *   当查到的目标不为一个时;
	 * @throws NullPointerException
	 *   无法查询到目标时. 请将该流程的 Instruction 在判断目标的 Instruction 之后执行.
	 */
	private void queryRecentSkillNum() {
		byte[] targets = (byte[]) getData(SkillReleasePackage.DATA_KEY_RANGE);
		if (targets == null) {
			throw new NullPointerException("targets is null.");
		}
		if (targets.length != 1) {
			throw new IllegalStateException("targets` length != 1");
		}

		byte atno = am.noForSeat(targets[0]);
		
		LOOP: {
			Iterator<SkillReleasePackage> it = pf.getOrderManager().queryByAtno(atno);
			for (; it.hasNext();) {
				SkillReleasePackage p = it.next();
				short skillId = (short) p.getData(SkillReleasePackage.DATA_KEY_SKILL_ID);
				
				switch (skillId) {
				case -1: case 0: case 165:
					// 确定释放失败
					if (ever) {
						continue;
					} else {
						onFail();
						break LOOP;
					}
				default:
					{
						if (keys != null) {
							putData(keys, p.getSkillNum());
						} else {
							putData("recent_skill_num", p.getSkillNum());
						}
						break LOOP;
					}
				}
			}
			onFail();
		}
	}
	
	/**
	 * 按条件查询一个
	 * @param p
	 *
	private SkillReleasePackage query(Predicate<? super SkillReleasePackage> p) {
		
		
		return null;
	}*/
	
	/**
	 * 技能释放失败应该完成的流程
	 */
	private void onFail() {
		ArrayList<AInstruction> insts = new ArrayList<>();
		
		JsonObject jo = new JsonObject();
		jo.put("type", "fail");
		jo.put("-atseat", pack.getAtStaff().getSeat());
		
		AInstruction ins = (AInstruction) loadFormula("i.broadcast");
		ins.setPackage(pack);
		ins.set(jo);
		insts.add(ins);
		
		ins = (AInstruction) loadFormula("i.finish");
		ins.setPackage(pack);
		insts.add(ins);
		
		putNextEvents(insts);
	}

}
