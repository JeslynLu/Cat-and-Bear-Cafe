package dev.jeslynlu.catandbearcafe;

// Dish.java
// Brittany Vuong and Jeslyn Lu
// Dishes can be moved from area to area. They're initialized by the Chef and moved by the Waiter.
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Dish {
  private int x, y, wid, hgt, status, num;
  private int cost; // money earned from successfully serving a customer a dish 
  private Furniture location; 
  private ArrayList<Dish> totDishes;
  public static final int EMPTY = 0, HAS_FOOD = 1; // either an empty plate or with food
  private Point[] verticies = { // array of points that the dishes should be located
  new Point(10, 175), // table 0
  new Point(185, 120), // table 1
  new Point(220, 335), // table 2
  new Point(383, 199), // counter
  
  };

  // setting dish values
  public Dish(int width, int height, ArrayList<Dish> allDishes, Furniture area) {
    wid = width;
    hgt = height;
    totDishes = allDishes;
    location = area;
    
    x = (int)verticies[location.getNum()].getX();
    y = (int)verticies[location.getNum()].getY();
    num = location.getDishesLen()%2; // setting the dish to be placed in either one of two areas
    status= HAS_FOOD;
    cost = 0;
    totDishes.add(this);
  }

  // moving dish from one location to another (from counter to waiter's hands)
  public void move(Furniture newArea){
    location.removeDish();
    newArea.addDish(this);
    location = newArea;
  }

  // making food dish into empty plate
  public void eat(){
    status = EMPTY;
  }

  // setting the money value of the dish, depending on the customer's happiness
  public void setCost(int cash){
    cost = cash;
  }

  // deleting the dish
  public void remove(){
    location.removeDish();
    totDishes.remove(this);
  }

  // updates dish's location
  public void update(){
    if(location.isHand()){ 
      x = location.getX();
      y = location.getY();
    }
    else if(location.isCounter()&&num==1){ // if counter has dish already, draw new dish at another spot
      x = 385;
      y = 125;
    }
    else{
      x = (int)verticies[location.getNum()].getX();
      y = (int)verticies[location.getNum()].getY();
    }
  }

  // getting dish values
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

  public Furniture getLocation(){
    return location;
  }

  public int getCost() {
    return cost;
  }

  public boolean isEmpty() {
    if (status == EMPTY) {
      return true;
    } else {
      return false;
    }
  }

   public void draw(Graphics g, Image plate, Image food, Image money) {
    g.drawImage(plate, x, y, null);
    if(status==EMPTY){
      g.drawImage(money, x+34, y+4, null); // dish also draws money 
    }
    else{
      g.drawImage(food, x+2, y-3, null);
    }
  }
}
