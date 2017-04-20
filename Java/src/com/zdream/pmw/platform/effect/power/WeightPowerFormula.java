package com.zdream.pmw.platform.effect.power;

import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.platform.effect.SkillReleasePackage;

/**
 * <p>根据体重计算技能威力的公式</p>
 * <p>计算步骤：
 * <li>询问获得指定防御方的体重
 * <li>根据体重计算得到威力, (不进行过滤与修改)</li></p>
 * <p>体重对应威力：
 * <table>
 *   <tr><th>体重区间 (kg)</th><th>威力</th></tr>
 *   <tr><td>&lt; 9.9</td><td>20</td></tr>
 *   <tr><td>10.0 - 24.9</td><td>40</td></tr>
 *   <tr><td>25.0 - 49.9</td><td>60</td></tr>
 *   <tr><td>50.0 - 99.9</td><td>80</td></tr>
 *   <tr><td>100.0 - 199.9</td><td>100</td></tr>
 *   <tr><td>&gt; 200.0</td><td>120</td></tr>
 * </table></p>
 * 
 * @since v0.2.2 [2017-04-18]
 * @author Zdream
 * @version v0.2.2 [2017-04-18]
 */
public class WeightPowerFormula implements IPowerFormula {

	@Override
	public String name() {
		return "weight";
	}

	@Override
	public int power(SkillReleasePackage pack) {
		PokemonBaseData data = 
				pack.getAttends().pokemonBaseData(pack.getDfStaff(pack.getThiz()));
		float wt = data.getWt();
		
		if (wt >= 50) {
			if (wt < 100) {
				return 80;
			} else if (wt < 200) {
				return 100;
			} else {
				return 120;
			}
		} else {
			if (wt >= 25) {
				return 60;
			} else if (wt >= 10) {
				return 40;
			} else {
				return 20;
			}
		}
	}

}
