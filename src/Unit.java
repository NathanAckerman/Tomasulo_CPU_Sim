public abstract class Unit {

    private final int latency;
    private Instruction[] pipeline;

    public Unit(int numReservStation, final int latency, UnitName unitName) {
        ReservationStationStatusTable.createStations(unitName, numReservStation);
        this.latency = latency;
        this.pipeline = new Instruction[latency];
    }

    public void addInstruction(Instruction i) {
            // TODO Auto-generated method stub
    }

    public Instruction shiftPipelineRight(Instruction instruction) {
        Instruction finishedInstruction = null;

        if(this.pipeline[this.pipeline.length - 1] != null) {
            finishedInstruction = this.pipeline[this.latency - 1];
        }

        for(int i = pipeline.length - 1; i > 0; i--){
            this.pipeline[i] = this.pipeline[i-1];
        }

        this.pipeline[0] = instruction;

        return finishedInstruction;
    }

    public abstract void doCycle();
}
