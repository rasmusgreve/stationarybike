/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stationarybike;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rasmus
 */
public class Bike {
    private class NoBikeException extends Exception { 
        public NoBikeException() {}
        public NoBikeException(String msg) {super(msg);}
    }
    private double speed, distance; //km/hr , km
    private long duration; //seconds
    private OutputStream outStream;
    private BufferedReader inReader;
    
    /**
     * Create a new Bike connected via serial to the Arduino by trying all possible ports
     * Throws exception 
     */
    public Bike() throws NoBikeException
    {
        try {
            SerialPort port = tryAllPorts();
            setupAutoUpdater(port);
        } catch (Exception ex) {
            throw new NoBikeException("Bike could not be found on any port");
        }
        
    }
    
    public Bike(String portName) throws NoBikeException
    {
        try {
            SerialPort port = tryPort(portName);
            setupAutoUpdater(port);
        } catch (Exception ex) {
            throw new NoBikeException("Bike could not be found on the specified port");
        }
        
    }
    
    private void setupAutoUpdater(final SerialPort port)
    {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try {
                        String response = inReader.readLine();
                        if (response.startsWith("CSV;"))
                            updateFromCSV(response);
                        break;
                    }
                    catch (IOException ex)
                    {
                        continue; //Nothing to read at this time
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }
    
    private static ArrayList<String> getPortNames()
    {
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portNames = new ArrayList<String>();
        while(ports.hasMoreElements()) {
            portNames.add(((CommPortIdentifier)ports.nextElement()).getName());
        }     
        return portNames;
    }
    
    private SerialPort tryPort(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        SerialPort port = (SerialPort) portIdentifier.open("StationaryBike", 1000);
        port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        outStream = port.getOutputStream();
        inReader = new BufferedReader(new InputStreamReader(port.getInputStream()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            //Do nothing
        }
        String response;
        long endTime = System.currentTimeMillis() + 5000;
        while (true)
        {
            if (System.currentTimeMillis() > endTime)
                return null;
            try {
                outStream.write("I".getBytes());
                outStream.flush();
                response = inReader.readLine();
                break;
            }
            catch (IOException ex)
            {
                continue; //Nothing to read at this time
            }
        }
        
        if ("StationaryBikeV1.0".equals(response))
        {
            outStream.write("W".getBytes()); //Verbose off
            outStream.write("C".getBytes()); //CSV mode
            outStream.write("A".getBytes()); //Start auto stats
            outStream.flush();
            return port;
        }
        
        return null;
    }
    
    private SerialPort tryAllPorts() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException
    {
        for (String portName : getPortNames())
        {
            System.out.println("Trying portname " + portName);
            SerialPort port = tryPort(portName);
                if (port != null) return port;
        }
        System.out.println("Tried all ports - no luck");
        return null;
    }
    
    private void updateFromCSV(String data)
    {
        if (!data.startsWith("CSV;")) throw new IllegalArgumentException("Unexpected data (Expects \"CSV;double;long;double\")");
        String[] parts = data.split(";");
        if (parts.length < 4) throw new IllegalArgumentException("Unexpected data (Expects \"CSV;double;long;double\")");
        
        speed = Double.parseDouble(parts[1]);
        duration = Long.parseLong(parts[2]);
        distance = Double.parseDouble(parts[3]);
    }
    
    /**
     * @return The current speed in km/hr
     */
    public double getSpeed() {return speed;}
    
    /**
     * @return The current distance in km
     */
    public double getDistance() {return distance;}
    
    /**
     * @return The current duration in seconds
     */
    public long getDuration() {return duration;}
    
    /**
     * @return The current velocity in minutes/km
     */
    public double getVelocity(){return 60/speed;}
    
}
