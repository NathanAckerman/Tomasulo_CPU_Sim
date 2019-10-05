import java.util.ArrayList;

public class Station {

    public String stationName;

    public Double sourceRegister1Value;
    public Double sourceRegister2Value;

    // Do we need to keep track of these?
    public String sourceRegister1Name;
    public String sourceRegister2Name;

    public String sourceRegister1Renamed;
    public String sourceRegister2Renamed;

    public Instruction instruction;

    // The destination register renamed is the same as the stationName

    public Station(
        String stationName, 
        Double sourceRegister1Value,
        Double sourceRegister2Value, 
        String sourceRegister1Renamed, 
        String sourceRegister2Renamed,
        Instruction instruction) {

            this.stationName = stationName;
            this.sourceRegister1Value = sourceRegister1Value;
            this.sourceRegister2Value = sourceRegister2Value;
            this.sourceRegister1Renamed = sourceRegister1Renamed;
            this.sourceRegister2Renamed = sourceRegister2Renamed;
            this.instruction = instruction;

    }

    public boolean isWaitingOnValue(){
        return (sourceRegister1Value == null || sourceRegister2Value == null);
    }

    // Should we have a method that returns what it is waiting on? If so, will returning a List Type be an issue? 
    // I'm thinking that if it is waiting on one item, we will return a list of one item containing the sourceRegisterRenamed value.
    // If it is waiting on two items, then the list will be of size two containing both source registers renamed. 

    public ArrayList<String> getRegistersWaitedOn(){

        ArrayList<String> waitList = new ArrayList<String>();

        if (sourceRegister1Value == null) waitList.add(sourceRegister1Renamed);
        if (sourceRegister2Value == null) waitList.add(sourceRegister2Renamed);

        return waitList;
    }



}