package dev.jeslynlu.catandbearcafe;

// Chef.java
// Brittany Vuong and Jeslyn Lu
// Chefs can make dishes onto the counter area.
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.lang.Math.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class Chef {
  private int x,y, wid, hgt;

  private Image[] pics;
  private int frame, delay, status;
  private int totOrders; // orders to make dishes 
  private int cookCounter, cookDelay; // to time the cooking process
  private int maxSpeed; // determines how fast the chef can go
  private Furniture counter; // chef stays behind the counter
  public static final int IDLE = 0, BUSY = 1, WAIT = 2;

  public Chef(int xX, int yY, int width, int height, int maxFastness, Furniture area, Image[] pcs){
    //setting basic values given
    x = xX;
    y = yY;
    wid = width;
    hgt = height;
    pics = pcs;
    totOrders = 0; //no orders 
    cookDelay = 0; //delay count variable for cooking time
    maxSpeed = maxFastness; //maximum speed
    counter = area; //counter space

    status = IDLE; //chef is not cooking

    frame = 0; 
    delay = 0;		
  }

  public void addOrder(){ //adds to the total orders
    totOrders ++;
  }    

  public void makeFood(){
    //animates the chef cooking the food
    delay++;
    if(delay % 4  == 0){ 
      frame++; //frame increases
      if(frame == pics.length){
        frame = 0; //goes back to 0 at the end
      }
    }
  }

  // checks if chef got any orders and times the cooking process if so
  public void update(int currentSpeed,  ArrayList<Dish> allDishes){
    cookDelay = Math.max(3, maxSpeed-currentSpeed);
    if(totOrders > 0){ // if order comes in
      cookCounter ++;
      if(cookCounter <= cookDelay*10){ // delays the cooking process
        makeFood();
        status = BUSY;
      }
      else if(cookCounter > cookDelay*10){ // cooking time wait is done
        totOrders --;
        counter.addDish(new Dish(35, 35, allDishes, counter)); 
        cookCounter = 0;
        status = IDLE;
      }
    }  
  }

  public boolean canCook(){ // ensures only 2 dishes can be ready on the counter
    if(totOrders <=2 && counter.getDishesLen()<2){
      return true;
    }
    else{
      return false;
    }
  }
    
  public void draw(Graphics g) {
    if (status == IDLE){
      g.drawImage(pics[0],x,y,null);
    }
    else{
      g.drawImage(pics[frame],x,y,null);
    }
  }
}