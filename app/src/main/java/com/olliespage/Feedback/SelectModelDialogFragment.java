package com.olliespage.Feedback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment; // backward compatible library

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ollie on 30/07/14.
 */
public class SelectModelDialogFragment extends DialogFragment
{
    public interface SelectModelDialogListener {
        public void changeToSelectedModel(String name);
    }

    SelectModelDialogListener mListener; // create a listener for yourself
    AssetManager assetManager;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        assetManager = activity.getAssets();
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SelectModelDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SelectModelDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        try {
            final List<String> availableModels = new ArrayList<String>();
            for (String asset : assetManager.list("")) {
                if(asset.toLowerCase().endsWith(".json")){
                    availableModels.add(asset.substring(0, asset.length()-5));
                }
            } // the for loop gets all the json models
            builder.setTitle(R.string.select_sys_model)
                    .setItems(availableModels.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            mListener.changeToSelectedModel(availableModels.get(which));
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.create();
    }
}
