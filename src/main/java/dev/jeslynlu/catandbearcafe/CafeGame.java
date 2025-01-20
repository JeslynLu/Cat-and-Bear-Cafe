package dev.jeslynlu.catandbearcafe;

// Main.java
// Brittany Vuong and Jeslyn Lu
// Time-management restaurant game. The player controls the waiter with the mouse and click to complete actions. 
// Customers randomly come in to be seated at an open table by the player. The customer will go through stages of eating at a restaurant, and request things from Waiter during that time.
// Customers have a happiness bar. The longer they are left waiting, the more their happiness decreases. If the bar reaches zero, they leave. 
// The main objective is to earn as much money as possible by successfully serving customers before they run out of happiness. Depending on how much happiness they have by the time they are served, the money they leave varies. 
// Difficulty increases as time goes on.

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.*;
import java.net.URL;

public class CafeGame extends BaseFrame {
  public static final int INTRO = 0, GAME = 1, END = 2, MENU = 3, SOUND = 4, SETTINGS = 5, CREDITS = 6; // to move between screens
  private int screen = INTRO;
  public static final int BORDER = 30; // border of frame at the top
  public static final int WID = 500; // width of frame
  public static final int HGT = 370 + BORDER; // height of frame
  public static final int TABLE = 0, COUNTER = 3, DOOR = 4, HANDS = 5;

  //rectangles for all buttons
  public static final Rectangle button1 = new Rectangle(284,188,176,38); 
  public static final Rectangle button2 = new Rectangle(284,244,176,38);
  public static final Rectangle button3 = new Rectangle(284,300,176,38);
  
  public static final Rectangle menuButton = new Rectangle(9,7,17,25);
  public static final Rectangle soundButton = new Rectangle(40,5,21,21);

  public static final Rectangle settings_normal = new Rectangle(297,244,51,20);
  public static final Rectangle settings_hard= new Rectangle(355,244,51,20);
  public static final Rectangle settings_extreme = new Rectangle(413,244,51,20);
  public static final Rectangle settings_on = new Rectangle(315,315,60,24);
  public static final Rectangle settings_off = new Rectangle(387,315,60,24);
  public static final Rectangle settings_exit = new Rectangle(443,202,19,18);

  public static final Rectangle credits_exit = new Rectangle(391,49,29,28);

  private ArrayList<Furniture> areas; // holds all clickable areas: tables, counter, and door
  private ArrayList<Customer> customers; // holds all customers on screen
  private ArrayList<Dish> dishes; // holds all dishes on screen
  
  private Waiter player; // waiter that the player clicks to move and do actions
  private Chef bear; // animated character that makes food
  private Furniture waiterHands; // so waiter can hold dishes
  private Furniture door; // area where customers are spawned. customers line up in queue here
  private Furniture counter; // area where food is made and can be grabbed from
  
  private long startTime, elapsedTime, elapsedMins, elapsedSecs, secsDisplay; // will keep the current time
  private String counterStr = ""; // time since gameplay has started
  
  private int lvl; // level of game
  private int[] areasClicked; // keeps track of the areas user has clicked
  private int money; // keeps track of money earned- the score
  private int clicks; // keeps track of number of clicks 
  private int totUnHappy, unHappyMax; // how many customers leave angry and without paying. only so many customers can leave before you lose the game
  private boolean waiterMoving, customerMoving; // whether waiter/customer is currently moving on screen
  private int speed, maxSpeed; // speed for the whole game. as time goes on, difficulty increases by increasing speed
  private int spawnCounter; // delay counter for spawning customers

  public CafeGame() {
    super("Restaurant", WID, HGT);
    loadImages();
    areasClicked = new int[2]; // stores the first area clicked, and the area clicked right after

  }


public void loadImages() {
        gameFloorbig = loadImage("/images/gameFloorbig.png");
        intro = loadImage("/images/gameIntro.png");
        border = loadImage("/images/border.png");
        menu = loadImage("/images/menu.png");
        tablePic = loadImage("/images/table.png");
        counterPic = loadImage("/images/counter.png");
        doorPic = loadImage("/images/door.png");
        waiter = loadImage("/images/waiter1.png");
        chef = loadImage("/images/chef1.png");
        teen = loadImage("/images/teen8.png");
        mtPlate = loadImage("/images/mtPlate.png");
        omelettePic = loadImage("/images/omelettePic.png");
        moneyPic = loadImage("/images/moneyPic.png");
        soundOn = loadImage("/images/soundOn.png");
        soundOff = loadImage("/images/soundOff.png");
        settings = loadImage("/images/settings.png");
        credits = loadImage("/images/credits.png");

        waiterPics = new Image[12]; // creating array of sprites for waiter
        for (int i = 0; i < 12; i++) {
            waiterPics[i] = loadImage("/images/waiter" + (i + 1) + ".png");
        }

        chefPics = new Image[10]; //creating array of sprites for chef
        for (int i = 0; i < 10; i++) {
            chefPics[i] = loadImage("/images/chef" + (i + 1) + ".png");
        }

        teenPics = new Image[12]; //creating array of sprites for teen customer
        for (int i = 0; i < 12; i++) {
            teenPics[i] = loadImage("/images/teen" + (i + 1) + ".png");
        }

        customerStatusPics = new Image[6]; //array of all emotes for the customers
        customerStatusPics[0] = loadImage("/images/emoteBlankPic.png");
        customerStatusPics[1] = loadImage("/images/menuPic.png");
        customerStatusPics[2] = loadImage("/images/emoteAlertPic.png");
        customerStatusPics[3] = omelettePic;
        customerStatusPics[4] = loadImage("/images/emoteAngerPic.png");
        customerStatusPics[5] = loadImage("/images/emoteStarPic.png");
    }



    private Image loadImage(String path) {
      URL resource = getClass().getResource(path);
      if (resource == null) {
        System.err.println("Error: Resource not found at " + path);
        return null; // Handle missing resources
    }
    return new ImageIcon(resource).getImage();
  }

  // restart is used to reset and regenerate game objects for new gameplay
  public void restart() {
    screen = GAME;
    counterStr = "";
    startTime = System.currentTimeMillis(); // getting current time
    elapsedTime =0; elapsedMins=0; elapsedSecs=0; secsDisplay=0;
    areasClicked[0] = -1; // nothing has been clicked
    areasClicked[1] = -1;
    money = 0;
    clicks = 0;
    speed = 5; maxSpeed = 10;
    waiterMoving = false; customerMoving = false;
    lvl = 0;
    spawnCounter = 0;
    totUnHappy = 0; unHappyMax = Math.max(3, maxSpeed-speed); 

    areas = new ArrayList<Furniture>();
    areas.add(new Furniture(2, 153, 78, 83, 0)); // table 0
    areas.add(new Furniture(173, 108, 78, 83, 1)); // table 1
    areas.add(new Furniture(205, 317, 78, 83, 2)); // table 2
    counter=new Furniture(380, 100, 121, 204, COUNTER);
    door=new Furniture(89, 348, 73, 70, DOOR);
    areas.add(counter);
    areas.add(door);
    
    waiterHands = new Furniture(340, 160, 10, 10, HANDS);
    bear = new Chef(420, 150, 50, 100, maxSpeed, counter, chefPics); 
    player = new Waiter(35, 35, speed, money, waiterHands, bear); // waiter starts at counter
    customers = new ArrayList<Customer>();
    dishes = new ArrayList<Dish>();
    spawnCustomer(); // starts the game with one customer
  }

  // returns what area the user has clicked on
  public int getArea(int x, int y) {
    Point mouse = new Point(x, y);
    for (int i = 0; i < areas.size(); i++) {
      if (areas.get(i).getRect().contains(mouse)) {
        return i;
      }
    }
    return -1; // no area was clicked
  }

  public void move() {
    
    Point mouse = new Point(mx, my); // current mouse position
    if (screen == INTRO) {
      if (mb > 0) {
        if (button1.contains(mouse)){ // checks if button is clicked
          restart();
          screen = GAME;
        }
        if (button2.contains(mouse)){
          screen = SETTINGS;
        }
        if (button3.contains(mouse)){
          screen = CREDITS;
        }
      }
    } 

    else if (screen == SETTINGS){ //checking all buttons in settings for click
      if (settings_exit.contains(mouse)){
        screen = INTRO;
      }
      if (settings_normal.contains(mouse)){ // increasing difficulty
        speed = 5;
        maxSpeed = 10;
      }
      if (settings_hard.contains(mouse)){
        speed = 10;
        maxSpeed = 20;
      }
      if (settings_extreme.contains(mouse)){
        speed = 15;
        maxSpeed = 30;
      }
      if (settings_on.contains(mouse)){ // flag for music playing or not

      }
      if (settings_off.contains(mouse)){

      }
    }
    else if (screen == CREDITS){
      if (credits_exit.contains(mouse)){
        screen = INTRO;
      }
    }
    else if (screen == GAME) {
      if (mb > 0) {
        if (menuButton.contains(mouse)){ //checks if menu button is clicked
          screen = MENU;
        }
      }
      timeCounter();
      getSelectedAreas();
      
      // difficulty increases with time, after a certain time difficulty stops increasing
      if(elapsedMins>=(lvl+1) && elapsedMins<maxSpeed){
        lvl ++;
        speed ++;
        player.update(speed);
      }
      // spawning customers over time
      spawnCounter ++;
      if(spawnCounter>50*(maxSpeed+1-speed)){ // customers spawn faster with increasing difficulty
        spawnCounter = 0;
        int spawnChance = new Random().nextInt(101); // making spawming a bit more random
        if(spawnChance<75){
          spawnCustomer();
        }
      }
      // updating on screen values
      waiterHands.updateHands(player);

      for(int i = 0; i < customers.size(); i++){
        customers.get(i).update();
        // totUnHappy = customers.get(i).getTotUnHappy();
      }
      for(int i = 0; i < dishes.size(); i++){
        dishes.get(i).update();
      }

      bear.update(speed, dishes);
      money = player.getMoney(); // getting total money earned
 
      if(elapsedMins >= 2){ // game ends once a certain amount of time has passed
         screen = INTRO;
      }
    }

    else if (screen == MENU){ //menu page in the game
      if (mb > 0){
        if (button1.contains(mouse)){
          screen = GAME;
        }
        else if (button2.contains(mouse)){
          restart();
        }
        else if (button3.contains(mouse)){
          screen = INTRO;
        }
      }
    }
      
    else if (screen == END) {

    }
  }

  public void timeCounter(){ // counts game time
    elapsedTime = System.currentTimeMillis() - startTime; // gets time passed after game screen starts
    elapsedSecs = elapsedTime / 1000;
    secsDisplay = elapsedSecs % 60;
    elapsedMins = elapsedSecs / 60;
    if (secsDisplay < 10) {
      counterStr = elapsedMins + ":0" + secsDisplay;
    } else {
      counterStr = elapsedMins + ":" + secsDisplay;
    }
  }
  
  public void getSelectedAreas(){ // getting the areas user has clicked and performing relevant actions
    if (mb > 0 && waiterMoving==false && customerMoving==false) { 
      clicks ++;
      if (clicks==1) {
        areasClicked[0] = getArea(mx, my);
      } 
      else if (clicks==2) {
        areasClicked[1] = getArea(mx, my);
      }
      else if(clicks>2){ // resets click trackers
        clicks=0;
        areasClicked[0] = -1;
        areasClicked[1] = -1;
      }

    }
    if(areasClicked[0] ==-1 && areasClicked[1] == -1){
        clicks = 0;
    }
    else if(areasClicked[0] == areasClicked[1]){ // preventing double clicking on the same area
      areasClicked[1]=-1;
      clicks = 1;
    }
    int firstArea = areasClicked[0]; // first area selected
    int secArea = areasClicked[1]; // second area selected

    // moving customer at front of door queue to a table by click
    if (firstArea == DOOR) {
      if (secArea >= 0 && secArea <= 2) { // if secArea is a table
        if (door.isOccupied() && areas.get(secArea).isEmpty()) {
          customerMoving = true;
        }
        else{
          clicks=0;
          areasClicked[0] = -1;
          areasClicked[1] = -1;
        }
      }
    }
    // moving waiter to an area by click
    else if (firstArea != -1) {
      if (player.getCurrent() == firstArea) {
        player.serve(areas.get(firstArea));
        clicks=0;
        areasClicked[0] = -1;
        waiterMoving = false;
      } 
      else {
        player.move(areas.get(firstArea));
        clicks=0;
        waiterMoving = true;
      }
    }
    if(customerMoving){
      moveCustomer(door.getFront(), secArea);
    }
  }
  // actually moving customer to new area
  public void moveCustomer(Customer frontPerson, int secArea){
    if(frontPerson.getLocation()==secArea){
      door.removePerson(door.getFront());
      frontPerson.serve();
      clicks=0; // resetting areas selected
      areasClicked[0] = -1;
      areasClicked[1] = -1;
      customerMoving = false;
    }
    else{
      frontPerson.move(areas.get(secArea));
    }
  }

  // spawns customers of two different types
  public void spawnCustomer(){
    int typeA = Math.max(3, maxSpeed-speed); // impatient type, lower patienceRate
    int typeB = Math.max(8, maxSpeed-speed+5); // patient type, higher patienceRate
    int patienceRate = new Random().nextBoolean() ? typeA : typeB;
    customers.add(new Customer(44, 70, patienceRate, speed, totUnHappy, door, customers));
  }


  @Override
  public void draw(Graphics g) {
    Font titleFnt = new Font("Agency FB", Font.BOLD, 20);
    if (screen == INTRO) {
      g.drawImage(intro, 0, 0, null);
    } 
    else if (screen == GAME) {
      Graphics gg = getGraphics();
      g.drawImage(border, 0, 0, null);
      g.drawImage(gameFloorbig, 0, BORDER, null);
      

      // drawing the time counter
      Font borderFnt = new Font("Agency FB", Font.PLAIN, 15);
      g.setColor(Color.BLACK);
      g.setFont(borderFnt);
      g.drawString(counterStr, 310, 22);
      String moneyStr = Integer.toString(money);
      g.drawString("$"+moneyStr, 430, 22);

      if (areas == null) {
        return;
      }
      // drawing main floor areas
      for (int i = 0; i < areas.size(); i++) {
        if (i == 3) {
          areas.get(i).draw(g, counterPic);
        } else if (i == 4) {
          areas.get(i).draw(g, doorPic);
        } else {
          areas.get(i).draw(g, tablePic);
        }
      }
      for(int i = 0; i < customers.size(); i++){
        customers.get(i).draw(g, teenPics, customerStatusPics);
      }
      for(int i = 0; i < dishes.size(); i++){
        dishes.get(i).draw(g, mtPlate, omelettePic, moneyPic);
      }
      bear.draw(g);
      player.draw(g, waiterPics); 

      
    }

    else if (screen == MENU) {
      g.drawImage(menu,0,0,null);
    }
    else if (screen == SETTINGS){
      g.drawImage(settings,0,0,null);
    }
    else if (screen == CREDITS){
      g.drawImage(credits,0,0,null);
    }
    else if (screen == END) {
      g.setColor(new Color(0, 0, 147, 255));
      g.fillRect(0, 0, WID, HGT);
      g.setColor(Color.WHITE);
      g.setFont(titleFnt);
      g.drawString("Level cleared!", 85, 100);
    }
  }

  public static void main(String[] args) {
    new CafeGame();
  }
}