public class BranchUnit extends Unit {

    public BranchUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.BU);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.BU);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_Value = new Double(123123); // Would have to change this
    }
}