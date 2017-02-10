package com.jahanzaib.fixit.post;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jahanzaib on 1/10/17.
 */

public class PosActivity extends BaseActivity implements View.OnClickListener {

    public static final int CAM_REQUEST = 1001;
    private static final String TAG = PosActivity.class.getSimpleName();
    //
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_RELATEDTO = "related_to";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_USERID = "user_id";
    //
    @InjectView(R.id.post_user_image)
    ImageView imageView;
    @InjectView(R.id.select_imagebutton)
    ImageButton selectImage;
    @InjectView(R.id.posting_floatingBtn)
    FloatingActionButton buttonUpload;
    @InjectView(R.id.post_description_edittext)
    EditText desc;
    @InjectView(R.id.category_spinner)
    Spinner categorySpin;
    @InjectView(R.id.location_spinner)
    Spinner locationSpin;
    //
    //
    SharedPreferences sharedPreferences;
    SessionManagerPOS sessionManagerPOS;
    String newLoc = "";
    String newCategory = "";
    //
    private String image = null;
    //
    private ArrayList<String> categories;
    private ArrayList<String> location;
    //
    //
    private String UPLOAD_URL = "https://fixit-xubiey.c9users.io/fixit_post.php";
    //Bitmap to get image from gallery
    private Bitmap bitmap;
    //Image request code
    private int PICK_IMAGE_REQUEST = 1;
    //Uri to store the image uri
    private Uri filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.inject(this);

        selectImage.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);


        // Spinner Drop down elements
        categories = new ArrayList<String>();
        categories.add("K-Electric");
        categories.add("Traffic Police");
        categories.add("KWSB (Karachi water & senatary board)");
        categories.add("KMC (Karachi metroplitant copration) ");
        categories.add("SSGC");
        categories.add("KDA (Karachi development authority)");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterCategories = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        categorySpin.setAdapter(dataAdapterCategories);
        categorySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newCategory = categories.get(i);
                Toast.makeText(PosActivity.this, "" + categories.get(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(PosActivity.this, "Please Select Category", Toast.LENGTH_SHORT).show();
            }
        });

        // Spinner Drop down elements
        location = new ArrayList<String>();
        location.add("Liaquatabad, Karachi, Pakistan");
        location.add("Ancholi, Karachi, Pakistan");
        location.add("Bahadurabad, Karachi, Pakistan");
        location.add("Dhoraji, Karachi, Pakistan");
        location.add("Dalmia, Karachi, Pakistan");
        location.add("Water pump, Karachi, Pakistan");
        location.add("Hasan square, Karachi, Pakistan");
        location.add("Gulshan Iqbal, Karachi, Pakistan");
        location.add("Korangi, Karachi, Pakistan");
        location.add("Malir, Karachi, Pakistan");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterLocation = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, location);
        // Drop down layout style - list view with radio button
        dataAdapterLocation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        locationSpin.setAdapter(dataAdapterLocation);
        locationSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(PosActivity.this, "" + location.get(i), Toast.LENGTH_SHORT).show();
                newLoc = location.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(PosActivity.this, "Please Select Location", Toast.LENGTH_SHORT).show();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sessionManagerPOS = new SessionManagerPOS(this);

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void posting() {

        if (desc.getText().toString().isEmpty()) {
            desc.setError("Required!");
            return;
        }

        if (bitmap == null) {
            Toast.makeText(this, "Please select image!", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hideProgressDialog();
                        Log.d(TAG, "onResponse: " + s);
                        startActivity(new Intent(getApplication(), MainActivity.class));
                        finish();
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideProgressDialog();
                        Log.d(TAG, "onErrorResponse: " + volleyError);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String

                if (bitmap == null) {
                    image = "";
                } else {
                    image = getStringImage(bitmap);
                }

                Log.d(TAG, "getParams: " + "image :" + image);

                int id = sessionManagerPOS.getId().get("id");
                Log.d(TAG, "getParams: " + id);
                //Getting Image Name
                String description = desc.getText().toString().trim();

                Log.d(TAG, "getParams:Description " + description);
                Log.d(TAG, "getParams:LOCATION.. " + newLoc);
                Log.d(TAG, "getParams:Category.. " + newCategory);

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_USERID, String.valueOf(id));
                params.put(KEY_DESCRIPTION, description);
                params.put(KEY_RELATEDTO, String.valueOf(newCategory));
                params.put(KEY_LOCATION, String.valueOf(newLoc));

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                selectImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CAM_REQUEST && resultCode == RESULT_OK) {
            String path = "sdcard/camera_app/cam.jpg";
            Drawable fromPath = Drawable.createFromPath(path);
            selectImage.setImageDrawable(Drawable.createFromPath(path));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.select_imagebutton:
                showFileChooser();
                break;

            case R.id.posting_floatingBtn:
                posting();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.capture) {
            Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = getFile();
            in.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(in, CAM_REQUEST);
        }
        return super.onOptionsItemSelected(item);
    }

    public File getFile() {

        File folder = new File("sdcard/camera_app");
        if (!folder.exists()) {
            folder.mkdir();
        }

        File image = new File(folder, "cam.jpg");
        return image;
    }
}
