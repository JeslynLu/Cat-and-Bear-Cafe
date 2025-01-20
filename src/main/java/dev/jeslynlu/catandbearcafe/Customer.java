package dev.jeslynlu.catandbearcafe;

// Customer.java
// Brittany Vuong and Jeslyn Lu
// Customers go through the stages of eating at a restaurant. They have a happiness bar that decreases when they are at a waiting stage. If their happiness gets too low, they leave.
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.lang.Math.*;

public class Customer {
  private int x, y, wid, hgt, speed, status, totUnHappy;
  private int happiness, patienceRate; //happiness level and patience rate
  private boolean angry, satisfied; // moods that customers are in depending on their happiness
  private long startTime; 
  private ArrayList<Customer> totCustomers; //array list of all customers

  private Furniture location; // table or area the customer is staying at
  public static final int SEAT_WAIT=0, MENU_LOOK=1, ORDERING_WAIT = 2, FOOD_WAIT=3, EATING=4, LEAVING=5; // stages of being at a restaurant
  private Color happyCols[] = {new Color(199, 193, 193),new Color(251,5,5), new Color(251,163,4), new Color(251,242,6), new Color(154,244,154,255), new Color(6,218,6)};
  // happiness bar colours from red (angry) to green (happy)
  private static final int DOOR=3; // in Customer class, DOOR is 3, unlike in other classes

  private Point[] verticies = {
    new Point(15,205),  // table 0  41, 135
    new Point(130,110), // table 1  146, 149
    new Point(160,310), // table 2  286, 297
    new Point(105, 317), // door
  };

  private Image[] pics; //sprites
  private int frame, delay, dir; //variables for animation
  private boolean moving; //whether the customer is moving or not
  public static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, WAIT = 2; //direction variables and wait variable for delay

  private int counter, stayDelay; //delay for customers length of stay

  // setting customer values
  public Customer(int width, int height, int happyRate, int fastness, int allUnHappy, Furniture startArea, ArrayList<Customer> allCustomers) {
    wid = width;
    hgt = height;
    patienceRate = happyRate;
    happiness = 50; // happiness starts half full
    speed = fastness;
    totUnHappy = allUnHappy;
    location = startArea;
    startArea.addPerson(this);
    totCustomers = allCustomers;

    // so customers get in a line by the door
    if(startArea.getQueueNum(this)==0){ 
      x = (int)verticies[DOOR].getX();
    }
    else{
      x = (int)verticies[DOOR].getX() - startArea.getQueueNum(this)*wid;
    }
    y = (int)verticies[DOOR].getY();
    status = SEAT_WAIT;
    angry = false; satisfied=false;
    
    counter = 0;
    stayDelay = 30;
    dir = 0;
    moving = false; //starts off still
  }
  
  public void move(Furniture area){
    //this takes a destination of the furniture class and moves the customer to that area
    int d = area.getNum(); //index of area in the array of areas
    Point src = verticies[getLocationNum()]; //current location point
    Point dest = verticies[d]; //destination point
    double[] v = similar(src, dest, speed); //gets the smaller speed values using similar triangles
    int vx = (int)v[0]; //x and y speeds
    int vy = (int)v[1];

    if (distance(new Point(x,y), dest) <= 50) { //customer is at the destination
      x = (int)dest.getX();
      y = (int)dest.getY();
      location = area; //updates current location
      area.addPerson(this); //adds the customer to that table
      moving = false; //stops moving
    }
    else{
      moving = true; //is moving
      x += vx;
      y += vy;
      if (Math.abs(vx)>Math.abs(vy)){ // if |vx| is bigger, direction is left or right
        if (vx>0){
          dir = RIGHT;
        }
        if (vx<0){
          dir = LEFT;
        }
      }
      if (Math.abs(vy)>Math.abs(vx)){ // if |vy| is bigger, direction is up or down
        if (vy>0){
          dir = DOWN;
        }
        if (vy<0){
          dir = UP;
        }
      }
      
      delay++;
      if(delay % WAIT == 0){
				frame++; //changes the frame during movement
				if(frame == 3){
					frame = 0; //goes back to 0 when the last frame is reached
        }
      }
    }
  }
  
  public void leave(){ // only for leaving as this one doesn't change location
    Point src = verticies[getLocationNum()]; //current location
    Point dest = new Point(105, 350); //door location with lower y position
    //same as move
    double[] v = similar(src, dest, speed);
    int vx = (int)v[0];
    int vy = (int)v[1];
    if (distance(new Point(x,y), dest) <= 50) {
      x = (int)dest.getX();
      y = (int)dest.getY();
      location.removePerson(this); // removes customer from the table
      totCustomers.remove(this); // removes customer from total customers
      moving = false; // stops moving
    }
    else{
      // SAME AS MOVE
      moving = true;
      x += vx;
      y += vy;
      if (Math.abs(vx)>Math.abs(vy)){
        if (vx>0){
          dir = RIGHT;
        }
        if (vx<0){
          dir = LEFT;
        }
      }
      if (Math.abs(vy)>Math.abs(vx)){
        if (vy>0){
          dir = DOWN;
        }
        if (vy<0){
          dir = UP;
        }
      }
      delay++;
      if(delay % WAIT == 0){
				frame++;
				if(frame == 3){
					frame = 0;
				}
			}
    }
  }
  
 public double[] similar(Point p1, Point p2, int sp){ 
    // similar returns two values for the smaller x and y speed that the movement uses
    double[] s = {0,0};
    double wid = p2.getX()-p1.getX(); //width between the two x values
    double hei = p2.getY()-p1.getY(); //height between the two y values
    double hyp = Math.max(1,Math.hypot(wid,hei));
    s[0] = sp * wid / hyp; // new x speed
    s[1] = sp * hei / hyp; // new y speed
    return s;
  }
  
  public double distance(Point p1, Point p2) { //calculates the distance between two points
    return Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
  }
  

  public void serve() {
    // if a custiomer's request is meet by Waiter, their happiness goes up a bit
    if(happiness+25>100){
      happiness = 100;
    }
    else{
      happiness = happiness+25;
    }
    startTime = System.currentTimeMillis();
    status++;
 
  }

  public void statusTimer(){ // for certain status stages, customer takes some time before moving onto next status/stage
    long elapsedTime = System.currentTimeMillis() - startTime; // gets time passed after timer started
    long elapsedSecs = elapsedTime / 1000;
    if(elapsedSecs>=patienceRate){ 
      if(status==MENU_LOOK){
        status=ORDERING_WAIT;
      }
      else if(status==EATING){
        status=LEAVING;
      }
    }
  }

  // updates the customers location, and starts the timer for certain actions
  public void update() {
    if(location.isDoor() && moving==false){
      if(location.getQueueNum(this)==0){ // so that the customer queue moves up
        x = (int)verticies[DOOR].getX();
      }
      else{
        x = (int)verticies[DOOR].getX() - location.getQueueNum(this)*wid;
      }
    }
    if(status==LEAVING){ // customers that have been served all the way pay
      if(happiness>80){
        satisfied=true;
      }
      pay();
      leave();
    }
    else if(status==MENU_LOOK || status==EATING){ // these stages take the customer some time
       statusTimer(); 
     }
    else if(status==SEAT_WAIT || status==ORDERING_WAIT || status==FOOD_WAIT){
      counter ++; // as customer waits, their happiness goes down
      if(counter>patienceRate*5){
        if (happiness-- <= 0) { // customer leaves without paying when no happiness
          status=LEAVING;
          totUnHappy ++;
        } 
        else {
          happiness--; // customer loses happiness over time
        }   
        counter = 0;
      }
      if(happiness<20){
        angry = true;
      }
      else{
        angry = false;
      }
    }

  }

  public void pay(){ // customer pays depending on hiow happy they are
    if(happiness>0){
      location.getDish().setCost(2*speed+(happiness/10)); // money earned for succesfully serving customer based on difficulty, happiness amount is like the tip amount
    }
  }

  // public int getTotUnHappy() { 
  //   return totUnHappy;
  // }

  // getting customer values
  public Rectangle getRect() {
    return new Rectangle(x, y, wid, hgt);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getWid() {
    return wid;
  }

  public int getHgt() {
    return hgt;
  }

  public int getHappiness() {
    return happiness;
  }
  private int getLocationNum(){ // since DOOR in customer value is different than DOOR value for all other classes
    if(location.isDoor()){
      return DOOR;
    }
    else{
      return location.getNum();
    }
  }
  public int getLocation(){
    return location.getNum();
  }
  public int getStatus(){
    return status;
  }

  public boolean waitingToOrder() {
    if (status == ORDERING_WAIT) {
      return true;
    } else {
      return false;
    }
  }
  public boolean waitingOnFood() {
    if (status == FOOD_WAIT) {
      return true;
    } else {
      return false;
    }
  }

  public void draw(Graphics g, Image[] pics, Image[] statusPics) {
    Image[] nPics = new Image[3]; // array for images in a specific direction
    if (moving){
      if (dir == DOWN){
        for (int i=0; i<3; i++){
          nPics[i] = pics[i+6];
        }
        g.drawImage(nPics[frame],x,y,null);
        // displays the images of the smaller array for that specific direction
      }
      if (dir == UP){
        for (int i=0; i<3; i++){
          nPics[i] = pics[i+3];
        }
        g.drawImage(nPics[frame],x,y,null);
      }
      if (dir == LEFT){
        for (int i=0; i<3; i++){
          nPics[i] = pics[i];
        }
        g.drawImage(nPics[frame],x,y,null);
      }
      if (dir == RIGHT){
        for (int i=0; i<3; i++){
          nPics[i] = pics[i+9];
        }
        g.drawImage(nPics[frame],x,y,null);
      }
    }
    else{
      if (getLocationNum() == 1){ // if customer is at table one, they face downward
        g.drawImage(pics[7],x,y,null);
      }
      else if (getLocationNum() == 2){ // faces to the right at table 2
        g.drawImage(pics[10],x,y,null);
      }
      else{ // facing up
        g.drawImage(pics[4],x,y,null);
      }
    }
    
    // drawing happiness bar
    g.setColor(happyCols[0]);
    g.fillRect(x+1, y+hgt+5, wid-5, 5);
    double happinessDoub = happiness;
    double widDoub = wid-5;
    g.setColor(happyCols[happiness/20]);
    g.fillRect(x+1, y+hgt+5, (int)(widDoub*happinessDoub/100), 5);

   if(status==MENU_LOOK){ // drawing menu
     g.drawImage(statusPics[MENU_LOOK],location.getX()+20,location.getY()-10,null); 
   }
   if(status==ORDERING_WAIT){ // drawing alert marks
     g.drawImage(statusPics[ORDERING_WAIT],x+6,y-35,null); 
   }
   if(status==FOOD_WAIT){
     g.drawImage(statusPics[0],x+6,y-35,null); // blank bubble
     g.drawImage(statusPics[FOOD_WAIT],x+6,y-35,null); // food pic
   }
   if(angry){ // drawing angry vein
     g.drawImage(statusPics[4],x-10,y-5,null);
   }
   if(satisfied){ // drawing star
     g.drawImage(statusPics[5],x-10,y-5,null);
   }
  }
  

}