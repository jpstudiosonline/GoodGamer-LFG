package com.jpstudiosonline.goodgamer.userAppArea.tabFragments;

/**
 * Created by jahnplay on 11/2/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

public class GameNameFragment extends Fragment{

    ListView listView;

    public GameNameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_game_name, container, false);

        View view = inflater.inflate(R.layout.fragment_game_name, container, false);

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.gameLetterList);

        // Defined Array values to show in ListView
        String[] values = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "0"};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);


        // Assign adapter to ListView
        listView.setAdapter(new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, values));
        //listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String  gameLetter = (String) listView.getItemAtPosition(position);
                Intent gameSelectIntent = new Intent(getContext(), SelectGameActivity.class);
                gameSelectIntent.putExtra("gameLetter", gameLetter);
                startActivity(gameSelectIntent);

                // Show Alert
                //Toast.makeText(getContext(),"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG).show();

            }

        });

        return view;
    }

}