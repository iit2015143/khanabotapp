package com.anuragmalti.iamroot.khanabot;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.Date;
import java.util.List;

public class Menu implements ParentObject {
        /* Create an instance variable for your list of children */
    private List<Object> mChildrenList;
    public String title;

    public Menu(String tit){
        title = tit;
    }

    @Override
    public List<Object> getChildObjectList() {
        return mChildrenList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mChildrenList = list;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String string){
        this.title=string;
    }
}
