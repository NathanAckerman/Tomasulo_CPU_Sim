import java.util.ArrayList;
import java.util.HashMap;

public final class ReservationStationStatusTable {

    private static HashMap<UnitName, ArrayList<Instruction>> stationMap = new HashMap<>();
    private static HashMap<UnitName, Integer> stationSizeMap = new HashMap<>();

    // No one can instantiate this class
    private ReservationStationStatusTable(){ }


    public static boolean createStations(UnitName unitName, int size) {
        if(stationSizeMap.containsKey(unitName) || stationMap.containsKey(unitName)) return false;

        stationSizeMap.put(unitName, new Integer(size));
        stationMap.put(unitName, new ArrayList<Instruction>());
        
        return true;
    }

    public static boolean addInstructionToStation(UnitName unitName, Instruction i) {

        if(!stationMap.containsKey(unitName)) return false;

        if(stationMap.get(unitName).size() == stationSizeMap.get(unitName).intValue()) return false;
        

        // Do some renaming here for source registers in order to store it into Station object
        return true;
    }

    // If there is a station that is ready (meaning an instruction that has all of its values), return that instruction
    // so that we can send it through the Unit
    public static Instruction getNextReadyInstruction(UnitName unitName) { 
        ArrayList<Instruction> stations = stationMap.get(unitName);

        for (Instruction i : stations)
            if (!i.isWaitingOnValue())
                return i;

        return null;
    }

    public static void updateStations() {
        // TO DO: Whenever a value is obtained from the CDB, we can update our stations
    }

    public static boolean isReservationStationFull(UnitName unitName) {
        return stationMap.get(unitName).size() == stationSizeMap.get(unitName).intValue();
    }
    
}
