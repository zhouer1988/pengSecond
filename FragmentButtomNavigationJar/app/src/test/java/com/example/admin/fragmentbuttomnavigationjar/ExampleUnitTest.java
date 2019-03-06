package com.example.admin.fragmentbuttomnavigationjar;

import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        List<String> testList = new ArrayList<>();
        testList.add("srfff");
        testList.add("srfff");
        testList.add(null);
        testList.add("srfff");
        testList.add("srfff");
        for (String s : testList) {
            System.out.println(s);
        }
    }
}