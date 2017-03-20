package com.zdream.pmw.monster.prototype;

/**
 * 精灵性格<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月14日
 * @version v0.2
 */
public enum EPokemonNature implements IPokemonDataType {
	HARDY("努力", (byte) 1, (byte) 1),
	LONELY("孤僻", (byte) 1, (byte) 2),
	BRAVE("勇敢", (byte) 1, (byte) 3),
	ADAMANT("固执", (byte) 1, (byte) 4),
	NAUGHTY("顽皮", (byte) 1, (byte) 5),
	BOLD("大胆", (byte) 2, (byte) 1),
	DOCILE("坦率", (byte) 2, (byte) 2),
	RELAXED("悠闲", (byte) 2, (byte) 3),
	IMPISH("淘气", (byte) 2, (byte) 4),
	LAX("无虑", (byte) 2, (byte) 5),
	TIMID("胆小", (byte) 3, (byte) 1),
	HASTY("急躁", (byte) 3, (byte) 2),
	SERIOUS("认真", (byte) 3, (byte) 3),
	JOLLY("开朗", (byte) 3, (byte) 4),
	NAIVE("天真", (byte) 3, (byte) 5),
	MODEST("谨慎", (byte) 4, (byte) 1),
	MILD("温和", (byte) 4, (byte) 2),
	QUIET("冷静", (byte) 4, (byte) 3),
	BASHFUL("害羞", (byte) 4, (byte) 4),
	RASH("马虎", (byte) 4, (byte) 5),
	CALM("沉着", (byte) 5, (byte) 1),
	GENTLE("温顺", (byte) 5, (byte) 2),
	SASSY("傲慢", (byte) 5, (byte) 3),
	CAREFUL("慎重", (byte) 5, (byte) 4),
	QUIRKY("浮躁", (byte) 5, (byte) 5);

	private String title;
	private byte upItem;
	private byte downItem;
	
	private EPokemonNature(String title, byte up, byte down){
		this.title = title;
		this.upItem = up;
		this.downItem = down;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public int getUpItem(){
		return this.upItem;
	}
	
	public int getDownItem(){
		return this.downItem;
	}
}
