/*
  Stationary bike hack sketch
  Created 19/07/2013
  Rasmus Greve
  
  Class representing a stationary bike making calculations of speed etc. by getting timely revolution inputs
*/
#include "Arduino.h"
#include "Bike.h"

Bike::Bike(int distPerRevolution, int speedBufferSize) : DistPerRevolution(distPerRevolution), speedBuffer(speedBufferSize) {
  revolutions = 0;
}

/*
  Reset the bike (distance, speed, duration)
*/
void Bike::reset(){ 
  revolutions = 0;      //Distance
  speedBuffer.reset();  //Speed
  timeKeeper.reset();   //Time
}

//Mark that the pedals have spun one revolution
void Bike::revolution(){
  if (lastTime == 0) lastTime = millis(); //Initialize
  timeKeeper.start();                     //Time
  speedBuffer.add(millis()-lastTime);     //Speed
  lastTime = millis();                    //Speed
  revolutions++;                          //Distance
}

//Pause the bike (timekeeping and speed)
void Bike::pause(){
  timeKeeper.pause();
  speedBuffer.reset();
}

//Get current speed in km/hr
float Bike::getSpeed(){
  revTimeMean = speedBuffer.getMean();
  if (revTimeMean == 0) return 0;
  return (DistPerRevolution * 3600) / revTimeMean;
}

//Get current distance in km
float Bike::getDistance(){
  return (revolutions * DistPerRevolution) / 1000.0;
}

//Get current duration in seconds
unsigned long Bike::getDuration(){
  return timeKeeper.getMillis()/1000;
}

