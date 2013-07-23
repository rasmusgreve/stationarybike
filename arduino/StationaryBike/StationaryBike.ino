/*
  Stationary bike hack sketch
  Created 19/07/2013
  Rasmus Greve
  
  The Kilberry PEC-3330 stationary bike from Harald Nyborg has a jack cable from the pedals to the bike computer. (Manual: http://www.harald-nyborg.dk/pdf/8612.pdf)
  As a test I examined what kind of signal it was using - the pedals act as a button being pressed once per revolution.
  Studies of the bike computer showed that a revolution on the pedals is considered moving 4 meters (25 required pr 100 meters.)
  There might be more to the distance and speed calculation, but for now this will do.
  
*/
#include "Bike.h"

//Behaviour settings
#define NoiseFilter 5      //min # of consecutive high or low ticks required to signify a high or a low.
#define StatsInterval 1000 //ms - time between sending stats to computer
#define PauseDelay 3000    //ms - time without revolutions before pausing the bike (allows for speeds down to 5km/hr)
#define DistPerRev 4       //meters - distance per revolution
#define SpeedBufferSize 3  //How many of the last speed measurements should be used to calculate the current speed (mean)
//Wiring settings
#define BikeInputPint 2
#define RevolutionOutputPin 13

//end of settings

int buttonState = 0;
int highRemaining = NoiseFilter;
int lowRemaining = NoiseFilter;
bool wasHigh = false;
bool conseqLow = false;

unsigned long lastRevTime;
Bike bike(DistPerRev, SpeedBufferSize);

void setup() {
  Serial.begin(9600);
  pinMode(RevolutionOutputPin, OUTPUT);      
  pinMode(BikeInputPint, INPUT); 
}

void loop(){
  buttonState = digitalRead(BikeInputPint);
  digitalWrite(RevolutionOutputPin, buttonState);
  handleRevolution();
  handlePause();
  handleCommunicationOutput();
  handleCommunicationInput();
}

/*
  Require at least NoiseFilter consecutive high readings followed by at least NoiseFilter consecutive low readings before triggering a revolution
*/
void handleRevolution()
{
  if (buttonState == HIGH) {     
    if (highRemaining > 0) highRemaining--;
    if (highRemaining == 0) wasHigh = true;
    conseqLow = false;
    lowRemaining = NoiseFilter;
  } 
  else {
    highRemaining = NoiseFilter;
    if (wasHigh) conseqLow = true;
    wasHigh = false;
    if (lowRemaining > 0) lowRemaining--;
    if (lowRemaining == 0 && conseqLow){
      wasHigh = false;
      conseqLow = false;
      lowRemaining = NoiseFilter;
      bike.revolution();
      lastRevTime = millis();
    }
  }
}

void handlePause()
{
  if (lastRevTime + PauseDelay < millis())
  {
    lastRevTime = millis();
    bike.pause();
  }
}
