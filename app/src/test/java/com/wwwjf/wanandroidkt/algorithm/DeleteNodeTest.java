package com.wwwjf.wanandroidkt.algorithm;

import org.junit.Test;

public class DeleteNodeTest {

    /**
     * 单链表删除倒数第N个节点，并返回head节点
     */
    @Test
    public void deleteLastNode(){


        ListNode l5 = new ListNode(5);
        ListNode l4 = new ListNode(4, l5);
        ListNode l3 = new ListNode(3, l4);
        ListNode l2 = new ListNode(2, l3);
        ListNode l1 = new ListNode(1, l2);

        deleteLastNode(2,l1);
    }

    private void deleteLastNode(int n, ListNode head) {
        ListNode first = head;
        ListNode second = head;
        while (true){
            if (second.next == null){
                first.next = first.next.next;
                break;
            }
            second = second.next;
            if (n<=0){
                first = first.next;
            }
            n--;
        }
        System.out.println("first="+first.val+",second"+second);
        head.toString();
    }
}
