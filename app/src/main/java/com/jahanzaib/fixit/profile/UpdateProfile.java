package com.jahanzaib.fixit.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jahanzaib.fixit.BaseActivity;
import com.jahanzaib.fixit.R;
import com.jahanzaib.fixit.login.MainActivity;
import com.jahanzaib.fixit.login.SessionManagerPOS;
import com.jahanzaib.fixit.utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jahanzaib on 1/12/17.
 */

public class UpdateProfile extends BaseActivity implements View.OnClickListener {

	//
	public static final String TAG = UpdateProfile.class.getSimpleName();
	//
	//storage permission code
//	private static final int STORAGE_PERMISSION_CODE = 123;
	//
	private static final String URL_REQUEST = "https://fixit-xubiey.c9users.io/update_user_profile.php";
	//
	//
	private static final String KEY_USERID = "user_id";
	private static final String KEY_FIRSTNAME = "first_name";
	private static final String KEY_LASTNAME = "last_name";
	private static final String KEY_IMAGE = "image";
	private static final String KEY_LOCATION = "location";
	//
	//
	@InjectView(R.id.user_profile_image)
	ImageView userProfile;
	@InjectView(R.id.firstname_editText)
	EditText firstNameEt;
	@InjectView(R.id.lastname_editText)
	EditText lastNameEt;
	@InjectView(R.id.update_user_location)
	Spinner locationSpinner;
	//
	//
	private SharedPreferences sharedPreferences;
	private SessionManagerPOS sessionManagerPOS;

	private int id;
	//
	//Bitmap to get image from gallery
	private Bitmap bitmap;
	//Image request code
	private int PICK_IMAGE_REQUEST = 1;
	//Uri to store the image uri
	private Uri filePath;
	private String image = null;
	private String fName = null;
	private String lName = null;
	private String location = null;
	//	private List<User> userContent;
	private ArrayList<String> locationArray = null;
	private String newLocation = null;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_profile);
		ButterKnife.inject(this);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);


		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sessionManagerPOS = new SessionManagerPOS(this);


		String fn = sessionManagerPOS.getUserDetails().get(KEY_FIRSTNAME);
		String ln = sessionManagerPOS.getUserDetails().get(KEY_LASTNAME);
		String l = sessionManagerPOS.getUserDetails().get(KEY_LOCATION);
		String i = sessionManagerPOS.getUserDetails().get(KEY_USERID);


		Log.d(TAG, "onCreate: ...........id.....................++++++++++++++++" + i);
		Log.d(TAG, "onCreate: ...........FNAME..................++++++++++++++++" + fn);
		Log.d(TAG, "onCreate: ...........lName..................++++++++++++++++" + ln);
		Log.d(TAG, "onCreate: ...........location...............++++++++++++++++" + l);

		//__________________________________________________________________________________________
		id = sessionManagerPOS.getId().get("id");
		String fullName = sessionManagerPOS.getUserDetails().get("name");
		location = sessionManagerPOS.getUserDetails().get("location");

		int indexLName = fullName.indexOf(" ");
		fName = fullName.substring(0, indexLName);
		lName = fullName.substring(indexLName + 1);
		//________________________________________________________________________________________


		// Spinner Drop down elements
		locationArray = new ArrayList<String>();
		locationArray.add("Liaquatabad, Karachi, Pakistan");
		locationArray.add("Ancholi, Karachi, Pakistan");
		locationArray.add("Bahadurabad, Karachi, Pakistan");
		locationArray.add("Dhoraji, Karachi, Pakistan");
		locationArray.add("Dalmia, Karachi, Pakistan");
		locationArray.add("Water pump, Karachi, Pakistan");

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapterLocation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationArray);
		// Drop down layout style - list view with radio button
		dataAdapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		locationSpinner.setAdapter(dataAdapterLocation);
		locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				newLocation = locationArray.get(i);
				Toast.makeText(getApplicationContext(), "" + locationArray.get(i), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				Toast.makeText(getApplicationContext(), "Please Select Category", Toast.LENGTH_SHORT).show();
			}
		});


		//________________________________________________________________________________________


		//int myLocation = Integer.parseInt(location);
//		Log.d(TAG, "onCreate: MY LOCATION No :  " + myLocation);
		Log.d(TAG, "onCreate: LOCATION OF USER :  " + location);
		Log.d(TAG, "onCreate: LOCATION OF CURRENT SPINNER :  " + newLocation);
		firstNameEt.setText(fName);
		lastNameEt.setText(lName);
//		locationSpinner.setSelection(myLocation);
	}

	@Override
	public void onClick(View view) {
		showFileChooser();
	}

	public String getStringImage(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] imageBytes = baos.toByteArray();
		String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
		return encodedImage;
	}

	private void showFileChooser() {
		Toast.makeText(this, "You Clicked", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_update_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.update_my_profile) {
			updateProfile();
		}

		if (id == R.id.discard) {
			discardEveryThing();
		}

		if (id == R.id.home)
			NavUtils.navigateUpFromSameTask(UpdateProfile.this);
		return super.onOptionsItemSelected(item);
	}

	private void discardEveryThing() {
		startActivity(new Intent(getBaseContext(), MainActivity.class));
		finish();
	}

	private void updateProfile() {


		showProgressDialog();

		StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REQUEST,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String s) {
						hideProgressDialog();
						Toast.makeText(UpdateProfile.this, "Successfully Updated!", Toast.LENGTH_SHORT).show();
						Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
						startActivity(new Intent(getBaseContext(), MainActivity.class));
						finish();
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						hideProgressDialog();

						String message = null;
						if (volleyError instanceof NetworkError) {
							message = "Cannot connect to Internet...Please check your connection!";
							Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
						} else if (volleyError instanceof ServerError) {
							message = "The server could not be found. Please try again after some time!!";
							Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
						} else if (volleyError instanceof AuthFailureError) {
							message = "Cannot connect to Internet...Please check your connection!";
							Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();

						} else if (volleyError instanceof ParseError) {
							message = "Parsing error! Please try again after some time!!";
							Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();

						} else if (volleyError instanceof NoConnectionError) {
							message = "Cannot connect to Internet...Please check your connection!";
							Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();

						} else if (volleyError instanceof TimeoutError) {
							message = "Connection TimeOut! Please check your internet connection.";
							Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
						}


						NetworkResponse networkResponse = volleyError.networkResponse;
						String errorMessage = "Unknown error";
						if (networkResponse == null) {
							if (volleyError.getClass().equals(TimeoutError.class)) {
								errorMessage = "Request timeout";
								Toast.makeText(getApplicationContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
							} else if (volleyError.getClass().equals(NoConnectionError.class))
								errorMessage = "Failed to connect server";
							Toast.makeText(getApplicationContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
						} else {
							String result = new String(networkResponse.data);
							try {
								JSONObject response = new JSONObject(result);
								String status = response.getString("status");
								message = response.getString("message");

								if (networkResponse.statusCode == 404) {
									errorMessage = "Resource not found";
									Toast.makeText(getApplicationContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();

								} else if (networkResponse.statusCode == 401) {
									errorMessage = message + " Please login again";
									Toast.makeText(getApplicationContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
								} else if (networkResponse.statusCode == 400) {
									errorMessage = message + " Check your inputs";
									Toast.makeText(getApplicationContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
								} else if (networkResponse.statusCode == 500) {
									errorMessage = message + " Something is getting wrong";
									Toast.makeText(getApplicationContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						volleyError.printStackTrace();

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				//Converting Bitmap to String

				if (bitmap == null)
					image = "";
				else
					image = getStringImage(bitmap);

				fName = firstNameEt.getText().toString().trim();
				lName = lastNameEt.getText().toString().trim();

				//Creating parameters
				Map<String, String> params = new Hashtable<String, String>();

				//Adding parameters
				params.put(KEY_USERID, String.valueOf(id));
				params.put(KEY_IMAGE, image);
				params.put(KEY_FIRSTNAME, fName);
				params.put(KEY_LASTNAME, lName);
				params.put(KEY_LOCATION, newLocation);
				//returning parameters
				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}
}

