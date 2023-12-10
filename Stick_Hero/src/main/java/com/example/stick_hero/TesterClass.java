package com.example.stick_hero;


import javafx.scene.shape.Rectangle;
//import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TesterClass {
    @Test
    public void testPillar() {
        Rectangle pill1 = Pillars.makePillar();
        double width1 = pill1.getWidth();
        assertTrue(width1>=40);
    }

    @Test
    public void testStick() {
        Stick st = new Stick(2,15);
        assertTrue(st!=null);
    }

    @Test
    public void testPillar2() {

        Rectangle pill = Pillars.makePillar();
        double width = pill.getWidth();
        assertTrue(width <= 110);
    }
    @Test
    public void testPillar3(){
        Rectangle pill = Pillars.makePillar();
        Rectangle pill2 = Pillars.makePillar();
        assertTrue(pill.getHeight() == pill2.getHeight());
    }
}
