package com.zdream.pmw.platform.test;

import com.zdream.pmw.util.json.JsonBuilder;
import com.zdream.pmw.util.json.JsonValue;

/**
 * 
 * @author Zdream
 */
public class TestJson {

	public static void main(String[] args) {
		JsonBuilder b = new JsonBuilder();
		JsonValue v = b.parseJson("{\"a\":666,\"b\":777}");
		System.out.println(v);
		
		v = b.parseJson("{\"a\":666,}");
		System.out.println(v);
		
		v = b.parseJson("[{\"a\":666,},{\"q-6\":true,}]");
		System.out.println(v);
		
		v = b.parseJson("[{\"a\":666,},{\"q-6\":true,\"ccc\":[84,null,566,1,{}]}]");
		System.out.println(v);
		
		v = b.parseJson("[{\"s\":7.5,},{\"q-6\":null,\"ccc\":[84,null,\" \",1,{}],\"s\":[],}]");
		System.out.println(v);

	}

}
