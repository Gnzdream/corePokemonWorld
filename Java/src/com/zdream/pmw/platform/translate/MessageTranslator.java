package com.zdream.pmw.platform.translate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.platform.prototype.IMessageCallback;
import com.zdream.pmw.platform.translate.parse.DefaultParser;
import com.zdream.pmw.platform.translate.template.ITemplateChooser;
import com.zdream.pmw.platform.translate.translater.ITranslate;
import com.zdream.pmw.util.common.CodeSpliter;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 将战场信息翻译成人们看得懂的语言并显示<br>
 * <br>
 * <b>v0.2.1</b>
 * <p>添加 team 属性</p>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月26日
 * @version v0.2.1
 */
public class MessageTranslator {
	
	MessageRepository repository;
	
	/* ************
	 *	  属性    *
	 ************ */
	
	byte team = (byte) -1;
	
	public void setTeam(byte team) {
		this.team = team;
	}
	
	BattlePlatform pf;
	
	public void setPlatform(BattlePlatform pf) {
		this.pf = pf;
	}
	
	boolean print;
	
	/**
	 * 设置是否在控制台打印消息
	 * <p>这是一个 debug 开关. 如果将其设置为 true, 它就会在每个翻译之后, 附加输出
	 * 该次, 收到的消息.</p>
	 * @param print
	 * @since v0.2.2
	 */
	public void setPrint(boolean print) {
		this.print = print;
	}
	
	/* ************
	 *	文字替换  *
	 ************ */
	
	public String[] translate(String msg) {
		String[] ss = CodeSpliter.split(msg);
		String cmd = ss[0];
		
		// 1. 原消息解析
		JsonValue dict = repository.getDictionary(cmd);
		DefaultParser parser = new DefaultParser();
		Map<String, String> context = parser.parse(ss, dict);
		
		if (team != -1) {
			context.put("/team", Byte.toString(team));
		}
		
		// 2. 选择合适的模板
		ITemplateChooser chooser = repository.getChooser(cmd);
		String[] tempIds = chooser.choose(cmd, context, pf);
		if (tempIds == null) {
			if (Boolean.toString(true).equals(context.get("/mute"))) {
				if (print) {
					return new String[]{">>> " + msg};
				} else {
					return new String[]{};
				}
			} else {
				return new String[]{">>> " + msg};
			}
		}
		
		String[] temps = repository.getTemplates(tempIds);
		
		// 3. 用模板和上下文生成语言
		final int length = temps.length;
		String[] results = new String[print ? length + 1 : length];
		for (int i = 0; i < length; i++) {
			String result = handleTemplate(temps[i], context);
			if (Boolean.toString(true).equals(context.get("/mute"))) {
				results[i] = "( " + result + ")";
			} else {
				results[i] = result;
			}
		}
		
		if (print) {
			results[results.length - 1] = ">>> " + msg;
		}
		
		return results;
	}
	
	private String handleTemplate(String template, Map<String, String> context) {
		Pattern r = Pattern.compile("\\[(\\S+?)\\]");
		Matcher m = r.matcher(template);
		StringBuilder builder = new StringBuilder(template.length() * 2);
		int offset = 0;
		
		while (m.find(offset)) {
			String handle = m.group(1);
			String replace;
			
			if (context.containsKey(handle)) {
				// 参数匹配
				replace = context.get(handle);
			} else {
				// 方法结果匹配
				ITranslate t = this.repository.getTranslator(handle);
				if (t == null) {
					replace = handle;
				} else {
					replace = t.translate(handle, context, platform, repository);
					context.put(handle, replace);
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
		// v0.2.1
		repository = new MessageRepository();
	}
	
	public IMessageCallback getMessageCallback() {
		return msgback;
	}
	
	class MessageCallback implements IMessageCallback {
		
		String msg;

		@Override
		public void onMessage(BattlePlatform platform, String msg) {
			MessageTranslator.this.platform = platform;
			setPlatform(platform);
			try {
				String[] texts = translate(msg);
				for (int i = 0; i < texts.length; i++) {
					System.out.println(texts[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(msg);
			}
		}
		
	}
	MessageCallback msgback = new MessageCallback();
}
