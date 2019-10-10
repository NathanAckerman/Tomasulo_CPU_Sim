public class FPMultUnit extends Unit {

    public FPMultUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.FPMULT);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPMULT);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
