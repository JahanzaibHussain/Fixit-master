package com.jahanzaib.fixit.location;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jahanzaib.fixit.BaseActivity;
import com.jahanzaib.fixit.R;
import com.jahanzaib.fixit.login.SessionManagerPOS;
import com.jahanzaib.fixit.utils.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.jahanzaib.fixit.utils.Utils.contains;

/**
 * Created by Jahanzaib on 12/21/16.
 */

public class LocationActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = LocationActivity.class.getSimpleName();

    private static final String KEY_USERID = "user_id";
    private static final String KEY_LOCATION = "location";

    private static String URL_USER_POST = "https://fixit-xubiey.c9users.io/fixit_post_from_location.php?location=";
    String userId = "user_id=";
    private ArrayList<LocationDetail> arrayList;
    private BaseActivity baseActivity;
    private LocationAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private SessionManagerPOS sessionManagerPOS;
    // initially offset will be 0, later will be updated while parsing the json
    private int id;
    private String newURL;
    private String location;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = new BaseActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sessionManagerPOS = new SessionManagerPOS(getContext());

        id = sessionManagerPOS.getId().get("id");
//		newURL = URL_USER_POST + userId + id;


        location = sessionManagerPOS.getUserDetails().get("location");

//        if (location.contains(", ")) {
//            location.replace(" ", "%20");
       String newLoc = location.replaceAll(" ", "%20");
        newURL = URL_USER_POST + newLoc;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.my_wall_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        arrayList = new ArrayList<>();

        adapter = new LocationAdapter(getContext(), arrayList);

        listView = (ListView) rootView.findViewById(R.id.myListWall);

        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        sendRequest(id, adapter, arrayList, location);
                                    }
                                }
        );

        return rootView;
    }

    private void sendRequest(final int id, final LocationAdapter adapter, final ArrayList<LocationDetail> arraylist, final String location) {

        String url = newURL;

        swipeRefreshLayout.setRefreshing(true);

        JsonArrayRequest stringRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        baseActivity.hideProgressDialog();
                        parseJSONResponse(response, adapter, arraylist, location);
                        adapter.notifyDataSetChanged();
                        adapter.notifyDataSetInvalidated();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        swipeRefreshLayout.setRefreshing(false);

                        String message = null;
                        if (volleyError instanceof NetworkError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        } else if (volleyError instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        } else if (volleyError instanceof AuthFailureError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();

                        } else if (volleyError instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();

                        } else if (volleyError instanceof NoConnectionError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();

                        } else if (volleyError instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                            Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }


                        NetworkResponse networkResponse = volleyError.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (volleyError.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                                Toast.makeText(getContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
                            } else if (volleyError.getClass().equals(NoConnectionError.class))
                                errorMessage = "Failed to connect server";
                            Toast.makeText(getContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            String result = new String(networkResponse.data);
                            try {
                                JSONObject response = new JSONObject(result);
                                String status = response.getString("status");
                                message = response.getString("message");

                                if (networkResponse.statusCode == 404) {
                                    errorMessage = "Resource not found";
                                    Toast.makeText(getContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();

                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + " Please login again";
                                    Toast.makeText(getContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + " Check your inputs";
                                    Toast.makeText(getContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " Something is getting wrong";
                                    Toast.makeText(getContext(), "" + errorMessage, Toast.LENGTH_SHORT).show();
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

                params.put(KEY_USERID, String.valueOf(id));
                params.put(KEY_LOCATION, location);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void parseJSONResponse(JSONArray response, LocationAdapter adapter, ArrayList<LocationDetail> arrayList, String location) {

        if (response != null && response.length() > 0) {
            try {

                for (int i = 0; i < response.length(); i++) {

                    JSONObject obj = response.getJSONObject(i);

                    String image = "NA";
                    String firstName = "NA";
                    String lastName = "NA";
                    String description = "NA";
                    location = "NA";
                    String relatedTo = "NA";
                    String solved = "NA";


                    if (contains(obj, "first_name"))
                        firstName = obj.getString("first_name");


                    if (contains(obj, "last_name"))
                        lastName = obj.getString("last_name");
                    String fullName = firstName + " " + lastName;

                    if (contains(obj, "image_path"))
                        image = obj.getString("image_path");
                    String st = "https://fixit-xubiey.c9users.io/";
                    String newImage = st + image;

                    if (contains(obj, "description")) {
                        description = obj.getString("description");
                      }
                    if (contains(obj, "location"))
                        location = obj.getString("location");

                    if (contains(obj, "related_to"))
                        relatedTo = obj.getString("related_to");


                    if (contains(obj, "Solved")) {
                        solved = obj.getString("Solved");
                        Log.d(TAG, "parseJSONResponse: Location ACTIVITY SOLVED ........" + solved);
                    }

                    if (!firstName.equals("NA") && !lastName.equals("NA"))
                        arrayList.add(new LocationDetail(fullName, newImage, description, location, relatedTo,solved));
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
            }
        }
    }

    @Override
    public void onRefresh() {
        arrayList.clear();
        sendRequest(id, adapter, arrayList, location);
    }
}
