/**
 * <p>指挥部分所在包</p>
 * <p>在版本 v0.2.3 之后, 将每次伤害的实现进行拆分,
 * 像确定技能、范围、判定属性免疫、命中、能否发动等
 * 都视为一个个判定流程的子流程. 这些子流程将视为
 * 一个个事件逐个进行判断和执行.</p>
 * 
 * @since v0.2.3 [2017-04-21]
 * @author Zdream
 * @version v0.2.3 [2017-04-21]
 */
package com.zdream.pmw.platform.effect.instruction;