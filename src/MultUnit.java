public class MultUnit extends Unit {

    public MultUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.MULT, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;

        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.MULT);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_value = new Double(123123); // Would have to change this
        moveToWBOrBuffer(finishedInstruction);
    }
}