import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.Math;

public class CDB {
	public final int NUM_BUSES;
	private Queue<Instruction> bus = new LinkedList<Instruction>();
	private Simulator sim;

	public CDB(final int width, Simulator the_sim) {
		NUM_BUSES = width;
		sim = the_sim;
	}

	public void doCycle(int min_rob_bw) {
		// Part 1
		if (sim.rob2 == null) {

			int rob_num_ready = sim.rob.queryReadyInstructions();
			int wb_num_ready = sim.wb.queryReadyInstructions();

			int bw_for_rob = Math.min(rob_num_ready, min_rob_bw);
			int bw_for_wb = Math.min(wb_num_ready, NUM_BUSES - bw_for_rob);
			int consumed_bw = bw_for_rob + bw_for_wb;

			if (NUM_BUSES > consumed_bw) {
				int rem_bw = NUM_BUSES - consumed_bw;
				int rem_rob = rob_num_ready - bw_for_rob;
				int rem_wb = wb_num_ready - bw_for_wb;

				if (rem_rob > 0) {
					int incr = Math.min(rem_bw, rem_rob);
					bw_for_rob += incr;
					consumed_bw += incr;
					rem_bw = NUM_BUSES - consumed_bw;
				}

				if (rem_bw > 0 && rem_wb > 0) {
					int incr = Math.min(rem_bw, rem_wb);
					bw_for_wb += incr;
					consumed_bw += incr;
				}
			}

			ArrayList<Instruction> rob_insts = sim.rob.dequeue(bw_for_rob);
			for (Instruction inst : rob_insts) {
				if (inst.threadNum == 1) {
					sim.rf.commit(inst);
				}
			}

			ArrayList<Instruction> wb_insts = sim.wb.pull(bw_for_wb);

			// putting values into reservation station instructions
			for (Instruction inst : wb_insts) {
				sim.rf.wb_push(inst);

				ArrayList<Instruction> allInstructions = ReservationStationStatusTable.getAllInstructions();

				for (Instruction reservInst : allInstructions) {
					if (reservInst.source_reg1_renamed_str != null
							&& reservInst.source_reg1_renamed_str.equals(inst.dest_reg_renamed_str)
							&& reservInst.threadNum == inst.threadNum) {
						reservInst.source_reg1_value = inst.dest_reg_value;
					}

					if (reservInst.source_reg2_renamed_str != null
							&& reservInst.source_reg2_renamed_str.equals(inst.dest_reg_renamed_str)
							&& reservInst.threadNum == inst.threadNum) {
						reservInst.source_reg2_value = inst.dest_reg_value;
					}

					if (reservInst.opcode.equals("sd") || reservInst.opcode.equals("fsd")) {
						if (reservInst.dest_reg_renamed_str != null
								&& reservInst.dest_reg_renamed_str.equals(inst.dest_reg_renamed_str)
								&& reservInst.threadNum == inst.threadNum) {
							reservInst.dest_reg_value = inst.dest_reg_value;
						}
					}
				}
			}
		} else { // Part 2
			int min_rob1_bw = min_rob_bw / 2;
			int min_rob2_bw = min_rob_bw - min_rob1_bw; // In case where min_rob_bw isnt even

			int rob1_num_ready = sim.rob.queryReadyInstructions();
			int rob2_num_ready = sim.rob2.queryReadyInstructions();

			int wb_num_ready = sim.wb.queryReadyInstructions();

			int bw_for_rob1 = Math.min(rob1_num_ready, min_rob1_bw);
			int bw_for_rob2 = Math.min(rob2_num_ready, min_rob2_bw);

			int bw_for_wb = Math.min(wb_num_ready, NUM_BUSES - (bw_for_rob1 + bw_for_rob2));
			int consumed_bw = (bw_for_rob1 + bw_for_rob2) + bw_for_wb;

			if (NUM_BUSES > consumed_bw) {
				int rem_bw = NUM_BUSES - consumed_bw;
				int rem_rob1 = rob1_num_ready - bw_for_rob1;
				int rem_rob2 = rob2_num_ready - bw_for_rob2;
				int rem_robs = rem_rob1 + rem_rob2;

				int rem_wb = wb_num_ready - bw_for_wb;
				int additional_bw_for_rob1;
				int additional_bw_for_rob2;

				if (rem_robs > 0) {
					if (rem_rob1 > rem_rob2) {
						additional_bw_for_rob1 = Math.min(rem_bw, rem_rob1);
						additional_bw_for_rob2 = Math.min(rem_bw - additional_bw_for_rob1, rem_rob2);
					} else {
						additional_bw_for_rob2 = Math.min(rem_bw, rem_rob2);
						additional_bw_for_rob1 = Math.min(rem_bw - additional_bw_for_rob2, rem_rob1);
					}

					bw_for_rob1 += additional_bw_for_rob1;
					bw_for_rob2 += additional_bw_for_rob2;

					consumed_bw += bw_for_rob1 + bw_for_rob2;
					rem_bw = NUM_BUSES - consumed_bw;
				}

				if (rem_bw > 0 && rem_wb > 0) {
					int incr = Math.min(rem_bw, rem_wb);
					bw_for_wb += incr;
					consumed_bw += incr;
				}
			}

			ArrayList<Instruction> robs_instrs = sim.rob.dequeue(bw_for_rob1);
			robs_instrs.addAll(sim.rob.dequeue(bw_for_rob2));

			for (Instruction inst : robs_instrs) {
				if (inst.threadNum == 1) {
					sim.rf.commit(inst);
				} else if (inst.threadNum == 2) {
					sim.rf2.commit(inst);
				}
			}

			ArrayList<Instruction> wb_insts = sim.wb.pull(bw_for_wb);

			// putting values into reservation station instructions
			for (Instruction inst : wb_insts) {
				if (inst.threadNum == 1) {
					sim.rf.wb_push(inst);
				} else {
					sim.rf2.wb_push(inst);
				}

				ArrayList<Instruction> allInstructions = ReservationStationStatusTable.getAllInstructions();

				for (Instruction reservInst : allInstructions) {
					if (reservInst.source_reg1_renamed_str != null
							&& reservInst.source_reg1_renamed_str.equals(inst.dest_reg_renamed_str)
							&& reservInst.threadNum == inst.threadNum) {
						reservInst.source_reg1_value = inst.dest_reg_value;
					}

					if (reservInst.source_reg2_renamed_str != null
							&& reservInst.source_reg2_renamed_str.equals(inst.dest_reg_renamed_str)
							&& reservInst.threadNum == inst.threadNum) {
						reservInst.source_reg2_value = inst.dest_reg_value;
					}

					if (reservInst.opcode.equals("sd") || reservInst.opcode.equals("fsd")) {
						if (reservInst.dest_reg_renamed_str != null
								&& reservInst.dest_reg_renamed_str.equals(inst.dest_reg_renamed_str)
								&& reservInst.threadNum == inst.threadNum) {
							reservInst.dest_reg_value = inst.dest_reg_value;
						}
					}
				}
			}
		}
	}

	/*
	 * \brief Push an instruction to the CDB
	 *
	 * \return true on success or false otherwise
	 */
	public boolean push(Instruction inst) {
		if (isFull())
			return false;

		return bus.add(inst);
	}

	public boolean isFull() {
		return bus.size() >= NUM_BUSES;
	}

	public void clear() {
		bus.clear();
	}

	// TODO implement this
	public boolean registerNameMatch(Instruction inst, String registerName) {
		return true;
	}
}
