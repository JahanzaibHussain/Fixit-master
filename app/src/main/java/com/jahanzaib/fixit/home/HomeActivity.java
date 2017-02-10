package com.jahanzaib.fixit.home;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
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

public class HomeActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener {

    public static final String TAG = HomeActivity.class.getSimpleName();
    private static final String KEY_USERID = "user_id";
    private static final String KEY_POST = "post_id";
    private static String URL_USER_POST = "https://fixit-xubiey.c9users.io/fixit_user_posts.php?";
    String userId = "user_id=";
    private ArrayList<HomeDetail> arrayList;
    private HomeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private SessionManagerPOS sessionManagerPOS;
    // initially offset will be 0, later will be updated while parsing the json
    private int id;
    private String newURL;
    private View rootView;

//run kro

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sessionManagerPOS = new SessionManagerPOS(getContext());

        id = sessionManagerPOS.getId().get("id");

        newURL = URL_USER_POST + userId + id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.my_wall_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        arrayList = new ArrayList<>();

        adapter = new HomeAdapter(getContext(), arrayList);

        listView = (ListView) rootView.findViewById(R.id.myListWall);

        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        sendRequest(id, adapter, arrayList, newURL);
                                    }
                                }
        );

        return rootView;
    }


    public void sendRequest(final int id, final HomeAdapter adapter, final ArrayList<HomeDetail> arraylist, String url) {

        url = newURL;

        swipeRefreshLayout.setRefreshing(true);

        JsonArrayRequest stringRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJSONResponse(response, adapter, arraylist);
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
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void parseJSONResponse(JSONArray response, HomeAdapter adapter, ArrayList<HomeDetail> arrayList) {

        if (response != null && response.length() > 0) {
            try {

                for (int i = 0; i < response.length(); i++) {

                    JSONObject obj = response.getJSONObject(i);

                    String image = "NA";
                    String firstName = "NA";
                    String lastName = "NA";
                    String description = "NA";
                    String location = "NA";
                    String relatedTo = "NA";
                    String postId = "NA";
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

                    if (contains(obj, "description"))
                        description = obj.getString("description");

                    if (contains(obj, "location"))
                        location = obj.getString("location");

                    if (contains(obj, "related_to"))
                        relatedTo = obj.getString("related_to");


                    if (contains(obj, "post_id"))
                        postId = obj.getString("post_id");


                    if (contains(obj, "Solved")) {
                        solved = obj.getString("Solved");
                        Log.d(TAG, "parseJSONResponse: HOME ACTIVITY SOLVED ........" + solved);
                    }
                    if (!firstName.equals("NA") && !lastName.equals("NA"))
                        arrayList.add(new HomeDetail(fullName, newImage, description, location, relatedTo, postId ,solved));
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
        sendRequest(id, adapter, arrayList, newURL);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


        final HomeDetail h = arrayList.get(i);

        final String url = "https://fixit-xubiey.c9users.io/fixit_user_post_delete.php";
        final String postREq = h.getPostId();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set title
        alertDialogBuilder.setTitle("Do you want to delete?");

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendRequestPOSTDEL(postREq, url);
                    }
                })
                .setNegativeButton("Solved", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        sendRequestPOSTDEL(postREq, "https://fixit-xubiey.c9users.io/fixit_after_before.php");

                        Toast.makeText(getContext(), "Solved Soon..", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        return false;
    }


    public void sendRequestPOSTDEL(final String post_id, String url) {

        swipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response.contains("Deleted")) {
                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            onRefresh();
                        } else {
                            Toast.makeText(getContext(), "Error" + response, Toast.LENGTH_SHORT).show();
                        }
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

                params.put(KEY_POST, String.valueOf(post_id));

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
