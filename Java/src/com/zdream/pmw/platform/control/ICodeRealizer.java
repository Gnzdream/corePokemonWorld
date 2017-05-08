package com.zdream.pmw.platform.control;

import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 指令实现接口<br>
 * v0.2 realize 方法添加 BattlePlatform 的参数引用<br>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2016年4月4日
 * @version 0.2
 */
public interface ICodeRealizer extends IMessageCode {
	
	/**
	 * 能够处理的指令
	 * @return
	 */
	public String[] codes();
	
	/**
	 * 获得指令行
	 * @param value
	 * @param pfs
	 * @return
	 */
	public String commandLine(Aperitif ap, BattlePlatform pf);
	
	/**
	 * 处理对应的指令
	 * 
	 * @version 0.2.1
	 * <p>版本 0.2.1 之后, 将取消返回参数</p>
	 * 
	 * @param codes
	 *   提交需要处理的指令及参数<br>
	 *   codes[0] 是方法 codes() 返回数组的一个元素
	 * @param pf
	 */
	public void realize(String[] codes, BattlePlatform pf);
	
}
