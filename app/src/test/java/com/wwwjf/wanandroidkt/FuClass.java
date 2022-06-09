package com.wwwjf.wanandroidkt;

public class FuClass {

    private String name;

    public int age = 100;
    public static int staticAge = 250;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void doSomeThing(){
        System.out.println("fuclass print.....");
    }
}
