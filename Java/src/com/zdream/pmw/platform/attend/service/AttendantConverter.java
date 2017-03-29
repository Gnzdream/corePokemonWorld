package com.zdream.pmw.platform.attend.service;

import com.zdream.pmw.monster.data.IPokemonDataContainer;
import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.data.PokemonDataBuffer;
import com.zdream.pmw.monster.prototype.Pokemon;
import com.zdream.pmw.monster.prototype.PokemonHandler;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.trainer.prototype.TrainerData;

/**
 * 参与者数据的转换器<br>
 * <code>Attendant</code> 的相关转化服务接口实现类<br>
 * 完成 <code>Pokemon</code>、<code>Attendant</code> 数据间的转换<br>
 * <br>
 * <b>v0.1</b><br>
 *   修改自类 <code>AttendantConverter</code>，由于其普遍性抽出接口<br>
 * <br>
 * <b>v0.2</b><br>
 *   将名称改为 <code>AttendantConverter</code>，并停用单例模式<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月11日
 * @version v0.2
 */
public class AttendantConverter {
	
	AttendManager am;
	
	public AttendantConverter(AttendManager am) {
		this.am = am;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	public Attendant convertToAttendant(Pokemon pm, TrainerData data, byte no) {
		
		Attendant at = new Attendant(no);
		at.setAbility(pm.getAbility());
		at.setAbnormal(pm.getAbnormal());
		at.setExperience(pm.getExperience());
		at.setForm(pm.getForm());
		at.setGender(pm.getGender());
		at.setHappiness(pm.getHappiness());
		at.setHeldItem(pm.getHeldItem());
		at.setIdentifier(pm.getIdentifier());
		at.setLevel(pm.getLevel());
		at.setNaturn(pm.getNaturn());
		at.setNickname(pm.getNickname());
		
		at.setRealTrainer(service.isRealTrainer(pm, data));
		System.arraycopy(pm.getSkill(), 0, at.getSkill(), 0, 4);
		System.arraycopy(pm.getSkillPP(), 0, at.getSkillPP(), 0, 4);
		System.arraycopy(pm.getSkillPPUps(), 0, at.getSkillPPUps(), 0, 4);
		at.setSpeciesID(pm.getSpeciesID());
		System.arraycopy(pm.getStat(), 0, at.getStat(), 0, at.getStat().length);
		System.arraycopy(pm.getStatEV(), 0, at.getStatEV(), 0, at.getStatEV().length);
		System.arraycopy(pm.getStatIV(), 0, at.getStatIV(), 0, at.getStatIV().length);
		at.setHpi(pm.getHpi());
		
		{
			PokemonBaseData baseData = buffer.getBaseData(pm.getSpeciesID(), pm.getForm());
			System.arraycopy(baseData.getSpeciesValue(), 0, at.getSpeciesValue(), 0, at.getSpeciesValue().length);
		}
		
		return at;
	}
	
	/* ************
	 *	调用结构  *
	 ************ */
	
	/**
	 * 精灵静态数据的缓存池
	 */
	private IPokemonDataContainer buffer = PokemonDataBuffer.getInstance();
	
	/**
	 * 精灵相关服务
	 */
	private PokemonHandler service = PokemonHandler.getInstance();
	
}
