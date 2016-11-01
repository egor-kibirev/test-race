/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package race;

import java.util.Random;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *
 * @author Egor
 */


abstract class Vehicle{ // абстрактный класс транспортного средства, имеет набор внутренних классов для реализации различных состояний
    /*возможно стоило сделать классы состояний отдельными, но тогда для работы пришлось бы им передавать ссылку на объект ТС и для соблюдения 
    инкапсуляции писать методы для работы с внутренними данными, поэтому решил использовать внутренние*/
    
    class VehicleStateOnStartGrid extends VehicleState{
        @Override
        public void ride(){
            System.out.println(Vehicle.this);
            currentspeed = currentspeed + maxspeed/10;
            odometerinfo = odometerinfo + ((currentspeed*1000)/3600) ;
            lapTime++;
            changeState(new VehicleStateRiding());
        };
    }
    class VehicleStateRiding extends VehicleState{
        @Override
        public void ride(){
            if(currentspeed<maxspeed){currentspeed = currentspeed + maxspeed/10; }
            if(currentspeed>maxspeed){currentspeed = maxspeed;}
            odometerinfo = odometerinfo + ((currentspeed*1000)/3600) ;   // одометр будет в метрах
            if(lapTime%transmitfrequensy==0) transmitTelemetry("");
            lapTime++;
            checkPenetration();
        };
    }
    class VehicleStateRepairing extends VehicleState{
        VehicleStateRepairing(){transmitTelemetry("have penetration!"); }
        public void ride() {
            if(currentspeed>0) {currentspeed = currentspeed-maxspeed/15;lapTime++;return;}  //гасим скорость
            if(currentspeed<=0) currentspeed=0f;    //если остановились можно приступать к ремонту
            repairtime--;
            lapTime++;
            
            if(repairtime<=0){
                //transmitTelemetry("Repair complete!");
                repairtime=VEHICLE_REPAIRTIME;
                vehicleState = new VehicleStateRiding();
            }
            
            
        }
    }
    class VehicleState{
        public void ride(){}
    }
    
    private VehicleState vehicleState;    //объект отвечающий за поведение в различных состояниях транспортного средства
    private String type;                  //тип ТС
    private String name;                  // имя или модель ТС
    private Float maxspeed;               //максимальная скорость / км/ч
    private Float penetrationchance;      //шанс прокола        / нецелое число от нуля до 1
    private Float currentspeed;           //текущаяя скорость   / км/ч
    private Float odometerinfo;           //текущее пройденое расстояние / метры
    private Integer lapTime;              //время затраченное на прохождение круга / секунды
    private Integer repairtime;           //счетчик время ремонта прокола колеса / секунды
    private final Integer VEHICLE_REPAIRTIME = 600; //время ремонта прокола колеса / секунды
    private final Integer transmitfrequensy = 10; //частота передачи данных в консоль во время гонки / секунды
    
    private void changeState(VehicleState vehicleState){this.vehicleState = vehicleState;}
    @Override
    public String toString(){
        return "\n\ntype: "+getType()+" name: "+getName()+"\n | maxspeed: "+getMaxSpeed()+" km/h  "+" | penetration chance: "+getPenetrationChance()
        +" | odometer "+odometerinfo +" meters" + " | laptime "+lapTime +" seconds";
    }
    
    public void initialize(String type,String name,Float maxspeed,Float penetrationChance) {
        this.type = type;
        this.name = name;
        this.maxspeed = maxspeed;
        this.penetrationchance = penetrationChance;  
        this.currentspeed = 0f;
        this.odometerinfo = 0f;
        this.lapTime = 0;
        vehicleState = new VehicleStateOnStartGrid();
        repairtime = VEHICLE_REPAIRTIME;
    }
    public void ride(){
        vehicleState.ride();
    }
    public void checkPenetration(){
        Random rand;
        rand = new Random(lapTime);
        float pen = new Random().nextFloat();
        if(pen<penetrationchance){
            vehicleState = new VehicleStateRepairing();
        }
        
    }
    public void transmitTelemetry(String message){System.out.println(" | odometer:"+odometerinfo +" | "+ name + " | "+message);}
    
    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public Float getMaxSpeed() {
        return maxspeed;
    }
    public Float getPenetrationChance() {
        return penetrationchance;
    }
    public Float getCurrentSpeed() {
        return currentspeed;
    }
    public Float getOdometerInfo() {
        return odometerinfo;
    }
    public Integer getLapTime(){return lapTime;}
    public void setOdometerInfoToZero() {odometerinfo=0f;}
}

public class VehicleFactory { // фабрика объектов транспортных средств на базе анонимных классов
    static String getAttribute(Element vData,String name) throws Exception {
        String s = vData.getAttribute(name);
        if(s.length()!=0) return s;
        else {System.err.println(name);throw new Exception();}
    }
    public static Vehicle getVehicle(Element vData){
                  
                try{
                    final String type = getAttribute(vData,"type");
                    final Float maxspeed = Float.valueOf(getAttribute(vData,"maxspeed"));
                    final Float penetrationChance = Float.valueOf(getAttribute(vData,"penchance"));;
                    final String name = ((Node)vData).getChildNodes().item(0).getTextContent();
                    
                    switch(type){
                        case "car": 
                            final Integer mPassengers = Integer.valueOf(getAttribute(vData,"passengers"));
                            return new Vehicle(){
                                                    private Integer passengers;
                                                    {
                                                        super.initialize(type,name,maxspeed,penetrationChance);
                                                        this.passengers = mPassengers;
                                                    }
                                                    @Override
                                                    public String toString(){
                                                        return super.toString() + " | number of passengers: "+this.passengers;
                                                    }
                                                };
                        case "motorcycle":
                            final Boolean mSidecar = Boolean.valueOf(getAttribute(vData,"sidecar"));
                            return new Vehicle(){
                                                     private Boolean sidecar;
                                                    
                                                    {
                                                        super.initialize(type,name,maxspeed,penetrationChance);
                                                        this.sidecar = mSidecar;
                                                    }
                                                    @Override
                                                    public String toString(){
                                                        return super.toString()+ " | use sidecar: "+sidecar ;
                                                    }
                                                };
                        case "truck": 
                            final Float mCargoweight = Float.valueOf(getAttribute(vData,"cargoweight"));;
                            return new Vehicle(){
                                                    private Float cargoweight;
                                                    {
                                                        super.initialize(type,name,maxspeed,penetrationChance);
                                                        this.cargoweight = mCargoweight;
                                                    }
                                                    @Override
                                                    public String toString(){
                                                        return super.toString()+" | cargoweight: "+cargoweight + " kg";

                                                    }
                                                };
                    }
                }catch(Exception e){e.printStackTrace(); System.err.println("Vehicle has not been loaded! check config file!");}
                         
                return null;
            }
      
    public static void main(String... args){};
}
