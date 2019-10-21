import java.util.*;
import java.lang.IllegalArgumentException;

public class Simulator
{
	public int cycle;
	private Integer pc = 1000;
	private Integer pc2 = 1000;

	public ArrayList<Unit> units = new ArrayList<Unit>();

	// TODO parameterize these
	public TomRenameTable rename_table = new TomRenameTable();
	public TomRenameTable rename_table2 = new TomRenameTable();
	public Memory mem = new Memory();
	public RegisterFile rf = new RegisterFile(mem, rename_table);
	public RegisterFile rf2 = new RegisterFile(mem, rename_table);
	private CDB cdb = new CDB(4, this);
	private InstructionKiller instr_killer = new InstructionKiller(this);
	public ROB rob = new ROB(16, rename_table, instr_killer);
	public ROB rob2 = new ROB(16, rename_table, instr_killer);
	public WB wb = new WB(1, units);
	private BTB btb = new BTB(); 
	private BTB btb2 = new BTB(); 
	private InstructionEvaluator instr_eval;
	private InstructionCache instruction_cache;
	private InstructionCache instruction_cache2;

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


		for (Unit unit : units)
			unit.doCycle();

		issuer.doCycle(4);
		instruction_cache.doCycle();

		// - needs to be primed for the first cycle of the simluation
		cdb.clear();

		this.cycle = this.cycle + 1;
	}

	/* Simulates Part 1 */
	public void run()
	{
		//need to manually run 2 cycles so that first instructions get into the issuer on cycle 2
		run_cycle();
		run_cycle();
		while(!SimulationDone()) {
			run_cycle();
		}

		this.rf.printRegisters();
		System.out.println("\n\n");
		System.out.println("Sim Ending at cycle: "+this.cycle);

	}

	public boolean SimulationDone()
	{
		System.out.println("Entries in ROB: "+this.rob.getNumEntries());
		System.out.println("Entries in Issuer: "+this.issuer.getNumEntries());
		return this.rob.getNumEntries() == 0 && this.issuer.getNumEntries() == 0;
	}

	public boolean SimulationDoneSMT()
	{
		System.out.println("Entries in ROB: "+this.rob.getNumEntries());
		System.out.println("Entries in ROB2: "+this.rob2.getNumEntries());
		System.out.println("Entries in Issuer: "+this.issuer.getNumEntries());
		return this.rob.getNumEntries() == 0 && this.rob2.getNumEntries() == 0 && this.issuer.getNumEntries() == 0;
	}

	/* Simulates Part 2 */
	public void run_smt()
	{
		run_cycle_smt();
		run_cycle_smt();

		while(!SimulationDoneSMT()){
			run_cycle_smt();
		}

		System.out.println("Register file 1");
		this.rf.printRegisters();
		System.out.println("-------------------");
		System.out.println("Register file 2");
		this.rf2.printRegisters();
		System.out.println("-------------------");

		System.out.println("\n\n");
		System.out.println("Sim Ending at cycle: "+this.cycle);
	}

	public void run_cycle_smt()
	{
		System.out.println("\n\n*******************\n\n");
		System.out.println("Cycle: "+this.cycle);
		System.out.println("\n\n*******************\n\n");

		// TODO parameterize this
		// this also does wb and rob
		int min_rob_bw = 2;
		cdb.doCycle(min_rob_bw);


		for (Unit unit : units)
			unit.doCycle();

		issuer.doCycleSMT();

		cdb.clear();

		this.cycle = this.cycle + 1;
	}

	public static void main(String[] args) 
	{
		if (args.length == 0) {
			throw new IllegalArgumentException("No test file passed to the simulator!");
		} else if (args.length == 1) {
			String filepath = args[0];

			Simulator simulator = new Simulator();

			InstructionCache instruction_cache = new InstructionCache(simulator.issuer, simulator.pc, 1);
			simulator.issuer =  new Issuer(8, 4, simulator.units, simulator.rob, simulator.rename_table, instruction_cache, simulator.btb, simulator.rf);
			simulator.instruction_cache = instruction_cache;
			simulator.instruction_cache.issuer = simulator.issuer;
			simulator.instr_eval = new InstructionEvaluator(simulator.rob, simulator.rob2, simulator.btb, simulator.btb2, simulator.mem, instruction_cache, null);
			Memory memory = simulator.mem;

			Parser.parseFile(filepath, instruction_cache, memory, 1);
			System.out.println(instruction_cache);
			simulator.run();

			//System.out.println(instruction_cache.toString());
			System.out.println(memory.toString());
			System.out.println("Total committed instructions: " + simulator.rob.committedCount);
			// TODO: Print cycle count and other information

		} else if (args.length == 2) {
			// Part2: SMT
			
			// Load input file paths
			String filepath_1 = args[0];
			String filepath_2 = args[1];

			Simulator simulator = new Simulator();


			// Create new memory object (shared between both input programs)
			Memory memory = simulator.mem;

			// Create new instruction cache object for filepath_1
			InstructionCache instruction_cache_1 = new InstructionCache(simulator.issuer, simulator.pc, 1);

			// Create new instruction cache object for filepath_2
			InstructionCache instruction_cache_2 = new InstructionCache(simulator.issuer, simulator.pc, 2);

			// Parse file_1 and fill the instruction cache and memory
			Parser.parseFile(filepath_1, instruction_cache_1, memory, 1);

			// Parse file_2 and fill the instruction cache and memory
			Parser.parseFile(filepath_2, instruction_cache_2, memory, 2);

			simulator.issuer =  new Issuer(simulator, 8, 4, simulator.units, simulator.rob, simulator.rob2, simulator.rename_table, simulator.rename_table2, instruction_cache_1, instruction_cache_2, simulator.btb, simulator.btb2, simulator.rf, simulator.rf2);
			simulator.instruction_cache = instruction_cache_1;
			simulator.instruction_cache2 = instruction_cache_2;
			simulator.instruction_cache.issuer = simulator.issuer;
			simulator.instruction_cache2.issuer = simulator.issuer;

			simulator.instr_eval = new InstructionEvaluator(simulator.rob, simulator.rob2, simulator.btb, simulator.btb2, simulator.mem, instruction_cache_1, instruction_cache_2);

			// Print Registers

			simulator.run_smt();

			// Print Data Memory
			System.out.println(memory.toString());
			

			// Print cycle count and other information
			
		}

		System.out.println("Done running simulator!");
	}
}
