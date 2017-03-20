package com.zdream.pmwdb.mongo.dao;

import java.util.List;
import java.util.ListIterator;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.data.dao.IPokemonDataDao;
import com.zdream.pmw.monster.prototype.EPokemonType;
import com.zdream.pmwdb.mongo.MongoBase;

/**
 * 基于 Mongo 的怪兽基础数据 DAO 实现<br>
 * 
 * @since v0.2
 * @author Zdream
 * @date 2017年3月1日
 * @version v0.2
 */
public class PokemonDataDaoImpl implements IPokemonDataDao {

	@Override
	public PokemonBaseData getData(short speciesID) {
		Document document = new Document("species_id", speciesID);
		FindIterable<Document> it = MongoBase.getCollection("pm").find(document);
		
		return toModel(it);
	}

	@Override
	public PokemonBaseData getData(short speciesID, byte form) {
		Document document = new Document("species_id", speciesID)
				.append("form", form);
		FindIterable<Document> it = MongoBase.getCollection("pm").find(document);
		
		return toModel(it);
	}
	
	/* ************
	 *	私有方法  *
	 ************ */
	
	private PokemonBaseData toModel(FindIterable<Document> it) {
		
		MongoCursor<Document> cursor = it.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			
			PokemonBaseData data = new PokemonBaseData();
			data.setSpeciesID(doc.getInteger("species_id").shortValue());
			data.setForm(doc.getInteger("form").byteValue());
			// data.setAbility1(rs.getShort(1));
			// data.setAbility2(rs.getShort(2));
			// data.setAbilitySecret(rs.getShort(3));
			
			// 属性
			List<?> typearr = (List<?>) doc.get("type", List.class);
			EPokemonType[] types = new EPokemonType[typearr.size()];
			for (ListIterator<?> ittype = typearr.listIterator(); ittype.hasNext();) {
				Object obj = ittype.next();
				String typestr = obj.toString();
				
				EPokemonType type = EPokemonType.parseEnum(typestr);
				types[ittype.previousIndex()] = type;
			}
			data.setTypes(types);
			
			// 种族值
			Document valdoc = doc.get("val", Document.class);
			short[] speciesValue = new short[]
					{
							valdoc.getInteger("hp").shortValue(),
							valdoc.getInteger("at").shortValue(),
							valdoc.getInteger("df").shortValue(),
							valdoc.getInteger("sa").shortValue(),
							valdoc.getInteger("sd").shortValue(),
							valdoc.getInteger("sp").shortValue()
					};
			data.setSpeciesValue(speciesValue);
			
			data.setWt(doc.getDouble("wt").floatValue());
			data.setSpeciesName(doc.getString("species_name"));
			
			return data;
		}
		
		return null;
	}
	
	/* ************
	 *	  测试    *
	 ************ */
	
	public static void main(String[] args) {
		PokemonDataDaoImpl i = new PokemonDataDaoImpl();
		System.out.println(i.getData((short) 1));
	}

}
