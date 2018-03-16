package com.example.ajcin.flashbackmusicteam16;


import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment {

    Button download_btn;
    EditText URL;
    TextView text;

    public DownloadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        download_btn = (Button) view.findViewById(R.id.enter_btn);
        URL = (EditText) view.findViewById(R.id.url_box);
        text = (TextView) view.findViewById(R.id.download_instructions);

        download_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String entered_url = URL.getText().toString();
                DownloadHandler handler = new DownloadHandler(getContext());
                handler.download_file( getContext(), "https://www.androidtutorialpoint.com/wp-content/uploads/2016/09/AndroidDownloadManager.mp3");
            }
        });
        return view;
    }

}
