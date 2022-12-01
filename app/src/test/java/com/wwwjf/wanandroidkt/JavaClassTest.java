package com.wwwjf.wanandroidkt;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JavaClassTest {

    @Test
    public void method(){
        List<ZiClass> list1 = new ArrayList<>();
        list1.add(new ZiClass());
        showList1(list1);
        List<FuClass> list11 = new ArrayList<>();
        showList1(list11);

        List<ZiClass> list2 = new ArrayList<>();
        showList2(list2);
        List<Object> list3 = new ArrayList<>();
        showList2(list3);

     }

    private void showList1(List<? extends FuClass> list) {
        list.get(0);
//        list1.add(new ZiClass()); //不能add
    }

    private void showList2(List<? super  ZiClass> list){
//        FuClass ziClass = list.get(0);//不能get
        list.add(new ZiClass());
    }

    @Test
    public void polymorphismTest(){
        FuClass fuClass = new ZiClass();
        fuClass.doSomeThing();
        System.out.println("age="+fuClass.age+",staticAge="+ZiClass.staticAge);
        if (fuClass instanceof FuClass){
            System.out.println("fuClass 类型是FuClass");
        } else {
            System.out.println("fuClass 类型是ZiClass");
        }
    }

    @Test
    public void polymorphism2Test(){
        A a1 = new A();
        A a2 = new B();
        B b = new B();
        C c = new C();
        a1.show(b);
        a1.show(c);
        System.out.println("----------");
        a2.show(b);
        a2.show(c);
        System.out.println("----------");
        c.show(a1);
        c.show(a2);
    }

    @Test
    public void andTest(){
        System.out.println(5&9);
    }
}
