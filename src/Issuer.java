import java.util.*;
public class Issuer {

	private int size_limit;//how many instrs can be queued
	private int issue_limit;//how many insts can be issued in one cycle
	private Queue<Instruction> queue;

	private ArrayList<Unit> units;
	private ROB rob;
	private int total_issued;
	private TomRenameTable rename_table;
	private InstructionCache fetcher;
	private BTB btb;
	private RegisterFile regfile;

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

	public boolean enqueueInstruction(Instruction instr)
	{
		if (queue.size() >= size_limit) {
			return false;
		} else {
			queue.add(instr);
			if (instr.opcode.equals("beq") || instr.opcode.equals("bne")) {
				int addr = btb.predict(instr.address);
				fetcher.next_pc = addr;
			}
			return true;
		}
	}

	//issue up to issue_limit number of instr
	public void doCycle()
	{
		int num_issued = 0;
		while (queue.size() > 0 && num_issued < issue_limit && !rob.isFull() && !ReservationStationStatusTable.isReservationStationFull(getUnitName(queue.peek()))) {
			issueHeadInstr();	
			num_issued++;
		}
	}

	//check if the instr at the head of this queue is runnable (has a reservation station)
	private void issueHeadInstr() {
		Instruction head = queue.remove();
		head.issue_id = total_issued;
		total_issued++;
		UnitName unit_name = getUnitName(head);
		Integer rename_s1 = rename_table.getRename(head.source_reg1_original_str);
		Integer rename_s2 = rename_table.getRename(head.source_reg2_original_str);


		int rob_index = rob.enqueue(head);

		if (!(head.opcode.equals("fsd") || head.opcode.equals("sd"))) {
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
			head.dest_reg_renamed_str = Integer.toString(rename_table.getRename(head.dest_reg_original_str));
		}

		regfile.read(head);
		ReservationStationStatusTable.addInstructionToStation(unit_name, head);
	}

	public void killInstr() {
		queue.clear();
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
				return UnitName.MULT;
			case "fadd":
			case "fsub":
				return UnitName.FPADD;
			case "fmul":
				return UnitName.FPMULT;
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

	public int getNumEntries()
	{
		return queue.size();
	}



}
