package com.anuragmalti.iamroot.khanabot;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

public class MenuParentViewHolder extends ParentViewHolder{

    public TextView leveltwo;
    public ImageButton mParentDropDownArrow;

    public MenuParentViewHolder(View itemView) {
        super(itemView);

        leveltwo = (TextView) itemView.findViewById(R.id.leveltwo);
        mParentDropDownArrow = (ImageButton) itemView.findViewById(R.id.parent_list_item_expand_arrow);
    }
}
