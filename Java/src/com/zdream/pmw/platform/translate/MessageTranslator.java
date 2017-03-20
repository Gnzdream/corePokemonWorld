package com.zdream.pmw.platform.translate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.IMessageCallback;
import com.zdream.pmw.util.common.CodeSpliter;

/**
 * 将战场信息翻译成人们看得懂的语言并显示<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月26日
 * @version v0.2
 */
public class MessageTranslator {
	
	Properties pro;
	
	/* ************
	 *	文字替换  *
	 ************ */
	
	Map<String, ITranslate> trm = new HashMap<String, ITranslate>();
	
	public String translate(String msg) {
		String[] ss = CodeSpliter.split(msg);
		String cmd = ss[0];
		
		String mean = this.pro.getProperty("meaning." + cmd);
		String template = this.pro.getProperty("template." + cmd);
		if (mean == null || template == null) {
			return msg;
		}
		String[] means = mean.split(" ");
		Map<String, String> param = new HashMap<>();
		
		for (int i = 0; i < ss.length - 1; i++) {
			param.put(means[i], ss[i + 1]);
		}
		
		// template
		Pattern r = Pattern.compile("\\[(\\S+?)\\]");
		Matcher m = r.matcher(template);
		StringBuilder builder = new StringBuilder(template.length() * 2);
		int offset = 0;
		
		while (m.find(offset)) {
			String handle = m.group(1);
			String replace;
			
			if (param.containsKey(handle)) {
				// 参数匹配
				replace = param.get(handle);
			} else {
				// 方法结果匹配
				ITranslate t = this.trm.get(handle);
				if (t == null) {
					replace = handle;
				} else {
					replace = t.translate(handle, param, platform);
					param.put(handle, replace);
				}
			}
			
			// 一个字符的差, 因为 "[]"
			builder.append(template.substring(offset, m.start())).append(replace);
			offset = m.end();
		}
		builder.append(template.substring(offset));
		
		return builder.toString();
	}
	
	/* ************
	 *	 初始化   *
	 ************ */
	
	BattlePlatform platform;

	public MessageTranslator() {
		// 初始化模板
		pro = new Properties();
		try {
			pro.load(this.getClass()
					.getResourceAsStream("/com/zdream/pmw/platform/translate/cn.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ITranslate[] trs = new ITranslate[] {new ParticipantTranslate(), new SkillTranslate()};
		for (int i = 0; i < trs.length; i++) {
			String[] handlers = trs[i].canHandle();
			for (int j = 0; j < handlers.length; j++) {
				trm.put(handlers[j], trs[i]);
			}
		}
	}
	
	public IMessageCallback getMessageCallback() {
		return msgback;
	}
	
	class MessageCallback implements IMessageCallback {
		
		String msg;

		@Override
		public void onMessage(BattlePlatform platform, String msg) {
			MessageTranslator.this.platform = platform;
			try {
				System.out.println(translate(msg));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(msg);
			}
		}
		
	}
	MessageCallback msgback = new MessageCallback();

}
