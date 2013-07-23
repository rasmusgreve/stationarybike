/*
  Stationary bike hack sketch
  Created 20/07/2013
  Rasmus Greve
  
  A timekeeper for keeping track of passed time
*/
#include "Arduino.h"
#include "TimeKeeper.h"

void TimeKeeper::start(){
  if (running) return;
  startTime = millis();
  running = true;
}

void TimeKeeper::pause(){
  if (!running) return;
  previous += millis() - startTime;
  running = false;
}

void TimeKeeper::reset(){
  previous = 0;
  running = false;
}

long TimeKeeper::getMillis(){
  if (running)
    return (millis() - startTime) + previous;
  else
    return previous;
}
