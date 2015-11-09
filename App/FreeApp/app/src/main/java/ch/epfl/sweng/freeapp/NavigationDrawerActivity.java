package ch.epfl.sweng.freeapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.sliding.menu.adapter.NavDrawerListAdapter;
import ch.epfl.sweng.freeapp.sliding.menu.model.NavDrawerItem;


public class NavigationDrawerActivity extends Activity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private String[] categories;

    private android.support.v7.app.ActionBarDrawerToggle drawerListener;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        categories = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, categories));
        mDrawerList.setOnItemClickListener(this);

        //drawerListener = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout,
          //                      R.drawable.ic_drawer1, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0){
            Intent intent = new Intent(this, FoodActivity.class);
            startActivity(intent);
        }
        else if(position == 1){
            Intent intent = new Intent(this, SportActivity.class);
            startActivity(intent);
        }
        else if(position == 2){
            Intent intent = new Intent(this, NightLifeActivity.class);
            startActivity(intent);
        }
        else if(position == 3){
            Intent intent = new Intent(this, CultureActivity.class);
            startActivity(intent);
        }
        else if(position == 4){
            Intent intent = new Intent(this, ClothesActivity.class);
            startActivity(intent);
        }
        else if(position == 5){
            Intent intent = new Intent(this, MiscellaneousActivity.class);
            startActivity(intent);
        }

        selectItem(position);
    }

    public void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        setTitle(categories[position]);
    }

}
