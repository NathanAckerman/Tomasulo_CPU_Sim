public class BTB {
	
	BTBEntry table[];


	BTB()
	{
		this.table = new BTBEntry[16];
	}

	//return the addr that the next PC should be
	public int predict(int address)
	{
		int index = getBitsForTable(address);
		if (table[index] != null && table[index].address == address) {
			return table[index].predicted_pc;
		}

		return address+1;
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

	BTBEntry(int address, int predicted_pc, boolean predict_taken) {
		this.address = address;
		this.predicted_pc = predicted_pc;
	}
}
