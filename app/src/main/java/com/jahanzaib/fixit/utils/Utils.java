package com.jahanzaib.fixit.utils;

import org.json.JSONObject;

/**
 * Created by Jahanzaib on 1/14/17.
 */

public class Utils {

	public static boolean contains(JSONObject jsonObject, String key) {
		return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
	}

}
