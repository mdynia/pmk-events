package org.cat.pmk.events.datamodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import org.cat.pmk.events.R;
import org.cat.pmk.events.sfdc.SfdcRestApi;
import org.cat.pmk.events.sfdc.QueryResponseHandler;

public class InstalledBaseAdapter extends ArrayAdapter<InstalledBase> implements View.OnClickListener{

    private ArrayList<InstalledBase> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtProduct;
        TextView txtBrand;
        TextView txtSerialNo;
        ImageView info;
    }

    public InstalledBaseAdapter(ArrayList<InstalledBase> data, Context context) {
        super(context, R.layout.row_event, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        InstalledBase installedBase =(InstalledBase)object;


        switch (v.getId())
        {
            case R.id.ib_row_icon:
                SfdcRestApi.createOrder(installedBase.getId(),"mc-premium",new QueryResponseHandler() {

                });

                break;
        }
    }

    private int lastPosition = -1;


    @Override
    // the method that returns the actual view used as a row within the ListView at a particular position.
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        InstalledBase installedBase = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_event, parent, false);
            viewHolder.txtProduct = (TextView) convertView.findViewById(R.id.ib_row_product);
            viewHolder.txtSerialNo = (TextView) convertView.findViewById(R.id.ib_row_sn);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.ib_row_icon);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom: R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtProduct.setText(installedBase.getProduct());
        viewHolder.txtSerialNo.setText(installedBase.getSerialNumber());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);

        // Return the completed view to render on screen
        return convertView;
    }
}