import java.lang.*;
public class InstructionEvaluator {
	static ROB rob;
	static BTB btb;
	static Integer pc;

	InstructionEvaluator(ROB the_rob, BTB the_btb, Integer the_pc) {
		rob = the_rob;
		btb = the_btb;
		pc = the_pc;
	}

	//evaluate the result of the given instruciton
	//if it it a branch instruction, also update the BTB and PC
	//note: the casting is done because you cant do bitwise operations on floats
	public static void eval(Instruction instr)
	{
		switch (instr.opcode) {
			case "and":
				instr.dest_reg_value = (float)((Float.floatToRawIntBits(instr.source_reg1_value) & Float.floatToRawIntBits(instr.source_reg2_value)));		
				break;

			case "andi":
				instr.dest_reg_value = (float)((Float.floatToRawIntBits(instr.source_reg1_value) & Float.floatToRawIntBits(instr.immediate)));		
				break;

			case "or":
				instr.dest_reg_value = (float)((Float.floatToRawIntBits(instr.source_reg1_value) & Float.floatToRawIntBits(instr.source_reg2_value)));		
				break;

			case "ori":
				instr.dest_reg_value = (float)((Float.floatToRawIntBits(instr.source_reg1_value) & Float.floatToRawIntBits(instr.immediate)));		
				break;

			case "slt":
				instr.dest_reg_value = (float)((Float.floatToRawIntBits(instr.source_reg1_value) & Float.floatToRawIntBits(instr.source_reg2_value)));		
				break;

			case "slti":
				instr.dest_reg_value = (float)((Float.floatToRawIntBits(instr.source_reg1_value) & Float.floatToRawIntBits(instr.immediate)));		
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
				instr.dest_reg_value = instr.source_reg1_value / instr.source_reg2_value;
				break;

			case "beq":
				boolean condition_val = (instr.source_reg1_value == instr.source_reg1_value);
				if (condition_val && instr.predicted_target != instr.target) {//predicted not taken but was
					rob.killInstructionsBetween(instr.address, 0);
					btb.updatePrediction(instr.address, instr.target, true);
					pc = instr.target;
				}
				if (!condition_val && instr.predicted_target != instr.address+1) {//predicted taken but wasnt
					rob.killInstructionsBetween(instr.address, 0);
					btb.updatePrediction(instr.address, instr.address+1, false);
					pc = instr.address+1;
				}
				break;

			case "bne":
				boolean condition_val2 = (instr.source_reg1_value != instr.source_reg1_value);
				if (condition_val2 && instr.predicted_target != instr.target) {//predicted not taken but was
					rob.killInstructionsBetween(instr.address, 0);
					btb.updatePrediction(instr.address, instr.target, true);
					pc = instr.target;
				}
				if (!condition_val2 && instr.predicted_target != instr.address+1) {//predicted taken but wasnt
					rob.killInstructionsBetween(instr.address, 0);
					btb.updatePrediction(instr.address, instr.address+1, false);
					pc = instr.address+1;
				}
				break;

			default:
				System.out.println("This should never run, instr opcode was not recognized");
		}
	}
}
