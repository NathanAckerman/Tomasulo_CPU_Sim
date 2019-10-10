public class LoadStoreUnit extends Unit {

    public LoadStoreUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.LOADSTORE);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.LOADSTORE);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
