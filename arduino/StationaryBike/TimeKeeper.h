/*
  Stationary bike hack sketch
  Created 20/07/2013
  Rasmus Greve
  
  A timekeeper for keeping track of passed time
*/
#include "Arduino.h"

#ifndef TimeKeeper_h
#define TimeKeeper_h

class TimeKeeper{
  public:
    void start();
    void pause();
    void reset();
    long getMillis();
  private:
    bool running;
    long startTime, previous;
};

#endif
