package com.anuragmalti.iamroot.khanabot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.zip.Inflater;

public class MenuExpandableAdapter extends ExpandableRecyclerAdapter<MenuParentViewHolder, MenuChildViewHolder>{
    public LayoutInflater mInflater;
    public Context context;

    public MenuExpandableAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MenuParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.menuparent, viewGroup, false);
        return new MenuParentViewHolder(view);

    }

    @Override
    public MenuChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.menuchild, viewGroup, false);
        return new MenuChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(MenuParentViewHolder menuParentViewHolder, int i, Object o) {
        Menu menu = (Menu) o;
        menuParentViewHolder.leveltwo.setText(menu.getTitle());
    }

    @Override
    public void onBindChildViewHolder(final MenuChildViewHolder holder, int i, Object o) {
        MenuChild menuChild = (MenuChild) o;
        ////Log.e("childviewholder",menuChild.item.toString());
        try {
            final JSONObject nothotdeal = menuChild.item;
            holder.foodname.setText(nothotdeal.getString("name"));
            holder.nameofrest.setText(nothotdeal.getString("resname"));
            final JSONArray price = nothotdeal.getJSONArray("price");
            holder.price.setText("Rs "+ price.getString(price.length()-1));
            nothotdeal.put("index",price.length()-1);
            if(price.length()==1){
                holder.radiovisible.setVisibility(View.GONE);
            }
            else{
                holder.radiovisible.setVisibility(View.VISIBLE);
                holder.radiovisible.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId==R.id.full) {
                            try {
                                holder.price.setText("Rs "+ price.getString(price.length()-1));
                                nothotdeal.put("index",price.length()-1);
                                holder.change.setText(0+"");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(checkedId == R.id.half){
                            try {
                                holder.price.setText("Rs "+ price.getString(0));
                                nothotdeal.put("index",0);
                                holder.change.setText(0+"");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = Integer.parseInt(holder.change.getText().toString());
                    value++;
                    holder.change.setText(value.toString());
                    HomePage.updatecart(true,nothotdeal);
                    ((RestaurantProfile)context).notifychange();
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = Integer.parseInt(holder.change.getText().toString());
                    if(value>0) {
                        value--;
                        holder.change.setText(value.toString());
                        HomePage.updatecart(false,nothotdeal);
                        ////Log.e("error nothot",nothotdeal.toString());
                        ((RestaurantProfile)context).notifychange();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
