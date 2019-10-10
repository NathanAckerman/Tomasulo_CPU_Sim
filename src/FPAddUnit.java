public class FPAddUnit extends Unit {

    public FPAddUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.FPADD, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;

        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPADD);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
