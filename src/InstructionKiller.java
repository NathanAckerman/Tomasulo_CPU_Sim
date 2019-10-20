public class InstructionKiller {
	public Simulator sim;

	InstructionKiller(Simulator the_sim)
	{
		sim = the_sim;
	}

	public void killInstructionAnywhere(Instruction instr)
	{
		//TODO what is going on with the buffers for wb?
		
		//find and kill the instruction if in units
		boolean killed_in_units = false; 
		for (Unit u : sim.units)
			killed_in_units = killed_in_units || u.killInstr(instr);

		ReservationStationStatusTable.killInstruction(instr);
		
		//find and kill the instruction if in the issuer

		//need to unmap the register it reserved because it was going to write into it
		if (instr.dest_reg_original_str != null && !instr.opcode.equals("sd") && !instr.opcode.equals ("fsd")) {
			sim.rename_table.removeRename(instr.dest_reg_original_str, instr);
		}
	}

}
