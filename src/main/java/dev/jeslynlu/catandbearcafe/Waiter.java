package dev.jeslynlu.catandbearcafe;

// Waiter.java
// Brittany Vuong and Jeslyn Lu
// Waiter can be controlled by the player to perform actions. Waiter responds to requests from Customers, carries dishes to table from counter, and cleans tables.
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.lang.Math.*;
import java.io.*; 
import javax.imageio.*; 
import javax.swing.*;

public class Waiter {
  private int x, y, wid, hgt, speed, totMoney;
  private Chef chef;
  private Furniture hands; // waiter can hold up to 2 dishes at a time

  private Image[] pics; 
  private int frame, delay, dir, status, current;
	private boolean moving;
  public static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, WAIT = 2;
  
  private Point[] verticies = { // array of points that the waiter can go to
    new Point(80, 182), // table 0
    new Point(268, 125), // table 1
    new Point(230, 240), // table 2
    new Point(340, 160), // counter
  };

  // setting waiter values
  public Waiter(int width, int height, int fastness, int allMoney, Furniture waiterHands, Chef c) {
    x = (int)verticies[CafeGame.COUNTER].getX(); // waiter starts at counter
    y = (int)verticies[CafeGame.COUNTER].getY();
    wid = width; 
    hgt = height;
    totMoney = allMoney; //total amount of money in the game
    current = CafeGame.COUNTER; 
    chef = c;
    
    speed = fastness;
    hands = waiterHands;
    frame = 0;
    delay = 0;
    dir = 0;
    moving = false; 
  }

  // when waiter gets to the selected area, they can perform mutiple actions
  public void serve(Furniture area){
    if(area.isCounter() && area.isOccupied()){ // getting dish from counter into hands
      if(hands.getDishesLen()<1){
        area.getDish().move(hands);  
      } 
    }
    
    if(area.isTable() && area.isOccupied()){ // responding to requests from Customers
      if(area.getPeopleLen()>0){ // taking customers order
        if(area.getCustomer().waitingToOrder() && chef.canCook()){
          area.getCustomer().serve();
          chef.addOrder();
        }
      }
      if(area.getDishesLen()==0 && hands.getDishesLen()>0){ // setting dish from hands onto table
        if(area.getCustomer().waitingOnFood()){
          hands.getDish().move(area);
          area.getCustomer().serve();
        }
      }
      if(area.getDishesLen()>0){ // cleaning dirty table and getting money
        if(area.getDish().isEmpty()){
          totMoney += area.getDish().getCost();
          area.getDish().remove();
        }
      }
    }
  }

  
  public void move(Furniture area){
    move(area.getNum());
  }
  public void move(int d){
    Point src = verticies[current]; //first and second point of the movement
    Point dest = verticies[d];
    double[] v = similar(src, dest, speed); // using similar triangles
    int vx = (int)v[0];
    int vy = (int)v[1];

    if (distance(new Point(x,y), dest) <= 50) { // at/near the destination
      x = (int)dest.getX();
      y = (int)dest.getY();
      current = d;
      moving = false;
    }
    else{
      moving = true;
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
        if (vy>0){}
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

  public void update(int newSpeed){ //changes speed to given integer
    speed = newSpeed; 
  }
  
  // getting waiter values
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
  public int getCurrent() {
    return current;
  }
  public int getMoney() { // getting total money earned in the game
    return totMoney;
  }

  public void draw(Graphics g, Image[] pics) {
    Image[] nPics = new Image[3]; //array for images in a specific direction
    if (moving){
      if (dir == DOWN){
        for (int i=0; i<3; i++){
          nPics[i] = pics[i+6]; 
        }
        g.drawImage(nPics[frame],x,y,null); 
        //displays the images of the smaller array for that specific direction
      }
      if (dir == UP){
        for (int i=0; i<3; i++){
          nPics[i] = pics[i];
        }
        g.drawImage(nPics[frame],x,y,null);
      }
      if (dir == LEFT){
        for (int i=0; i<3; i++){
          nPics[i] = pics[i+9];
        }
        g.drawImage(nPics[frame],x,y,null);
      }
      if (dir == RIGHT){
        for (int i=0; i<3; i++){
          nPics[i] = pics[i+3];
        }
        g.drawImage(nPics[frame],x,y,null);
      }
    }
    else{
      g.drawImage(pics[7],x,y,null); //default direction character facing up
    }
  }

}