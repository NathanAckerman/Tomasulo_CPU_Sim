import java.util.*;
import java.lang.IllegalArgumentException;

public class Simulator
{
	private int cycle;
	private Integer pc = 1000;

	public ArrayList<Unit> units = new ArrayList<Unit>();

	// TODO parameterize these
	public TomRenameTable rename_table = new TomRenameTable();
	public Memory mem = new Memory();
	public RegisterFile rf = new RegisterFile(mem, rename_table);
	private CDB cdb = new CDB(4, this);
	private InstructionKiller instr_killer = new InstructionKiller(this);
	public ROB rob = new ROB(16, rename_table, instr_killer);
	public WB wb = new WB(1, units);
	private BTB btb = new BTB(); 
	private InstructionEvaluator instr_eval;
	private InstructionCache instruction_cache;

	public Issuer issuer;

	public Simulator()
	{
		this.cycle = 0;
		this.units.add(new Unit(1, 4, UnitName.INT));
		this.units.add(new Unit(4, 2, UnitName.MUL));
		this.units.add(new Unit(1, 2, UnitName.LOAD));
		this.units.add(new Unit(1, 2, UnitName.STORE));
		this.units.add(new Unit(3, 3, UnitName.FPADD));
		this.units.add(new Unit(4, 4, UnitName.FPMUL));
		this.units.add(new Unit(8, 2, UnitName.FPDIV));
		this.units.add(new Unit(1, 2, UnitName.BU));
	}

	private void run_cycle()
	{
		System.out.println("\n\n*******************\n\n");
		System.out.println("Cycle: "+this.cycle);
		System.out.println("\n\n*******************\n\n");
		
		// TODO parameterize this
		// this also does wb and rob
		int min_rob_bw = 2;
		cdb.doCycle(min_rob_bw);

		// TODO move items from wb to cdb

		// TODO get finished instruction from each unit
		for (Unit unit : units)
			unit.doCycle();

		issuer.doCycle();
		instruction_cache.doCycle();

		// - needs to be primed for the first cycle of the simluation

		cdb.clear();

		this.cycle = this.cycle + 1;
	}

	/* Simulates Part 1 */
	public void run(InstructionCache instruction_cache, Memory memory)
	{
		// TODO fetch instruction from instruction_cache
		// TODO deal with PC

		run_cycle(); run_cycle();


		while(!SimulationDone()) {
			run_cycle();
		}


		System.out.println("\n\n");
		System.out.println("Sim Ending at cycle: "+this.cycle);

		this.rf.printRegisters();

	}

	public boolean SimulationDone()
	{
		System.out.println("Entries in ROB: "+this.rob.getNumEntries());
		System.out.println("Entries in Issuer: "+this.issuer.getNumEntries());
		return this.rob.getNumEntries() == 0 && this.issuer.getNumEntries() == 0;
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

			Simulator simulator = new Simulator();

			InstructionCache instruction_cache = new InstructionCache(simulator.issuer, simulator.pc);
			simulator.issuer =  new Issuer(8, 4, simulator.units, simulator.rob, simulator.rename_table, instruction_cache, simulator.btb, simulator.rf);
			simulator.instruction_cache = instruction_cache;
			simulator.instruction_cache.issuer = simulator.issuer;
			simulator.instr_eval = new InstructionEvaluator(simulator.rob, simulator.btb, simulator.mem, instruction_cache);
			Memory memory = simulator.mem;

			Parser.parseFile(filepath, instruction_cache, memory);
			System.out.println(instruction_cache);
			simulator.run(instruction_cache, memory);

			//System.out.println(instruction_cache.toString());
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
			//InstructionCache instruction_cache_1 = new InstructionCache();

			// Parse file_1 and fill the instruction cache and memory
			//Parser.parseFile(filepath_1, instruction_cache_1, memory);

			// Create new instruction cache object for filepath_2
			//InstructionCache instruction_cache_2 = new InstructionCache();

			// Parse file_2 and fill the instruction cache and memory
			//Parser.parseFile(filepath_2, instruction_cache_2, memory);

			// Print Instruction Cache 1
			//System.out.println(instruction_cache_1.toString());

			// Print Instruction Cache 2
			//System.out.println(instruction_cache_2.toString());

			// Print Registers
			
			// Print Data Memory
			System.out.println(memory.toString());
			

			// Print cycle count and other information
			
		}

		System.out.println("Done running simulator!");
	}
}
