package com.quatre.phoenix.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import com.quatre.phoenix.R;

public class SnackbarMaker {
    public static void showCustomSnackbar(View parentView, String message, boolean isSuccess) {
        Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT);

        View snackbarView = snackbar.getView();
        // setBackgroundColor doesn't work because Material displays a layer above
        snackbarView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(parentView.getContext(), isSuccess ? R.color.success : R.color.error)));
        snackbar.setTextColor(Color.WHITE);

        // add icon
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        Drawable icon = ContextCompat.getDrawable(parentView.getContext(), isSuccess ? R.drawable.ic_check : R.drawable.ic_error);
        if (icon != null) {
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            textView.setCompoundDrawables(icon, null, null, null);
            textView.setCompoundDrawablePadding(16); // spacing between icon and text
        }

        snackbar.show();
    }
}
