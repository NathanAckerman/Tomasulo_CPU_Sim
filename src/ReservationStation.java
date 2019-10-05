import java.util.ArrayList;

public class ReservationStation {
    private final int STATION_SIZE;

    private ArrayList<Station> stations = new ArrayList<Station>();

    public ReservationStation(final int stationSize) {
        this.STATION_SIZE = stationSize;
    }

    // Adding Instruction To Station
    // Returns true if instruction has been added successfully
    // Returns false otherwise
    public boolean addInstructionToStation(Instruction i) {
        if (stations.size() == STATION_SIZE){
            return false;
        }

        // Do some renaming here for source registers in order to store it into Station object
        return true;
    }

    // If there is a station that is ready (meaning an instruction that has all of its values), return that instruction
    // so that we can send it through the Unit
    public Instruction getNextReadyInstruction() { 
        for(Station s : stations){
            if(!s.isWaitingOnValue()) return s.instruction;
        }
        return null;
    }

    public void updateStations() {
        // TO DO: Whenever a value is obtained from the CDB, we can update our stations
    }


}