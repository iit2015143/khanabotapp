package com.anuragmalti.iamroot.khanabot;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class MenuChildViewHolder extends ChildViewHolder{

    public ImageView add,remove;
    public TextView change,foodname,nameofrest,price;
    public View view;
    public RadioButton half,full;
    public RadioGroup radiovisible;

    public MenuChildViewHolder(View itemView) {
        super(itemView);
        this.view =itemView;
        change = (TextView)(view.findViewById(R.id.change));
        add = (ImageView)(view.findViewById(R.id.add));
        remove = (ImageView)(view.findViewById(R.id.remove));
        foodname=(TextView)(view.findViewById(R.id.foodname));
        nameofrest=(TextView)(view.findViewById(R.id.resname));
        price = (TextView)(view.findViewById(R.id.price));
        half = (RadioButton)(view.findViewById(R.id.half));
        full = (RadioButton)(view.findViewById(R.id.full));
        radiovisible = (RadioGroup)(view.findViewById(R.id.radiovisible));

    }
}
