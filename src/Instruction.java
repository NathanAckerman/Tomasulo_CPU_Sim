public class Instruction
{
	public int address;
	public String opcode;
	public int source_reg1;
	public int source_reg2;
	public int dest_reg;
	public int immediate;
	public int target;
	public Double source_reg1_value;
	public Double source_reg2_value;
	public Double dest_reg_value;

	public Instruction()
	{
		this.address = -1;
		this.opcode = "";
		this.source_reg1 = -1;
		this.source_reg2 = -1;
		this.dest_reg = -1;
		this.immediate = -1;
		this.target = -1;
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
		result += "source_reg1: " + Integer.toString(this.source_reg1) +"\n";
		result += "source_reg2: " + Integer.toString(this.source_reg2) +"\n";
		result += "dest_reg: " + Integer.toString(this.dest_reg) +"\n";
		result += "immediate: " + Integer.toString(this.immediate) +"\n";
		result += "target: " + Integer.toString(this.target) +"\n";
		return result;
	}
}
