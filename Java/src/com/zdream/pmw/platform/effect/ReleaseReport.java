package com.zdream.pmw.platform.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zdream.pmw.util.common.ArraysUtils;

/**
 * <p>技能释放生成的报告
 * 
 * @since v0.2.3 [2017-04-26]
 * @author Zdream
 * @version v0.2.3 [2017-04-26]
 */
public class ReleaseReport {
	
	HashMap<String, int[]> idxMapper;
	ArrayList<ReportItem> rplist;
	
	enum EReportType {
		HEAD,
		DATA,
		INFO
	}
	
	class ReportItem {
		EReportType type;
		String key;
		Object value;
		ReportItem(EReportType type, String key, Object value) {
			this.type = type;
			this.key = key;
			this.value = value;
		}
	}
	
	void putHead(String name) {
		if (name == null) {
			throw new NullPointerException("name can not be null.");
		}
		put(name, null, EReportType.HEAD);
	}
	
	void putData(String key, Object value) {
		if (key == null) {
			throw new NullPointerException("key can not be null.");
		}
		put(key, value, EReportType.DATA);
	}
	
	void putInfo(Object value) {
		if (value == null) {
			throw new NullPointerException("value can not be null.");
		}
		put(null, value, EReportType.INFO);
	}
	
	public Map<String, Object> getDatas(String head) {
		int idx = headPos(head);
		if (idx == -1) {
			return null;
		}
		
		final int len = rplist.size();
		HashMap<String, Object> map = new HashMap<>();
		
		LOOP:
		for (int i = idx + 1; i < len; i++)
		{
			ReportItem item = rplist.get(idx);
			switch (item.type) {
			case HEAD:
				break LOOP;
			case DATA:
				map.put(item.key, item.value);
				break;
			default:
				break;
			}
		}
		return map;
	}
	
	public Object getData(String key) {
		int idx = pos(key);
		if (idx == -1) {
			return null;
		}
		ReportItem item = rplist.get(idx);
		if (item.type != EReportType.DATA) {
			throw new IllegalArgumentException(key + " is not a key of head!");
		}
		
		return item.value;
	}
	
	public Object[] getDataRecords(String key) {
		int[] idxs = poses(key);
		if (idxs == null) {
			return null;
		}
		Object[] os = new Object[idxs.length];
		int count = 0;
		
		for (int i = 0; i < idxs.length; i++) {
			int idx = idxs[i];
			ReportItem item = rplist.get(idx);
			if (item.type != EReportType.DATA) {
				continue;
			}
			
			os[count++] = item.value;
		}
		if (count != os.length) {
			Object[] os1 = new Object[count];
			System.arraycopy(os, 0, os1, 0, count);
			return os1;
		}
		return os;
	}
	
	int headPos(String key) throws IllegalArgumentException {
		int idx = pos(key);
		if (idx == -1) {
			return -1;
		}
		if (rplist.get(idx).type != EReportType.HEAD) {
			throw new IllegalArgumentException(key + " is not a key of head!");
		}
		return idx;
	}
	
	int pos(String key) {
		int[] is = idxMapper.get(key);
		if (is == null) {
			return -1;
		}
		return is[0];
	}
	
	int[] poses(String key) {
		return idxMapper.get(key);
	}
	
	private void put(String key, Object value, EReportType type) {
		if (key != null) {
			int[] is = idxMapper.get(key);
			if (is == null) {
				is = new int[]{rplist.size()};
			} else {
				int[] ns = new int[is.length + 1];
				System.arraycopy(is, 0, ns, 1, is.length);
				ns[0] = rplist.size();
				is = ns;
			}
			idxMapper.put(key, is);
		}
		rplist.add(new ReportItem(type, key, value));
	}
	
	public void clear() {
		idxMapper.clear();
		rplist.clear();
	}
	
	public ReleaseReport() {
		idxMapper = new HashMap<>();
		rplist = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		final int len = rplist.size();
		StringBuilder b = new StringBuilder(len * 32);
		for (int i = 0; i < len; i++) {
			ReportItem item = rplist.get(i);
			switch (item.type) {
			case HEAD:
				b.append("<<").append(item.key).append(">>").append('\n').append('\t');
				break;
			case DATA:
				b.append('\t').append(item.key).append(':');
				if (item.value.getClass().isArray()) {
					b.append(ArraysUtils.toString(item.value));
				} else {
					b.append(item.value);
				}
				b.append('\n').append('\t');
				break;
			case INFO:
				b.append('\t').append(">> ").append(item.value).append('\n').append('\t');
				break;
			}
		}
		
		return b.toString();
	}

}
