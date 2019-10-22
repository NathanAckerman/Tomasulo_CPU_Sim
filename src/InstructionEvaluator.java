import java.lang.*;
public class InstructionEvaluator {
	static ROB rob;
	static ROB rob2;
	static BTB btb;
	static BTB btb2;
	static Memory mem;
	//static Integer next_pc;
	static InstructionCache cache;
	static InstructionCache cache2;

	InstructionEvaluator(ROB the_rob, ROB the_rob2, BTB the_btb, BTB the_btb2, Memory the_mem, InstructionCache the_cache, InstructionCache the_cache2) {
		rob = the_rob;
		rob2 = the_rob2;
		btb = the_btb;
		btb2 = the_btb2;
		//next_pc = cache.next_pc;
		cache = the_cache;
		cache2 = the_cache2;
		mem = the_mem;
	}

	//evaluate the result of the given instruciton
	//if it it a branch instruction, also update the BTB and PC
	//note: the casting is done because you cant do bitwise operations on floats
	public static void eval(Instruction instr)
	{
		ROB cur_rob = null;
		BTB cur_btb = null;
		InstructionCache cur_cache = null;
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
				if (instr.source_reg1_value.intValue() < instr.source_reg2_value.intValue()) {
					instr.dest_reg_value = (float)1;
				} else {
					instr.dest_reg_value = (float)0;
				}
				break;

			case "slti":
				if (instr.source_reg1_value.intValue() < instr.immediate) {
					instr.dest_reg_value = (float)1;
				} else {
					instr.dest_reg_value = (float)0;
				}
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
				if (instr.threadNum == 1) {
					cur_rob = rob;
					cur_btb = btb;
					cur_cache = cache;
				} else {
					cur_rob = rob2;
					cur_btb = btb2;
					cur_cache = cache2;
				}
				boolean condition_val = (instr.source_reg1_value == instr.immediate);
				if (condition_val && instr.predicted_target != instr.target) {//predicted not taken but was
					cur_rob.killInstructionsAfter(instr);
					cur_btb.updatePrediction(instr.address, instr.target, true);
					cur_cache.next_pc = instr.target;
					break;
				}
				if (!condition_val && instr.predicted_target != instr.address+1) {//predicted taken but wasnt
					cur_rob.killInstructionsAfter(instr);
					cur_btb.updatePrediction(instr.address, instr.address+1, false);
					cur_cache.next_pc = instr.address+1;
					break;
				}
				break;

			case "bne":
				if (instr.threadNum == 1) {
					cur_rob = rob;
					cur_btb = btb;
					cur_cache = cache;
				} else {
					cur_rob = rob2;
					cur_btb = btb2;
					cur_cache = cache2;
				}
				boolean condition_val2 = (instr.source_reg1_value != instr.immediate);
				if (condition_val2 && instr.predicted_target != instr.target) {//predicted not taken but was
					cur_rob.killInstructionsAfter(instr);
					cur_btb.updatePrediction(instr.address, instr.target, true);
					cur_cache.next_pc = instr.target;
					break;
				}
				if (!condition_val2 && instr.predicted_target != instr.address+1) {//predicted taken but wasnt
					cur_rob.killInstructionsAfter(instr);
					cur_btb.updatePrediction(instr.address, instr.address+1, false);
					cur_cache.next_pc = instr.address+1;
					break;
				}
				break;

			default:
		}
	}
}
