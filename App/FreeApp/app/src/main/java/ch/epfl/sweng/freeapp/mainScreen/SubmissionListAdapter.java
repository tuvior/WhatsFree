package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.SubmissionShortcut;

/**
 * Created by lois on 11/10/15.
 */
public class SubmissionListAdapter extends ArrayAdapter<SubmissionShortcut> {

    public SubmissionListAdapter(Context context, int resource, List<SubmissionShortcut> submissions) {
        super(context, resource, submissions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_list_row, null);
        }

        SubmissionShortcut p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.name);

            if (tt1 != null) {
                tt1.setText(p.getName());
            }
        }

        return v;
    }

}
