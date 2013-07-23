Stationary bike Java interface
==============

Java interfacing of Kilberry stationary bike using an Arduino.

The Kilberry PEC-3330 stationary bike from Harald Nyborg has a jack cable from the pedals to the bike computer. (http://www.harald-nyborg.dk/pdf/8612.pdf)

As a test I examined what kind of signal it was using; the pedals act as a button being pressed once per revolution - piece of cake to interface.T

Studies of the bike computer showed that a revolution on the pedals is considered moving 4 meters (25 required pr 100 meters.)
There might be more to the distance and speed calculation, but for now this will do.
