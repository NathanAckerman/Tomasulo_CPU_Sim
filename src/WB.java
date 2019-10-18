import java.util.ArrayList;

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
        this.units = unit_arr
    }

    public boolean isFull() 
    {
        // I dont think the WB can ever be bottlenecked by the ROB.
        // If the ROB does not have room, it doesnt matter to the WB
        // as the instructions that it is attempting to give to ROB already
        // has a reserved spot. The WB will never stall unless its maxPushSize = 0
        
        return numPushed < maxPushSize; 
    }

    public void push(Instruction i)
    {
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


}
