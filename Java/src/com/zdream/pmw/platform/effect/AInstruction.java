package com.zdream.pmw.platform.effect;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.ESkillRange;
import com.zdream.pmw.platform.control.IMessageCode;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.accuracy.IAccuracyFormula;
import com.zdream.pmw.platform.effect.damage.IDamageFormula;
import com.zdream.pmw.platform.effect.moveable.IMoveableFormula;
import com.zdream.pmw.platform.effect.power.IPowerFormula;
import com.zdream.pmw.platform.effect.range.IRangeFormula;
import com.zdream.pmw.platform.order.event.IEvent;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonObject;
import com.zdream.pmw.util.json.JsonValue;

/**
 * <p>基础指挥部分</p>
 * 
 * @since v0.2.3 [2017-04-21]
 * @author Zdream
 * @version v0.2.3 [2017-04-21]
 */
public abstract class AInstruction implements IEvent, IFormula, IMessageCode {
	
	protected SkillReleasePackage pack;
	protected BattlePlatform pf;
	protected EffectManage em;
	protected AttendManager am;
	
	public void setPackage(SkillReleasePackage pack) {
		this.pack = pack;
	}
	
	/**
	 * 告诉系统该指挥部分做了哪些工作. 
	 * <p>这个返回值将交给 {@link SkillReleasePackage}, 用它来关联该指挥部分.
	 * <p>一般该返回值在 {@link IMessageCode} 中定义.</p>
	 * @return
	 */
	public String canHandle() {
		return name();
	}
	
	@Override
	public void restore() {
		this.pack = null;
	}

	@Override
	public final void action(BattlePlatform pf) {
		this.pf = pf;
		this.em = pf.getEffectManage();
		this.am = pf.getAttendManager();
		pack.relate(this);
		
		preHandle();
		try {
			execute();
		} catch (RuntimeException e) {
			em.logPrintf(IPrintLevel.PRINT_LEVEL_ERROR, e);
			putInfo("ERROR: " + e.getMessage());
		}
		postHandle();
		
		this.pf = null;
		this.em = null;
		this.am = null;
	}
	
	protected abstract void execute();
	
	protected void preHandle() {}
	
	protected void postHandle() {}
	
	protected IFormula loadFormula(JsonObject param) {
		return em.formualLoader.loadFormula(param);
	}
	
	protected IFormula loadFormula(String name) {
		return em.formualLoader.loadFormula(name);
	}

	protected IPowerFormula defaultPowerFormula() {
		return em.formualLoader.defaultPowerFormula();
	}

	protected IAccuracyFormula defaultAccuracyFormula() {
		return em.formualLoader.defaultAccuracyFormula();
	}

	protected IMoveableFormula defaultMoveableFormula() {
		return em.formualLoader.defaultMoveableFormula();
	}

	protected IDamageFormula defaultDamageFormula() {
		return em.formualLoader.defaultDamageFormula();
	}

	protected IRangeFormula skillRangeFormula(ESkillRange e) {
		return em.formualLoader.skillRangeFormula(e);
	}
	
	protected IRangeFormula defaultTargetsFormula(String name) {
		return em.formualLoader.defaultTargetsFormula(name);
	}
	
	protected void putNextEvent(IEvent eve) {
		pf.getOrderManager().pushEventToNext(eve);
	}
	
	protected void putNextEvents(List<IEvent> eves) {
		pf.getOrderManager().pushEventsToNext(eves);
	}
	
	protected void removeInstructions(Collection<String> tasks) {
		pf.getOrderManager().removeInstructions(tasks);
	}
	
	protected void removeInstructions(String... tasks) {
		HashSet<String> set = new HashSet<>();
		for (int i = 0; i < tasks.length; i++) {
			if (tasks[i] != null) {
				set.add(tasks[i]);
			}
		}
		
		if (!set.isEmpty())
			pf.getOrderManager().removeInstructions(set);
	}
	
	protected void putData(String key, Object value) {
		pack.putData(key, value);
	}

	protected void putInfo(Object value) {
		pack.putInfo(value);
	}
	
	protected Object getData(String key) {
		return pack.getData(key);
	}
	
	protected Object getData(String key, Object def) {
		Object o = pack.getData(key);
		return (o == null) ? def : o;
	}
	
	protected Object[] getDataRecords(String key) {
		return pack.getDataRecords(key);
	}
	
	protected String[] reference(String name) {
		return em.formualLoader.reference(name);
	}
	
	protected JsonObject getParameter() {
		return (JsonObject) getData(SkillReleasePackage.DATA_KEY_PARAM);
	}
	
	/**
	 * 获得配置项
	 * @param key
	 * @return
	 */
	protected JsonObject getParameter(String key) {
		JsonObject root = getParameter();
		JsonValue v = root.getJsonValue(key);
		if (v != null) {
			return v.asObject();
		}
		return null;
	}
	
	/**
	 * 获得配置项, 不存在时创建一个返回
	 * @param key
	 * @return
	 */
	protected JsonObject getParameterOrCreate(String key) {
		JsonObject root = getParameter();
		JsonValue v = root.getJsonValue(key);
		if (v != null) {
			return v.asObject();
		} else {
			JsonObject p = new JsonObject();
			root.add(key, p);
			return p;
		}
	}
	
	@Override
	public String toString() {
		return name();
	}
}
