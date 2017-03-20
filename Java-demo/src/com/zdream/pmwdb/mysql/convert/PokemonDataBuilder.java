package com.zdream.pmwdb.mysql.convert;

import java.math.BigDecimal;

import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmw.util.common.NumericSwitcher;
import com.zdream.pmwdb.mysql.model.PokemonDataModel;

/**
 * 精灵静态数据的构造类<br>
 * <br>
 * <b>v0.2</b><br>
 *   修改了所在包<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月24日
 * @version v0.2
 */
public class PokemonDataBuilder {
	
	/**
	 * 将精灵的基础数据转成数据库数据
	 * @param data
	 * @return
	 */
	@Deprecated
	public static PokemonDataModel convertToModel(PokemonBaseData data) {
		PokemonDataModel model = new PokemonDataModel();
		model.setSpeciesID(data.getSpeciesID());
		model.setForm(data.getForm());
		{
			short[] s = data.getAbilities();
			model.setAbility1(s[0]);
			model.setAbility2(s[1]);
			model.setAbilitySecret(s[2]);
		}
		{
			byte[] b = new byte[8];
			EPokemonType[] types = data.getTypes();
			if (types != null) {
				b[0] = (byte) types[0].ordinal();
				b[1] = (byte)((types.length > 1) ? types[1].ordinal() : 0);
			}
			
			short[] s = data.getSpeciesValue();
			if (s != null) {
				for (int i = 0; i < 6; i++) {
					b[2 + i] = (byte) s[i];
				}
			}
			
			model.setData(b);
		}
		model.setWt(BigDecimal.valueOf(data.getWt()));
		return model;
	}
	
	/**
	 * 将数据库数据转成精灵的基础数据模型
	 * @param model
	 * @param model2
	 * @return
	 */
	public static PokemonBaseData convertFromModel(PokemonDataModel model) {
		PokemonBaseData data = new PokemonBaseData();
		data.setSpeciesID(model.getSpeciesID());
		data.setForm(model.getForm());
		data.setAbilities(new short[]{model.getAbility1(), model.getAbility2(), model.getAbilitySecret()});
		{
			byte[] b = model.getData();
			
			EPokemonType[] types = new EPokemonType[(b[1] == 0) ? 1 : 2];
			for (int i = 0; i < types.length; i++) {
				types[i] = EPokemonType.values()[b[i]];
			}
			data.setTypes(types);
			
			short[] s = new short[6];
			for (int i = 0; i < s.length; i++) {
				s[i] = (short)(NumericSwitcher.toUnsignedShort(b[i + 2]));
			}
			data.setSpeciesValue(s);
		}
		data.setWt(model.getWt().floatValue());
		data.setSpeciesName(model.getSpeciesName());
		return data;
	}

}
