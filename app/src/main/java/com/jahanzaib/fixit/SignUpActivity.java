package com.jahanzaib.fixit;

/**
 * Created by Minnu on 11/8/2016.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.jahanzaib.fixit.profile.User;
import com.jahanzaib.fixit.utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignUpActivity extends AppCompatActivity {

	private static final String KEY_FIRSTNAME = "first_name";
	private static final String KEY_LASTNAME = "last_name";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_LOCATION = "location";


	private static final String TAG = SignUpActivity.class.getSimpleName();
	private static final String URL_REQUEST = "https://fixit-xubiey.c9users.io/fixit_singup.php";


	@InjectView(R.id.first_input_name)
	EditText _firstNameText;

	@InjectView(R.id.last_input_name)
	EditText _lastNameText;

	@InjectView(R.id.input_email)
	EditText _emailText;

	@InjectView(R.id.input_password)
	EditText _passwordText;

	@InjectView(R.id.btn_signup)
	Button _signupButton;
	@InjectView(R.id.sign_up_spinner)
	Spinner _spinner;
	@InjectView(R.id.link_login)
	TextView _loginLink;
	//
	//
	private String newCategory = "";
	private ArrayList<String> location;
	private List<User> userContent;
	private String[] emailArr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		ButterKnife.inject(this);


		// Spinner Drop down elements
		location = new ArrayList<String>();
		location.add("Liaquatabad, Karachi, Pakistan");
		location.add("Ancholi, Karachi, Pakistan");
		location.add("Bahadurabad, Karachi, Pakistan");
		location.add("Dhoraji, Karachi, Pakistan");
		location.add("Dalmia, Karachi, Pakistan");
		location.add("Water pump, Karachi, Pakistan");

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapterLocation = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, location);
		// Drop down layout style - list view with radio button
		dataAdapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		_spinner.setAdapter(dataAdapterLocation);
		_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				newCategory = location.get(i);
				Toast.makeText(getApplicationContext(), "" + location.get(i), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				Toast.makeText(getApplicationContext(), "Please Select Category", Toast.LENGTH_SHORT).show();
			}
		});

		_signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signup();
			}
		});

		_loginLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Finish the registration screen and return to the Login activity
				finish();
			}
		});
	}

	public void signup() {
		Log.d(TAG, "Signup");

		if (newCategory == "Please select the location..") {
			Toast.makeText(this, "Please select the location..", Toast.LENGTH_SHORT).show();
			_signupButton.setEnabled(true);
			return;
		}


		String NAME_PATTERN = "^[A-Z][a-zA-Z]+$";

		Pattern p = Pattern.compile(NAME_PATTERN);
		Matcher matcher = p.matcher(_firstNameText.getText());

		if (!matcher.matches()) {
			_firstNameText.setError("Only Letters are allowed First Letter Should be Capital");
			return;
		}

		Matcher matcher_lastname = p.matcher(_lastNameText.getText());

		if (!matcher_lastname.matches()) {
			_lastNameText.setError("Only Letters are allowed First Letter Should be Capital");
			return;
		}

		String EMAIL_PATTERN =
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern email_pattren = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher_email = email_pattren.matcher(_emailText.getText());

		if (!matcher_email.matches()) {
			_emailText.setError("Invalid Email");
			return;
		}



		if (_firstNameText.getText().length() < 3) {
			_firstNameText.setError("First Name is too short");
			_signupButton.setEnabled(true);
			return;
		}

		if (_firstNameText.getText().length() > 12) {
			_firstNameText.setError("First Name is too long");
			_signupButton.setEnabled(true);
			return;
		}

		if (_lastNameText.getText().length() < 3) {
			_lastNameText.setError("Last Name is too short");
			_signupButton.setEnabled(true);
			return;
		}
		if (_lastNameText.getText().length() > 12) {
			_lastNameText.setError("Last Name is too long");
			_signupButton.setEnabled(true);
			return;
		}

		if (_emailText.getText().length() < 3
				|| _emailText.getText().length() > 40) {
			_emailText.setError("Email Invalid");
			_signupButton.setEnabled(true);
			return;
		}


		if (_passwordText.getText().length() < 6 || _passwordText.getText().length() > 40) {
			_passwordText.setError("Password Invalid");
			_signupButton.setEnabled(true);
			return;
		}


		final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Creating Account...");
		progressDialog.show();

		final String firstName = _firstNameText.getText().toString();
		final String lastName = _lastNameText.getText().toString();
		final String email = _emailText.getText().toString();
		final String password = _passwordText.getText().toString();


		// TODO: Implement your own signup logic here.
		StringRequest stringRequest = new StringRequest(
				Request.Method.POST,
				URL_REQUEST,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						progressDialog.dismiss();
						if (response.contains("SucFIXITcess")) {
							Toast.makeText(SignUpActivity.this, "Success" , Toast.LENGTH_LONG).show();
							startActivity(new Intent(getApplication(), LoginActivity.class));
							finish();
						}
						if (response.contains("FaiFIXITled")) {
							Toast.makeText(SignUpActivity.this, "Email Already Exist Please LogIn", Toast.LENGTH_SHORT).show();
							;
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						_signupButton.setVisibility(View.VISIBLE);
						progressDialog.dismiss();
						Toast.makeText(SignUpActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();

						String message = null;
						if (volleyError instanceof NetworkError) {
							message = "Cannot connect to Internet...Please check your connection!";
							Toast.makeText(getApplication(), "" + message, Toast.LENGTH_SHORT).show();
						} else if (volleyError instanceof ServerError) {
							Log.d(TAG, "onErrorResponse: " + volleyError);
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
			protected Map<String, String> getParams() {

				Map<String, String> params = new HashMap<String, String>();

				params.put(KEY_FIRSTNAME, firstName);
				params.put(KEY_LASTNAME, lastName);
				params.put(KEY_PASSWORD, password);
				params.put(KEY_EMAIL, email);
				params.put(KEY_LOCATION, String.valueOf(newCategory));


				return params;
			}
		};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}

	private void alert(String message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

//	public void onSignupSuccess() {
//		_signupButton.setEnabled(true);
//		setResult(RESULT_OK, null);
//		finish();
//	}


//	private void registerUser() {
//		final String username = editTextUsername.getText().toString().trim();
//		final String password = editTextPassword.getText().toString().trim();
//		final String email = editTextEmail.getText().toString().trim();
//
//
//	}

//	public void onSignupFailed() {
//		Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
//
//		_signupButton.setEnabled(true);
//	}
//
//	public boolean validate() {
//
//		boolean valid = true;
///*
//		String firstName = _firstNameText.getText().toString();
//		String lastName = _lastNameText.getText().toString();
//		String email = _emailText.getText().toString();
//		String password = _passwordText.getText().toString();
//
//		if (firstName.isEmpty() || firstName.length() < 3   ) {
//			_nameText.setError("at least 3 characters");
//			valid = false;
//		} else {
//			_nameText.setError(null);
//		}
//
//		if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//			_emailText.setError("enter a valid email address");
//			valid = false;
//		} else {
//			_emailText.setError(null);
//		}
//
//		if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
//			_passwordText.setError("between 4 and 10 alphanumeric characters");
//			valid = false;
//		} else {
//			_passwordText.setError(null);
//		}*/
//
//		return valid;
//	}
}