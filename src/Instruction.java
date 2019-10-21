public class Instruction
{
	public int address;
	public String opcode;
	public int source_reg1;
	public int source_reg2;
	public int dest_reg;
	public String dest_reg_original_str;
	public String source_reg1_original_str;
	public String source_reg2_original_str;
	public String dest_reg_renamed_str;
	public String source_reg1_renamed_str;
	public String source_reg2_renamed_str;
	public int immediate;
	public int target;
	public int predicted_target;
	public Float source_reg1_value;
	public Float source_reg2_value;
	public Float dest_reg_value;
	public boolean invalid;
	public int issue_id;
	public boolean completed;
	public int threadNum;

	public Instruction()
	{
		this.address = -1;
		this.opcode = "";
		this.source_reg1 = -1;
		this.source_reg2 = -1;
		this.dest_reg = -1;
		this.immediate = -1;
		this.target = -1;
		this.invalid = false;
		this.completed = false;
	}

	public int getAddess()  { return this.address; }
	public void setAddress(int address) { this.address = address; }

	public String getOpcode() { return this.opcode; }
	public void setOpcode(String opcode) { this.opcode = opcode; }

	public int getSourceReg1() { return this.source_reg1; }
	public void setSourceReg1(int source_reg1) { this.source_reg1 = source_reg1; }

	public int getSourceReg2() { return this.source_reg2; }
	public void setSourceReg2(int source_reg2) { this.source_reg2 = source_reg2; }

	public int getDestReg() { return this.dest_reg; }
	public void setDestReg(int dest_reg) { this.dest_reg = dest_reg; }

	public int getImmediate() { return this.immediate; }
	public void setImmediate(int immediate) { this.immediate = immediate; }

	public int getTarget() { return this.target; }
	public void setTarget(int target) { this.target = target; }

	public String toString()
	{
		String result = "";
		result += "Instruction Address: " + Integer.toString(this.address) +"\n";
		result += "opcode: " + this.opcode +"\n";
		result += "immediate: " + Integer.toString(this.immediate) +"\n";
		result += "target: " + Integer.toString(this.target) +"\n";
		result += "dest_reg_original_str:" + dest_reg_original_str + "\n";
		result += "dest_reg_renamed_str:" + dest_reg_renamed_str + "\n";
		result += "dest_value: " + dest_reg_value + "\n";
		result += "source_reg1_original_str:" + source_reg1_original_str + "\n";
		result += "source_reg1_rename_str:" + source_reg1_renamed_str + "\n";
		result += "source_reg1_value:" + source_reg1_value + "\n";
		result += "source_reg2_original_str:" + source_reg2_original_str + "\n";
		result += "source_reg2_renamed_str:" + source_reg2_renamed_str + "\n";
		result += "source_reg2_value:" + source_reg2_value + "\n";
		result += "completed: " + completed + "\n";
		result += "threadNum: " + threadNum + "\n";
		return result;
	}

	public boolean isWaitingOnValue()
	{
		switch (opcode) {
			case "and":
				return (source_reg1_value == null || source_reg2_value == null);

			case "andi":
				return source_reg1_value == null;

			case "or":
				return (source_reg1_value == null || source_reg2_value == null);

			case "ori":
				return source_reg1_value == null;

			case "slt":
				return (source_reg1_value == null || source_reg2_value == null);

			case "slti":
				return source_reg1_value == null;

			case "add":
				return (source_reg1_value == null || source_reg2_value == null);

			case "addi":
				return source_reg1_value == null;

			case "sub":
				return (source_reg1_value == null || source_reg2_value == null);

			case "subi":
				return source_reg1_value == null;

			case "mul":
				return (source_reg1_value == null || source_reg2_value == null);

			case "fmul":
				return (source_reg1_value == null || source_reg2_value == null);

			case "fadd":
				return (source_reg1_value == null || source_reg2_value == null);

			case "fsub":
				return (source_reg1_value == null || source_reg2_value == null);

			case "fdiv":
				return (source_reg1_value == null || source_reg2_value == null);

			case "ld":
				return source_reg1_value == null;

			case "fld":
				return source_reg1_value == null;

			case "beq":
				return source_reg1_value == null;

			case "bne":
				return source_reg1_value == null;

			case "sd": case "fsd":
				return (source_reg1_value == null || dest_reg_value == null);

			default:
				System.out.println("Invalid opcode");
				System.exit(1);
				return true;
		}
	}
}
