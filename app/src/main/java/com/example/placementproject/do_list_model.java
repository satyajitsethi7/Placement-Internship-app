package com.example.placementproject;

public class do_list_model {
    int id;
    String title,subtitle,link;
    public do_list_model(String title,String subtitle,String link){
        this.title=title;
        this.subtitle=subtitle;
        this.link=link;
    }
    public do_list_model(int id,String title,String subtitle,String link)
    {
        this.id=id;
        this.title=title;
        this.subtitle=subtitle;
        this.link=link;
    }
}
