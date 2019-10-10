public class IntUnit extends Unit {

    public IntUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.INT, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;

        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.INT);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
