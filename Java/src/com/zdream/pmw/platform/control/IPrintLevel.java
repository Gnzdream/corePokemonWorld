package com.zdream.pmw.platform.control;

/**
 * DEBUG 详细信息输出流所给出的消息重要等级<br>
 * 
 * @since v0.1
 * @author Zdream
 * @date 2016年3月31日
 * @version v0.2
 */
public interface IPrintLevel {

	/**
	 * DEBUG 信息流严重错误信息等级
	 */
	int PRINT_LEVEL_ERROR = 1;
	/**
	 * DEBUG 信息流警告信息等级
	 */
	int PRINT_LEVEL_WARN = 2;
	/**
	 * DEBUG 信息流一般消息信息等级
	 */
	int PRINT_LEVEL_INFO = 3;
	/**
	 * DEBUG 信息流 DEBUG 信息等级
	 */
	int PRINT_LEVEL_DEBUG = 4;
	/**
	 * DEBUG 信息流详细信息等级
	 */
	int PRINT_LEVEL_VERBOSE = 5;

}