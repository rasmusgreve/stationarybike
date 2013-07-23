/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stationarybike;

import java.io.IOException;

/**
 *
 * @author Rasmus
 */
public class BasicTextOutput {
    public static void main(String[] args)
    {
        try {
            Bike bike = Bike.connect();
            begin(bike);
        } catch (Bike.NoBikeException ex) {
            System.out.println("Could not connect to bike");
            System.exit(1);
        }
    }
    
    public static void begin(final Bike bike)
    {
        System.out.println("Starting");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long time = -1;
                double speed = -1;
                while (true){
                    if (time != bike.getDuration() || speed != bike.getSpeed())
                    {
                        System.out.print(String.format("Speed %05.2f km/hr - ", bike.getSpeed()));
                        System.out.print(String.format("Distance %06.2f km - ", bike.getDistance()));
                        System.out.print(String.format("Duration %02d:%02d", bike.getDuration() / 60, bike.getDuration() % 60));
                        System.out.println();
                    }
                    time = bike.getDuration();
                    speed = bike.getSpeed();
                }
            }
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
        try {
            System.in.read();
            bike.disconnect();
        } catch (IOException ex) {
            System.exit(0);
        }
    }
}
