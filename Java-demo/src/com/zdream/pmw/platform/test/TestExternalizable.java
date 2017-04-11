package com.zdream.pmw.platform.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.zdream.pmw.monster.data.PokemonBaseData;
import com.zdream.pmw.monster.data.PokemonDataBuffer;
import com.zdream.pmw.monster.data.SkillDataBuffer;
import com.zdream.pmw.monster.skill.Skill;

/**
 * 
 * @author Zdream
 * @version v0.2.2
 */
public class TestExternalizable {

	public static void main(String[] args) {
		TestExternalizable t = new TestExternalizable();
		
		try {
			// t.writePokemonBaseDataToFile((short) 1, (byte) 0, "logs/pm_data_1_0.log");
			// System.out.println("write successful");
			
			// PokemonBaseData data = t.readPokemonBaseDataFromFile("logs/pm_data_1_0.log");
			// System.out.println(data.getSpeciesName());
			
			t.writeSkillToFile((short) 172, "logs/skill_data_1.log");
			System.out.println("write successful");
			
			Skill data = t.readSkillFromFile("logs/skill_data_1.log");
			System.out.println(data.getTitle());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	void writePokemonBaseDataToFile(short speciesID, byte form, String filepath)
			throws FileNotFoundException, IOException {
		PokemonDataBuffer buffer = PokemonDataBuffer.getInstance();
		PokemonBaseData data = buffer.getBaseData(speciesID, form);
		
		FileOutputStream outStream = new FileOutputStream(filepath);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

		objectOutputStream.writeObject(data);
		outStream.close();
	}
	
	PokemonBaseData readPokemonBaseDataFromFile(String filepath)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath));
		PokemonBaseData data = (PokemonBaseData) in.readObject();
		
		in.close();
		return data;
	}
	
	void writeSkillToFile(short SkillId, String filepath)
			throws FileNotFoundException, IOException {
		SkillDataBuffer buffer = SkillDataBuffer.getInstance();
		Skill data = buffer.getBaseData(SkillId);
		
		FileOutputStream outStream = new FileOutputStream(filepath);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

		objectOutputStream.writeObject(data);
		outStream.close();
	}
	
	Skill readSkillFromFile(String filepath)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath));
		Skill data = (Skill) in.readObject();
		
		in.close();
		return data;
	}

}
