/*
  Stationary bike hack sketch
  Created 19/07/2013
  Rasmus Greve
  
  Class representing a stationary bike making calculations of speed etc. by getting timely revolution inputs
*/
#include "CircularBuffer.h"
#include "TimeKeeper.h"

#ifndef Bike_h
#define Bike_h
class Bike
{
  public:
    Bike(int distPerRevolution, int speedBufferSize);
    void reset();
    void pause();
    void revolution();
    
    float getSpeed();
    float getDistance();
    unsigned long getDuration();
  private:
    const int DistPerRevolution;
    unsigned long revolutions;
    unsigned long lastTime;
    float revTimeMean;
    CircularBuffer speedBuffer;
    TimeKeeper timeKeeper;
};
#endif
