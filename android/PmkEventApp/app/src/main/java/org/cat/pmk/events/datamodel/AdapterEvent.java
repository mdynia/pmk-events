package org.cat.pmk.events.datamodel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.cat.pmk.events.R;

import java.util.ArrayList;

public class AdapterEvent extends ArrayAdapter<Event> implements View.OnClickListener {

    private ArrayList<Event> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
        TextView txtGodzina;
        TextView txtData;
        TextView txtAdres;
        ImageView iconShare;
        ImageView iconNav;
    }

    public AdapterEvent(ArrayList<Event> data, Context context) {
        super(context, R.layout.row_nabozenstwo, data);
        this.dataSet = data;
        this.mContext=context;
    }


    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Event event =(Event)object;

        CharSequence text = "";
        switch (v.getId())
        {
            case R.id.ib_row_icon_nav:
                Uri gmmIntentUri = null;
                // Creates an Intent that will load a map
                String geo = event.getGeolocationTag();

                if (geo!=null && geo.length()>0) {
                    gmmIntentUri = Uri.parse("google.navigation:mode=d&q=" + geo);
                } else {
                    String address = event.getAddressForUriParam();
                    gmmIntentUri = Uri.parse("google.navigation:mode=d&q=" + address);
                }

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mapIntent.setPackage("com.google.android.apps.maps");
                mContext.startActivity(mapIntent);
                break;

            case R.id.ib_row_icon_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, event.getTitle()
                        + ", " + event.getStartDateDescription()
                        + ", " + event.getDescription()
                        + ". Szczegóły na http://www.pmk-bielefeld.de"
                );
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, event.getTitle());
                sendIntent.putExtra(Intent.EXTRA_EMAIL, "");
                sendIntent.putExtra(Intent.EXTRA_CC, "");
                sendIntent.putExtra(Intent.EXTRA_BCC, "");

                mContext.startActivity(sendIntent);
                break;

        }
    }

    private int lastPosition = -1;


    @Override
    // the method that returns the actual view used as a row within the ListView at a particular position.
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_nabozenstwo, parent, false);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.ib_row_title);
            viewHolder.txtGodzina = (TextView) convertView.findViewById(R.id.ib_row_godzina);
            viewHolder.txtData = (TextView) convertView.findViewById(R.id.ib_row_data);
            viewHolder.txtAdres = (TextView) convertView.findViewById(R.id.ib_row_adres);
            viewHolder.iconShare    = (ImageView) convertView.findViewById(R.id.ib_row_icon_share);
            viewHolder.iconNav    = (ImageView) convertView.findViewById(R.id.ib_row_icon_nav);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom: R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.txtTitle.setText(event.getTitle());
        viewHolder.txtGodzina.setText(event.getStartDateDescription());
        viewHolder.txtData.setText(event.getStartDate());
        String address = event.getAddress();
        if (event.getDistance() > 1) {
            address += " \nOdległość ok."+event.getDistance()+" km";
        } else {
            address += " \nBardzo blisko Ciebie.";
        }
        viewHolder.txtAdres.setText(address);

        viewHolder.iconShare.setOnClickListener(this);
        viewHolder.iconShare.setTag(position);

        viewHolder.iconNav.setOnClickListener(this);
        viewHolder.iconNav.setTag(position);


        // Return the completed view to render on screen
        return convertView;
    }
}