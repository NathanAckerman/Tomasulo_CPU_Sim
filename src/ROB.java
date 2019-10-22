import java.util.ArrayList;

public class ROB
{
	private final int ROB_SIZE;
	private int cur_size;
	// The queue is inclusive between front_i and back_i, under mod ROB_SIZE
	private int front_i;
	private int back_i;
	private TomRenameTable rename_table;
	private InstructionKiller instr_killer;

	private Instruction[] queue;
	public int committedCount = 0;

	public ROB(final int size, TomRenameTable table, InstructionKiller the_instr_killer)
	{
		ROB_SIZE = size;
		cur_size = 0;
		front_i = 0;
		back_i = 0;
		rename_table = table;
		instr_killer = the_instr_killer;
		queue = new Instruction[16];
	}

	public int enqueue(Instruction inst)
	{
		if (isFull()) {
			return -1;
		}
		queue[back_i] = inst;
		if(!(inst.opcode.equals("sd") || inst.opcode.equals("fsd"))) {
			rename_table.setRename(inst.dest_reg_original_str, back_i, inst);
		}
		int index_in_rob = back_i;
		back_i = incr(back_i);
		cur_size += 1;
		return index_in_rob;
	}

	public ArrayList<Instruction> dequeue(int count)
	{
		ArrayList<Instruction> arr = new ArrayList<Instruction>();
		for (int i = 0; i < count; i++) {
			Instruction inst = dequeue();
			if (inst == null)
				break;
			arr.add(inst);
		}
		return arr;
	}

	public Instruction dequeue()
	{
		if (cur_size == 0)
			return null;

		Instruction inst = queue[front_i];
		if (!inst.completed)
			return null;

		if(!(inst.opcode.equals("sd") || inst.opcode.equals("fsd"))) {
			rename_table.removeRename(inst.dest_reg_original_str, inst);
		}

		queue[front_i] = null;
		front_i = incr(front_i);
		cur_size -= 1;

		committedCount++;
		if (inst.opcode.equals("bne") || inst.opcode.equals("beq")) {
			if(inst.threadNum == 1) {
				instr_killer.sim.issuer.branch_in_pipeline = false;
			} else {
				instr_killer.sim.issuer.branch_in_pipeline2 = false;
			}
		}
		return inst;
	}

	public int queryReadyInstructions()
	{
		int a = 0;
		for (int i = front_i; a < cur_size; i = incr(i)) {
			a++;
		}
		int count = 0;
		int c = 0;
		for (int i = front_i; c < cur_size; i = incr(i)) {
			if(queue[i] == null){
				continue;
			}
			if (queue[i].completed){
				count++;
			} else {
				break;
			}
			c++;
		}
		return count;
	}

	public void killInstructionsAfter(Instruction instr)
	{
		int num_killed = 0;
		boolean killing = false;
		boolean inst_is_branch = false;
		int c = 0;
		for (int i = front_i; c < cur_size; i = incr(i) ) {
			if (queue[i] != null) {
				if (queue[i] == instr) {
					killing = true;
					if(instr.threadNum == 1) {
						instr_killer.sim.issuer.killInstr();
					} else {
						instr_killer.sim.issuer.killInstr2();
					}
					inst_is_branch = true;
				}
				if (killing && !inst_is_branch) {
					instr_killer.killInstructionAnywhere(queue[i]);
					queue[i] = null;
					num_killed++;
					back_i = decr(back_i);
				}else if(killing){
					inst_is_branch = false;
				}
				c++;
			}
		}
		cur_size -= num_killed;
	}

	public boolean isFull() { return cur_size == ROB_SIZE; }
	public int getNumEntries() { return cur_size; }

	private int decr(int i) {
		if( i == 0) {
			return ROB_SIZE - 1;
		} else {
			return (i - 1);
		}
	}

	private int incr(int i) { return (i + 1) % ROB_SIZE; }
}
