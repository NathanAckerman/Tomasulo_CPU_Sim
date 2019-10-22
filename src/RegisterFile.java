import java.util.*;

public class RegisterFile
{
    private HashMap<String, Integer> intRegisters = new HashMap<String, Integer>() {{
        // Pre-populating
        for(int i = 0; i < 32; i++) {
            put("R" + i, new Integer(0));
        }
    }};

    private HashMap<String, Float> fpRegisters = new HashMap<String, Float>() {{
        // Pre-populating
        for(int i = 0; i < 32; i++) {
            put("F" + i, new Float(0));
        }
    }};

    private HashMap<String, Integer> intRegistersRenamed = new HashMap<String, Integer>();
    private HashMap<String, Float> fpRegistersRenamed = new HashMap<String, Float>();

    private Memory mem;
    private TomRenameTable renameTable;

    public RegisterFile(Memory the_mem, TomRenameTable renameTable)
    {
        mem = the_mem;
        this.renameTable = renameTable;
    }

    // Fill out the instruction with values
    public void read(Instruction i) {
        String opcode = i.getOpcode();
    
        switch (opcode) {
            case "andi": case "ori":
            case "slti": case "addi":
            case "subi": case "ld":
            case "fld": case "beq":
            case "bne": {
                if(!setIntFirstRegisterValue(i)) {
                    System.out.println(i.toString() + "produced an error 1");
		            System.exit(1);
                }

                break;
            }
            case "fsd":
            {
                if(i.source_reg1_renamed_str != null){
                    if(fpRegistersRenamed.get(i.source_reg1_renamed_str) != null) {
                        i.source_reg1_value = fpRegistersRenamed.get(i.source_reg1_renamed_str);
                    }
                }else if(fpRegisters.containsKey(i.source_reg1_original_str)) {
                    i.source_reg1_value = fpRegisters.get(i.source_reg1_original_str).floatValue();
                }else{
                    System.out.println(i.toString() + "produced an error in fsd fd read 1");
                    System.exit(1);
                }

                if(i.dest_reg_renamed_str != null){
                    if(fpRegistersRenamed.get(i.dest_reg_renamed_str) != null) {
                        i.dest_reg_value= fpRegistersRenamed.get(i.dest_reg_renamed_str);
                    }
                }else if(intRegisters.containsKey(i.dest_reg_original_str)) {
                    i.dest_reg_value = intRegisters.get(i.dest_reg_original_str).floatValue();
                }else{
                    System.out.println(i.toString() + "produced an error in fsd fd read 2");
                    System.exit(1);
                }
                break;
            }

            case "sd":
            {
                if(i.source_reg1_renamed_str != null){
                    if(intRegistersRenamed.get(i.source_reg1_renamed_str) != null) {
                        i.source_reg1_value = intRegistersRenamed.get(i.source_reg1_renamed_str).floatValue();
                    }
                }else if(intRegisters.containsKey(i.source_reg1_original_str)) {
                    i.source_reg1_value = intRegisters.get(i.source_reg1_original_str).floatValue();
                }else{
                    System.out.println(i.toString() + "produced an error in fsd fd read 3 ");
                    System.exit(1);
                }

                if(i.dest_reg_renamed_str != null){
                    if(intRegistersRenamed.get(i.dest_reg_renamed_str) != null) {
                        i.dest_reg_value= intRegistersRenamed.get(i.dest_reg_renamed_str).floatValue();
                    }
                }else if(intRegisters.containsKey(i.dest_reg_original_str)) {
                    i.dest_reg_value = intRegisters.get(i.dest_reg_original_str).floatValue();
                }else{
                    System.out.println(i.toString() + "produced an error in fsd fd read 4");
                    System.exit(1);
                }
            break;
            }

            // int non-immediate
            case "and": case "or": case "slt": 
            case "add": case "mul":
            case "sub": {
                if(!setIntFirstRegisterValue(i) || !setIntSecondRegisterValue(i)) {
                    System.out.println(i.toString() + "produced an error 2");      
		            System.exit(1);
                } 

                break;
            }

            // floating point 
            case "fadd": case "fsub":
            case "fmul": case "fdiv": {
                if(!setFPFirstRegisterValue(i) || !setFPSecondRegisterValue(i)) {
                    System.out.println(i.toString() + "produced an error 4");      
		            System.exit(1);
                }  
                break;
            }

            default: 
                System.out.println("Opcode " + opcode + " is either invalid or has not been considered");
		System.exit(1);
        }
    }

    public boolean commit(Instruction i) {

        String opcode = i.getOpcode();

        switch (opcode) {
            // Ignoring Store operations
            // Ignoring Branch Instruction because I'm assuming branch instruction wont ever be pushed by WB
            case "and": case "andi":
            case "or": case "ori":
            case "slt": case "slti":
            case "add": case "addi":
            case "sub": case "subi":
            case "mul": case "ld": {
                return CommitToIntRegisters(i);
            }
            case "fmul": case "fdiv":
            case "fadd": case "fsub":
            case "fld": {
                return CommitToFPRegisters(i);
            }
            case "sd": {
                mem.add_int(i.dest_reg_value.intValue() + i.immediate, i.source_reg1_value.intValue());
                return true;
            }
            case "fsd": {
                mem.add_float(i.dest_reg_value.intValue() + i.immediate, i.source_reg1_value.intValue());
                return true;
            }
            case "bne": case "beq": {
                return true;
            }
            default: {
                System.out.println("Something went wrong in commit function. Opcode " + opcode + " is not valid");
                System.out.println("Error happened with instruction detail:\n" + i.toString());
		        System.exit(1);
                return false;
            }
        }
    }

    public boolean wb_push(Instruction i){
        String opcode = i.getOpcode();

        switch (opcode) {
            // Ignoring Store operations
            // Ignoring Branch Instruction because I'm assuming branch instruction wont ever be pushed by WB
            case "and": case "andi":
            case "or": case "ori":
            case "slt": case "slti":
            case "add": case "addi":
            case "sub": case "subi":
            case "mul": case "ld": {
                return pushToIntRegistersRenamed(i);
            }
            case "fmul": case "fdiv":
            case "fadd": case "fsub":
            case "fld": {
                return pushToFPRegistersRenamed(i);
            }
		    case "fsd": case "sd":
            case "bne": case "beq": {
                return true;
            }

            default: {
                System.out.println("Something went wrong in wb_push function. Opcode " + opcode + " is not valid");
                System.out.println("Error happened with instruction detail:\n" + i.toString());
		        System.exit(1);
                return false;
            }
        }
    }


    public boolean setIntFirstRegisterValue(Instruction i){
        if(i.source_reg1_renamed_str != null) { //If the renamed string is in the table
            if(this.intRegistersRenamed.get(i.source_reg1_renamed_str) != null){ //If there is a value in the registerRenamed spectulative list
                i.source_reg1_value = intRegistersRenamed.get(i.source_reg1_renamed_str).floatValue(); // Set the value
            }
            return true;
        }else if(intRegisters.containsKey(i.source_reg1_original_str)) {
            i.source_reg1_value = intRegisters.get(i.source_reg1_original_str).floatValue();
            return true;
        }else{
            return false;
        }
    }

    public boolean setIntSecondRegisterValue(Instruction i){
        if(i.source_reg2_renamed_str != null) { //If the renamed string is in the table
            if(this.intRegistersRenamed.get(i.source_reg2_renamed_str) != null){ //If there is a value in the registerRenamed spectulative list
                i.source_reg2_value = intRegistersRenamed.get(i.source_reg2_renamed_str).floatValue(); // Set the value
            }
            return true;
        }else if(intRegisters.containsKey(i.source_reg2_original_str)) {
            i.source_reg2_value = intRegisters.get(i.source_reg2_original_str).floatValue();
            return true;
        } else{
            return false;
        }
    }

    public boolean setFPFirstRegisterValue(Instruction i){
        if(i.source_reg1_renamed_str != null) { //If the renamed string is in the table
            if(this.fpRegistersRenamed.get(i.source_reg1_renamed_str) != null){ //If there is a value in the registerRenamed spectulative list
                i.source_reg1_value = fpRegistersRenamed.get(i.source_reg1_renamed_str).floatValue(); // Set the value
            }
            return true;
        }else if(fpRegisters.containsKey(i.source_reg1_original_str)) {
            i.source_reg1_value = fpRegisters.get(i.source_reg1_original_str).floatValue();
            return true;
        }else {
            return false;
        }
    }

    public boolean setFPSecondRegisterValue(Instruction i){
        if(i.source_reg2_renamed_str != null) { //If the renamed string is in the table
            if(this.fpRegistersRenamed.get(i.source_reg2_renamed_str) != null){ //If there is a value in the registerRenamed spectulative list
                i.source_reg2_value = fpRegistersRenamed.get(i.source_reg2_renamed_str).floatValue(); // Set the value
            }
            return true;
        }else if(fpRegisters.containsKey(i.source_reg2_original_str)) {
            i.source_reg2_value = fpRegisters.get(i.source_reg2_original_str).floatValue();
            return true;
        }else {
            return false;
        }
    }

    public boolean pushToIntRegistersRenamed(Instruction i) {
        intRegistersRenamed.put(i.dest_reg_renamed_str, i.dest_reg_value.intValue());
        return true;

    }

    public boolean pushToFPRegistersRenamed(Instruction i) {
        fpRegistersRenamed.put(i.dest_reg_renamed_str, i.dest_reg_value);
        return true;
    }


    public boolean CommitToIntRegisters(Instruction i) {

        // All to-be committed instructions should exist in the renamed registers hashmap
        if (!intRegistersRenamed.containsKey(i.dest_reg_renamed_str)){
            return false;
        }

        if(!intRegisters.containsKey(i.dest_reg_original_str)) {
            return false; // Should contain. The hashmap is pre-populated
        }

        intRegistersRenamed.remove(i.dest_reg_renamed_str);
        intRegisters.put(i.dest_reg_original_str, i.dest_reg_value.intValue());

        return true;
        
    }

    public boolean CommitToFPRegisters(Instruction i) {

        // All to-be committed instructions should exist in the renamed registers hashmap
        if (!fpRegistersRenamed.containsKey(i.dest_reg_renamed_str)){
            return false;
        }

        if(!fpRegisters.containsKey(i.dest_reg_original_str)) {
            return false; // Should contain. The hashmap is pre-populated
        }

        fpRegistersRenamed.remove(i.dest_reg_renamed_str);
        fpRegisters.put(i.dest_reg_original_str, i.dest_reg_value);

        return true;
        
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void printRegisters()
    {
        System.out.println("\nPrinting Integer Registers Value:");

        for(int i = 0; i< intRegisters.size(); i++ ){
            String key = "R" + i;
            Integer value = intRegisters.get(key);
            System.out.println(key + ": " + value);
        }
        
        System.out.println("\n\n");
        System.out.println("Printing Floating Point Registers Value:");

        for(int i = 0; i< fpRegisters.size(); i++ ){
            String key = "F" + i;
            Float value = fpRegisters.get(key);
            System.out.println(key + ": " + value);
        }
    }



}
