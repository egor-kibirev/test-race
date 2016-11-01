/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package race;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Race {
    
    private Float raceDistance;
    private List<Vehicle> vehiclesList;
    
    private void loadConfig(String filepath){
        
        File config = new File(filepath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc=null;
        try {
            DocumentBuilder builder=factory.newDocumentBuilder();
             doc = builder.parse(config);
        }
        catch (Exception e){
            System.err.println("Config parsing error! Check config file! ");
            System.exit(-1);
            e.printStackTrace();
        }
        //Получаем дистанцию гонки
        try{
            raceDistance = Float.valueOf(((Element)doc.getElementsByTagName("race").item(0)).getAttribute("distance"));
        }
        catch(Exception e) {System.err.println("Distance of race has not loaded! check config file!");System.exit(-1);}
        //Генерируем список транспортных средств    
        NodeList vehicleNodeList = doc.getElementsByTagName("vehicle");
        for (int i = 0; i < vehicleNodeList.getLength(); i++) {
            Element vData = (Element)vehicleNodeList.item(i);
            Vehicle newVehicle = VehicleFactory.getVehicle(vData);
            if(newVehicle!=null) vehiclesList.add(newVehicle);
        }
    }
    private boolean checkRepeat(){
        while(true){
            System.out.println("\nDo you want to race again? y/n");
            Scanner in = new Scanner(System.in);
            String s = in.nextLine().trim();

            if(s.equals("y"))     return true;
            if(s.equals("n"))     return false;
            else System.out.println("You should input correct command 'y' or 'n'");
        }
    } 
    public void start() throws IOException{
       do{
            System.out.println("<--------------Race started--------------->");
            int finishedVehicles=0;            
            for(Vehicle v:vehiclesList) v.setOdometerInfoToZero();
            while(finishedVehicles<vehiclesList.size()){
                for(Vehicle vehicle:vehiclesList){
                    if(vehicle.getOdometerInfo()>=raceDistance*1000) continue;
                    vehicle.ride();
                    if(vehicle.getOdometerInfo()>(raceDistance*1000))
                        finishedVehicles++; 
                }
            }
            System.out.println("\n\n<--------------Lap result--------------->");
            printLapProtocol(vehiclesList);
       } while(checkRepeat());
       System.out.println("Goodbye!");
    }
    public void printLapProtocol(List<Vehicle> vehiclesList){
        //TreeMap – общий случай, компаратор указывается вручную
        Set<Vehicle> sortedMap = new TreeSet<Vehicle>(new Comparator<Vehicle>() {
        public int compare(Vehicle o1, Vehicle o2) {
                return o1.getLapTime().compareTo(o2.getLapTime());
        }
});

        for(Vehicle v:vehiclesList) {
            sortedMap.add(v);
        }
                
        System.out.println(sortedMap);
    };
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            String filepath = args[0];
            Race race = new Race(filepath);
            race.start();
        } catch (IOException ex) {
            Logger.getLogger(Race.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Error! You should write full path to config file!");
        }
    }
    
    Race(String configFilepath){
        raceDistance = 0.0f;
        vehiclesList = new LinkedList<Vehicle>();
        loadConfig(configFilepath);
    }
}
