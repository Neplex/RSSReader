package com.bitoaster.rssreader.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class AddLinkFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_addlink, null))
                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        EditText lien = (EditText) inflater.inflate(R.layout.dialog_addlink, null);
                        addChannel(lien.getText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_deny, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
    public void addChannel(String url){
        DBInteraction db = new DBInteraction(getActivity().getApplicationContext());
        Channel c = null;
        try {
            c = new ParserXML().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(c!=null){
            db.putChannel(c);
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.invalidlink), Toast.LENGTH_SHORT).show();
        }
    }
}
