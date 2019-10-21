import java.util.ArrayList;
import java.util.Collections;

public class WB
{
    private int maxPushSize;
    private int numPushed;
    private ArrayList<Unit> units;

    public WB(int maxPushSize, ArrayList<Unit> unit_arr)
    {
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
        Collections.sort(readiedInstructions, (a, b) -> a.issue_id - b.issue_id);

        ArrayList<Instruction> instructionsToPush = new ArrayList<Instruction>();

        // Grabbing count instructions
        for(int i = 0; i < count; i++){
            Instruction instr = readiedInstructions.get(i);
	        System.out.println("wb stage completed instr");
            instr.completed = true;
            instructionsToPush.add(instr);
        }

        // Removing instructions from pipeline
        for(Unit u: units) {
            Instruction instrFinished = u.pipeline[u.pipeline.length - 1];
            if(instrFinished != null && instructionsToPush.contains(instrFinished)){
                u.pipeline[u.pipeline.length - 1] = null;
            }
        }

        return instructionsToPush;
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
        System.out.println("WB has " + numReadied + " instructions");
        return numReadied;
    }
}
