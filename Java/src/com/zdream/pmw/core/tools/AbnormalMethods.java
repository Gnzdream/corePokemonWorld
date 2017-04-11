package com.zdream.pmw.core.tools;

import com.zdream.pmw.monster.prototype.EPokemonAbnormal;

/**
 * 用于解读和生成异常状态码的通用方法<br>
 * 方法类<br>
 * 由于在 Pokemon 和 Attendant 中储存的是异常状态码<br>
 * 而这些数值比较难以解读<br>
 * 因此该方法提供异常状态码和 <code>EPokemonAbnormal</code> 的转化<br>
 * 
 * <p><b>异常状态码</b></p>
 * <p>是在 Pokemon, Attendant 中储存的异常状态数据, 里面包含了两部分内容:</p>
 * <li>异常状态编号
 * <li>异常状态参数</li>
 * <p>具体看下面的 "异常状态参数" 说明</p>
 * 
 * <p><b>异常状态编号</b></p>
 * <p>用来说明怪兽得了哪种异常状态. 每种异常状态分别对应不同的、固定的异常状态编号.
 * 比如中毒的异常状态编号为 1, 剧毒的异常状态编号为 2, 麻痹的异常状态编号为 3 等等.
 * 你可以看 {@link EPokemonAbnormal} 枚举类, 异常状态对应的编号就是其异常状态编号</p>
 * 
 * <p><b>异常状态参数</b></p>
 * <p>为部分异常状态有特殊的参数, 如下:</p>
 * <li>剧毒为已经触发剧毒的回合数，值域为 [0, 15]，超过 15 按 15 计数；
 * <li>睡眠为剩余醒的回合数，值域为 [1, 15]</li>
 * <p>其余异常状态的异常状态参数为 0</p>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年4月17日
 * @version v0.2.1
 */
public class AbnormalMethods {
	
	public static byte CODE_NORMAL = 0;

	/**
	 * @param code
	 *   异常状态码
	 * @return
	 */
	public static EPokemonAbnormal toAbnormal(byte code) {
		return EPokemonAbnormal.parseEnum(code >>> 4);
	}

	/**
	 * @param code
	 *   异常状态码
	 * @return
	 * @since v0.2.1
	 */
	public static EPokemonAbnormal parseAbnormal(byte code) {
		return EPokemonAbnormal.parseEnum(code >> 4);
	}
	
	/**
	 * 将异常状态的代码取出其参数部分 (即异常状态参数)<br>
	 * 睡眠的状态参数是距醒来的回合数；<br>
	 * 剧毒的状态参数是已经中毒的回合数<br>
	 * @param code
	 *   异常状态码
	 * @return
	 *   异常状态参数
	 */
	public static int abnormalParam(byte code) {
		return code & 0xF;
	}
	
	/**
	 * 将异常状态转化成 byte<br>
	 * 支持无参数的异常状态，包括（普通）中毒、麻痹、烧伤、冻伤
	 * @param abnormal
	 * @return
	 *   异常状态码
	 */
	public static byte toBytes(EPokemonAbnormal abnormal) {
		return toBytes(abnormal, 0);
	}
	
	/**
	 * 将异常状态转化成 byte<br>
	 * 支持含参数的异常状态，包括剧毒、睡眠
	 * @param abnormal
	 * @param param
	 *   异常状态参数<br>
	 *   剧毒为已经触发剧毒的回合数，值域为 [0, 15]，超过 15 按 15 计数；<br>
	 *   睡眠为剩余醒的回合数，值域为 [1, 15]<br>
	 * @return
	 *   异常状态码
	 * @throws IllegalArgumentException
	 *   当<code>param < 0</code>时
	 */
	public static byte toBytes(EPokemonAbnormal abnormal, int param) {
		if (param < 0) {
			throw new IllegalArgumentException("the param " + param + " is illegal.");
		}
		
		if (param > 15) {
			param = 15;
		}
		return (byte) ((abnormal.ordinal() << 4) + param);
	}

}
