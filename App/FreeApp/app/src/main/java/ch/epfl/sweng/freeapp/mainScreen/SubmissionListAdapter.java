package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;

/**
 * Created by lois on 11/10/15.
 */
public class SubmissionListAdapter extends ArrayAdapter<Submission> {

    public SubmissionListAdapter(Context context, int resource, List<Submission> submissions) {
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

        Submission p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.name);
            ImageView imageView = (ImageView) v.findViewById(R.id.submissionImageView);

            if (tt1 != null) {
                tt1.setText(p.getName());
            }

            if (imageView != null) {
                //TODO: setImage
                String image = p.getImage();
                if (image != null) {
                    if (image.length() >= 1000) {
                        imageView.setImageBitmap(decodeImage(image));
                    }
                }
            }
        }

        return v;
    }

    //FIXME: also present in DisplaySubmissionActivity: refactor
    private Bitmap decodeImage(String input) {

        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

    }

}
