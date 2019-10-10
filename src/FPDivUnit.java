public class FPDivUnit extends Unit {

    public FPDivUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.FPDIV);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPDIV);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
