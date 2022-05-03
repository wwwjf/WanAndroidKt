package com.wwwjf.wanandroidkt.algorithm;

import org.junit.Test;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileOperateTest {

    public static final String testDir = "src/test/java/com/epay/epmm/file/";
    @Test
    public void writeFile() {
        boolean flag = false;
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(testDir+"fileoper.txt"));
            ListNode node = new ListNode(100);
            dataOutputStream.writeBytes("testtest");
            dataOutputStream.flush();
            dataOutputStream.close();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(testDir+"objectFile.txt"));
            objectOutputStream.writeObject(node);
            objectOutputStream.flush();
            objectOutputStream.close();
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(flag);
    }

    @Test
    public void readFile(){
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("objectFile.txt"));
            ListNode node = (ListNode) objectInputStream.readObject();
            objectInputStream.close();
            System.out.println(node.val);
            assertEquals(100,node.val);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
