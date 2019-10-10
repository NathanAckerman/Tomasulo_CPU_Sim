import java.util.*;
public class Issuer {

	private int size_limit;//how many instrs can be queued
	private int issue_limit;//how many insts can be issued in one cycle
	private Queue<Instruction> queue;

	private ArrayList<Unit> units;
	private ROB rob;

	Issuer(int size, int issue_num, ArrayList<Unit> unit_arr, ROB rob)
	{
		queue = new LinkedList<Instruction>();
		size_limit = size;
		issue_limit = issue_num;
		units = unit_arr;
		this.rob = rob;
	}

	public boolean enqueueInstruction(Instruction instr)
	{
		if (queue.size() >= size_limit) {
			return false;
		} else {
			queue.add(instr);
			return true;
		}
	}

	//issue up to issue_limit number of instr
	public void doCycle()
	{
		int num_issued = 0;
		while (queue.size() >= 0 && num_issued < issue_limit && !rob.isFull() && !ReservationStationStatusTable.isReservationStationFull(getUnitName(queue.peek()))) {
			//issueInstr(queue.remove()) TODO need reference to the reservation stations	
			
			num_issued++;
		}
	}

	//check if the instr at the head of this queue is runnable (has a reservation station)
	private void issueHeadInstr() {
		Instruction head = queue.remove();
		UnitName unit_name = getUnitName(head);
		ReservationStationStatusTable.addInstructionToStation(unit_name, head);
		rob.enqueue(head);
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
			case "lw":
			case "sw":
			case "fld":
			case "fsd":
			      return UnitName.LOADSTORE;
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

}
