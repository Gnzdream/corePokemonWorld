package com.zdream.pmwdb.mongo.tofile;

import java.util.Comparator;

/**
 * 
 * @author Zdream
 */
class EJsonComparator implements Comparator<EJson> {

	@Override
	public int compare(EJson o1, EJson o2) {
		return o1.id - o2.id;
	}
}
