package ch.epfl.sweng.freeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class NavigationDrawerActivity extends Activity implements AdapterView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private Toolbar toolbar;

    private android.support.v7.app.ActionBarDrawerToggle mDrawerListener;
    private MyAdapter myAdapter;


    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        myAdapter = new MyAdapter(this);
        mDrawerList.setAdapter(myAdapter);

        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        mDrawerListener = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);

                Toast.makeText(NavigationDrawerActivity.this, "Drawer opened", Toast.LENGTH_LONG ).show();
            }

            @Override
            public void onDrawerClosed(View drawerView){
                super.onDrawerClosed(drawerView);
                Toast.makeText(NavigationDrawerActivity.this, "Drawer closed", Toast.LENGTH_LONG ).show();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerListener);




        //mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, sidebarItems));
        mDrawerList.setOnItemClickListener(this);

        //mDrawerListener = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout,
          //                      R.drawable.ic_drawer1, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerListener.syncState();
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

        if(mDrawerListener.onOptionsItemSelected(item)){
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerListener.onConfigurationChanged(newConfig);
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
    }


}



class MyAdapter extends BaseAdapter{
    private Context context;
    String[] sidebarItems;
    int[] images;

    public MyAdapter(Context context) {
        this.sidebarItems = context.getResources().getStringArray(R.array.nav_drawer_items);
        this.images = new int[]{ R.drawable.home, R.drawable.food, R.drawable.sport,
                                 R.drawable.nightlife, R.drawable.culture, R.drawable.lifestyle,
                                 R.drawable.clothes, R.drawable.logout };
        this.context = context;
    }

    @Override
    public int getCount() {
        return sidebarItems.length;
    }

    @Override
    public Object getItem(int position) {
        return sidebarItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = null;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_row, parent, false);

        }
        else{
            row = convertView;
        }

        TextView titleTextView = (TextView) row.findViewById(R.id.textView);
        ImageView titleImageView = (ImageView) row.findViewById(R.id.image);

        titleTextView.setText(sidebarItems[position]);
        titleImageView.setImageResource(images[position]);

        return row;
    }
}
