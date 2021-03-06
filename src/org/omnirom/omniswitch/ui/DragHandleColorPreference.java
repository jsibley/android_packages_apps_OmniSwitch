/*
 *  Copyright (C) 2013 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.omnirom.omniswitch.ui;

import org.omnirom.omniswitch.R;
import org.omnirom.omniswitch.SettingsActivity;
import org.omnirom.omniswitch.SwitchConfiguration;
import org.omnirom.omniswitch.colorpicker.ColorPickerDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class DragHandleColorPreference extends Preference implements DialogInterface.OnDismissListener {
    private ImageView mLightColorView;
    private int mColorValue;
    private Resources mResources;
    private SharedPreferences mPrefs;
    /** The dialog, if it is showing. */
    private Dialog mDialog;
    private SwitchConfiguration mConfiguration;

    /**
     * @param context
     * @param attrs
     */
    public DragHandleColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs, com.android.internal.R.attr.preferenceStyle);
        mConfiguration = SwitchConfiguration.getInstance(context);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mColorValue = mPrefs.getInt(SettingsActivity.PREF_DRAG_HANDLE_COLOR_NEW,
                mConfiguration.getDefaultDragHandleColor());
        init();
    }

    private void init() {
        setLayoutResource(R.layout.preference_color);
        mResources = getContext().getResources();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mLightColorView = (ImageView) view.findViewById(R.id.light_color);
        updatePreferenceViews();
    }

    private void updatePreferenceViews() {
        final int width = (int) mResources
                .getDimension(R.dimen.color_button_width);
        final int height = (int) mResources
                .getDimension(R.dimen.color_button_height);

        if (mLightColorView != null) {
            mLightColorView.setEnabled(true);
            mLightColorView.setImageDrawable(createRectShape(width, height,
                    mColorValue));
        }
    }

    @Override
    protected void onClick() {
        if (mDialog != null && mDialog.isShowing()) return;
        mDialog = getDialog();
        mDialog.setOnDismissListener(this);
        mDialog.show();
    }

    private Dialog getDialog() {
        final ColorPickerDialog d = new ColorPickerDialog(getContext(),
                mColorValue, true);
        d.setButton(AlertDialog.BUTTON_POSITIVE,
                mResources.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mColorValue = d.getColor();
                        updatePreferenceViews();
                        mPrefs.edit()
                                .putInt(SettingsActivity.PREF_DRAG_HANDLE_COLOR_NEW,
                                        mColorValue).commit();
                    }
                });
        d.setButton(AlertDialog.BUTTON_NEUTRAL,
                mResources.getString(R.string.reset),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mColorValue = mConfiguration.getDefaultDragHandleColor();
                        updatePreferenceViews();
                        mPrefs.edit()
                                .putInt(SettingsActivity.PREF_DRAG_HANDLE_COLOR_NEW,
                                        mColorValue).commit();
                        d.dismiss();
                    }
                });
        d.setButton(AlertDialog.BUTTON_NEGATIVE,
                mResources.getString(R.string.cancel),
                (DialogInterface.OnClickListener) null);
        return d;
    }

    public int getColor() {
        return mColorValue;
    }

    public void setColor(int color) {
        mColorValue = color;
        updatePreferenceViews();
    }

    private static ShapeDrawable createRectShape(int width, int height,
            int color) {
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.setIntrinsicHeight(height);
        shape.setIntrinsicWidth(width);
        shape.getPaint().setColor(color);
        return shape;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mDialog = null;
    }
}
