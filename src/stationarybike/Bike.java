package stationarybike;

import gnu.io.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author Rasmus
 */
public class Bike {
    public static class NoBikeException extends Exception { 
        public NoBikeException() {}
        public NoBikeException(String msg) {super(msg);}
    }
    private double speed, distance; //km/hr , km
    private long duration; //seconds
    private OutputStream outStream;
    private BufferedReader inReader;
    private SerialPort port;
    
    private Bike() {
        
    }
    /**
     * Create a new Bike connected via serial to the Arduino by trying all possible ports
     */
    public static Bike connect() throws NoBikeException
    {
        try {
            System.out.println("Starting port scan");
            Bike b = new Bike();
            b.port = b.tryAllPorts();
            b.setupAutoUpdate();
            return b;
        } catch (Exception ex) {
            throw new NoBikeException("Bike could not be found on any port");
        }
    }
    /**
     * Create a new Bike connected via serial to the Arduino on the specified port
     */
    public static Bike connect(String portName) throws NoBikeException
    {
        try {
            Bike b = new Bike();
            b.port = b.tryPort(portName);
            b.setupAutoUpdate();
            return b;
        } catch (Exception ex) {
            throw new NoBikeException("Bike could not be found on the specified port");
        }
    }
    
    private void setupAutoUpdate()
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
        System.out.println("Got response: " + response);
        if ("StationaryBikeV1.0".equals(response))
        {
            outStream.write("W".getBytes()); //Verbose off
            outStream.write("C".getBytes()); //CSV mode
            outStream.write("A".getBytes()); //Start auto stats
            outStream.flush();
            return port;
        }
        
        throw new IOException("Unsuccessful connection");
    }
    
    private SerialPort tryAllPorts() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException
    {
        for (String portName : getPortNames())
        {
            System.out.println("Trying portname " + portName);
            SerialPort port = tryPort(portName);
                if (port != null) return port;
            System.out.println("unsuccessful");
        }
        System.out.println("Tried all ports - no luck");
        throw new IOException("Unsuccessful connection");
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
    
    public void disconnect()
    {
        port.close();
    }
    
    
    /**
     * Estimate calories burned
     * Calculation based on post from (http://forums.roadbikereview.com/racing-training-nutrition-triathlons/calories-burned-per-mile-formula-question-28863.html)
     * @param weight Riders weight in kg
     * @return An estimate of burned calories
     */
    public double getCalorieEstimate(double weight)
    {
        double avgSpeed = getDistance() / (getDuration()*3600); //in km/hr
        int MET = 2 + (int)(avgSpeed/3); //Approximation of MET table
        double energyExpenditure = 0.0175 * MET * weight; //Calories per minute
        return energyExpenditure * (getDuration() / 60);
    }
    
    
    /**
     * Reset speed, duration and distance on the Arduino
     * @return True if successful, false otherwise
     */
    public boolean reset()
    {
        try {
            outStream.write("R".getBytes());
            outStream.flush();
        } catch (IOException ex) {
            return false;
        }
        return true;
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
