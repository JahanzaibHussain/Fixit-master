package com.jahanzaib.fixit;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
	@Test
	public void useAppContext() throws Exception {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getTargetContext();

		assertEquals("com.jahanzaib.fixit", appContext.getPackageName());
	}

//	@Test
//	public void CheckResponce() throws Exception {
//		StringRequest stringRequest = new StringRequest(
//				"https://fixit-xubiey.c9users.io/fixit_user_posts.php?user_id=1",
//				new Response.Listener<String>() {
//					@Override
//					public void onResponse(String response) {
//						assertEquals(response, "asdasd332r3r");
//					}
//				},
//				new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						assertEquals(error, "asdasd332r3r");
//					}
//				});
//	}
}
