public class FPDivUnit extends Unit {

    public FPDivUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.FPDIV);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPDIV);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_value = new Double(123123); // Would have to change this
    }
}