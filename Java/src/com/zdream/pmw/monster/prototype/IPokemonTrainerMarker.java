package com.zdream.pmw.monster.prototype;

/**
 * 精灵标签的默认值<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月15日
 * @version v0.2
 */
public interface IPokemonTrainerMarker {
	/**
	 * 在标签列表中，以下是列表中的索引号
	 * {circle, triangle, square, heart, star, diamond}
	 */
	public static final int MARKER_CIRCLE = 0, 
			MARKER_TRIANGLE = 1,
			MARKER_SQUARE = 2,
			MARKER_HEART = 3,
			MARKER_STAR = 4,
			MARKER_DIAMOND = 5;
	
	/**
	 * 标签列表的长度
	 */
	public static final int MARKER_LENGTH = 6;
	
}
