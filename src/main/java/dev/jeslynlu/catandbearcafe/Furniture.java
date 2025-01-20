package dev.jeslynlu.catandbearcafe;

// Furniture.java
// Brittany Vuong and Jeslyn Lu
// Furnitures are the main areas of the game floor plus the WaiterHands, the Waiter's dish carrying area. Customers and dishes can be moved to and from all these areas. 
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Furniture {
  private int x, y, wid, hgt, type;
  private int status; // whether empty or occupied
  public static final int EMPTY = 0, OCCUPIED = 1; //occupied meaning there's either a person or dirty plate there
  public static final int COUNTER = 3, DOOR = 4, WAITER_HANDS = 5; 
  private ArrayList<Customer> people; // people occupying the area (used for tables and door)
  private ArrayList<Dish> dishes; // food and dirty plates on the area (used for tables and counter)

  // setting furniture values
  public Furniture(int xX, int yY, int width, int height, int kind) {
    x = xX;
    y = yY;
    wid = width;
    hgt = height;
    type = kind;
    status = EMPTY;
    people = new ArrayList<Customer>();
    dishes = new ArrayList<Dish>();
  }

  public void updateStatus() { // updated if areas are either empty or occupied
    if(people.size()==0 && dishes.size()==0){
      status=EMPTY;
    }
    else{
      status=OCCUPIED;
    }
  }

  public void addPerson(Customer person) { // add a customer to a table or door queue
    people.add(person);
    updateStatus();
  }

  public void removePerson(Customer person) { // remove a customer from table or door queue
    if(isTable()){
      for(int i=0; i<dishes.size(); i++){
        dishes.get(0).eat();
      }
    }
    for (int i = 0; i < people.size(); i++) {
      if (people.get(i).equals(person)) {
        people.remove(i);
      }
    }
    updateStatus();
  }
  public void clean(){ // removing dish from table
    dishes.remove(0);
    updateStatus(); // table is now empty
  }

  public void addDish(Dish newDish){ // adding dish to table
    dishes.add(newDish);
    updateStatus();
  }
  
  public void removeDish(){ // removing dish from table
    dishes.remove(0);
    updateStatus(); // table is now empty
  }

  public Dish getDish(){ // getting first dish in queue
    return dishes.get(0);
  }

  public int getQueueNum(Customer person){ // used for door. getting a customers number in the door queue
    for (int i = 0; i < people.size(); i++) {
      if (people.get(i).equals(person)) {
        return i;
      }
    }
    return -1;
  }

  public void updateHands(Waiter player){ // ensuring that waiter's carrying area is above their head
    if(type==WAITER_HANDS){
      setX(player.getX());
      setY(player.getY() - 35);
    }
  }

  public void setX(int newX){x = newX;}
  public void setY(int newY){y = newY;}
  // getting furniture values
  public Customer getCustomer(){ // getting person sitting at table
    return people.get(0);
  }
  public Customer getFront() { // getting person at front of the door queue
    return getCustomer();
  }
  public int getDishesLen(){
    return dishes.size();
  }
  public int getPeopleLen(){
    return people.size();
  }

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

  public int getStatus() {
    return status;
  }

  public int getNum() {
    return type;
  }

  public ArrayList<Customer> getPeople(){
    return people;
  }

  public boolean isEmpty() {
    if (status == EMPTY) {
      return true;
    } else {
      return false;
    }
  }
  public boolean isOccupied() {
    if (status == OCCUPIED) {
      return true;
    } else {
      return false;
    }
  }
  public boolean isCounter() {
    if (type == COUNTER) {
      return true;
    } else {
      return false;
    }
  }
  public boolean isDoor() {
    if (type == DOOR) {
      return true;
    } else {
      return false;
    }
  }
  public boolean isTable() {
    if (type>=0 || type <= 2) {
      return true;
    } else {
      return false;
    }
  }
  public boolean isHand() {
    if (type == WAITER_HANDS) {
      return true;
    } else {
      return false;
    }
  }

  public void draw(Graphics g, Image pic) {
    if(type==DOOR){
      g.drawImage(pic, x, 369, null);
    }
    else{
      g.drawImage(pic, x, y, null);
    }
  }
}