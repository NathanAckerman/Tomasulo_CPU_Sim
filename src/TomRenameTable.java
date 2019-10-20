import java.util.HashMap;
import java.util.LinkedList;
public class TomRenameTable {
	static HashMap<String, LinkedList<RenameEntry>> hm;


	TomRenameTable() 
	{
		hm = new HashMap<String, LinkedList<RenameEntry>>();
	}

	public Integer getRename(String reg)
	{
		if(reg == null) {
			return null;
		}

		LinkedList<RenameEntry> ll = hm.get(reg);
		if (ll == null) {
			return null;
		}
		return ll.getLast().rob_index;
	}

	public void setRename(String reg, int rob_index, Instruction instr)
	{
		if(instr == null) {
			return;
		}

		LinkedList<RenameEntry> ll = hm.get(reg);
		if (ll == null) {
			ll = new LinkedList<RenameEntry>();
			ll.add(new RenameEntry(rob_index, instr));
			hm.put(reg, ll);
		} else {
			ll.add(new RenameEntry(rob_index, instr));
		}
	}

	public void removeRename(String reg, Instruction instr)
	{
		LinkedList<RenameEntry> ll = hm.get(reg);
		RenameEntry el_to_remove = null;
		for (RenameEntry rn : ll) {
			if (rn.instr == instr) {
				el_to_remove = rn;
			}
		}
		ll.remove(el_to_remove);
		if (ll.size() == 0) {
			hm.remove(reg);
		}
	}

}

class RenameEntry {
	public Integer rob_index;
	public Instruction instr;

	RenameEntry(int rob_index, Instruction instr)
	{
		this.rob_index = rob_index;
		this.instr = instr;
	}
}
