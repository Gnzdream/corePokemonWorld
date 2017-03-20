package com.zdream.pmw.platform.test;

import com.zdream.pmw.platform.translate.MessageTranslator;

public class TestTranslate {

	public static void main(String[] args) {
		MessageTranslator t = new MessageTranslator();
		
		System.out.println(t.translate("entrance x1 x-2 x=3* \"56 5\" 9/9"));
	}

}
