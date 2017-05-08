package com.zdream.pmw.platform.effect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.zdream.pmw.platform.attend.ESkillRange;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.accuracy.IAccuracyFormula;
import com.zdream.pmw.platform.effect.damage.IDamageFormula;
import com.zdream.pmw.platform.effect.moveable.IMoveableFormula;
import com.zdream.pmw.platform.effect.power.IPowerFormula;
import com.zdream.pmw.platform.effect.range.IRangeFormula;
import com.zdream.pmw.util.json.JsonObject;

/**
 * 技能释放服务默认实现类
 * <br>
 * <b>v0.2</b><br>
 *   取消了对 pf.instance 的直接引用<br>
 * <br>
 * <b>v0.2.1</b><br>
 *   将关于技能的攻击、命中、伤害、附加效果等计算类、判定类都进行缓存<br>
 *   并且全部使用反射的方式创建<br>
 * 
 * <p><b>v0.2.2</b><br>
 *   在原有的基础上支持目标判定公式</p>
 * 
 * @since v0.1 [2016-04-03]
 * @author Zdream
 * @version v0.2.2 [2017-04-18]
 */
public class FormulaLoader {
	
	EffectManage em;
	
	public FormulaLoader(EffectManage em) {
		this.em = em;
		formulaMap = new HashMap<String, Object>();
		
		formulaLoader();
	}

	/* ************
	 *	数据结构  *
	 ************ */
	
	/**
	 * key(xxx.xxx) : classpath
	 */
	Properties efpro;
	HashSet<String> notSingletonSet;
	
	/**
	 * 储存各种攻击、命中、伤害、附加效果等计算类、判定类的映射容器<br>
	 * key(xxx.xxx) : formula
	 * 
	 * <p>其中, {@code AInstruction} 不是单例的, 因此不会放入这个 map 中.</p>
	 * 
	 * @since v0.2.1
	 */
	Map<String, Object> formulaMap;
	
	public static final String
			TAG_MOVABLE = "m.",
			TAG_DAMAGE = "d.",
			TAG_ACCURACY = "u.",
			TAG_POWER = "w.",
			TAG_ADDITION = "a.",
			TAG_RANGE = "r.",
			TAG_INSTRUCTION = "i.",
			// 暂未启用
			TAG_TARGET = "ra.",
			TAG_STATUS = "v.";
	
	/* ************
	 *	实现方法  *
	 ************ */

	/**
	 * <p>获得公式实例.
	 * <p>注意, 公式在每个战场中只保留一个实例
	 * @param param
	 * @return
	 * @throws NullPointerException
	 *   如果 param 为空
	 * @since v0.2.3
	 */
	IFormula loadFormula(JsonObject param) {
		// load
		IFormula f = loadFormula0(param.get("n").toString());
		// set
		f.restore();
		f.set(param);
		return f;
	}
	
	/**
	 * <p>获得公式实例.
	 * <p>注意, 公式在每个战场中只保留一个实例
	 * @param str
	 *   格式为 <code>&lt;tag&gt;.&lt;name&gt;</code><br>
	 *   其中, name 一般不同于 formula.name().<br>
	 *   可以参照 ef.properties 中的描述.
	 * @return
	 * @throws NullPointerException
	 *   如果 str 为空
	 * @since v0.2.3
	 */
	IFormula loadFormula(String str) {
		IFormula f = loadFormula0(str);
		f.restore();
		return f;
	}
	
	/**
	 * 默认的技能威力计算公式
	 * @return
	 * @since v0.2.3
	 */
	IPowerFormula defaultPowerFormula() {
		IPowerFormula f = loadFormula0(TAG_POWER + "default");
		f.restore();
		return f;
	}
	
	/**
	 * 默认的命中判断公式
	 * @return
	 * @since v0.2.3
	 */
	IAccuracyFormula defaultAccuracyFormula() {
		IAccuracyFormula f = loadFormula0(TAG_ACCURACY + "default");
		f.restore();
		return f;
	}
	
	/**
	 * 默认的能否行动判断公式
	 * @return
	 * @since v0.2.3
	 */
	IMoveableFormula defaultMoveableFormula() {
		IMoveableFormula f = loadFormula0(TAG_MOVABLE + "default");
		f.restore();
		return f;
	}
	
	/**
	 * 默认的伤害公式
	 * @return
	 * @since v0.2.3
	 */
	IDamageFormula defaultDamageFormula() {
		IDamageFormula f = loadFormula0(TAG_DAMAGE + "default");
		f.restore();
		return f;
	}
	
	/**
	 * 默认的范围公式, 即攻击单体的范围公式
	 * @return
	 * @since v0.2.3
	 */
	IRangeFormula skillRangeFormula(ESkillRange e) {
		IRangeFormula f = loadFormula0(TAG_RANGE + e.name().toLowerCase());
		if (f != null) {
			f.restore();
			return f;
		}
		
		em.logPrintf(EffectManage.PRINT_LEVEL_WARN, "无法确认 %s 对应的范围公式\r\n\tat %s",
				e, Thread.currentThread().getStackTrace()[1]);
		
		f = loadFormula0(TAG_RANGE + "default");
		f.restore();
		return f;
	}
	
	/**
	 * <p>得到指定伤害公式或者附加效果公式需要参考的对象.
	 * <p>比如, 在计算伤害的过程中, 可能需要在前计算出技能威力、攻击、防御
	 * 等其它的参数. 因此这些将作为返回对象, 希望执行该伤害计算指示之前,
	 * 已经得知这些必要参数的数值.
	 * </p>
	 * @param name
	 *   伤害公式或附加效果公式的名称,
	 *   格式为 d.xxx (伤害) 或 a.xxx (附加效果).
	 * @return
	 *   返回参考对象数组. 返回 null 时说明无参考对象.
	 * @since v0.2.3
	 */
	String[] reference(String name) {
		String key = name + ".refer";
		String refers = efpro.getProperty(key);
		
		if (refers == null) {
			return null;
		}
		
		return refers.split(",");
	}
	
	/**
	 * 返回效果公式的默认目标公式.
	 * @param key
	 *   附加效果公式的名称,
	 *   格式为 a.xxx.
	 * @return
	 * @since v0.2.3
	 */
	IRangeFormula defaultTargetsFormula(String name) {
		String key = name + ".tg";
		String fname = efpro.getProperty(key);
		
		if (fname == null) {
			return (IRangeFormula) loadFormula("ra.target");
		}
		
		return (IRangeFormula) loadFormula(TAG_TARGET + fname);
	}
	
	/**
	 * 获得技能默认的释放范围，获得 seat 列表
	 * @param seat
	 * @param skillID
	 * @return
	 */
	@Deprecated
	public byte[] defaultSkillRange(byte seat, short skillID) {
		return em.defaultTargets(seat, em.getAttends().getSkillRelease(skillID));
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	/**
	 * 将存储 addition 所有建立数据从 properties 文件加载到 additionOrigin 映射表中
	 */
	private void formulaLoader() {
		loadProperties("/com/zdream/pmw/platform/effect/ef.properties");
		loadSingleton();
		loadDefault();
	}
	
	/* ************
	 *	工具方法  *
	 ************ */

	/**
	 * 存储 properties 文件中的数据
	 * @param path
	 * @param origin
	 */
	private void loadProperties(String path) {
		efpro = new Properties();
		InputStream in = getClass().getResourceAsStream(path);
		try {
			efpro.load(in);
            in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 处理非单例和单例特殊情况的表
	 * @since v0.2.3
	 */
	private void loadSingleton() {
		notSingletonSet = new HashSet<>();
		String[] s = efpro.getProperty("notsingleton").split(",");
		
		for (int i = 0; i < s.length; i++) {
			notSingletonSet.add(s[i]);
		}
	}
	
	/**
	 * 加载默认的 formula
	 * @since v0.2.1
	 */
	private void loadDefault() {
		Set<Object> set = efpro.keySet();
		for (Iterator<Object> it = set.iterator(); it.hasNext();) {
			String key = it.next().toString();
			
			if (key.endsWith(".default")) {
				loadFormula0(key);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * 寻找相应的公式（附加）
	 * @param key
	 *   格式 xxx.xxx
	 * @return
	 * @since v0.2.1
	 */
	private <I extends IFormula> I loadFormula0(String key) {
		boolean singleton = isSingleton(key);
		
		// 第一步，查找 formulaMap 中是否有现成的
		if (singleton && formulaMap.containsKey(key)) {
			return (I) formulaMap.get(key);
		}
		
		// 第二步，在 efpro 中是否有关于其的信息，如果有，就进行构造、存储并返回
		if (efpro.containsKey(key)) {
			I formula = null;
			String className = efpro.getProperty(key);
			try {
				Class<?> clazz = Class.forName(className);
				Constructor<?>[] constructors = clazz.getDeclaredConstructors();
				
				if (constructors.length < 0) {
					System.err.println("不存在 " + className + " 的构造函数");
					return null;
				}
				
				for (int i = 0; i < constructors.length; i++) {
					Constructor<?> c = constructors[i];
					Class<?>[] paramClazzs = c.getParameterTypes();
					try {
						if (paramClazzs.length == 0) {
							formula = (I) c.newInstance();
							break;
						} else if (paramClazzs.length == 1) {
							if (paramClazzs[0] == em.getClass()) {
								formula = (I) c.newInstance(em);
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
					}
				}
				
				if (singleton) {
					// whatever formula is null or not
					formulaMap.put(key, formula);
				}
			} catch (Exception e) {
				e.printStackTrace();
				em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
			}
			return formula;
		}
		return null;
	}
	
	/**
	 * <p>询问该 {@code formula} 是否为单例的.
	 * <p>当该 {@code formula} 不为单例时, 将不会加入到 {@link #formulaMap} 中存放.
	 * 每次需要时将产生一个实例.</p>
	 * @param key
	 * @return
	 * @since v0.2.3
	 */
	private boolean isSingleton(String key) {
		if (key.startsWith(TAG_INSTRUCTION)) {
			return false;
		} else if (key.startsWith(TAG_ADDITION)) {
			return false;
		} else if (key.startsWith(TAG_STATUS)) {
			return false;
		} else if (key.startsWith(TAG_TARGET)) {
			return false;
		}
		
		if (notSingletonSet.contains(key)) {
			return false;
		}
		
		return true;
	}
	
}
