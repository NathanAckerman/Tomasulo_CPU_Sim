/*
	Parser

	Author: Maher Khan

 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.lang.IllegalArgumentException;

public class Parser 
{

	public static void parseFile(String path, InstructionCache instruction_cache, Memory memory, int thread_number)
	{

		HashMap<String, Integer> labels = new HashMap<String, Integer>();

		Boolean data_start = false;
		int instructionCacheBaseAddress = 1000;
		int cur_instruction_num = 0;

		try {
			Scanner scanner = new Scanner(new File(path));
			int counter = 1000;

			while (scanner.hasNextLine()) {

				String next_line = scanner.nextLine();
				if (next_line.indexOf(':') != -1) {
					labels.put(next_line.split(":", 2)[0], counter);
					next_line = next_line.split(":", 2)[1].trim();
				}
				counter++;
			}
		} catch (FileNotFoundException e) {
				e.printStackTrace();
		}


		try 
		{

			Scanner scanner = new Scanner(new File(path));

			while (scanner.hasNextLine()) 
			{
				String next_line = scanner.nextLine();

				if(next_line.replace(" ", "").equals("DATA"))
				{
					data_start = true;
				}

				else if(data_start == false)
				{
					Instruction instruction = new Instruction();
					instruction.threadNum = thread_number;

					instruction.setAddress(instructionCacheBaseAddress + cur_instruction_num);

					cur_instruction_num = cur_instruction_num + 1;

					if (next_line.indexOf(':') != -1)
					{
						labels.put(next_line.split(":", 2)[0], instruction.getAddess());
						next_line = next_line.split(":", 2)[1].trim();
					}

					String[] splitted = next_line.split(" ", 2);

					instruction.setOpcode(splitted[0]);

					splitted = splitted[1].replace(" ", "").split(",");

					if (instruction.getOpcode().equals("add"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("sub"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("addi"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[2]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
					}
					else if (instruction.getOpcode().equals("subi"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[2]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
					}
					else if (instruction.getOpcode().equals("mul"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("fadd"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("fsub"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("fmul"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("fdiv"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("ld"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[1].split("\\(")[0]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("\\(")[1].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						String s_reg1 =  splitted[1].split("\\(")[1];
						instruction.source_reg1_original_str = s_reg1.substring(0, s_reg1.length()-1);
					}
					else if (instruction.getOpcode().equals("sd"))
					{
						instruction.setSourceReg1(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[1].split("\\(")[0]));
						instruction.setDestReg(Integer.parseInt(splitted[1].split("\\(")[1].split("")[1]));
						String s_dest = splitted[1].split("\\(")[1];
						instruction.dest_reg_original_str = s_dest.substring(0, s_dest.length()-1);
						instruction.source_reg1_original_str = splitted[0];
					}
					else if (instruction.getOpcode().equals("fld"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[1].split("\\(")[0]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("\\(")[1].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						String s_reg1 =  splitted[1].split("\\(")[1];
						instruction.source_reg1_original_str = s_reg1.substring(0, s_reg1.length()-1);
					}
					else if (instruction.getOpcode().equals("fsd"))
					{
						instruction.setSourceReg1(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[1].split("\\(")[0]));
						instruction.setDestReg(Integer.parseInt(splitted[1].split("\\(")[1].split("")[1]));
						String s_dest = splitted[1].split("\\(")[1];
						instruction.dest_reg_original_str = s_dest.substring(0, s_dest.length()-1);
						instruction.source_reg1_original_str = splitted[0];
					}
					else if (instruction.getOpcode().equals("and"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("or"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("andi"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[2]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
					}
					else if (instruction.getOpcode().equals("ori"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[2]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
					}
					else if (instruction.getOpcode().equals("slt"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setSourceReg2(Integer.parseInt(splitted[2].split("")[1]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
						instruction.source_reg2_original_str = splitted[2];
					}
					else if (instruction.getOpcode().equals("slti"))
					{
						instruction.setDestReg(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setSourceReg1(Integer.parseInt(splitted[1].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[2]));
						instruction.dest_reg_original_str = splitted[0];
						instruction.source_reg1_original_str = splitted[1];
					}
					else if (instruction.getOpcode().equals("beq"))
					{
						instruction.setSourceReg1(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[1].replace("$", "")));
						instruction.setTarget(labels.get(splitted[2]));
						instruction.source_reg1_original_str = splitted[0];
					}
					else if (instruction.getOpcode().equals("bne"))
					{
						instruction.setSourceReg1(Integer.parseInt(splitted[0].split("")[1]));
						instruction.setImmediate(Integer.parseInt(splitted[1].replace("$", "")));
						instruction.setTarget(labels.get(splitted[2]));
						instruction.source_reg1_original_str = splitted[0];
					}

					else
					{
						throw new IllegalArgumentException("opcode not recognized: " + instruction.getOpcode());
					}

					instruction_cache.instructions.add(instruction);

				}

				else if(data_start==true)
				{
					String[] splitted = next_line.split("=", 2);

					int key = Integer.parseInt(splitted[0].trim().split("\\(")[1].split("\\)")[0]);

					if(splitted[1].indexOf('.') != -1)
					{
						float val = Float.parseFloat(splitted[1].trim());
						memory.add_float(key, val);
					}
					else
					{
						int val = Integer.parseInt(splitted[1].trim());
						memory.add_int(key, val);

					}

				}

			}

			scanner.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

}
