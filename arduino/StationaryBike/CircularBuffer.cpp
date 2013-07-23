/*
  Stationary bike hack sketch
  Created 19/07/2013
  Rasmus Greve
  
  A circular buffer for holding the last n values
*/
#include "Arduino.h"
#include "CircularBuffer.h"
//Create a new circular buffer with a capacity of cap
CircularBuffer::CircularBuffer(int cap){
  buffer = (long*)malloc(cap*sizeof(long));
  n = cap;
  p = 0;
  reset();
}
//Add a new item to the buffer
void CircularBuffer::add(long value){
  buffer[p] = value;
  p = (p + 1) % n;
}
//Remove all items from the buffer
void CircularBuffer::reset(){
  for (i = 0; i < n; i++)
    buffer[i] = 0;
}
//Get the mean of all the values in the buffer
float CircularBuffer::getMean(){
  counter = 0;
  mean = 0;
  for (i = 0; i < n; i++){
    if (buffer[i] == 0) continue;
    mean += buffer[i];
    counter++;
  }
  if (counter == 0) return 0;
  return mean/counter;
}
//Destructor for the circular buffer freeing the buffer array
CircularBuffer::~CircularBuffer(){
  free(buffer);
}

