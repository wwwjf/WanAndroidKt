package com.wwwjf.wanandroidkt.algorithm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MergeTwoListTest {

    @Test
    public void mergeTwoLists() {

        ListNode l3 = new ListNode(4);
        ListNode l2 = new ListNode(3, l3);
        ListNode l1 = new ListNode(2, l2);

        ListNode l33 = new ListNode(3);
        ListNode l22 = new ListNode(2, l33);
        ListNode l11 = new ListNode(1, l22);

        ListNode l666 = new ListNode(4);
        ListNode l555 = new ListNode(3, l666);
        ListNode l444 = new ListNode(3, l555);
        ListNode l333 = new ListNode(2, l444);
        ListNode l222 = new ListNode(2, l333);
        ListNode l111 = new ListNode(1, l222);
        assertEquals(l111.toString(),mergeTwoLists(l1,l11).toString());

    }

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {

        ListNode resultNode = new ListNode(0);
        ListNode head1 = list1;
        ListNode head2 = list2;
        ListNode temp = resultNode;
        while (head1 != null || head2 != null) {
            if (head1 == null) {
                temp.next = head2;
                head2 = head2.next;
            } else if (head2 == null) {
                temp.next = head1;
                head1 = head1.next;
            } else if (head1.val < head2.val) {
                temp.next = head1;
                head1 = head1.next;
            } else {
                temp.next = head2;
                head2 = head2.next;
            }

            temp = temp.next;
        }
        return resultNode.next;
    }
}
