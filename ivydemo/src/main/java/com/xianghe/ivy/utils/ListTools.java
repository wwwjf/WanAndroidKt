package com.xianghe.ivy.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ListTools {

    /**
     * 删除重复元素,保持顺序
     * @param list
     */
    public static <T> void removeDuplicateWithOrder(List<T> list) {
        Set<T> set = new HashSet<T>();
        List<T> newList = new ArrayList<T>();
        for (Iterator<T> iter = list.iterator(); iter.hasNext();) {
            T element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
    }
    /**
     * 删除重复元素，不保持顺序
     * @param list
     */
    public   static <T>  void  removeDuplicate(List<T> list)   {
        HashSet<T> h  =   new  HashSet<T>(list);
        list.clear();
        list.addAll(h);
    }
}