package com.example.stick_hero;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ScorePlayer {

    ArrayList<Integer> scorelist = new ArrayList<Integer>();


    private int score = 0;

    public ScorePlayer() {
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void iterateScoreList(){
        Iterator iter = scorelist.iterator();

        while(iter.hasNext()){
            System.out.println(iter.next());
    }
    }

//        public static void serialize(ScorePlayer s1) throws IOException {
//         ObjectOutputStream out = null;
//         try {
//         out = new ObjectOutputStream (
//         new
//                 FileOutputStream("out.txt"));
//         out.writeObject(s1);
//         } finally {
//         out.close();
//         }
//        }
//
//
//    public static ScorePlayer deserialize() throws IOException, ClassNotFoundException {
//        ObjectInputStream in = null;
//        ScorePlayer s1;
//        try {
//            in = new ObjectInputStream(
//                    new FileInputStream("out.txt"));
//            s1 = (ScorePlayer) in.readObject();
//        } finally {
//            in.close();
//        }
//        return s1;
//    }

}
