/*
  Stationary bike hack sketch
  Created 19/07/2013
  Rasmus Greve
  
  A circular buffer for holding the last n values
*/
#include "Arduino.h"

#ifndef CircularBuffer_h
#define CircularBuffer_h

class CircularBuffer{
  public:
    CircularBuffer(int capacity);
    ~CircularBuffer();
    void add(long value);
    void reset();
    float getMean();
  private:
    long* buffer;
    int n, p, i, counter;
    float mean;
};

#endif
