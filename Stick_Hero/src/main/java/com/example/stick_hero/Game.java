package com.example.stick_hero;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Game implements Initializable {

    private Stage stage;
    static Scene scene;
    private Parent root;

    //needs to be serialized.
    private int highScore=0;


    @FXML
    private AnchorPane anchor_pane_game;
    @FXML
    private Rectangle starter_pillar;
    @FXML
    private Text current_score;
    @FXML
    private Text cherry_count;
    @FXML
    private ImageView score_cherry;
    @FXML
    private Text main_high;
    @FXML
    //private Rectangle rectangle_stick;

    private Timeline timeline_stickGrow;
    private Timeline timeline_herosuccessful;
    private Timeline timeline_herofailure;
    private Timeline next_iter_prep;
    private Timeline stick_fall_timeline;
    private Timeline hero_fall_timeline;

    private Timeline stick_fall_trial;

    private boolean flagOnMouseReleaseMethod=true;
    private boolean flagOnMousePushMethod=true;
    private boolean isInitialFlag;
    private boolean cherry_collected = false;
    private boolean hero_inverted = false;
    private boolean hero_moving = false;

    private Rectangle pillar1;
    private Rectangle pillar2;
    private Stick activeStick;
    private ImageView hero1;
    private Rectangle rectangle_stick;

    ScorePlayer score;

    public int getScore(){
        return score.getScore();
    }
    private RotateTransition stickFallTransition;

    public void switchToPauseMenu(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("PauseMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public Game(){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        score = new ScorePlayer();
        current_score.setText(String.valueOf(score.getScore()));

        //SceneManager.GameScene.setOnKeyTyped(this::flipHero);
//        HelloApplication.game_scene.setOnKeyTyped(this::flipHero);


        //Add the first random pillar that you will add to the scene.
        pillar1 = starter_pillar;
        System.out.println("Pillar1 X === " + pillar1.getX());
        System.out.println("Pillar1 Width === " + pillar1.getWidth());

        pillar2 = Pillars.makePillar();
        anchor_pane_game.getChildren().add(pillar2);
        isInitialFlag = true;
        System.out.println("Pillar2 X === " + pillar2.getX());
        System.out.println("Pillar2 Width === " + pillar2.getWidth());

        //adding hero to the game
        hero1 = Hero.getInstance();
        anchor_pane_game.getChildren().add(hero1);

        //adding the stick to the game
        rectangle_stick = Stick.makeStick();
        rectangle_stick.setWidth(5);
        rectangle_stick.setHeight(0);
        anchor_pane_game.getChildren().add(rectangle_stick);
        System.out.println(rectangle_stick.getId());

        System.out.println("Stick X === " + rectangle_stick.getX());
        System.out.println("Stick Y === " + rectangle_stick.getY());


        //timeline for growing the stick.
        timeline_stickGrow = new Timeline(new KeyFrame(Duration.millis(5), e->{
            rectangle_stick.setY(rectangle_stick.getY()-1);
            rectangle_stick.setHeight(rectangle_stick.getHeight()+1);

        }));
        timeline_stickGrow.setCycleCount(Animation.INDEFINITE);



        //setting up the RotateTransition to start the stick fall transition.
        stickFallTransition = new RotateTransition(Duration.seconds(1), rectangle_stick);
        stickFallTransition.setByAngle(90);
        stickFallTransition.setAutoReverse(false);
        stickFallTransition.setCycleCount(1);

        stickFallTransition.setOnFinished(e-> {
            boolean heroReaches = isInPillar(pillar1,pillar2,rectangle_stick);
            hero_moving = true;
//            heroMove_success();
//            heroMove_fail();
            if(heroReaches == true){
                heroMove_success();
            }
            else{
                heroMove_fail();
            }
        });




        //timeline for making the hero fall.
        hero_fall_timeline = new Timeline(new KeyFrame(Duration.millis(10), e->{
            hero1.setY(hero1.getY()+1);
        }));
        hero_fall_timeline.setCycleCount(50);
        hero_fall_timeline.setOnFinished(e->{
            //need to load the Game over and all there.
//            Stage s = (Stage) scene.getWindow();


//            Parent root = null;
//            try {
//                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("GameOver.fxml")));
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//
//            scene = new Scene(root);
//            stage.setScene(scene);
//            stage.show();
        });



    }

    public void rotateStick(Rectangle stick){

        double pivotX = stick.getX()+stick.getWidth()/2;
        double pivotY = stick.getY()+stick.getHeight();

        Rotate rotate = new Rotate(0, pivotX,pivotY);
        rotate.setAngle(90);
        stick.getTransforms().add(rotate);

        stick_fall_trial = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rotate.angleProperty(), 0)),
                new KeyFrame(Duration.millis(500), new KeyValue(rotate.angleProperty(), 90))
        );

        stick_fall_trial.setCycleCount(1);


        stick_fall_trial.setOnFinished(e->{
            hero_moving = true;
            heroMove_success();
        });
        stick_fall_trial.play();

    }


    private void movePivot(Node node, double x, double y){
        node.getTransforms().add(new Translate(x,-y));
        node.setTranslateX(x);
        node.setTranslateY(y);
    }

    public void startGrowingStickMethod(MouseEvent event) throws IOException{
        if(flagOnMousePushMethod){
            timeline_stickGrow.play();
            flagOnMousePushMethod = false;
        }
    }
    public void stopGrowingStickMethod(MouseEvent event) throws IOException {
        if (flagOnMouseReleaseMethod) {
            timeline_stickGrow.stop();
            System.out.println("Stick Length =====" + rectangle_stick.getHeight());

            movePivot(rectangle_stick,0, rectangle_stick.getHeight()/2);
            stickFallTransition.setNode(rectangle_stick);
            stickFallTransition.play();
//            rotateStick(rectangle_stick);
            System.out.println("hi+++++++++++++++");
            flagOnMouseReleaseMethod = false;
        }
    }

    private boolean isInPillar(Rectangle pillar_initial, Rectangle pillar_final , Rectangle stick){

        double minXreq = pillar_final.getLayoutX() - (pillar_initial.getLayoutX()+ pillar_initial.getWidth());
        System.out.println("Minimum required X: " + minXreq);
        double width = pillar_final.getWidth();
        double maxXreq = minXreq + width;
        System.out.println("Maximum required X: " + maxXreq);
        if(stick.getHeight() > minXreq && stick.getHeight() < maxXreq){
            System.out.println("Will X reach: " + "true");
            return true;
        }
        else{
            System.out.println("Will X reach: " + "false");
            return false;
        }

    }

    private void heroMove_success(){
        int heroMoveCycleCount = (int) ((int) (pillar2.getX() + pillar2.getWidth()) - (pillar1.getX()+pillar1.getWidth()) - 5);

        timeline_herosuccessful = new Timeline(new KeyFrame(Duration.millis(10), e->{
            hero1.setX(hero1.getX()+1);
            checkCherryCollected();
        }
        ));

        timeline_herosuccessful.setCycleCount(heroMoveCycleCount);
        timeline_herosuccessful.play();
        timeline_herosuccessful.setOnFinished(e->{
            hero_moving = false;
            update_score();
            nextIterPrep();
        });
    }

    private void heroMove_fail(){
        int heroMoveCount = (int) (rectangle_stick.getHeight()+hero1.getFitWidth()+5);

        timeline_herofailure = new Timeline(new KeyFrame(Duration.millis(2), e->{
            hero1.setX(hero1.getX()+1);
        }));
        timeline_herofailure.setCycleCount(heroMoveCount);
        timeline_herofailure.play();
        timeline_herofailure.setOnFinished(e->{
            update_high_score();
            hero_fall_timeline.play();

        });
    }

    private void update_high_score(){
        int score = Integer.parseInt(current_score.getText());
        ScorePlayer score123 = new ScorePlayer();
        score123.iterateScoreList();

        if(score > highScore){
            highScore = score;
        }
    }

    private void update_score(){
        score.setScore(score.getScore()+1);
        current_score.setText(String.valueOf(score.getScore()));

        if(cherry_collected == true){
            int cherry = Integer.parseInt(cherry_count.getText());
            cherry+=1;
            cherry_count.setText(String.valueOf(cherry));
        }
    }

    private void nextIterPrep(){

        int nextIterCycleCount = (int) (pillar2.getX()+pillar2.getWidth()-(pillar1.getX()+pillar1.getWidth()));
        next_iter_prep = new Timeline(new KeyFrame(Duration.millis(5), e->{
            hero1.setX(hero1.getX()-1);
            rectangle_stick.setX(rectangle_stick.getX()-1);
            pillar1.setX(pillar1.getX()-1);
            pillar2.setX(pillar2.getX()-1);

        }));

        next_iter_prep.setCycleCount(nextIterCycleCount);
        next_iter_prep.play();
        next_iter_prep.setOnFinished(e->{
            add_rem_elem();
        });

    }

    private void add_rem_elem(){

        //maybe I will need to add some of the
        Rectangle trash = pillar1;
        anchor_pane_game.getChildren().remove(trash);
        pillar1 = pillar2;

        //adding a new pillar to the game for next iteration of the game.
        pillar2 = Pillars.makePillar();
        anchor_pane_game.getChildren().add(pillar2);

        //add the new stick to the iteration.

        Rectangle trash_stick = rectangle_stick;
//        trash_stick.setVisible(false);
//        anchor_pane_game.getChildren().remove(trash_stick);
//        anchor_pane_game.getChildren().remove(rectangle_stick);
        rectangle_stick = Stick.makeStick();
        anchor_pane_game.getChildren().add(rectangle_stick);

        rectangle_stick.setHeight(5.0);
        rectangle_stick.setWidth(5.0);
        rectangle_stick.setX(262.0);
        rectangle_stick.setY(298.0);
        rectangle_stick.setLayoutX(0);
        rectangle_stick.setLayoutY(0);
        //setting the flags for the next iteration.
        flagOnMouseReleaseMethod=true;
        flagOnMousePushMethod=true;

        //Setting cherry for the next iteration.
        setCherry();
        cherry_collected = false;

    }

    //this function returns the x coordinate of the cherry.
    private double getCherryRandX(){
        double minX = pillar1.getLayoutX()+pillar1.getWidth();
        double maxX = pillar2.getLayoutX();

        Random randomXcalc = new Random();
        return minX + (maxX-minX)* randomXcalc.nextDouble();
    }

    //make sure to remove the commented code
    public void setCherry(){
        Random rand1 = new Random();
        boolean cherryhere = rand1.nextBoolean();
        System.out.println("Cherry placed in this iteration.");
            double cherryX = getCherryRandX();
            score_cherry.setX(cherryX);
            if(!score_cherry.isVisible()){
                score_cherry.setVisible(true);
            }
//        if(cherryhere== false){
//            System.out.println("No cherry in this iteration. ");
//        }
//        else{
//            System.out.println("Cherry placed in this iteration.");
//            double cherryX = getCherryRandX();
//            score_cherry.setLayoutX(cherryX);
//            if(score_cherry.isVisible()==false){
//                score_cherry.setVisible(true);
//            }
//        }
        return;
    }

    public void showHighScore(MouseEvent event){
        main_high.setText(String.valueOf(highScore));
    }

    public void switchToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void reviveHero(ActionEvent event){
        int cherry = Integer.parseInt(cherry_count.getText());
        if(cherry>2){
            cherry=cherry-2;
            //how should I revive the game?
        }
        cherry_count.setText(String.valueOf(cherry));
    }

    public void loadGameOverPage(){

    }
    private void checkCherryCollected(){
        if(hero1.getX() == score_cherry.getX() && hero_inverted== true){
            score_cherry.setVisible(false);
            cherry_collected = true;
        }
    }

    public void flipHero(ActionEvent e){
        if(hero_moving == true && hero_inverted==false){
            hero1.setScaleY(-1);
            hero1.setY(hero1.getY()+hero1.getFitHeight()+5);
            hero_inverted = true;
        }
        else if(hero_moving == false){
            return;
        }
        else if(hero_moving == true && hero_inverted == true){
            hero1.setScaleY(1);
            hero1.setY(hero1.getY()-hero1.getFitHeight()-5);
            hero_inverted = false;
        }
    }

    //serialisation implemented here
    public void saveGame(MouseEvent e) throws IOException {
        ScorePlayer s1 = new ScorePlayer();
        s1.setScore(score.getScore());
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream (
                    new
                            FileOutputStream("out.txt"));
            out.writeObject(s1);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            out.close();
        }
    }

    //deserialisation implemented here
    public void loadGame(MouseEvent e) throws IOException, ClassNotFoundException {
        ObjectInputStream in = null;
        ScorePlayer s1;
        try {
            in = new ObjectInputStream(
                    new FileInputStream("out.txt"));
            s1 = (ScorePlayer) in.readObject();
        } finally {
            in.close();
        }

    }


}
