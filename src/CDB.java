import java.util.LinkedList;
import java.util.Queue;

public class CDB
{
	public final int NUM_BUSES;
	private Queue<Instruction> bus = new LinkedList<Instruction>();

	public CDB(final int width)
	{
		NUM_BUSES = width;
	}

	/*
	 * \brief Push an instruction to the CDB
	 *
	 * \return true on success or false otherwise
	 */
	public boolean push(Instruction inst)
	{
		if (isFull())
			return false;

		return bus.add(inst);
	}

	// TODO mapping of register names
	public Double pullResult(String registerName)
	{
		for (Instruction inst : bus)
			if (registerNameMatch(inst, registerName))
				return inst.dest_reg_value;
	}

	public boolean isFull()	{ return bus.size() >= NUM_BUSES; }
	public void clear() { bus.clear(); }

	// TODO implement this
	public boolean registerNameMatch(Instruction inst, String registerName)
	{
		return true;
	}
}
