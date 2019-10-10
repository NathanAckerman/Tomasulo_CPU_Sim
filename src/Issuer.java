import java.util.*;
public class Issuer {

	private int size_limit;//how many instrs can be queued
	private int issue_limit;//how many insts can be issued in one cycle
	private Queue<Instruction> queue;

	private ArrayList<Unit> units;

	Issuer(int size, int issue_num, ArrayList<Unit> unit_arr)
	{
		queue = new LinkedList<Instruction>();
		size_limit = size;
		issue_limit = issue_num;
		units = unit_arr;
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
		while (queue.size() >= 0 && num_issued < issue_limit && headInstrIsIssuable()) {
			//issueInstr(queue.remove()) TODO need reference to the reservation stations	
			num_issued++;
		}
	}

	//check if the instr at the head of this queue is runnable (has a reservation station)
	private boolean headInstrIsIssuable() {
		Instruction head = queue.peek();
		//TODO need to check for space in reservation unit for the instr
		return true;
		/*
		if (resUnits.canTake(head)) {
			return true;
		} else {
			return false;
		}
		*/

	}

	//used externally to decide how many more instr can be put in here
	public int getEmptySpots()
	{
		return size_limit - queue.size();
	}

}
