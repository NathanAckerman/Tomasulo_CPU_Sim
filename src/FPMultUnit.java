public class FPMultUnit extends Unit {

    public FPMultUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.FPMULT, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;

        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPMULT);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
