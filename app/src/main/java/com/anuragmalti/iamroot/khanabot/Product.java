package com.anuragmalti.iamroot.khanabot;

public class Product {

    public String name;
    public Integer price;
    public String category;
    public Product(String name, Integer price, String category) {
        super();
        this.name = name;
        this.price = price;
        this.category = category;
    }
    public String getName() {
        return name;
    }
    public String getCategory(){return category;}
    public void setCategory(String category){this.category=category;}
    public void setName(String name) {
        this.name = name;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }

}
