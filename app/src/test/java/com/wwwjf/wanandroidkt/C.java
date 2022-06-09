package com.wwwjf.wanandroidkt;

public class C extends B{

    public void show(C obj){
        System.out.println("C and C");
    }

    @Override
    public void show(A obj){
        System.out.println("C and A");
    }
}
