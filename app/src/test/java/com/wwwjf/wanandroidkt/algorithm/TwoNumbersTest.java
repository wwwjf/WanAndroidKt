package com.wwwjf.wanandroidkt.algorithm;

import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.assertEquals;

public class TwoNumbersTest {

    @Test
    public void addTwoNumbers() {

        ListNode l4 = new ListNode(3,null);
        ListNode l3 = new ListNode(4,l4);
        ListNode l2 = new ListNode(2,l3);
        ListNode l1 = new ListNode(7,l2);

        ListNode n3 = new ListNode(1,null);
        ListNode n2 = new ListNode(6,n3);
        ListNode n1 = new ListNode(5,n2);

        //[7,2,4,3]
        //[  5,6,1]
        ListNode ln4 = new ListNode(4,null);
        ListNode ln3 = new ListNode(0,ln4);
        ListNode ln2 = new ListNode(8,ln3);
        ListNode ln1 = new ListNode(7,ln2);
        assertEquals(ln1.toString(),addTwoNumbers(l1,n1).toString());

        ListNode ll1 = new ListNode(5,null);
        ListNode nn1 = new ListNode(5,null);
        ListNode llnn2 = new ListNode(0,null);
        ListNode llnn1 = new ListNode(1,llnn2);
        assertEquals(llnn1.toString(),addTwoNumbers(ll1,nn1));
    }
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        Stack<Integer> stack1 = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();
        ListNode resultNode = new ListNode(0);
        while (l1 != null){
            stack1.push(l1.val);
            l1 = l1.next;
        }
        while (l2 != null){
            stack2.push(l2.val);
            l2 = l2.next;
        }
        int len = Math.max(stack1.size(),stack2.size());
        int bit = 0;
        ListNode tempNode;
        int sum;
        while (!stack1.isEmpty() || !stack2.isEmpty() ||bit !=0){
            sum = bit;
            if (!stack1.empty()){
                sum += stack1.pop();
            }
            if (!stack2.empty()){
                sum += stack2.pop();
            }
            tempNode = new ListNode();
            bit = sum /10;
            tempNode.val = sum%10;
            tempNode.next = resultNode.next;
            resultNode.next = tempNode;

        }
        return resultNode.next;
    }
}
