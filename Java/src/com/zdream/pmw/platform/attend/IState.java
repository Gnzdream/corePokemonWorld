package com.zdream.pmw.platform.attend;

import com.zdream.pmw.platform.attend.service.EStateSource;
import com.zdream.pmw.platform.control.IMessageCode;
import com.zdream.pmw.platform.effect.Aperitif;
import com.zdream.pmw.platform.prototype.BattlePlatform;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 状态接口<br>
 * <br>
 * <b>v0.1.1</b>
 *   补充了待实现的方法<br>
 * <br>
 * <b>v0.2</b>
 *   execute 方法补充了默认实现<br>
 * <br>
 * <b>v0.2.1</b>
 *   添加了 set 方法并补充默认实现<br>
 * <br>
 * 
 * @since v0.1.1
 * @author Zdream
 * @date 2016年4月12日
 * @version v0.2.1
 */
public interface IState extends IMessageCode {
	
	/**
	 * 用来显示这个状态的名称<br>
	 * 能够让程序清楚地辨明此状态<br>
	 * 并将其状态在其它状态中找到并进行操作<br>
	 * 不推荐使用中文<br>
	 * @return
	 *   该状态名称的字符串<br>
	 */
	public String name();
	
	/**
	 * 发动源<br>
	 * 表明该状态是由什么源头引发的<br>
	 * @see EStateSource
	 * @return
	 */
	public EStateSource source();
	
	/**
	 * 对于一个消息（拦截前消息），该状态是否可以拦截并处理它<br>
	 * 如果可以，那么该状态就会等待进行拦截和处理<br>
	 * @see IState#execute(JsonValue)
	 * @param msg
	 *   拦截前消息的 head 信息
	 * @return
	 */
	public boolean canExecute(String msg);
	
	/**
	 * 处理对应的消息（拦截前消息）<br>
	 * <br>
	 * 注意，由于每个状态有发动的优先级<br>
	 * 因此可能就算 canExecute() 方法返回 true，该方法也不会执行<br>
	 * <br>
	 * 比如计算躲避的时候，飞空状态和道具光粉产生的状态都会触发，即 canExecute(1) 方法均返回 true<br>
	 * 但是由于飞空状态在处理“计算躲避”状态时的优先度高，它将被触发，并选择拦截并直接返回<br>
	 * 那么比飞空状态优先度低的状态，道具光粉产生的状态都不会执行
	 * @param interceptor
	 *   拦截器
	 * @param value
	 * @param pf @since 0.2
	 *   这是环境
	 * @return
	 */
	default public String execute(Aperitif value,
			IStateInterceptable interceptor,
			BattlePlatform pf) {
		return interceptor.nextState();
	}
	
	/**
	 * 状态在指定消息（拦截前消息）下的发动优先度<br>
	 * 返回的优先度高的状态先发动<br>
	 * @param msg
	 * @return
	 */
	public int priority(String msg);

	/**
	 * 为该状态设置数据。数据以 Json 格式传递进来.<br>
	 * <p>由于 effect 建立了 {@code com.zdream.pmw.platform.effect.StateBuilder} 工厂类,
	 * 需要有一个方式来规范地加工生产需要的状态, 此时用 new 实例再 set 这样的做法是低效的,
	 * 因此决定采用这个接口的方式, 让状态可能需要的数据作为一个整体传入, 让状态类自行决定
	 * 怎样设置数据.</p>
	 * @param  v 需要设置的数据
	 * @param  pf
	 * @since  v0.2.1
	 */
	default public void set(JsonValue v, BattlePlatform pf) {}
	
	/**
	 * 说明这个状态是属于哪个种类的<br>
	 * <p>该方法的添加主要是为了让容器能够更好地管理状态类</p>
	 * <p>比如所有的异常状态类都会返回 "abnormal", 
	 * 因为它们都是属于<b>异常状态</b>的状态</p>
	 * @return
	 *   可以返回 null
	 * @since  v0.2.1
	 */
	default public String ofCategory() {
		return name();
	}
	
}
