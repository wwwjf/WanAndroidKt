package com.wwwjf.wanandroidkt;

public class ZiClass extends FuClass{

    public int age = 40;

    public static int staticAge = 200;
/*
    static {
        staticAge = 220;
    }*/
    @Override
    public void doSomeThing() {
        System.out.println("ziclass print......");
    }

    public void ziClassDoSomeThing(){
        System.out.println("ziclassDoSomeThing print......");
    }
}
