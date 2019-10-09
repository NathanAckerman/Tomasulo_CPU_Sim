public class LoadStoreUnit extends Unit {

    public LoadStoreUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.LOADSTORE, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.LOADSTORE);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_value = new Double(123123); // Would have to change this
        moveToWBOrBuffer(finishedInstruction);
    }
}