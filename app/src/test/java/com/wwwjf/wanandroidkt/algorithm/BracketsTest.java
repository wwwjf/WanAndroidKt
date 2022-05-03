package com.wwwjf.wanandroidkt.algorithm;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Stack;

/**
 * 判断括号是否成对出现
 * (){}[]
 */
public class BracketsTest {

    @Test
    public void bracketTest() {
        String str = "{(})";
        Assert.assertFalse(isValid(str));
    }

    public boolean isValid(String s) {
        HashMap<Character, Character> map = new HashMap<>();
        map.put(')', '(');
        map.put('}', '{');
        map.put(']', '[');
        Stack<Character> stack = new Stack<>();
        if (s.length() % 2 == 1) {
            return false;
        }
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (!map.containsKey(c)){
                stack.push(c);
                continue;
            }
            if (stack.empty() || stack.peek() != map.get(c)){
                //栈顶元素不是对应的符号
                return false;
            }
            stack.pop();
        }
        return stack.empty();
    }
}
