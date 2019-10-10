public class LoadStoreUnit extends Unit {

    public LoadStoreUnit(int numReservStation, int latency, WB wb) {
        super(numReservStation, latency, UnitName.LOADSTORE, wb);
    }

    @Override
    public void doCycle() {
        if(shouldStall()) return;
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.LOADSTORE);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
