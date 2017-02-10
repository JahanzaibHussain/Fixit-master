package com.jahanzaib.fixit.profile;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahanzaib on 1/8/17.
 */

public class EtsJSONParser {

	public static final String TAG = EtsJSONParser.class.getSimpleName();

	public static List<User> parse(String response, String email, String password) {

		email = "";
		password = "";
		int id = 0;
		String fname = "";
		String lname = "";
		String location = "";

		try {
			JSONArray obj = new JSONArray(response);

			List<User> users = new ArrayList<>();

			User user = new User();

			for (int i = 0; i < obj.length(); i++) {

				JSONObject jsonObject = obj.getJSONObject(i);

				if (contains(jsonObject, "id")) {
					id = jsonObject.getInt("id");
					user.setID(id);
				}
				if (contains(jsonObject, "username"))
					user.setUSER_NAME(jsonObject.getString("username"));

				if (contains(jsonObject, "first_name")) {
					fname = jsonObject.getString("first_name");
					user.setFIRST_NAME(fname);
				}
				if (contains(jsonObject, "last_name")) {
					lname = jsonObject.getString("last_name");
					user.setLAST_NAME(lname);
				}
				if (contains(jsonObject, "email")) {
					email = jsonObject.getString("email");
					user.setEMAIL(email);
				}
				if (contains(jsonObject, "password")) {
					password = jsonObject.getString("password");
					user.setPASSWORD(password);
				}
				if (contains(jsonObject, "location")) {
					location = jsonObject.getString("location");
					user.setLOCATION(location);
				}

//				user.setLOCATION(obj.getString("location"));

//
//				user.setIS_ACTIVE(obj.getString("is_active"));
//				user.setCREATED_AT(obj.getString("created_at"));

				users.add(user);
				Log.d(TAG, "parse: " + user);
			}

			return users;


		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean contains(JSONObject jsonObject, String key) {
		return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
	}
}
