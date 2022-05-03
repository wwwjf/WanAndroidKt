package com.wwwjf.wanandroidkt.algorithm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReverseNodeTest {

    @Test
    public void reverList() {
        ListNode l5 = new ListNode(5, null);
        ListNode l4 = new ListNode(4, l5);
        ListNode l3 = new ListNode(3, l4);
        ListNode l2 = new ListNode(2, l3);
        ListNode l1 = new ListNode(1, l2);

        ListNode ring7 = new ListNode(7);
        ListNode ring6 = new ListNode(6,ring7);
        ListNode ring5 = new ListNode(5,ring6);
        ListNode ring4 = new ListNode(4, ring5);
        ListNode ring3 = new ListNode(3, ring4);
        ListNode ring2 = new ListNode(2, ring3);
        ListNode ring1 = new ListNode(1, ring2);
        ring7.next = ring5;

        ListNode rl5 = new ListNode(1, null);
        ListNode rl4 = new ListNode(2, rl5);
        ListNode rl3 = new ListNode(3, rl4);
        ListNode rl2 = new ListNode(4, rl3);
        ListNode rl1 = new ListNode(5, rl2);
        assertEquals(rl1.toString(), reverseList1(l1).toString());
        reverseList1(l5);
//        System.out.println("-------------\n"+l1.toString()+"\n-------------");
//        assertEquals(rl1.toString(),reverseList2(l1).toString());
        assertFalse(hasRing(l1));
        assertTrue(hasRing(ring1));
        assertEquals(ring5.val,findStartRingNode(ring1).val);
        assertEquals(3,calculateRingNum(ring1));

    }


    /**
     * 单链表反转1
     */
    public ListNode reverseList1(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        ListNode temp;
        while (curr != null) {
            temp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = temp;
        }
        return prev;
    }


    /**
     * 单链表反转2
     */
    public ListNode reverseList2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newHead = reverseList2(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    /**
     * 单链表是否有环
     * @param head
     * @return
     */
    public boolean hasRing(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        ListNode slow = head.next;
        ListNode fast = head.next.next;
        while (true) {
            if (fast == null || fast.next == null) {
                return false;
            } else if (fast == slow) {
                System.out.println("有环，相遇点："+fast.val+","+slow.val);
                return true;
            } else {
                slow = slow.next;
                fast = fast.next.next;
            }
        }
    }

    /**
     * 找到环入口点
     * @param head
     * @return
     */
    public ListNode findStartRingNode(ListNode head){
        if (head == null || head.next == null){
            return null;
        }
        ListNode slow = head;
        ListNode fast = head;
        while (slow != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast){
                break;
            }
        }
        if (slow == null ||fast.next == null){
            return null;
        }
        ListNode start = head;
        while (start != slow){
            start = start.next;
            slow = slow.next;
        }
        System.out.println("入口点："+slow.val);
        return slow;

    }

    /**
     * 计算环节点个数
     * @param head
     * @return
     */
    public int calculateRingNum(ListNode head){
        ListNode slow = head;
        ListNode fast = head;
        while (slow != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast){
                break;
            }
        }
        if (slow == null || fast.next == null){
            return 0;
        }
        int num = 0;
        do {
            slow = slow.next;
            fast = fast.next.next;
            num++;
        } while (slow != fast);
        System.out.println("环上节点个数："+num);
        return num;
    }
}