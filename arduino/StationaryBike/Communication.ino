/*
  Stationary bike hack sketch
  Created 19/07/2013
  Rasmus Greve
  
  Miscelaneous communication functions
  
  Output modes
    0 : CSV
    1 : Human readable
*/


#define Identity "StationaryBikeV1.0"

int outputMode = 0;
unsigned long lastStatTime;
bool autoStats = false;
bool verbose = false;


void handleCommunicationInput()
{
  if (Serial.available() > 0) {
    switch (Serial.read()) {
      case 'h':
      case 'H':
        outputMode = 1;
        if (verbose) Serial.println("Mode changed to human readable");
        break;
      case 'c':
      case 'C':
        outputMode = 0;
        if (verbose) Serial.println("Mode changed to CSV");
        break;
      case 's':
      case 'S':
        printCurrentMode();
        break;
      case 'r':
      case 'R':
        bike.reset();
        if (verbose) Serial.println("Stats reset");
        printCurrentMode();
        break;
      case 'a':
      case 'A':
        autoStats = true;
        if (verbose) Serial.println("Automatic stats enabled");
        break;
      case 'b':
      case 'B':
        autoStats = false;
        if (verbose) Serial.println("Automatic stats disabled");
        break;
      case 'i':
      case 'I':
        Serial.println(Identity);
        break;
      case 'v':
      case 'V':
        verbose = true;
        Serial.println("Verbose output enabled");
        break;
      case 'w':
      case 'W':
        verbose = false;
        Serial.println("Verbose output disabled");
        break;
      case '\n':
      case '\r':
        break; //IGNORE LINE BREAK CHARS
      default:
        printCommHelp();
    }
  }
}

void printCommHelp()
{
  Serial.println("Commands available:");
  Serial.println("\tA - Start automatic stats updates");
  Serial.println("\tB - Stop automatic stats updates");
  Serial.println("\tC - CSV stats mode (CSV;km/hr;seconds;km)");
  Serial.println("\tH - Human readable stats mode");
  
  Serial.print("\tI - Identify (answers \"");
  Serial.print(Identity);
  Serial.println("\")");
  
  Serial.println("\tR - Reset and stop");
  Serial.println("\tS - Get stats now (current mode)");
  Serial.println("\tV - Verbose output on (debug)");
  Serial.println("\tW - Verbose output off");
  Serial.println("\t? - Show this help");
}

void printCurrentMode()
{
    switch (outputMode)
    {
      case 0:
        printCSVStats();
        break;
      case 1:
        printHumanStats();
        break;
      default:
        break; //Do nothing
    }
}

void printCSVStats()
{
  Serial.print("CSV;");
  Serial.print(bike.getSpeed());
  Serial.print(";");
  Serial.print(bike.getDuration());
  Serial.print(";");
  Serial.println(bike.getDistance());
}

void printHumanStats()
{
  Serial.print(bike.getSpeed());
  Serial.print(" km/hr - ");
  Serial.print(bike.getDuration());
  Serial.print(" sec - ");
  Serial.print(bike.getDistance());
  Serial.println(" km");
}

void handleCommunicationOutput()
{
  if (autoStats && lastStatTime + StatsInterval < millis())
  {
    printCurrentMode();
    lastStatTime = millis();
  }
}

