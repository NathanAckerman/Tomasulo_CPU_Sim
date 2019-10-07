public class FPMultUnit extends Unit {

    public FPMultUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.FPMULT);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPMULT);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_value = new Double(123123); // Would have to change this
    }
}