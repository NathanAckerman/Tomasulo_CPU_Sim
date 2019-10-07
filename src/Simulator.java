import java.util.HashMap;
import java.lang.IllegalArgumentException;

public class Simulator
{
	private int cycle;

	// TODO parameterize these
	private CDB cdb = new CDB(4);
	private ROB rob = new ROB(16);
	private WB wb = new WB(1);

	private ArrayList<Unit> units = new ArrayList<Unit>;
	units[0] = new IntUnit();
	units[1] = new MultUnit();
	units[2] = new LoadStoreUnit();
	units[3] = new FPAddUnit();
	units[4] = new FPMultUnit();
	units[5] = new FPDivUnit();
	units[6] = new BranchUnit();

	// TODO parameterize this
	private Issuer issuer = new Issuer(8, 4, units);

	private int pc;

	public Simulator()
	{
		this.cycle = 0;
	}

	private void run_cycle()
	{
		this.cycle = this.cycle + 1;
		// TODO prioritization policy, multiple instructions
		// TODO stall if necessary
		Instruction inst = rob.peek();
		if (inst && !cdb.isFull()) {
			cdb.push(inst);
			rob.dequeue();
		}

		// can multiple instructions write back at the same time?
		// TODO check push/enqueue ret val, support multiple insts
		// TODO stall if necessary
		// TODO pull from units instead of wb?
		inst = wb.peek();
		if (inst && !cdb.isFull() && !rob.isFull()) {
			cdb.push(inst);
			rob.enqueue(inst);
			wb.dequeue();
		}

		// TODO get finished instruction from each unit
		for (Unit unit : units)
			unit.doCycle();

		// TODO issue instruction

		// TODO BTB

		// - needs to be primed for the first cycle of the simluation

		cdb.clear();
	}

	/* Simulates Part 1 */
	public void run(InstructionCache instruction_cache, Memory memory)
	{
		// TODO fetch instruction from instruction_cache
		// TODO deal with PC
		run_cycle();
	}

	/* Simulates Part 2 */
	public void run_smt(InstructionCache instruction_cache_1, InstructionCache instruction_cache_2, Memory memory)
	{
		run_cycle();
	}

	public static void main(String[] args) 
	{
		if (args.length == 0) {
			throw new IllegalArgumentException("No test file passed to the simulator!");
		} else if (args.length == 1) {
			String filepath = args[0];
			InstructionCache instruction_cache = new InstructionCache();
			Memory memory = new Memory();

			Parser.parseFile(filepath, instruction_cache, memory);
			Simulator simulator = new Simulator();
			simulator.run(instruction_cache, memory);

			System.out.println(instruction_cache.toString());
			// TODO: Print Registers
			System.out.println(memory.toString());
			// TODO: Print cycle count and other information

		} else if (args.length == 2) {
			// Part2: SMT
			
			// Load input file paths
			String filepath_1 = args[0];
			String filepath_2 = args[1];

			// Create new memory object (shared between both input programs)
			Memory memory = new Memory();

			// Create new instruction cache object for filepath_1
			InstructionCache instruction_cache_1 = new InstructionCache();

			// Parse file_1 and fill the instruction cache and memory
			Parser.parseFile(filepath_1, instruction_cache_1, memory);

			// Create new instruction cache object for filepath_2
			InstructionCache instruction_cache_2 = new InstructionCache();

			// Parse file_2 and fill the instruction cache and memory
			Parser.parseFile(filepath_2, instruction_cache_2, memory);

			// Print Instruction Cache 1
			System.out.println(instruction_cache_1.toString());

			// Print Instruction Cache 2
			System.out.println(instruction_cache_2.toString());

			// Print Registers
			
			// Print Data Memory
			System.out.println(memory.toString());
			

			// Print cycle count and other information
			
		}

		System.out.println("Done running simulator!");
	}
}
