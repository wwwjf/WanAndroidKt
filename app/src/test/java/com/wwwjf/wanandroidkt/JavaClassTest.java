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
}
