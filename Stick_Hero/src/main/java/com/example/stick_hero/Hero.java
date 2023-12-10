package com.example.stick_hero;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Hero {
    private static ImageView heroImg = null;

    private Hero(){
    }

    public static ImageView getInstance(){
        if(heroImg== null){
            heroImg =new ImageView(HelloApplication.abs_path+"hero1.png");
        }

        heroImg.setX(224.0);
        heroImg.setY(253.0);
        heroImg.setFitHeight(51.0);
        heroImg.setFitWidth(41.0);
        heroImg.setPreserveRatio(true);
        heroImg.setSmooth(true);

        return heroImg;
    }


}
