package com.zdream.pmw.platform.test;

import com.zdream.pmw.util.random.RanValue;

/**
 * 
 * @author Zdream
 */
public class TestMore {
	
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(!RanValue.isBigger(0, 16));
		}
	}
	
}
