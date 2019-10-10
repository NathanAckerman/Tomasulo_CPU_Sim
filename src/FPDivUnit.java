public class FPDivUnit extends Unit {

    public FPDivUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.FPDIV, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;

        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPDIV);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
