public class MultUnit extends Unit {

    public MultUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.MULT);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.MULT);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_Value = new Double(123123); // Would have to change this
    }
}