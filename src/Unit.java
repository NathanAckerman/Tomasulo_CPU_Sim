public class Unit {

    public Instruction[] pipeline;
    private final UnitName unitName;
    private final int latency;

    public Unit(int latency, int numReservStation, UnitName unitName) 
    {
        ReservationStationStatusTable.createStations(unitName, numReservStation);
        this.pipeline = new Instruction[latency];
        this.unitName = unitName;
	this.latency = latency;
    }

	/*
	 * \brief Performing one cycle of pipeline execution
	 * \param[in] instruction Instruction to be added to the pipeline for execution
	 *
     * If the last cell of the pipeline is not empty, the pipeline will stall
     * It is assumed that the CDB or the WB will pick up the instruction in the last cell
     * of the pipeline and set that cell to null
     * 
     * \return true on success and fall otherwise (stalling)
    */
    public boolean shiftPipelineRight(Instruction instruction) 
    {
        if(this.pipeline[this.pipeline.length - 1] != null) {
            return false; // stalling
        }

        for(int i = pipeline.length - 1; i > 0; i--){
            this.pipeline[i] = this.pipeline[i-1];
        }

        this.pipeline[0] = instruction;

        return true;
    }

    public void doCycle(){
        Instruction readyInstruction = ReservationStationStatusTable.getNextReadyInstruction(this.unitName);

        boolean succeeded = shiftPipelineRight(readyInstruction);
        if(!succeeded) {
            System.out.println(this.unitName + " is stalling");
            return;
        }

        Instruction finishedInstruction = this.pipeline[this.pipeline.length - 1];
        if (finishedInstruction != null) InstructionEvaluator.eval(finishedInstruction);
        
    }

    // return true if instruction killed
    public boolean killInstr(Instruction instr) {
	for(int i = 0; i < latency; i++) {
	    if (pipeline[i] == instr) {
		pipeline[i] = null;
		return true;
	    }
	}
	return ReservationStationStatusTable.killInResStation(instr);
    }


}
