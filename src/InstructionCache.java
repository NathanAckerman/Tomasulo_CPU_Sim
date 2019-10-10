import java.util.ArrayList;

public class InstructionCache
{
	public ArrayList<Instruction> instructions;

	private final int BASE_ADDR = 1000;
	private Instruction[] cache_line;
	private int num_instr_in_left_unissued;
	private Issuer issuer;

	public InstructionCache(Issuer issuer)
	{
		instructions = new ArrayList<Instruction>();
		cache_line = new Instruction[4];
		num_instr_in_left_unissued = 0;
		this.issuer = null;
	}

	public String toString()
	{
		String result = "\nINSTRUCTION CACHE:\n\n";
		for (Instruction instruction: this.instructions)
			result += instruction.toString() + "\n";

		return result;
	}

	/*
	 * Given an instruction address, this function will
	 * find and return the associated Instruction object
	 */
	public Instruction findInstruction(int address)
	{
		for (Instruction instruction: this.instructions)
			if (instruction.getAddess() == address)
				return instruction;

		return null;
	}

	public void doCycle()
	{
		if (instr_left_in_line() && pc_in_line(pc)) {
			issueInstructions();
		} else {
			get_cache_line_with_pc(pc);
			issueInstructions();
		}
	}

	private void issueInstructions()
	{
		int num_spots_in_issuer = issuer.getEmptySpots();
		boolean issuing = false;
		int num_sent = 0;
		for (Instruction instr : cache_line) {
			if (num_sent == num_spots_in_issuer) {
				break;
			}
			if (instr != null && instr.address == pc) {
				issuing = true;
			}
			if (issuing) {
				if (issuer.enqueueInstruction(instr)) {
					num_sent++;
				}
				
			}
		}
	}

	private boolean pc_in_line(int pc)
	{
		for(Instruction instr: cache_line){
			if(instr.address == pc) {//TODO need to decide how pc references are done
				return true;
			}
		}
		return false;
	}

	private boolean instr_left_in_line()
	{
		return num_instr_in_left_unissued != 0;	
	}

	private void get_cache_line_with_pc(int pc) {
		Instruction pc_instr = findInstruction(pc);
		clear_cacheline();
	
		//TODO this is if instr width is 4 but it seems like he wrote his parser as 1??
		if ((pc-BASE_ADDR) % 16 == 0 ) { //if this instr is the first of 4 in a cache line
			cache_line[0] = pc_instr;
			cache_line[1] = findInstruction(pc+4);
			cache_line[2] = findInstruction(pc+8);
			cache_line[3] = findInstruction(pc+12);
		} else if ((pc-BASE_ADDR) % 16 == 4) {
			cache_line[0] = findInstruction(pc-4);
			cache_line[1] = pc_instr;
			cache_line[2] = findInstruction(pc+4);
			cache_line[3] = findInstruction(pc+8);
		} else if ((pc-BASE_ADDR) % 16 == 8) {
			cache_line[0] = findInstruction(pc-8);
			cache_line[1] = findInstruction(pc-4);
			cache_line[2] = pc_instr;
			cache_line[3] = findInstruction(pc+4);
		} else if ((pc-BASE_ADDR) % 16 == 12) {
			cache_line[0] = findInstruction(pc-12);
			cache_line[1] = findInstruction(pc-8);
			cache_line[2] = findInstruction(pc-4);
			cache_line[3] = pc_instr;
		}
	}

	private void clear_cacheline()
	{
		for(int i = 0; i < 4; i++)
		{
			cache_line[i] = null;
		}
	}

}
