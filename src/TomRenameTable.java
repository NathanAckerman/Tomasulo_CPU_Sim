import java.util.HashMap;
public class TomRenameTable {
	static HashMap<String, Integer> hm;

	TomRenameTable() 
	{
		hm = new HashMap<String, Integer>();
	}

	public int getRename(String reg)
	{
		return hm.get(reg);
	}

	public void setRename(String reg, int rob_index)
	{
		hm.put(reg, rob_index);
	}

	public void removeRename(String reg)
	{
		hm.remove(reg);
	}

}
