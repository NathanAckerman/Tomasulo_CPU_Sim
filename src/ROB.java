import java.util.ArrayList;

public class ROB
{
	private final int ROB_SIZE;
	private int cur_size;
	// The queue is inclusive between front_i and back_i, under mod ROB_SIZE
	private int front_i;
	private int back_i;
	private TomRenameTable rename_table;

	private ArrayList<Instruction> queue = new ArrayList<Instruction>();

	public ROB(final int size, TomRenameTable table)
	{
		ROB_SIZE = size;
		cur_size = 0;
		front_i = 0;
		back_i = ROB_SIZE - 1;
		rename_table = table;
	}

	public boolean enqueue(Instruction inst)
	{
		if (isFull())
			return false;

		back_i = incr(back_i);
		queue[back_i] = inst;
		rename_table.put(instr.dest_reg, back_i);
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
		rename_table.remove(inst.dest_reg);
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
	 * \param[in] addr1 Address of first instruction to kill
	 * \param[in] addr2 Address of last instruction to kill
	 *
	 * Removes instructions between addr1 and addr2, inclusive.
	 * If addr1 is not found, then nothing is removed. If addr2 is not found
	 * but addr1 is, then all instructions will be removed.
	 */
	public void killInstructionsBetween(int addr1, int addr2)
	{
		int addr1_i = -1;
		int addr2_i = -1;

		for (int i = 0; i < ROB_SIZE; i++) {
			if (queue[i].address == addr1)
				addr1_i = i;
			if (queue[i].address == addr2)
				addr2_i = i;
		}

		if (addr1_i == -1)
			return;

		int num_killed = 0;
		for (int i = addr1_i; i != (addr2_i == -1 ? back_i : addr2_i);
		     i = incr(i)) {
			queue[i] = null;
			num_killed++;
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
