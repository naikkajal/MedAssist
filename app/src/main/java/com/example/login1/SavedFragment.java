package com.example.login1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Set;

public class SavedFragment extends Fragment {

    private static final String PREFS_NAME = "SavedItemsPrefs";
    private static final String SAVED_ITEMS_KEY = "savedItems";

    public SavedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        LinearLayout savedContainer = view.findViewById(R.id.savedContainer);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedItems = prefs.getStringSet(SAVED_ITEMS_KEY, new HashSet<>());
        Set<String> currentItems = new HashSet<>(savedItems);

        for (String productname : currentItems) {
            CardView card = new CardView(requireContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 24);
            card.setLayoutParams(layoutParams);
            card.setCardElevation(4);
            card.setRadius(8);
            card.setCardBackgroundColor(getResources().getColor(R.color.navy_blue, null));
            card.setUseCompatPadding(true);

            LinearLayout innerLayout = new LinearLayout(requireContext());
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setPadding(24, 24, 24, 24);

            TextView textView = new TextView(requireContext());
            textView.setTextSize(18);
            textView.setText(productname);
            textView.setTextColor(getResources().getColor(android.R.color.black));

            Button deleteButton = new Button(requireContext());
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> {
                savedItems.remove(productname);
                prefs.edit().putStringSet(SAVED_ITEMS_KEY, savedItems).apply();
                Toast.makeText(getContext(), "Removed from saved", Toast.LENGTH_SHORT).show();
                // Refresh the fragment
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .detach(this)
                        .attach(this)
                        .commit();
            });

            innerLayout.addView(textView);
            innerLayout.addView(deleteButton);
            card.addView(innerLayout);

            // On click: open DescriptionActivity
            card.setOnClickListener(v -> {
                // ⬇️ Read saved values
                String uses = prefs.getString(productname + "_uses", "No uses available");
                String sideeffects = prefs.getString(productname + "_sideeffects", "No side effects");
                String manufacturer = prefs.getString(productname + "_manufacturer", "Unknown manufacturer");
                String imageurl = prefs.getString(productname + "_imageurl", "No image");

                Intent intent = new Intent(getActivity(), DescriptionActivity.class);
                intent.putExtra("productname", productname);
                intent.putExtra("uses", uses);
                intent.putExtra("sideeffects", sideeffects);
                intent.putExtra("manufacturer", manufacturer);
                intent.putExtra("imageurl", imageurl);
                startActivity(intent);
            });


            savedContainer.addView(card);
        }

        return view;
    }
}
