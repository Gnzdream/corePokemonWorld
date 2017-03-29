package com.zdream.pmw.platform.test;

import java.util.Scanner;

import com.zdream.pmw.platform.translate.MessageTranslator;

public class TestTranslate {

	public static void main(String[] args) {
		MessageTranslator t = new MessageTranslator();
		Scanner scan = null;
		while (true) {
			scan = new Scanner(System.in);
			System.out.print(">>> ");
			String str = scan.nextLine();
			
			if (str.trim().length() == 0) {
				continue;
			}
			if (str.equals("exit")) {
				break;
			}
			System.out.println(t.translate(str));
		}
		scan.close();
	}

}
