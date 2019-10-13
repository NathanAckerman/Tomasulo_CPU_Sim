import java.util.HashMap;

public class RegisterFile
{
    private Integer[] intRegisters = new Integer[32];
    private Float[] fpRegisters = new Float[32]; 

    private HashMap<String, Integer> intRegistersRenamed = new HashMap<String, Integer>();
    private HashMap<String, Float> floatRegistersRenamed = new HashMap<String, Float>();

    // Fill out the instruction with values
    public void read(Instruction i)
    {
        //TODO: Account for renamed scheme
        String opcode = i.getOpcode();
        
        //NOTE: The parser doesnt parse correctly. It only parses for values 0-9. For values greater than 9, it will only take the first digit

        switch (opcode) {
            // int immediate
            case "andi": case "ori":
            case "slti": case "addi":
            case "subi": {
                    // The Parser is set up in such a way that first source reg must be valid and second source reg must be immediate value
                    // I.E., addi R0, 100, R1 is invalid but addi R0, R1, 100 is valid
                    i.source_reg1_value = intRegisters[i.getSourceReg1()];
                    i.source_reg2_value = i.getImmediate();
                    break;
            }
            // int non-immediate
            case "and": case "or":
            case "slt": case "add":
            case "mult": case "sub": {
                i.source_reg1_value = intRegisters[i.getSourceReg1()];
                i.source_reg2_value = intRegisters[i.getSourceReg2()];
                break;
            }

            // loads
            // pretty sure floating loads still need to load their address
            // from int registers...
            case "ld": case "fld": {
                i.source_reg1_value = intRegisters[i.getSourceReg1()];
                break;
            }

            // int store
            case "sd": {
                i.dest_reg_value = intRegisters[i.dest_reg];
                i.source_reg1_value = intRegisters[i.getSourceReg1()];
                break;
            }

            // float store
            case "fsd": {
                i.dest_reg_value = fpRegisters[i.dest_reg];
                i.source_reg1_value = fpRegisters[i.getSourceReg1()];
                break;
            }

            // floating point 
            case "fadd": case "fsub":
            case "fmult": case "fdiv": {
                i.source_reg1_value = fpRegisters[i.getSourceReg1()];
                i.source_reg2_value = fpRegisters[i.getSourceReg2()];
                break;
            }

            case "beq": case "bne": {
                // Parser is set up in such a way that it is always comparing a register to a value. 
                // I.E., beq R1, R2, loop is invalid but beq R1, $5, loop is valid
                // I am going to assume that these beq takes in only INTEGER FLOATING POINT

                i.source_reg1_value = intRegisters[i.getSourceReg1()];
                i.source_reg2_value = i.getImmediate();
                break;
            }
            default: 
                System.out.println("Opcode " + opcode + " is either invalid or has not been considered");
        }
    }

    public void commit(Instruction i){
        // The only thing that we are allowing to committ are opcodes that does integer/floating point arithmetic and for storing words.
        
        String opcode = i.getOpcode();

        switch (opcode) {
            case "and": case "andi":
            case "or": case "ori":
            case "slt": case "slti":
            case "add": case "addi":
            case "sub": case "subi":
            case "mult": case "ld": {
                intRegisters[i.getDestReg()] = i.dest_reg_value; // Careful of dest_reg_value type here
                break;
            }
            case "fmult": case "fdiv":
	    case "fadd": case "fsub": case "fld": {
                fpRegisters[i.getDestReg()] = i.dest_reg_value; // Careful of dest_reg_value type here
            }

        }
    }
        

}
