public class FPAddUnit extends Unit {

    public FPAddUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.FPADD, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;

        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPADD);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
        finishedInstruction.dest_reg_value = new Double(123123); // Would have to change this
        moveToWBOrBuffer(finishedInstruction);
    }
}