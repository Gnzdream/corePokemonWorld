package com.zdream.pmw.platform.effect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.zdream.pmw.monster.skill.ESkillCategory;
import com.zdream.pmw.platform.attend.SkillRelease;
import com.zdream.pmw.platform.control.IPrintLevel;
import com.zdream.pmw.platform.effect.accuracy.IAccuracyFormula;
import com.zdream.pmw.platform.effect.addition.IAdditionFormula;
import com.zdream.pmw.platform.effect.damage.IDamageFormula;
import com.zdream.pmw.platform.effect.moveable.IMoveableFormula;
import com.zdream.pmw.platform.effect.power.IPowerFormula;
import com.zdream.pmw.util.json.JsonValue;

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
 * @since v0.1
 * @author Zdream
 * @date 2016年4月3日
 * @version v0.2.1
 */
public class SkillFormulaLoader {
	
	EffectManage em;
	
	public SkillFormulaLoader(EffectManage em) {
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
	
	/**
	 * 储存各种攻击、命中、伤害、附加效果等计算类、判定类的映射容器<br>
	 * key(xxx.xxx) : formula
	 * @since v0.2.1
	 */
	Map<String, Object> formulaMap;
	
	public static final String
			TAG_MOVABLE = "m.",
			TAG_DAMAGE = "d.",
			TAG_ACCURACY = "c.",
			TAG_POWER = "p.",
			TAG_ADDITION = "a.";
	
	/* ************
	 *	实现方法  *
	 ************ */

	/**
	 * 为技能选择技能计算公式
	 * @param pack
	 * @param skill
	 */
	public void loadFormula(SkillReleasePackage pack, SkillRelease skill) {
		JsonValue[] vals = skill.getAdditions();
		
		if (vals != null) {
			IAdditionFormula[] as = new IAdditionFormula[vals.length];
			int aidx = 0;
			
			for (int i = 0; i < vals.length; i++) {
				try {
					JsonValue val = vals[i];
					String name = val.getMap().get("n").getString();
					String tag = name.substring(0, name.indexOf(".") + 1);
					switch (tag) {
					case TAG_MOVABLE: {
						IMoveableFormula f = loadFormula(val);
						pack.setMovableFormula(f);
					} break;
					case TAG_DAMAGE: {
						IDamageFormula f = loadFormula(val);
						pack.setDamageFormula(f);
					} break;
					case TAG_ACCURACY: {
						IAccuracyFormula f = loadFormula(val);
						pack.setAccuracyFormula(f);
					} break;
					case TAG_POWER: {
						IPowerFormula f = loadFormula(val);
						pack.setPowerFormula(f);
					}  break;
					case TAG_ADDITION: {
						IAdditionFormula f = loadFormula(val);
						f.restore();
						f.set(val);
						as[aidx ++] = f;
					} break;

					default:
						break;
					}
				} catch (Exception e) {
					em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
				}
			}
			
			// addition
			if (aidx == 0) {
				pack.setAdditionFormulas(new IAdditionFormula[]
						{loadFormula(TAG_ADDITION + "default")});
			} else if (aidx == as.length) {
				pack.setAdditionFormulas(as);
			} else {
				IAdditionFormula[] afs = new IAdditionFormula[aidx];
				System.arraycopy(as, 0, afs, 0, aidx);
				pack.setAdditionFormulas(afs);
			}
		} else {
			// addition
			pack.setAdditionFormulas(new IAdditionFormula[]
					{loadFormula(TAG_ADDITION + "default")});
		}

		// other
		if (pack.getMovableFormula() == null) {
			pack.setMovableFormula(loadFormula(TAG_MOVABLE + "default"));
		}
		if (pack.getDamageFormula() == null) {
			if (skill.getSkill().getCategory() == ESkillCategory.STATUS) {
				pack.setDamageFormula(loadFormula(TAG_DAMAGE + "status"));
			} else {
				pack.setDamageFormula(loadFormula(TAG_DAMAGE + "default"));
			}
		}
		if (pack.getAccuracyFormula() == null) {
			pack.setAccuracyFormula(loadFormula(TAG_ACCURACY + "default"));
		}
		if (pack.getPowerFormula() == null) {
			pack.setPowerFormula(loadFormula(TAG_POWER + "default"));
		}
	}
	
	/**
	 * 获得技能默认的释放范围，获得 seat 列表
	 * @param seat
	 * @param skillID
	 * @return
	 */
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
	 * 加载默认的 formula
	 * @since v0.2.1
	 */
	private void loadDefault() {
		Set<Object> set = efpro.keySet();
		for (Iterator<Object> it = set.iterator(); it.hasNext();) {
			String key = it.next().toString();
			
			if (key.endsWith(".default")) {
				loadFormula(key);
			}
		}
	}@SuppressWarnings("unchecked")
	/**
	 * 寻找相应的公式（附加）
	 * @param key
	 *   格式 xxx.xxx
	 * @return
	 * @since v0.2.1
	 */
	private <I extends IFormula> I loadFormula(String key) {
		// 第一步，查找 formulaMap 中是否有现成的
		if (formulaMap.containsKey(key)) {
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
				// whatever formula is null or not
				formulaMap.put(key, formula);
			} catch (Exception e) {
				e.printStackTrace();
				em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
			}
			return formula;
		}
		return null;
	}
	
	/**
	 * 寻找相应的公式（附加）
	 * @param key
	 *   格式 xxx.xxx
	 * @return
	 * @since v0.2.1
	 */
	private <I extends IFormula> I loadFormula(JsonValue value) {
		try {
			String key = value.getMap().get("n").getString();
			return loadFormula(key);
		} catch (RuntimeException e) {
			e.printStackTrace();
			em.logPrintf(IPrintLevel.PRINT_LEVEL_WARN, e);
			return null;
		}
	}

}
