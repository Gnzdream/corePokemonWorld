package com.zdream.pmw.platform.control;

import java.util.List;

import com.zdream.pmw.platform.control.ai.DefaultAIBrain;
import com.zdream.pmw.platform.control.multithread.MultiAIControl;
import com.zdream.pmw.platform.control.multithread.MultiPlayerControl;
import com.zdream.pmw.platform.control.multithread.MultiRequsetSemaphore;
import com.zdream.pmw.platform.control.singlethread.AIControl;
import com.zdream.pmw.platform.control.singlethread.PlayerControl;
import com.zdream.pmw.platform.control.singlethread.SingleRequestSemaphore;
import com.zdream.pmw.platform.prototype.Fuse;
import com.zdream.pmw.platform.prototype.IBattleRule;
import com.zdream.pmw.platform.prototype.IMessageCallback;
import com.zdream.pmw.platform.prototype.IRequestCallback;
import com.zdream.pmw.platform.prototype.RuleConductor;

/**
 * 将 <code>ControlBase</code> 的生成的任务交给它<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年2月25日
 * @version v0.2
 */
public class ControlFactory implements IBattleRule {
	
	private ControlManager cm;

	public ControlFactory(ControlManager cm) {
		this.cm = cm;
	}
	
	/* *************
	 *	规则 消息  *
	 ************* */
	/*
	 * 按照规则, 能够自动设置线程模式等数据
	 */
	private RuleConductor referee;
	
	private Fuse msg;
	
	public void setReferee(RuleConductor referee) {
		this.referee = referee;
		
		switch (referee.getRule()) {
		case RULE_DEBUG:
			single = true;
			break;

		default:
			break;
		}
	}
	
	public void setBeginMessage(Fuse msg) {
		this.msg = msg;
	}
	
	
	/**
	 * 计算得到的玩家的队伍号
	 */
	public byte getPlayerTeam() {
		switch (referee.getRule()) {
		case RULE_DEBUG:
			return 0;

		default:
			return -1;
		}
	}
	
	/* ************
	 *	线程模式  *
	 ************ */
	/*
	 * 单线程 (默认)
	 * 多线程
	 */
	private boolean single = true;
	public boolean isSingle() {
		return single;
	}
	public void setSingle(boolean single) {
		this.single = single;
	}
	
	/* ************
	 *	工厂方法  *
	 ************ */
	
	public ControlBase[] createControls() {
		int length = referee.teamLength();
		ControlBase[] ctrls = new ControlBase[length];
		
		switch (referee.getRule()) {
		case RULE_DEBUG:
			createDebugControls(ctrls);
			break;
		
		// TODO 更多的规则, 见 RuleConductor

		default:
			createMultiDebugControls(ctrls);
			break;
		}
		
		return ctrls;
	}
	
	private void createDebugControls(ControlBase[] ctrls) {
		// 单线程
		List<IRequestCallback> callbacks = msg.getCallbacks();
		List<IMessageCallback[]> msgbacks = msg.getMessagebacks();
		
		/*
		 * 玩家部分
		 *  callback 是需要的！
		 */
		IRequestCallback call = callbacks.get(0);
		if (call != null) {
			PlayerControl pc = new PlayerControl(cm);
			pc.setTeam((byte) 0);
			pc.setCallback(call);
			pc.setMessagebacks(msgbacks.get(0));
			ctrls[0] = pc;
		} else {
			cm.logPrintf(IPrintLevel.PRINT_LEVEL_ERROR,
					"ControlFactory.createDebugControls(1) 玩家 (队伍 0) 没有设置回调函数!");
		}
		
		/*
		 * AI 部分
		 *  callback 不是必需的
		 *  一般情况下, call 为空, 此时应该让系统设置 AI
		 */
		call = callbacks.get(1);
		{
			AIControl ac = new AIControl(cm);
			ac.setTeam((byte) 1);
			if (call == null) {
				call = new DefaultAIBrain(cm).createAICallback();
			}
			
			ac.setCallback(call);
			// 默认的 ai 是没有消息回调函数的
			ctrls[1] = ac;
		}
	}
	
	private void createMultiDebugControls(ControlBase[] ctrls) {
		ctrls[0] = new MultiPlayerControl(cm);
		ctrls[0].setTeam((byte) 0);
		ctrls[1] = new MultiAIControl(cm);
		ctrls[1].setTeam((byte) 1);
	}

	public IRequestSemaphore createSemaphore() {
		switch (referee.getRule()) {
		case RULE_DEBUG:
			return new SingleRequestSemaphore(cm);
		
		// TODO 更多的规则, 见 RuleConductor

		default:
			return new MultiRequsetSemaphore(cm);
		}
	}

}
