import java.util.ArrayList;

public class Station {

    public String stationName;

    // Qs are the renamed register names.
    public String Qj;
    public String Qk;

    public Instruction instruction;


    // The destination register renamed is the same as the stationName

    public Station(
        String stationName, 
        String Qj, 
        String Qk,
        Instruction instruction) {
            this.stationName = stationName;
            this.Qj = Qj;
            this.Qk = Qk;
            this.instruction = instruction;
            // Vj, Vk, Op, and Address can all be found inside instruction
        }

    public boolean isWaitingOnValue(){
        return (this.instruction.source_reg1_value == null || this.instruction.source_reg2_value == null);
    }



}