package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.*;

public class Registry {
	private ArrayList<Member> memberArr;
	private File file;
	private JSONArray JSONArr = new JSONArray();

	public Registry() {
		this.memberArr = new ArrayList<Member>();
		this.file = new File(System.getProperty("user.home"),"members.json"); 
		if(!file.exists()) {
			try {
				file.createNewFile();// Creates a file in at the users computer
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Member> loadRegistry(){
		BufferedReader bf;
		try {
			// read the file if it consists anything. 
			bf = new BufferedReader(new FileReader(file.getPath()));
			String line;
			if((line = bf.readLine()) !=  null) {
				JSONArr = new JSONArray(line);
			}
			bf.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Parsing JSON to member.
		JSONObject jsObj;
		JSONArray jsBoats;
		for(int i = 0; i < JSONArr.length(); i++) {
			jsObj = JSONArr.getJSONObject(i);
			Member loadMember = new Member(jsObj.getString("name"), 
					jsObj.getString("persNR"),jsObj.getInt("id"));
			jsBoats = jsObj.getJSONArray("boats");
			for(int j = 0; j < jsBoats.length(); j++) {
				loadMember.addBoat(jsBoats.getJSONObject(j).getInt("length")
						, Boat.boatType.valueOf(jsBoats.getJSONObject(j).getString("boatType")));
			}
			memberArr.add(loadMember);
		}

		return memberArr;
	}
	public void addMember(Member member) {
		JSONObject JSONMember = parseToJSONObject(member);
		
		JSONArr.put(JSONMember); // Add member to the JSON Array and overwrite the file with the new array.
		FileWriter writer;
		try {
			writer = new FileWriter (file);
			writer.write(JSONArr.toString());
			writer.flush();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void changeMember(Iterator<Member> members) {
		JSONArr = new JSONArray(); // Clear the JSON array list and add all members.
		while(members.hasNext()){
			JSONArr.put(parseToJSONObject(members.next()));
		}
		//Write JSON array to file.
		FileWriter writer;
		try {
			writer = new FileWriter (file);
			writer.write(JSONArr.toString());
			writer.flush();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private JSONObject parseToJSONObject(Member member) {
		JSONObject JSONMember = new JSONObject();
		JSONArray JSONBoats = new JSONArray();
		
		//Parsing member to JSON 
		JSONMember.put("name", member.getName());
		JSONMember.put("persNR", member.getPersonalNR());
		JSONMember.put("id", member.getId());
		
		Iterator<Boat> itr = member.iterator();
		
		while(itr.hasNext()) {
			Boat nextBoat = itr.next();
			JSONObject JSONBoat = new JSONObject();
			JSONBoat.put("length", nextBoat.getLength());
			JSONBoat.put("boatType", nextBoat.getType());
			JSONBoats.put(JSONBoat);
		}
		
		JSONMember.put("boats", JSONBoats);
		
		return JSONMember;
	}
}
