package com.example.login1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private final ArrayList<String> productNames = new ArrayList<>();
    private final ArrayList<JSONObject> fullProductList = new ArrayList<>();

    private static final String BASE_URL =
            "http://10.0.2.2:8081/api/product/search?productname=";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                productNames
        );
        listView.setAdapter(adapter);

        // 🔥 LOAD ALL MEDICINES WHEN PAGE OPENS
        fetchProducts("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchProducts(newText);
                return true;
            }
        });

        listView.setOnItemClickListener(
                (AdapterView<?> parent, View itemView, int position, long id) -> {
                    try {
                        JSONObject product = fullProductList.get(position);

                        Intent intent = new Intent(
                                getContext(),
                                DescriptionActivity.class
                        );

                        intent.putExtra(
                                "productname",
                                product.optString("productname", "N/A")
                        );
                        intent.putExtra(
                                "uses",
                                product.optString("uses", "N/A")
                        );
                        intent.putExtra(
                                "sideeffects",
                                product.optString("sideeffects", "N/A")
                        );
                        intent.putExtra(
                                "imageurl",
                                product.optString("imageurl", "N/A")
                        );
                        intent.putExtra(
                                "manufacturer",
                                product.optString("manufacturer", "N/A")
                        );

                        startActivity(intent);

                    } catch (Exception e) {
                        Log.e("PRODUCT_CLICK", "Error opening description", e);
                    }
                }
        );

        return view;
    }

    private void fetchProducts(String query) {
        if (query == null) query = "";

        final String searchQuery = query.trim();

        new Thread(() -> {
            try {
                String finalUrl = BASE_URL + searchQuery;
                Log.d("DEBUG_FETCH", "URL: " + finalUrl);

                URL url = new URL(finalUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                conn.disconnect();

                JSONArray jsonArray = new JSONArray(response.toString());

                ArrayList<String> names = new ArrayList<>();
                ArrayList<JSONObject> fullList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject product = jsonArray.getJSONObject(i);
                    names.add(product.optString("productname", "N/A"));
                    fullList.add(product);
                }

                requireActivity().runOnUiThread(() -> {
                    productNames.clear();
                    fullProductList.clear();

                    productNames.addAll(names);
                    fullProductList.addAll(fullList);

                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                Log.e("DEBUG_FETCH", "Error fetching products", e);
            }
        }).start();
    }
}
