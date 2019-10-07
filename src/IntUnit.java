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
}