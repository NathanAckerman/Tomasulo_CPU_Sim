import java.util.ArrayList;
import java.util.Collections;

public class WB
{
    // private ROB rob;
    private int maxPushSize;
    private int numPushed;
    private ArrayList<Unit> units;

    // public WB(final ROB rob, int maxPushSize){} ---> Please delete the method signature below when we have ROB
    public WB(int maxPushSize, ArrayList<Unit> unit_arr)
    {
        // this.rob = rob;
        this.maxPushSize = maxPushSize;
        this.numPushed = 0;
        this.units = unit_arr;
    }

    public boolean isFull() 
    {
        return numPushed < maxPushSize; 
    }

    public ArrayList<Instruction> pull(int count)
    {
        // Grabbing candidate instructions
        ArrayList<Instruction> readiedInstructions = new ArrayList<Instruction>();
        for(Unit u: units){
            Instruction[] pipeline = u.pipeline;
            Instruction readied = pipeline[pipeline.length - 1];
            if(readied != null){
                readiedInstructions.add(readied);
            }
        }
        Collections.sort(readiedInstructions, (a, b) -> a.issueid.compareTo(b.issueid));

        ArrayList<Instruction> instructionsToPush = new ArrayList<Instruction>();

        // Grabbing count instructions
        for(int i = 0; i < count; i++){
            Instruction instr = readiedInstructions.get(i);
            instructionsToPush.add(instr);
        }

        // Removing instructions from pipeline
        for(Unit u: units) {
            Instruction instrFinished = u.pipeline[u.pipeline.length - 1];
            if(instrFinished != null && instructionsToPush.contains(instrFinished)){
                u.pipeline[u.pipeline.length - 1] = null;
            }
        }

        return readiedInstructions;
        
        //boolean CDBSuccess = CDB.push(i); ---> Please delete CDBSuccess below when we have CDB
        boolean CDBSuccess = false;

        if (!CDBSuccess) System.out.println("Something went wrong... this should never happen");

        //ROB.push(i); // TO-DO
        numPushed +=1;

    }

    public void reset()
    {
        numPushed = 0;
    }

    public void setNewPushLimit(int newLimit)
    {
        this.maxPushSize = newLimit;
    }

    public int queryReadyInstructions()
    {
        int numReadied = 0;
        for(Unit u: units){
            Instruction[] pipeline = u.pipeline;
            Instruction readied = pipeline[pipeline.length - 1];
            if(readied != null){
                numReadied +=1;
            }
        }

        return numReadied;
    }

    public class InstructionSort implements Comparator<Instruction> { 
        public int compare(Instruction a, Instruction b) 
        { 
            return a.issueid - b.issueid; 
        } 
    } 
}
