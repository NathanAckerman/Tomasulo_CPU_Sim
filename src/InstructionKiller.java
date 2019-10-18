public class InstructionKiller {
	Simulator sim;

	InstructionKiller(Simulator the_sim)
	{
		sim = the_sim;
	}

	public void killInstructionAnywhere(Instruction instr)
	{
		//TODO what is going on with the buffers for wb?
		
		//find and kill the instruction if in units
		boolean killed_in_units = sim.units.killInstr(instr);
		
		//find and kill the instruction if in the issuer
		if (!killed_in_units) {
			sim.issuer.killInstr();
		}

		//need to unmap the register it reserved because it was going to write into it
		if (instr.dest_reg_original_str != null) {
			sim.rename_table.removeRename(instr.dest_reg_original_str, instr);
		}
	}

}
