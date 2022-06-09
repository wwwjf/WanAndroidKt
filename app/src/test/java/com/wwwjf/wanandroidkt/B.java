package com.wwwjf.wanandroidkt;

public class B extends A{

    public void show(B obj){
        System.out.println("B and B");
    }

    @Override
    public void show(A obj){
        System.out.println("B and A");
    }
}
