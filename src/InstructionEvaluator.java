import java.lang.*;
public class InstructionEvaluator {
	static ROB rob;
	static BTB btb;
	static Memory mem;
	//static Integer next_pc;
	static InstructionCache cache;
	InstructionEvaluator(ROB the_rob, BTB the_btb, Memory the_mem, InstructionCache the_cache) {
		rob = the_rob;
		btb = the_btb;
		//next_pc = cache.next_pc;
		cache = the_cache;
		mem = the_mem;
	}

	//evaluate the result of the given instruciton
	//if it it a branch instruction, also update the BTB and PC
	//note: the casting is done because you cant do bitwise operations on floats
	public static void eval(Instruction instr)
	{
		switch (instr.opcode) {
			case "and":
				instr.dest_reg_value = (float) (instr.source_reg1_value.intValue() & instr.source_reg2_value.intValue());
				break;

			case "andi":
				instr.dest_reg_value = (float) (instr.source_reg1_value.intValue() & instr.immediate);
				break;

			case "or":
				instr.dest_reg_value = (float) (instr.source_reg1_value.intValue() | instr.source_reg2_value.intValue());
				break;

			case "ori":
				instr.dest_reg_value = (float) (instr.source_reg1_value.intValue() | instr.immediate);
				break;

			case "slt":
				instr.dest_reg_value = (float) (instr.source_reg1_value.intValue() << instr.source_reg2_value.intValue());
				break;

			case "slti":
				instr.dest_reg_value = (float) (instr.source_reg1_value.intValue() << instr.immediate);
				break;

			case "add":
				instr.dest_reg_value = instr.source_reg1_value + instr.source_reg2_value;
				break;

			case "addi":
				instr.dest_reg_value = instr.source_reg1_value + instr.immediate;
				break;

			case "sub":
				instr.dest_reg_value = instr.source_reg1_value - instr.source_reg2_value;
				break;

			case "subi":
				instr.dest_reg_value = instr.source_reg1_value - instr.immediate;
				break;

			case "mul":
				instr.dest_reg_value = instr.source_reg1_value * instr.source_reg2_value;
				break;

			case "fmul":
				instr.dest_reg_value = instr.source_reg1_value * instr.source_reg2_value;
				break;

			case "fadd":
				instr.dest_reg_value = instr.source_reg1_value + instr.source_reg2_value;
				break;

			case "fsub":
				instr.dest_reg_value = instr.source_reg1_value - instr.source_reg2_value;
				break;

			case "fdiv":
				try {
					instr.dest_reg_value = instr.source_reg1_value / instr.source_reg2_value;
				} catch(Exception e){
					System.out.println("Error:" + e);
				}
				break;

			case "ld":
				instr.dest_reg_value = new Float(mem.get_int(Math.round(instr.source_reg1_value) + instr.immediate));
				break;

			case "fld":
				instr.dest_reg_value = mem.get_float(Math.round(instr.source_reg1_value) + instr.immediate);
				break;

			case "beq":
				boolean condition_val = (instr.source_reg1_value == instr.immediate);
				if (condition_val && instr.predicted_target != instr.target) {//predicted not taken but was
					// TODO get ROB index
					rob.killInstructionsAfter(instr);
					btb.updatePrediction(instr.address, instr.target, true);
					cache.next_pc = instr.target;
					System.out.println("\n@@@@@@@@@@@@@@@@@@@@@\nBranch mispredict\n\n");
					System.out.println("Setting pc to: "+cache.next_pc);
					break;
				}
				if (!condition_val && instr.predicted_target != instr.address+1) {//predicted taken but wasnt
					// TODO get ROB index
					rob.killInstructionsAfter(instr);
					btb.updatePrediction(instr.address, instr.address+1, false);
					cache.next_pc = instr.address+1;
					System.out.println("\n@@@@@@@@@@@@@@@@@@@@@\nBranch mispredict\n\n");
					System.out.println("Setting pc to: "+cache.next_pc);
					break;
				}
				System.out.println("\n########################\nbranch correctly predicted\n");
				break;

			case "bne":
				boolean condition_val2 = (instr.source_reg1_value != instr.immediate);
				if (condition_val2 && instr.predicted_target != instr.target) {//predicted not taken but was
					// TODO get ROB index
					rob.killInstructionsAfter(instr);
					btb.updatePrediction(instr.address, instr.target, true);
					cache.next_pc = instr.target;
					System.out.println("\n@@@@@@@@@@@@@@@@@@@@@\nBranch mispredict\n\n");
					System.out.println("Setting pc to: "+cache.next_pc);
					break;
				}
				if (!condition_val2 && instr.predicted_target != instr.address+1) {//predicted taken but wasnt
					// TODO get ROB index
					rob.killInstructionsAfter(instr);
					btb.updatePrediction(instr.address, instr.address+1, false);
					cache.next_pc = instr.address+1;
					System.out.println("\n@@@@@@@@@@@@@@@@@@@@@\nBranch mispredict\n\n");
					System.out.println("Setting pc to: "+cache.next_pc);
					break;
				}
				System.out.println("\n########################\nbranch correctly predicted\n");
				break;

			default:
		}
	}
}
