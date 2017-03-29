package com.zdream.pmw.platform.translate.template;

import java.util.Map;

import com.zdream.pmw.platform.prototype.BattlePlatform;

/**
 * 模板选择器<br>
 * <p>根据解析返回消息得到的上下文, 选择一个合适的模板,
 * 来翻译这个消息。</p>
 * 
 * @since v0.2.1
 * @author Zdream
 * @date 2017年3月25日
 * @version v0.2.1
 */
public interface ITemplateChooser {
	
	/**
	 * 告诉调用者, 该类能够处理哪些数据<br>
	 * 像 force-state 之类的消息头<br>
	 * @return
	 */
	public String[] canHandle();
	
	/**
	 * 根据上下文选择消息模板<br>
	 * <p>模板是类似这样的文本:<blockquote>"[aaa]是数据"</blockquote>这句话,
	 * 其中 <code>[aaa]</code> 是变量, 就需要翻译者填充其实际意义的.</p>
	 * 该函数的作用是根据现在的情况选择模板.
	 * <p>该方法的输出是一串指向模板文本的 id 号码.</p>
	 * 
	 * @param head
	 *   要处理的数据, 说明该消息的消息头<br>
	 * @param context
	 *   上下文, 也就是环境, 前面步骤将消息转化成的数据都放置在此
	 * @param pf
	 *   战斗平台环境
	 * @return
	 *   你可以返回多个指向模板文本的 id 号码
	 */
	public String[] choose(String head, Map<String, String> context, BattlePlatform pf);

}
