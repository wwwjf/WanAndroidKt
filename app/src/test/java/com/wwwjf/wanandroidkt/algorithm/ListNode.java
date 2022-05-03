package com.wwwjf.wanandroidkt.algorithm;

import java.io.Serializable;

public class ListNode implements Serializable {
    private static final long serialVersionUID = -2922165073381506405L;
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(val);
        ListNode nextNode = next;
        while (true){
            if (nextNode == null){
                break;
            } else {
                stringBuilder.append(",");
            }
            stringBuilder.append(nextNode.val);
            nextNode= nextNode.next;

        }
        stringBuilder.append("]");
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }
}