import java.util.ArrayList;
import java.io.*;

public class InstructionCache
{
	public ArrayList<Instruction> instructions;

	private final int BASE_ADDR = 1000;
	private Instruction[] cache_line;
	private int num_instr_in_left_unissued;
	public Issuer issuer;
	private Integer pc;
	public Integer next_pc;
	public int cache_num;

	public InstructionCache(Issuer issuer, Integer pc, int cache_num)
	{
		instructions = new ArrayList<Instruction>();
		cache_line = new Instruction[4];
		num_instr_in_left_unissued = 0;
		this.issuer = null;
		this.pc = pc;
		this.next_pc = null;
		this.cache_num = cache_num;
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
		if (next_pc != null) {
			pc = next_pc;
			next_pc = null;
		}

		System.out.println("\n\n////////////////////////\n");
		System.out.println("In cache doCycle()");
		System.out.println("num_left_unissued = "+num_instr_in_left_unissued);
		System.out.println("pc is "+pc);
		int totalIssued = 0;
		if (instr_left_in_line() && pc_in_line(pc)) {
			System.out.println("instrs left in line including pc");
			totalIssued = issueInstructions();
		} else {
			System.out.println("need new cacheline");
			get_cache_line_with_pc(pc);
			totalIssued = issueInstructions();
		}
		System.out.println("instr sent to issuer this cycle: "+totalIssued);
		num_instr_in_left_unissued -= totalIssued;
		if (next_pc == null) {
			System.out.println("next pc was null");
			pc = pc + totalIssued;
		} else {
			System.out.println("next pc was set so need to grab that");
			System.out.println("next pc is: "+next_pc);
			pc = next_pc;
			next_pc = null;//will this cause any problems when killing since we already killed?
			//maybe we should just update the pc this cycle??
		}
	}

	private int issueInstructions()
	{
		//printCacheLine();
		int num_spots_in_issuer;
		if (cache_num == 1) {
			num_spots_in_issuer = issuer.getEmptySpots();
		} else {
			num_spots_in_issuer = issuer.getEmptySpots2();
		}
		System.out.println("\n\n^^^^^^^^^^^^^^^^^^^^\n");
		System.out.println("num_spots_in_issuer: "+num_spots_in_issuer);
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
				if (instr == null) {
					break;
				}
				if (issuer.enqueueInstruction(cloneInstruction(instr))) {
					System.out.println("\n\nInstruction Going To Issuer From Cache " + cache_num + ":\n"+instr);
					num_sent++;
				}
				
			}
			if(instr != null && issuing && (instr.opcode.equals("bne") || instr.opcode.equals("beq"))){
				return num_sent;
			}
		}

		return num_sent;
	}

	private void printCacheLine()
	{
		for (Instruction instr : cache_line)
		{
			System.out.println(instr);
		}
	}

	private boolean pc_in_line(int pc)
	{
		for(Instruction instr: cache_line){
			if( instr != null && instr.address == pc) {
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
	
		if ((pc-BASE_ADDR) % 4 == 0 ) {
			cache_line[0] = pc_instr;
			cache_line[1] = findInstruction(pc+1);
			cache_line[2] = findInstruction(pc+2);
			cache_line[3] = findInstruction(pc+3);
			num_instr_in_left_unissued = 4;
		} else if ((pc-BASE_ADDR) % 4 == 1) {
			cache_line[0] = findInstruction(pc-1);
			cache_line[1] = pc_instr;
			cache_line[2] = findInstruction(pc+1);
			cache_line[3] = findInstruction(pc+2);
			num_instr_in_left_unissued = 3;
		} else if ((pc-BASE_ADDR) % 4 == 2) {
			cache_line[0] = findInstruction(pc-2);
			cache_line[1] = findInstruction(pc-1);
			cache_line[2] = pc_instr;
			cache_line[3] = findInstruction(pc+1);
			num_instr_in_left_unissued = 2;
		} else if ((pc-BASE_ADDR) % 4 == 3) {
			cache_line[0] = findInstruction(pc-3);
			cache_line[1] = findInstruction(pc-2);
			cache_line[2] = findInstruction(pc-1);
			cache_line[3] = pc_instr;
			num_instr_in_left_unissued = 1;
		}
	}

	private void clear_cacheline()
	{
		for(int i = 0; i < 4; i++)
		{
			cache_line[i] = null;
		}
	}

	private Instruction cloneInstruction(Instruction orig_instr)
	{
		Instruction new_instr = new Instruction();
		
		new_instr.address = orig_instr.address;
		new_instr.opcode = orig_instr.opcode;
		new_instr.source_reg1 = orig_instr.source_reg1;
		new_instr.source_reg2 = orig_instr.source_reg2;
		new_instr.dest_reg = orig_instr.dest_reg;
		new_instr.dest_reg_original_str = orig_instr.dest_reg_original_str;
		new_instr.source_reg1_original_str = orig_instr.source_reg1_original_str;
		new_instr.source_reg2_original_str = orig_instr.source_reg2_original_str;
		new_instr.immediate = orig_instr.immediate;
		new_instr.target = orig_instr.target;
		new_instr.predicted_target = orig_instr.predicted_target;
		new_instr.threadNum = orig_instr.threadNum;
		return new_instr;
	}

}
