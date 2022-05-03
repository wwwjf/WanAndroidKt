package com.wwwjf.wanandroidkt;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

public class SystemArrayCopyTest {

    @Test
    public void copy(){

        FuClass fuClass = new FuClass();
        fuClass.setName("zhangsan");
        FuClass[] srcObjArr = {fuClass};
        FuClass[] destObjArr = new FuClass[10];
        System.arraycopy(srcObjArr,0,destObjArr,0,1);
        System.out.println("copy后数组值："+fuClass.getName()+","+destObjArr[0].getName());
        fuClass.setName("lisi");
        System.out.println("copy后修改原数组值："+fuClass.getName()+","+destObjArr[0].getName());
        //修改原对象数组值影响copy数组的值，浅拷贝
        Assert.assertEquals("lisi",destObjArr[0].getName());





        ArrayList<String> arrayList = new ArrayList<>();
        for (String s : arrayList) {
//            arrayList.remove(s);
        }
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("");
        linkedList.addFirst("");
        linkedList.addLast("");
        linkedList.push("");
        linkedList.pop();
        linkedList.poll();
        linkedList.remove();
        linkedList.clear();

        Collections.binarySearch(linkedList,"");

        HashMap<String,String> hashMap = new HashMap<>();
        Map<String, String> map = Collections.synchronizedMap(hashMap);
    }
}
