public abstract class Unit {

    private final int latency;
    private Instruction[] pipeline;
    private Instruction buffer; 
    private WB wb;

    public Unit(int numReservStation, final int latency, UnitName unitName, WB wb) 
    {
        ReservationStationStatusTable.createStations(unitName, numReservStation);
        this.latency = latency;
        this.pipeline = new Instruction[latency];
        this.wb = wb;
    }

    public void addInstruction(Instruction i) {
            // TODO Auto-generated method stub
    }

    public Instruction shiftPipelineRight(Instruction instruction) 
    {
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

    public boolean shouldStall()
    {
        return this.pipeline[this.pipeline.length - 1] != null && this.buffer != null && wb.isFull();
    }

    public void moveToWBOrBuffer(Instruction i)
    {
        if(!wb.isFull()) {
            if(this.buffer != null){
                wb.push(this.buffer);
                this.buffer = i;
            }else if(i != null){
                wb.push(i);
            }
        }else{
            this.buffer = i;
        }
    }

    public abstract void doCycle();
}