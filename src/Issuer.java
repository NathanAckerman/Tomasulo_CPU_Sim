import java.util.*;
public class Issuer {

	private int size_limit;//how many instrs can be queued
	private int issue_limit;//how many insts can be issued in one cycle
	private Queue<Instruction> queue;
	private Queue<Instruction> queue2;

	private ArrayList<Unit> units;
	private ROB rob;
	private ROB rob2;
	private int total_issued;
	private TomRenameTable rename_table;
	private TomRenameTable rename_table2;
	private InstructionCache fetcher;
	private InstructionCache fetcher2;
	private BTB btb;
	private BTB btb2;
	private RegisterFile regfile;
	private RegisterFile regfile2;
	public boolean branch_in_pipeline = false;
	public boolean branch_in_pipeline2 = false;
	public Simulator sim;

	Issuer(int size, int issue_num, ArrayList<Unit> unit_arr, ROB rob, TomRenameTable the_rename_table, InstructionCache the_fetcher, BTB the_btb, RegisterFile regfile)
	{
		queue = new LinkedList<Instruction>();
		size_limit = size;
		issue_limit = issue_num;
		units = unit_arr;
		this.rob = rob;
		total_issued = 0;
		rename_table = the_rename_table;
		fetcher = the_fetcher;
		btb = the_btb;
		this.regfile = regfile;
	}

	Issuer(Simulator the_sim, int size, int issue_num, ArrayList<Unit> unit_arr, ROB rob, ROB rob2, TomRenameTable the_rename_table, TomRenameTable the_rename_table2, InstructionCache the_fetcher, InstructionCache the_fetcher2, BTB the_btb, BTB the_btb2, RegisterFile regfile, RegisterFile regfile2)
	{
		sim = the_sim;
		queue = new LinkedList<Instruction>();
		queue2 = new LinkedList<Instruction>();
		size_limit = size;
		issue_limit = issue_num;
		units = unit_arr;
		this.rob = rob;
		this.rob2 = rob2;
		total_issued = 0;
		rename_table = the_rename_table;
		rename_table2 = the_rename_table2;
		fetcher = the_fetcher;
		fetcher2 = the_fetcher2;
		btb = the_btb;
		btb2 = the_btb2;
		this.regfile = regfile;
		this.regfile2 = regfile2;
	}

	public boolean enqueueInstruction(Instruction instr)
	{
		if (instr.threadNum == 1 && queue.size() >= size_limit) {
			return false;
		} else if (instr.threadNum == 2 && queue2.size() >= size_limit) {
			return false;
		} else {
			if(instr.threadNum == 1) {
				queue.add(instr);
				if (instr.opcode.equals("beq") || instr.opcode.equals("bne")) {
					int addr = btb.predict(instr.address);
					instr.predicted_target = addr;
					fetcher.next_pc = addr;
				}
			} else {
				queue2.add(instr);
				if (instr.opcode.equals("beq") || instr.opcode.equals("bne")) {
					int addr = btb2.predict(instr.address);
					instr.predicted_target = addr;
					fetcher2.next_pc = addr;
				}
			}
			return true;
		}
	}

	public int doCycle(int thread_issue_cap)
	{
		int num_issued = 0;
		while (queue.size() > 0 && num_issued < thread_issue_cap && num_issued < issue_limit && !rob.isFull() && !ReservationStationStatusTable.isReservationStationFull(getUnitName(queue.peek()))) {
			Instruction head = queue.peek();
			if(head != null && (head.opcode.equals("bne") || head.opcode.equals("beq")) && branch_in_pipeline) {
				return num_issued;
			}
			issueHeadInstr();	
			num_issued++;
		}
		return num_issued;
	}

	public int doCycle2(int thread_issue_cap)
	{
		int num_issued = 0;
		while (queue2.size() > 0 && num_issued < thread_issue_cap && num_issued < issue_limit && !rob2.isFull() && !ReservationStationStatusTable.isReservationStationFull(getUnitName(queue2.peek()))) {
			Instruction head = queue2.peek();
			if(head != null && (head.opcode.equals("bne") || head.opcode.equals("beq")) && branch_in_pipeline) {
				return num_issued;
			}
			issueHeadInstr2();	
			num_issued++;
		}
		return num_issued;
	}

	public void doCycleSMT()
	{
		if (sim.cycle % 2 == 0) {//prioritize first thread
			int num_issued = doCycle(issue_limit);
			if (num_issued < issue_limit) {
				int issues_remaining = issue_limit - num_issued;
				doCycle2(issues_remaining);
			}
		} else {//prioritize second thread
			int num_issued = doCycle2(issue_limit);
			if (num_issued < issue_limit) {
				int issues_remaining = issue_limit - num_issued;
				doCycle(issues_remaining);
			}
		}


		int q_size = queue.size();
		int q2_size = queue2.size();
		if (q_size > q2_size) {
			fetcher2.doCycle();
		} else if (q_size == q2_size) {
			if (sim.cycle % 2 == 0) {
				fetcher2.doCycle();
			} else {
				fetcher.doCycle();
			}
		} else {
			fetcher.doCycle();
		}
	}

	//check if the instr at the head of this queue is runnable (has a reservation station)
	private void issueHeadInstr() {
		Instruction head = queue.remove();

		if(head != null && head.address == 1010){
			System.out.println("Here");
		}

		if (head.threadNum != 1) {
			System.out.println("Uhh Ohh an instr got into the wrong issuer (1)");
			System.exit(1);
		}
		head.issue_id = total_issued;
		total_issued++;
		UnitName unit_name = getUnitName(head);
		Integer rename_s1 = rename_table.getRename(head.source_reg1_original_str);
		Integer rename_s2 = rename_table.getRename(head.source_reg2_original_str);

		if(head != null && head.address == 1010){
			System.out.println("Here");
		}
		int rob_index = rob.enqueue(head);

		if (!(head.opcode.equals("fsd") || head.opcode.equals("sd") || head.opcode.equals("bne") || head.opcode.equals("beq"))) {
			head.dest_reg_renamed_str = Integer.toString(rob_index);
		}

		if (rename_s1 == null) {
			head.source_reg1_renamed_str = null;
		} else {
			head.source_reg1_renamed_str = Integer.toString(rename_s1);
		}
		if (rename_s2 == null) {
			head.source_reg2_renamed_str = null;
		} else {
			head.source_reg2_renamed_str = Integer.toString(rename_s2);
		}

		if (head.opcode.equals("fsd") || head.opcode.equals("sd")) {
			Integer rename_int = rename_table.getRename(head.dest_reg_original_str);
			if (rename_int != null) {
				head.dest_reg_renamed_str = Integer.toString(rename_int);
			}

		}

		regfile.read(head);
		boolean success = ReservationStationStatusTable.addInstructionToStation(unit_name, head);
		if(!success){
			System.out.println("Error");
			System.exit(1);
		}
		if(head.opcode.equals("beq") || head.opcode.equals("bne")) {
			branch_in_pipeline = true;
		}
	}

	//check if the instr at the head of this queue is runnable (has a reservation station)
	private void issueHeadInstr2() {
		Instruction head = queue2.remove();
		if(head != null && head.address == 1010){
			System.out.println("Here");
		}
		if (head.threadNum != 2) {
			System.out.println("Uhh Ohh an instr got into the wrong issuer (2)");
			System.exit(1);
		}
		head.issue_id = total_issued;
		total_issued++;
		UnitName unit_name = getUnitName(head);
		Integer rename_s1 = rename_table2.getRename(head.source_reg1_original_str);
		Integer rename_s2 = rename_table2.getRename(head.source_reg2_original_str);

		int rob_index = rob2.enqueue(head);

		if (!(head.opcode.equals("fsd") || head.opcode.equals("sd") || head.opcode.equals("bne") || head.opcode.equals("beq"))) {
			head.dest_reg_renamed_str = Integer.toString(rob_index);
		}

		if (rename_s1 == null) {
			head.source_reg1_renamed_str = null;
		} else {
			head.source_reg1_renamed_str = Integer.toString(rename_s1);
		}
		if (rename_s2 == null) {
			head.source_reg2_renamed_str = null;
		} else {
			head.source_reg2_renamed_str = Integer.toString(rename_s2);
		}

		if (head.opcode.equals("fsd") || head.opcode.equals("sd")) {
			Integer rename_int = rename_table2.getRename(head.dest_reg_original_str);
			if (rename_int != null) {
				head.dest_reg_renamed_str = Integer.toString(rename_int);
			}

		}

		regfile2.read(head);
		boolean success = ReservationStationStatusTable.addInstructionToStation(unit_name, head);
		if(!success){
			System.out.println("Error2");
			System.exit(1);
		}
		if(head.opcode.equals("beq") || head.opcode.equals("bne")) {
			branch_in_pipeline2 = true;
		}
	}
	public void killInstr() {
		queue.clear();
	}

	public void killInstr2() {
		queue2.clear();
	}

	private UnitName getUnitName(Instruction instr)
	{
		switch (instr.opcode) {
			case "and":
			case "andi":
			case "or":
			case "ori":
			case "slt":
			case "slti":
			case "add":
			case "addi":
			case "sub":
			case "subi":
				return UnitName.INT;
			case "mul":
				return UnitName.MUL;
			case "fadd":
			case "fsub":
				return UnitName.FPADD;
			case "fmul":
				return UnitName.FPMUL;
			case "fdiv":
				return UnitName.FPDIV;
			case "beq":
			case "bne":
				return UnitName.BU;
			case "ld":
			case "fld":
			      return UnitName.LOAD;
			case "sd":
			case "fsd":
			      return UnitName.STORE;
		}
		System.out.println("unit name translation failed. should never see this.");
		System.exit(1);
		return UnitName.FPADD;
	}


	//used externally to decide how many more instr can be put in here
	public int getEmptySpots()
	{
		return size_limit - queue.size();
	}

	public int getEmptySpots2()
	{
		return size_limit - queue2.size();
	}

	public int getNumEntries()
	{
		int total = queue.size();
		if (queue2 != null) {
			total += queue2.size();
		}
		return total;
	}



}
