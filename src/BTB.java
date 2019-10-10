public class BTB {
	
	BTBEntry table[];


	BTB(int size)
	{
		this.table = new BTBEntry[size];
	}

	//return the addr that the next PC should be
	public int predict(int address)
	{
		int index = getBitsForTable(address);
		if (table[index] != null && table[index].address == address) {
			return table[index].predicted_pc;
		}

		return 0;
	}

	public void updatePrediction(int address, int predicted_pc, boolean predict_taken)
	{
		int index = getBitsForTable(address);
		this.table[index] = new BTBEntry(address, predicted_pc, predict_taken);
	}

	private int getBitsForTable(int address)
	{
		return (address & 0xF0) >> 4;
	}

	
}

class BTBEntry {
	int address;
	int predicted_pc;
	boolean predict_taken;// is this even necessary?

	BTBEntry(int address, int predicted_pc, boolean predict_taken) {
		this.address = address;
		this.predicted_pc = predicted_pc;
		this.predict_taken = true; //TODO do we default to true or false to start?
	}
}
