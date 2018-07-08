package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class FilterableAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Product> mOriginalValues; // Original Values
    private ArrayList<Product> mDisplayedValues;    // Values to be displayed
    LayoutInflater inflater;
    Context context;

    public FilterableAdapter(Context context, JSONObject data) throws JSONException {
        this.context = context;
        Iterator<String> keys = data.keys();

        mOriginalValues = new ArrayList<>();

        while(keys.hasNext()){
            String key = keys.next();
            mOriginalValues.add(new Product("",0,key));
            JSONObject inObject = data.getJSONObject(key);
            Iterator<String> inKeys = inObject.keys();
            while(inKeys.hasNext()){
                String inKey = inKeys.next();
                JSONArray jsonArray = inObject.getJSONArray(inKey);
                int value = jsonArray.getInt(0);
                mOriginalValues.add(new Product(inKey,value,key));
            }
        }
        
        this.mDisplayedValues = mOriginalValues;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        LinearLayout llContainer;
        TextView Name,Price;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row, null);
            holder.llContainer = (LinearLayout)convertView.findViewById(R.id.llContainer);
            holder.Name = (TextView) convertView.findViewById(R.id.Name);
            holder.Price = (TextView) convertView.findViewById(R.id.Price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        
        if(mDisplayedValues.get(position).price > 0) {
            holder.Name.setText(mDisplayedValues.get(position).name);
            holder.Price.setText(mDisplayedValues.get(position).price + "");
        }
        else{
            holder.Name.setText(mDisplayedValues.get(position).category);
            holder.Price.setText("");
        }

        holder.llContainer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(context, mDisplayedValues.get(position).category, Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (ArrayList<Product>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Product> FilteredArrList = new ArrayList<Product>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Product>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).name;
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(new Product(mOriginalValues.get(i).name,mOriginalValues.get(i).price,mOriginalValues.get(i).category));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}

