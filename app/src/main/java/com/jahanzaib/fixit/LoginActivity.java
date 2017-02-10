package com.jahanzaib.fixit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.jahanzaib.fixit.login.MainActivity;
import com.jahanzaib.fixit.login.SessionManagerPOS;
import com.jahanzaib.fixit.profile.EtsJSONParser;
import com.jahanzaib.fixit.profile.User;
import com.jahanzaib.fixit.utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends BaseActivity {


	public static final String KEY_USERNAME = "username";
	public static final String KEY_EMAIL = "email";
	private static final String KEY_PASSWORD = "password";
	private static final String TAG = "LoginActivity";
	private static final int REQUEST_SIGNUP = 0;
	//	private static final String URL_REQUEST = "https://fixit-xubiey.c9users.io/fixit_login.php";
	private static final String URL_REQUEST = "https://fixit-xubiey.c9users.io/fixit_login.php";


	@InjectView(R.id.input_email)
	EditText _emailText;
	@InjectView(R.id.input_password)
	EditText _passwordText;
	@InjectView(R.id.btn_login)
	Button _loginButton;
	@InjectView(R.id.link_signup)
	TextView _signupLink;

	SessionManagerPOS sessionManager;
	private List<User> userContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		ButterKnife.inject(this);

		sessionManager = new SessionManagerPOS(this);

		_loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {


				String EMAIL_PATTERN =
						"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
								+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

				Pattern email_pattren = Pattern.compile(EMAIL_PATTERN);
				Matcher matcher_email = email_pattren.matcher(_emailText.getText());

				if (!matcher_email.matches()) {
					_emailText.setError("Invalid Email");
					return;
				}


				if (_emailText.getText().length() < 3 || _emailText.getText().length() > 30 ||
						!_emailText.getText().toString().contains("@") || !_emailText.getText().toString().contains(".com")) {
					alert("Invalid Email or Password");
					_loginButton.setEnabled(true);
					return;
				}


				if (_passwordText.getText().length() < 6 || _passwordText.getText().length() > 20) {
					alert("Invalid Email or Password");
					_loginButton.setEnabled(true);
					return;
				}

				if (checkNetwork()) {
					loginUser(_emailText.getText().toString(), _passwordText.getText().toString());
				} else {
					Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
				}
			}

		});
		_signupLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Start the Signup activity
				Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
				startActivityForResult(intent, REQUEST_SIGNUP);
			}
		});

		if (sessionManager.isUserLoggedIn()) {
			Intent i = new Intent(LoginActivity.this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			finish();
		}
	}


	private void loginUser(final String userEmail, final String userPass) {
		showProgressDialog();
		// TODO: Implement your own signup logic here.
		StringRequest stringRequest = new StringRequest(
				Request.Method.POST,
				URL_REQUEST,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						hideProgressDialog();
						//Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
						if (response.contains("first_name") && response.contains("last_name") && response.contains("id") && response.contains("email")) {
							userContent = EtsJSONParser.parse(response, userEmail, userPass);
							int id = userContent.get(0).getID();
							String firstName = userContent.get(0).getFIRST_NAME();
							String lastName = userContent.get(0).getLAST_NAME();
							String email = userContent.get(0).getEMAIL();
							String password = userContent.get(0).getPASSWORD();
							String location = userContent.get(0).getLOCATION();
							String full_name = firstName + " " + lastName;
							sessionManager.createUserLoginSession(id, full_name, email, password, location);
							startActivity(new Intent(getApplication(), MainActivity.class));
							hideProgressDialog();
							finish();
						} else {
							_loginButton.setVisibility(View.VISIBLE);
							_passwordText.setText("");
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						hideProgressDialog();
						Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
						_loginButton.setVisibility(Button.VISIBLE);
						String message = null;
						if (volleyError instanceof NetworkError) {
							message = "Cannot connect to Internet...Please check your connection!";
							Toast.makeText(getApplication(), "" + message, Toast.LENGTH_SHORT).show();
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
							//	Toast.makeText(getApplicationContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
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

				params.put(KEY_PASSWORD, userPass);
				params.put(KEY_EMAIL, userEmail);

				return params;
			}

			@Override
			public Priority getPriority() {
				return Priority.IMMEDIATE;
			}
		};
		AppController.getInstance().addToRequestQueue(stringRequest);
	}


	public boolean validate() {
		boolean valid = true;

		String email = _emailText.getText().toString();
		String password = _passwordText.getText().toString();

		if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			_emailText.setError("enter a valid email address");
			valid = false;
		} else {
			_emailText.setError(null);
		}
		return valid;
	}

	public void onLoginFailed() {
		Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
		_loginButton.setEnabled(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SIGNUP) {
			if (resultCode == RESULT_OK) {
				// TODO: Implement successful signup logic here
				// By default we just finish the Activity and log them in automatically
				this.finish();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sessionManager.isUserLoggedIn()) {
			finish();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (sessionManager.isUserLoggedIn()) {
			finish();
		}
	}


	private void alert(String message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

	public boolean checkNetwork() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}
}