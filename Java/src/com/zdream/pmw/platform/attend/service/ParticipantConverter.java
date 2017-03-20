package com.zdream.pmw.platform.attend.service;

import java.util.Arrays;

import com.zdream.pmw.monster.data.PokemonDataBuffer;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.platform.attend.AttendManager;
import com.zdream.pmw.platform.attend.Attendant;
import com.zdream.pmw.platform.attend.Participant;

/**
 * 上场精灵数据的转换器<br>
 * <code>Attendant</code> 的相关转化服务接口实现类<br>
 * 完成 <code>Attendant</code>、<code>Participant</code> 数据间的转换<br>
 * <br>
 * <b>v0.1</b><br>
 *   修改自类 <code>ParticipantBuilder</code>，由于其普遍性抽出接口<br>
 * <br>
 * <b>v0.2</b><br>
 *   将名称改为 <code>ParticipantConverter</code>，并停用单例模式<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月11日
 * @version v0.2
 */
public class ParticipantConverter {
	
	AttendManager am;
	
	public ParticipantConverter(AttendManager am) {
		this.am = am;
	}
	
	/* ************
	 *	实现方法  *
	 ************ */
	
	public Participant convertToParticipant(Attendant attendant) {
		Participant participant = new Participant(attendant);
		
		EPokemonType[] types = 
				buffer.getBaseData(attendant.getSpeciesID(), attendant.getForm()).getTypes();
		participant.setTypes(Arrays.copyOf(types, types.length, EPokemonType[].class));
		
		return participant;
	}

	/* ************
	 *	调用结构  *
	 ************ */
	
	/**
	 * 缓存池
	 */
	private PokemonDataBuffer buffer = PokemonDataBuffer.getInstance();

}
