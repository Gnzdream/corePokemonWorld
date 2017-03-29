package com.zdream.pmw.platform.translate.translater;

import java.util.Map;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.translate.MessageRepository;

/**
 * 将参数翻译成代表的数据<br>
 * <br>
 * <p>v0.2.1</p>
 * <p><code>translate</code> 方法中增加了 <code>repository</code> 参数,
 * 允许实现方用输出仓库中存在的预设文本</p>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月26日
 * @version v0.2.1
 */
public interface ITranslate {
	
	/**
	 * 告诉调用者, 该类能够处理哪些数据<br>
	 * 像 pm.name 之类的<br>
	 * @return
	 */
	public String[] canHandle();
	
	/**
	 * 翻译 handle 部分的数据<br>
	 * 举个例子: "[aaa]是数据" 这句话, 当 <code>handle=aaa</code> 时,<br>
	 * 该函数的作用是说明 aaa 是什么, 返回这个参数代表的数据<br>
	 * 
	 * @param handle
	 *   要处理的数据, 就是上面的例子中 "[]" 中包括的数据,<br>
	 *   它是上面 <code>canHandle()</code> 方法中的一个元素.<br>
	 * @param param
	 *   附加参数
	 * @param pf
	 *   环境
	 * @param repository
	 *   仓库, 用于取预设模板和模板中预设文字
	 * @return
	 */
	public String translate(String handle, Map<String, String> param,
			BattlePlatform pf, MessageRepository repository);

}
