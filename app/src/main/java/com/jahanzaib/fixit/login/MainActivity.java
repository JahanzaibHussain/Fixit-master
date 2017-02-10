package com.jahanzaib.fixit.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jahanzaib.fixit.BaseActivity;
import com.jahanzaib.fixit.LoginActivity;
import com.jahanzaib.fixit.R;
import com.jahanzaib.fixit.post.PosActivity;
import com.jahanzaib.fixit.profile.UpdateProfile;
import com.jahanzaib.fixit.utils.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.jahanzaib.fixit.utils.Utils.contains;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_IMAGE = "image";
    private static final String TAG = MainActivity.class.getSimpleName();
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final String KEY_USERID = "user_id";
    SessionManagerPOS sessionManagerPOS;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tabs)
    TabLayout tabLayout;
    @InjectView(R.id.viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.fab_new_post)
    FloatingActionButton fab;
    SharedPreferences sharedPreferences;
    ///////////////////////////////////////////////////////////////////////////////////////////////
//		private String currImage;
//	private String currLocation;
    //////////////////////////////////////////////////////////////////////////////////////////////
    private TextView NameTV, EmailTV;
    private ImageView userImage;

    //Bitmap to get image from gallery
    private Bitmap bitmap;
    //Image request code
    private int PICK_IMAGE_REQUEST = 1;
    //Uri to store the image uri
    private Uri filePath;
    private String UPLOAD_URL = "https://fixit-xubiey.c9users.io/fixit_user_profile.php?user_id=";
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private int id;
    private String email;
    private String fName;
    private String lName;
    private String location;
    private String image;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.inject(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionManagerPOS = new SessionManagerPOS(this);

        id = sessionManagerPOS.getId().get("id");
        email = sessionManagerPOS.getUserDetails().get("email");
        String fullName = sessionManagerPOS.getUserDetails().get("name");
        location = sessionManagerPOS.getUserDetails().get("location");

        int indexLName = fullName.indexOf(" ");
        fName = fullName.substring(0, indexLName);
        lName = fullName.substring(indexLName + 1);

        Log.d(TAG, "onCreate: ...........id.....................++++++++++++++++" + id);
        Log.d(TAG, "onCreate: ...........FNAME..................++++++++++++++++" + fName);
        Log.d(TAG, "onCreate: ...........FNAME..................++++++++++++++++" + email);
        Log.d(TAG, "onCreate: ...........lName..................++++++++++++++++" + lName);
        Log.d(TAG, "onCreate: ...........location...............++++++++++++++++" + location);


        //________________________________________________________________________________________
        setSupportActionBar(toolbar);
        sendRequest(image, fName, lName, location);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), PosActivity.class));
            }
        });

        // Create the adapter that will return a fragment for each section
        SimpleFragmentPager adapter = new SimpleFragmentPager(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        NameTV = (TextView) header.findViewById(R.id.header_user_name);
        userImage = (ImageView) header.findViewById(R.id.header_user_image);
        EmailTV = (TextView) header.findViewById(R.id.header_user_email);

//		NameTV.setText(fullName);
        EmailTV.setText(email);
    }


    //____________________________________________________________________________________________
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            if (id == R.id.logout) {

                sharedPreferences = getSharedPreferences("MY", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                sessionManagerPOS.logoutUser();
                Log.d(TAG, "Now log out and start the activity login");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_userid)
            item.setTitle("I.D : " + id);

        if (id == R.id.menu_user_Name)
            item.setTitle("Name : " + fName + "" + lName);


        if (id == R.id.menu_user_Location)
            item.setTitle("Location : " + location);

        if (id == R.id.settings) {
            startActivity(new Intent(this, UpdateProfile.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendRequest(final String image, final String fName, final String lName, final String location) {

        String url = UPLOAD_URL + id;

        JsonArrayRequest stringRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        hideProgressDialog();
                        parseJSONResponse(response, image, fName, lName, location);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideProgressDialog();
                        NetworkResponse networkResponse = volleyError.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (volleyError.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (volleyError.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String status = response.getString("status");
                                String message = response.getString("message");

                                Log.e("Error Status", status);
                                Log.e("Error Message", message);

                                if (networkResponse.statusCode == 404)
                                    errorMessage = "Resource not found";
                                else if (networkResponse.statusCode == 401)
                                    errorMessage = message + " Please login again";
                                else if (networkResponse.statusCode == 400)
                                    errorMessage = message + " Check your inputs";
                                else if (networkResponse.statusCode == 500)
                                    errorMessage = message + " Something is getting wrong";

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("Error", errorMessage);
                        volleyError.printStackTrace();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put(KEY_USERID, String.valueOf(id));

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void parseJSONResponse(JSONArray response, String image, String fName, String lName, String loc) {

        if (response != null && response.length() > 0) {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);

                    image = "NA";
                    fName = "NA";
                    lName = "NA";
                    loc = "NA";

                    if (contains(obj, "first_name"))
                        fName = obj.getString("first_name");
                    if (contains(obj, "last_name"))
                        lName = obj.getString("last_name");
                    if (contains(obj, "image_path"))
                        image = obj.getString("image_path");
                    if (contains(obj, "location"))
                        loc = obj.getString("location");
                    Log.d(TAG, "parseJSONResponse: LOCATION.......... " + loc);
                }

                NameTV.setText(fName + " " + lName);

                Glide.with(this).load("https://fixit-xubiey.c9users.io/uploads/" + id + "/profile_pic/" + image)
                        .asBitmap().centerCrop()
                        .into(new BitmapImageViewTarget(userImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplication().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                userImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            } catch (JSONException e) {
                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
            }
        }
    }
}