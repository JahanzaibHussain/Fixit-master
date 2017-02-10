package com.jahanzaib.fixit.mywall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jahanzaib.fixit.R;
import com.jahanzaib.fixit.home.HomeDetail;
import com.jahanzaib.fixit.utils.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.jahanzaib.fixit.utils.Utils.contains;

/**
 * Created by Jahanzaib on 12/21/16.
 */

public class WallActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    public static final String TAG = WallActivity.class.getSimpleName();
    private static String URL_USER_POST = "https://fixit-xubiey.c9users.io/fixit_all_users_posts.php";
    private ArrayList<HomeDetail> arrayList;
    private WallAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    // initially offset will be 0, later will be updated while parsing the json@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.my_wall_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        arrayList = new ArrayList<>();

        adapter = new WallAdapter(getContext(), arrayList);

        listView = (ListView) rootView.findViewById(R.id.myListWall);
        listView.setOnItemClickListener(this);

        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        sendRequest();
                                    }
                                }
        );

        return rootView;
    }

    private void sendRequest() {


        swipeRefreshLayout.setRefreshing(true);

        JsonArrayRequest stringRequest = new JsonArrayRequest(
                URL_USER_POST,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJSONResponse(response);
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
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public void parseJSONResponse(JSONArray response) {

        if (response != null && response.length() > 0) {
            try {

                for (int i = 0; i < response.length(); i++) {

                    JSONObject obj = response.getJSONObject(i);

                    String image = "NA";
                    String firstName = "NA";
                    String lastName = "NA";
                    String description = "NA";
                    String location = "NA";
                    String solve = "NA";
                    String relatedTo = "NA";

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

                    if (contains(obj, "Solved")) {
                        solve = obj.getString("Solved");
                        Log.d(TAG, "parseJSONResponse: WALL ACTIVITY ================>" + solve);
                    }

                    if (contains(obj, "related_to"))
                        relatedTo = obj.getString("related_to");

                    if (!firstName.equals("NA") && !lastName.equals("NA"))
                        arrayList.add(new HomeDetail(fullName, newImage, description, location, relatedTo, solve));
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
        sendRequest();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        HomeDetail h = arrayList.get(i);
        Toast.makeText(getContext(), "Post " + h.getPostId(), Toast.LENGTH_SHORT).show();

    }
}
