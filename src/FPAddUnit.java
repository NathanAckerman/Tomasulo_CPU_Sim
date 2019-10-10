public class FPAddUnit extends Unit {

    public FPAddUnit(int numReservStation, int latency) {
        super(numReservStation, latency, UnitName.FPADD);
    }

    @Override
    public void doCycle() {
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(UnitName.FPADD);
        Instruction finishedInstruction = shiftPipelineRight(readyInstruction);
	if (finishedInstruction != null) {
		InstructionEvaluator.eval(finishedInstruction);
	}
    }
}
