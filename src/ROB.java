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

	private ArrayList<Instruction> queue = new ArrayList<Instruction>();

	public ROB(final int size, TomRenameTable table, InstructionKiller the_instr_killer)
	{
		ROB_SIZE = size;
		cur_size = 0;
		front_i = 0;
		back_i = ROB_SIZE - 1;
		rename_table = table;
		instr_killer = the_instr_killer;
	}

	public boolean enqueue(Instruction inst)
	{
		if (isFull())
			return false;

		back_i = incr(back_i);
		queue[back_i] = inst;
		rename_table.put(inst.dest_reg_str, back_i);
		cur_size += 1;
		return true;
	}

	public ArrayList<Instruction> dequeue(int count)
	{
		ArrayList<Instruction> arr;
		for (int i = 0; i < count; i++)
			arr[i] = dequeue();

		return arr;
	}

	public Instruction dequeue()
	{
		if (cur_size == 0)
			return null;

		Instruction inst = queue[front_i];
		if (!inst.dest_reg_value)
			return null;
		// will this work?
		// it needs to be cleared to avoid false positives
		// in instruction killing
		rename_table.remove(inst.dest_reg_str);
		queue[front_i] = null;
		front_i = decr(front_i);
		cur_size -= 1;
		return inst;
	}

	public ArrayList<Instruction> peek(int count)
	{
		ArrayList<Instruction> arr;
		for (int i = 0; i < count; i++)
			arr[i] = peek();

		return arr;
	}

	public Instruction peek()
	{
		if (cur_size == 0)
			return null;

		Instruction inst = queue[front_i];
		if (!inst.dest_reg_value)
			return null;
		return inst;
	}

	/*
	 * \brief Kill instructions between two addresses
	 * \param[in] i1 Index of first instruction to kill
	 * \param[in] i2 Index of last instruction to kill
	 *
	 * Removes instructions between i1 and i2, inclusive.
	 * If i1 is -1, then nothing is removed. If i2 is non-negative
	 * but i2 is -1, then all instructions from i1 until the end of the
	 * queue will be killed. If either i1 or i2 are greater than the size
	 * of the queue, then nothing is removed.
	 */
	public void killInstructionsBetween(int i1, int i2)
	{
		if (i1 >= ROB_SIZE || i2 >= ROB_SIZE)
			return;

		if (i1 == -1)
			return;

		int num_killed = 0;
		for (int i = i1; i != (i2 == -1 ? back_i : i2); i = incr(i)) {
			if (queue[i] != null) {
				instr_killer.killInstructionAnywhere(queue[i]);
			}
			queue[i] = null;
			num_killed++;
		}
		if (queue[i] != null) {
			instr_killer.killInstructionAnywhere(queue[i]);
		}
		queue[i] = null;
		num_killed++;

		cur_size -= num_killed;
		back_i = incr(i);
	}

	public boolean isFull() { return cur_size == ROB_SIZE; }

	private int decr(int i) { return (i - 1) % ROB_SIZE; }
	private int incr(int i) { return (i + 1) % ROB_SIZE; }
}
