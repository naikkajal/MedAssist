package com.example.login1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.HashSet;
import java.util.Set;

public class SavedFragment extends Fragment {

    private static final String PREFS_NAME = "SavedItemsPrefs";
    private static final String SAVED_ITEMS_KEY = "savedItems";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        LinearLayout savedContainer = view.findViewById(R.id.savedContainer);

        SharedPreferences prefs =
                requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Set<String> savedItems =
                prefs.getStringSet(SAVED_ITEMS_KEY, new HashSet<>());

        for (String name : savedItems) {

            // ===== CARD =====
            CardView card = new CardView(requireContext());
            card.setRadius(16);
            card.setCardElevation(6);
            card.setUseCompatPadding(true);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

            cardParams.bottomMargin = 24;
            card.setLayoutParams(cardParams);

            // ===== CARD CONTENT =====
            LinearLayout content = new LinearLayout(requireContext());
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(32, 32, 32, 32);

            // Name
            TextView tvName = new TextView(requireContext());
            tvName.setText(name);
            tvName.setTextSize(18);
            tvName.setTypeface(null, Typeface.BOLD);

            // Uses
            TextView tvUses = new TextView(requireContext());
            tvUses.setText(prefs.getString(name + "_uses", ""));
            tvUses.setTextSize(14);
            tvUses.setTextColor(Color.parseColor("#6B7280"));
            tvUses.setPadding(0, 12, 0, 12);

            // Buttons row
            LinearLayout btnRow = new LinearLayout(requireContext());
            btnRow.setOrientation(LinearLayout.HORIZONTAL);
            btnRow.setGravity(Gravity.END);

            MaterialButton btnView = new MaterialButton(requireContext());
            btnView.setText("View Details");
            btnView.setCornerRadius(50);
            btnView.setStrokeWidth(2);
            btnView.setStrokeColorResource(android.R.color.darker_gray);
            btnView.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnView.setTextColor(Color.parseColor("#1565C0"));



            MaterialButton btnDelete = new MaterialButton(requireContext());
            btnDelete.setText("Delete");
            btnDelete.setCornerRadius(50);
            btnDelete.setStrokeWidth(2);
            btnDelete.setStrokeColorResource(android.R.color.darker_gray);
            btnDelete.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnDelete.setTextColor(Color.BLACK);



            btnRow.addView(btnView);
            btnRow.addView(btnDelete);

            // Actions
            btnView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DescriptionActivity.class);
                intent.putExtra("productname", name);
                intent.putExtra("uses", prefs.getString(name + "_uses", ""));
                intent.putExtra("sideeffects", prefs.getString(name + "_sideeffects", ""));
                intent.putExtra("manufacturer", prefs.getString(name + "_manufacturer", ""));
                intent.putExtra("imageurl", prefs.getString(name + "_imageurl", ""));
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                Set<String> updated = new HashSet<>(savedItems);
                updated.remove(name);
                prefs.edit().putStringSet(SAVED_ITEMS_KEY, updated).apply();
                savedContainer.removeView(card);
            });

            // Assemble
            content.addView(tvName);
            content.addView(tvUses);
            content.addView(btnRow);
            card.addView(content);
            savedContainer.addView(card);
        }

        return view;
    }
}
