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

    public RegisterFile(Memory the_mem)
    {
        mem = the_mem;
    }

    // Fill out the instruction with values
    public void read(Instruction i) {
        String opcode = i.getOpcode();
    
        switch (opcode) {
            case "andi": case "ori":
            case "slti": case "addi":
            case "subi": case "sd": 
            case "ld": case "fld": {
                if(!setIntFirstRegisterValue(i)) {
                    System.out.println(i.toString() + "produced an error 1");
		    System.exit(1);
                }

                break;
            }

            // int non-immediate
            case "and": case "or": case "slt": 
            case "add": case "mult":
            case "sub": {
                if(!setIntFirstRegisterValue(i) || !setIntSecondRegisterValue(i)) {
                    System.out.println(i.toString() + "produced an error 2");      
		    System.exit(1);
                } 

                break;
            }

            case "fsd":
            case "beq": case "bne": {
                // NOTE FOR BRANCHES: 
                // Parser is set up in such a way that it is always comparing a register to a value. 
                // I.E., beq R1, R2, loop is invalid but beq R1, $5, loop is valid
                // Assumptions: BEQ takes in only integer registers followed by one immediate
                if(!setFPFirstRegisterValue(i)) {
                    System.out.println(i.toString() + "produced an error 3");
		    System.exit(1);
                }

                break;
            }

            // floating point 
            case "fadd": case "fsub":
            case "fmult": case "fdiv": {
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

	System.out.println("Committing instruction:");
	System.out.println(i);
        String opcode = i.getOpcode();

        switch (opcode) {
            // Ignoring Store operations
            // Ignoring Branch Instruction because I'm assuming branch instruction wont ever be pushed by WB
            case "and": case "andi":
            case "or": case "ori":
            case "slt": case "slti":
            case "add": case "addi":
            case "sub": case "subi":
            case "mult": case "ld": {
                return CommitToIntRegisters(i);
            }
            case "fmult": case "fdiv":
            case "fadd": case "fsub":
            case "fld": {
                return CommitToFPRegisters(i);
            }
            case "sd": {
                mem.add_int(i.source_reg1_value.intValue() + i.immediate, i.dest_reg_value.intValue());
                return true;
            }
            case "fsd": {
                mem.add_float(i.source_reg1_value.intValue() + i.immediate, i.dest_reg_value.intValue());
                return false;
            }
            default: {
                System.out.println("Something went wrong in wb_push function. Opcode " + opcode + " is not valid");
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
            case "mult": case "ld": {
                return pushToIntRegistersRenamed(i);
            }
            case "fmult": case "fdiv":
            case "fadd": case "fsub":
            case "fld": {
                return pushToFPRegistersRenamed(i);
            }
            default: {
                System.out.println("Something went wrong in wb_push function. Opcode " + opcode + " is not valid");
                System.out.println("Error happened with instruction detail:\n" + i.toString());
                return false;
            }
        }
    }


    public boolean setIntFirstRegisterValue(Instruction i){
        if(intRegistersRenamed.containsKey(i.source_reg1_renamed_str)) {
            i.source_reg1_value = intRegistersRenamed.get(i.source_reg1_renamed_str).floatValue();
            return true;
        }else if(intRegisters.containsKey(i.source_reg1_original_str)) {
            i.source_reg1_value = intRegisters.get(i.source_reg1_original_str).floatValue();
            return true;
        }else{
            return false;
        }
    }

    public boolean setIntSecondRegisterValue(Instruction i){
        if(intRegistersRenamed.containsKey(i.source_reg2_renamed_str)) {
            i.source_reg2_value = intRegistersRenamed.get(i.source_reg2_renamed_str).floatValue();
            return true;
        }else if(intRegisters.containsKey(i.source_reg2_original_str)) {
            i.source_reg2_value = intRegisters.get(i.source_reg2_original_str).floatValue();
            return true;
        } else{
            return false;
        }
    }

    public boolean setFPFirstRegisterValue(Instruction i){
        if(fpRegistersRenamed.containsKey(i.source_reg1_renamed_str)) {
            i.source_reg1_value = fpRegistersRenamed.get(i.source_reg1_renamed_str).floatValue();
            return true;
        }else if(fpRegisters.containsKey(i.source_reg1_original_str)) {
            i.source_reg1_value = fpRegisters.get(i.source_reg1_original_str).floatValue();
            return true;
        }else {
            return false;
        }
    }

    public boolean setFPSecondRegisterValue(Instruction i){
        if(fpRegistersRenamed.containsKey(i.source_reg2_renamed_str)) {
            i.source_reg2_value = fpRegistersRenamed.get(i.source_reg2_renamed_str).floatValue();
            return true;
        }else if(fpRegisters.containsKey(i.source_reg2_original_str)) {
            i.source_reg2_value = fpRegisters.get(i.source_reg2_original_str).floatValue();
            return true;
        }else {
            return false;
        }
    }

    public boolean pushToIntRegistersRenamed(Instruction i) {
        if (intRegistersRenamed.size() < 8 || intRegistersRenamed.containsKey(i.dest_reg_renamed_str)) {
            intRegistersRenamed.put(i.dest_reg_renamed_str, i.dest_reg_value.intValue());
            return true;
        }

        return false;
    }

    public boolean pushToFPRegistersRenamed(Instruction i) {
        if (fpRegistersRenamed.size() < 8 || fpRegistersRenamed.containsKey(i.dest_reg_renamed_str)) {
            fpRegistersRenamed.put(i.dest_reg_renamed_str, i.dest_reg_value);
            return true;
        }

        return false;
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
	Iterator hm_iter2 = intRegisters.entrySet().iterator();
	while (hm_iter2.hasNext()) {
		Map.Entry pair = (Map.Entry)hm_iter2.next();
		int int_val = (int)pair.getValue();
		String int_reg = (String)pair.getKey();
		System.out.println("Reg: "+int_reg+"    val: "+Integer.toString(int_val));
	}

	Iterator hm_iter = fpRegisters.entrySet().iterator();
	while (hm_iter.hasNext()) {
		Map.Entry pair = (Map.Entry)hm_iter.next();
		Float f_val = (float)pair.getValue();
		String f_reg = (String)pair.getKey();
		System.out.println("Reg: "+f_reg+"    val: "+Float.toString(f_val));
	}
    }



}
