package dev.jeslynlu.catandbearcafe;

// // Sound.java
// // Brittany Vuong and Jeslyn Lu
// // Code from Mr. McKenzie
// // Sounds stores, stops, and plays sounds

// import java.io.*;
// import javax.sound.sampled.*;

// public class Sound{
//   private Clip clip;
//   private AudioInputStream sound;
//   File file;
  
//   // setting sound file
//   public Sound(String filename){
//     setClip(filename);
//   }
  
//   // sets clip
//   public void setClip(String filename){
//     try{
//       File f = new File(filename);
//       clip = AudioSystem.getClip();
//       clip.open(AudioSystem.getAudioInputStream(f));
//     } catch(Exception e){
//       System.out.println("error"); 
//     }
//   }
  
//   // starts sound
//   public void play(){
//     clip.setFramePosition(0);
//     clip.start();
//   }
//   // stop sound
//   public void stop(){
//     clip.stop();
//   }
// }