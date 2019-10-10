import java.lang.*;
public class InstructionEvaluator {
	static ROB rob;
	static BTB btb;

	InstructionEvaluator(ROB rob, BTB btb) {
		this.rob = rob;
		this.btb = btb;
	}

	//evaluate the result of the given instruciton
	//if it it a branch instruction, also update the BTB and PC
	//note: the casting is done because you cant do bitwise operations on doubles
	public static void eval(Instruction instr)
	{
		switch (instr.opcode) {
			case "and":
				instr.dest_reg_value = (double)((Double.doubleToRawLongBits(instr.source_reg1_value) & Double.doubleToRawLongBits(instr.source_reg2_value)));		
				break;

			case "andi":
				instr.dest_reg_value = (double)((Double.doubleToRawLongBits(instr.source_reg1_value) & Double.doubleToRawLongBits(instr.immediate)));		
				break;

			case "or":
				instr.dest_reg_value = (double)((Double.doubleToRawLongBits(instr.source_reg1_value) & Double.doubleToRawLongBits(instr.source_reg2_value)));		
				break;

			case "ori":
				instr.dest_reg_value = (double)((Double.doubleToRawLongBits(instr.source_reg1_value) & Double.doubleToRawLongBits(instr.immediate)));		
				break;

			case "slt":
				instr.dest_reg_value = (double)((Double.doubleToRawLongBits(instr.source_reg1_value) & Double.doubleToRawLongBits(instr.source_reg2_value)));		
				break;

			case "slti":
				instr.dest_reg_value = (double)((Double.doubleToRawLongBits(instr.source_reg1_value) & Double.doubleToRawLongBits(instr.immediate)));		
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
				}
				if (!condition_val && instr.predicted_target != instr.address+4) {//predicted taken but wasnt
					rob.killInstructionsBetween(instr.address, 0);
					btb.updatePrediction(instr.address, instr.address+4, false);
				}
				break;

			case "bne":
				boolean condition_val2 = (instr.source_reg1_value != instr.source_reg1_value);
				if (condition_val2 && instr.predicted_target != instr.target) {//predicted not taken but was
					rob.killInstructionsBetween(instr.address, 0);
					btb.updatePrediction(instr.address, instr.target, true);
				}
				if (!condition_val2 && instr.predicted_target != instr.address+4) {//predicted taken but wasnt
					rob.killInstructionsBetween(instr.address, 0);
					btb.updatePrediction(instr.address, instr.address+4, false);
				}
				break;

			default:
				System.out.println("This should never run, instr opcode was not recognized");
		}
	}
}
