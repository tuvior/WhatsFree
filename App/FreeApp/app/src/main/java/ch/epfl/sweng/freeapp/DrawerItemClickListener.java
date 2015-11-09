package ch.epfl.sweng.freeapp;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by MbangaNdjock on 09.11.15.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch(position){
            case 0:
                        Intent intent = new Intent(parent.getContext(), SportActivity.class);
                        SportActivity sportActivity = new SportActivity();
                        sportActivity.getApplication().startActivity(intent);
                        break;
        }
    }

}
