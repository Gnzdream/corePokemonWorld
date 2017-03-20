package com.zdream.pmw.platform.effect;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 状态用于自己说明自己需要的参数、创建状态所需的数据<br>
 * <p>由于状态生成所需要的数据并不相同, 随着状态的增加, 
 * 在类 {@code com.zdream.pmw.platform.effect.StateBuilder}
 * 枚举全部状态的生成方法并不靠谱, 因此决定部分特殊的状态
 * 实现该接口, 来自己生成自己需要的数据、参数.
 * 
 * @see com.zdream.pmw.platform.effect.StateBuilder
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月4日
 * @version v0.2.1
 */
public interface IStateMessageFormater {
	
	/**
	 * 产生状态的命令
	 * @param value
	 * @param pf
	 * @return
	 */
	public String forceCommandLine(JsonValue value, BattlePlatform pf);
	
	/**
	 * 按照命令解析, 并实现状态的施加<br>
	 * 需要按照 codes 的数据, 按照一定的规则写入 argv 中.
	 * @param codes
	 * @param argv
	 * @param pf
	 */
	public void forceRealize(String[] codes, JsonValue argv, BattlePlatform pf);

}
