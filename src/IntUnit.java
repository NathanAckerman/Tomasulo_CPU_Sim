<<<<<<< HEAD
public class IntUnit extends Unit {

    public IntUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.INT);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.INT);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_Value = new Double(123123); // Would have to change this
    }
=======

public class IntUnit implements Unit {
    
    public IntUnit(int numReservStation) {
        this.rs = new ReservationStation[numReservStation];
    }

    // What is addInstruction for? Putting instructions into the reservation Station? 
    public void addInstruction(Instruction i) {
        // TODO Auto-generated method stub

    }

    public boolean isReservationFull() {
        // TODO Auto-generated method stub
        return false;
    }

    public void doCycle() {
        // TODO Auto-generated method stub

    }

>>>>>>> 0ebb78b... WIP: Created Unit Interface, ReservationStation class, Station class for Reservation, and started on IntUnit
}