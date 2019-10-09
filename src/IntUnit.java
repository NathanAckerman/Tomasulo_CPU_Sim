public class IntUnit extends Unit {

    public IntUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.INT, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;

        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.INT);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_value = new Double(123123); // Would have to change this
        moveToWBOrBuffer(finishedInstruction);
    }
}