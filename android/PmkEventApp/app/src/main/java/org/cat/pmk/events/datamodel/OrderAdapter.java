package org.cat.pmk.events.datamodel;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import org.cat.pmk.events.R;
import org.cat.pmk.events.sfdc.SfdcRestApi;
import org.cat.pmk.events.sfdc.QueryResponseHandler;

public class OrderAdapter extends ArrayAdapter<Order> {

    private ArrayList<Order> dataSet;
    Context mContext;

    private int lastPosition = -1;

    // View lookup cache
    private static class ViewHolder {
        TextView txtProducts;
        TextView txtAppointmentDate;
        TextView txtOptions;
    }

    public OrderAdapter(ArrayList<Order> data, Context context) {
        super(context, R.layout.row_order, data);
        this.dataSet = data;
        this.mContext=context;

    }



    @Override
    // the method that returns the actual view used as a row within the ListView at a particular position.
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Order order = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_order, parent, false);
            viewHolder.txtProducts = (TextView) convertView.findViewById(R.id.ib_row_products);
            viewHolder.txtAppointmentDate = (TextView) convertView.findViewById(R.id.ib_row_aptmt);
            viewHolder.txtOptions = (TextView) convertView.findViewById(R.id.ib_row_options);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom: R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtProducts.setText(order.getInstalledBaseList());
        viewHolder.txtAppointmentDate.setText(order.getAppointments());
        viewHolder.txtOptions.setText(order.getOrderLines());


        // Return the completed view to render on screen
        return convertView;
    }
}