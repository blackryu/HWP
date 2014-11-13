package tinyVM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

public class VM {

	private int register[] = new int[16];
	private int rx, ry;
	private int programCounter = 0;
	private int memory[] = new int[4096];
	private int profilerArray[] = new int[4096];
	private String linesOfCode[] = new String[4096]; // assembler commands
	private int profilerCounter = 0;
	private int cycles;

	Stack<Integer> registerStack = new Stack<Integer>();
	Stack<Integer> subroutineStack = new Stack<Integer>();

	public int getMemory(int i) {
		return memory[i];
	}

	public void readFile(String file) {
		// initialize FileReader and BufferedReader
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		// read file and call returnOpCode
		String line = "";
		try {
			// write commands in memory
			while ((line = bufferedReader.readLine()) != null) {
				if (line != "" || line.charAt(0) != '/') {
					memory[programCounter] = returnOpCode(line);
				}
				// save command lines
				linesOfCode[programCounter] = line;
				programCounter++;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// close FileReader and BufferedReader
		try {
			bufferedReader.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int returnOpCode(String command) {
		// split assembler string
		String[] commandSplitted = new String[0];
		commandSplitted = command.split(" ");
		int opCode = 0;
		rx = 0;
		ry = 0;

		switch (commandSplitted[0]) {
		case "NOP": {
			return 0;
		}
		case "LOAD": {
			opCode = Integer.parseInt(commandSplitted[1]);
			// Shift for R0 and LOAD
			opCode = opCode << 4;
			// LOAD Bit
			opCode += 1;
			return opCode;
		}
		case "MOV": {
			// move from register to register
			if (commandSplitted[1].charAt(0) == 'R'
					&& commandSplitted[2].charAt(0) == 'R') {

				rx = computeReg(commandSplitted[1]);
				ry = computeReg(commandSplitted[2]);

				opCode += ry;
				opCode <<= 4;
				opCode += rx;
				opCode <<= 4;
				opCode += 2;
				return opCode;
			}
			// move from mem to reg
			else if (commandSplitted[1].charAt(0) == 'R'
					&& commandSplitted[2].charAt(0) == '(') {

				rx = computeReg(commandSplitted[1]);
				ry = computeMem(commandSplitted[2]);

				opCode += 1;
				opCode <<= 4;
				opCode += ry;
				opCode <<= 4;
				opCode += rx;
				opCode <<= 4;
				opCode += 2;
				return opCode;

			}
			// move from reg to mem
			else if (commandSplitted[1].charAt(0) == '('
					&& commandSplitted[2].charAt(0) == 'R') {

				rx = computeMem(commandSplitted[1]);
				ry = computeReg(commandSplitted[2]);

				opCode += 2;
				opCode <<= 4;
				opCode += ry;
				opCode <<= 4;
				opCode += rx;
				opCode <<= 4;
				opCode += 2;
				return opCode;
			}

			// move from memory to memory
			else if (commandSplitted[1].charAt(0) == '('
					&& commandSplitted[2].charAt(0) == '(') {

				rx = computeMem(commandSplitted[1]);
				ry = computeMem(commandSplitted[2]);

				opCode += 3;
				opCode <<= 4;
				opCode += ry;
				opCode <<= 4;
				opCode += rx;
				opCode <<= 4;
				opCode += 2;
				return opCode;
			}

			return opCode;
		}
		case "ADD": {

			rx = computeReg(commandSplitted[1]);
			ry = computeReg(commandSplitted[2]);

			opCode += ry;
			opCode <<= 4;
			opCode += rx;
			opCode <<= 4;
			opCode += 3;
			return opCode;
		}
		case "SUB": {
			rx = computeReg(commandSplitted[1]);
			ry = computeReg(commandSplitted[2]);
			opCode += ry;
			opCode <<= 4;
			opCode += rx;
			opCode <<= 4;
			opCode += 4;
			return opCode;
		}
		case "MUL": {
			rx = computeReg(commandSplitted[1]);
			ry = computeReg(commandSplitted[2]);
			opCode += ry;
			opCode <<= 4;
			opCode += rx;
			opCode <<= 4;
			opCode += 5;
			return opCode;
		}
		case "DIV": {
			rx = computeReg(commandSplitted[1]);
			ry = computeReg(commandSplitted[2]);
			opCode += ry;
			opCode <<= 4;
			opCode += rx;
			opCode <<= 4;
			opCode += 6;
			return opCode;
		}
		case "PUSH": {
			rx = computeReg(commandSplitted[1]);
			opCode += rx;
			opCode <<= 4;
			opCode += 7;
			return opCode;
		}
		case "POP": {
			rx = computeReg(commandSplitted[1]);
			opCode += rx;
			opCode <<= 4;
			opCode += 8;
			return opCode;
		}
		case "JMP": {
			opCode = Integer.parseInt(commandSplitted[1]);
			opCode <<= 4;
			opCode += 9;
			return opCode;
		}
		case "JIZ": {
			opCode = Integer.parseInt(commandSplitted[1]);
			opCode <<= 4;
			opCode += 10;
			return opCode;
		}
		case "JIH": {
			opCode = Integer.parseInt(commandSplitted[1]);
			opCode <<= 4;
			opCode += 11;
			return opCode;
		}
		case "JSR": {
			opCode = Integer.parseInt(commandSplitted[1]);
			opCode <<= 4;
			opCode += 12;
			return opCode;
		}
		case "RTS": {
			return 13;
		}

		}// end switch
		return opCode;
	}

	public void executeOpCode(int[] filledMemory) {
		boolean fileWritten = false;
		// go to first memory entry
		programCounter = 0;
		rx = 0;
		ry = 0;
		// run all opCodes
		for (programCounter = 0; programCounter < 4095; programCounter++) {
			if (profilerCounter == cycles && !fileWritten) {
				writeProfilerFile();
				fileWritten = true;
			}

			int command = filledMemory[programCounter];
			// count number of line executions
			profilerArray[programCounter]++;
			profilerCounter++;
			switch (command & 0b0000_0000_0000_1111) {
			// NOP
			case 0: {
				break;
			}
			// LOAD
			case 1: {
				// Mask value
				register[0] = (command & 0b1111_1111_1111_0000) >> 4;
				break;
			}
			// MOV
			case 2: {
				// reg to reg
				if (command >> 12 == 0) {
					rx = (command & 0b0000_0000_1111_0000) >> 4;
					ry = (command & 0b0000_1111_0000_0000) >> 8;
					register[rx] = register[ry];
					break;
				}
				// from mem to reg
				else if (command >> 12 == 1) {
					rx = (command & 0b0000_0000_1111_0000) >> 4;
					ry = (command & 0b0000_1111_0000_0000) >> 8;
					register[rx] = memory[register[ry]];
					break;
				}
				// reg to mem
				else if (command >> 12 == 2) {
					rx = (command & 0b0000_0000_1111_0000) >> 4;
					ry = (command & 0b0000_1111_0000_0000) >> 8;
					memory[register[rx]] = register[ry];
					break;
				}
				// memory to memory
				else {
					rx = (command & 0b0000_0000_1111_0000) >> 4;
					ry = (command & 0b0000_1111_0000_0000) >> 8;
					memory[register[rx]] = memory[register[ry]];

					break;
				}
			}
			// ADD
			case 3: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				ry = (command & 0b0000_1111_0000_0000) >> 8;
				register[rx] += register[ry];
				break;
			}
			// SUB
			case 4: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				ry = (command & 0b0000_1111_0000_0000) >> 8;
				register[rx] -= register[ry];
				break;

			}
			// MUL
			case 5: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				ry = (command & 0b0000_1111_0000_0000) >> 8;
				register[rx] *= register[ry];
				break;
			}
			// DIV
			case 6: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				ry = (command & 0b0000_1111_0000_0000) >> 8;
				if (register[ry] != 0)
					register[rx] /= register[ry];
				break;
			}
			// PUSH
			case 7: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				registerStack.push(register[rx]);
				break;
			}
			// POP
			case 8: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				if (!registerStack.isEmpty())
					register[rx] = registerStack.pop();
				break;
			}
			// Jump
			case 9: {
				programCounter = (command & 0b1111_1111_1111_0000) >> 4;
				programCounter -= 1;
				break;
			}
			// Jump if zero
			case 10: {
				if (register[0] == 0) {
					programCounter = (command & 0b1111_1111_1111_0000) >> 4;
					programCounter -= 1;
					break;
				} else
					break;
			}
			// Jump if higher
			case 11: {
				if (register[0] > 0) {
					programCounter = (command & 0b1111_1111_1111_0000) >> 4;
					programCounter -= 1;
					break;
				} else
					break;
			}
			// Jump subroutine
			case 12: {
				subroutineStack.push(programCounter);
				programCounter = (command & 0b1111_1111_1111_0000) >> 4;
				programCounter -= 1;
				break;
			}
			// Return subroutine
			case 13: {
				if (!subroutineStack.isEmpty())
					programCounter = subroutineStack.pop();
				else {
					if (!fileWritten) {
						writeProfilerFile();
						fileWritten = true;
					}
					return;
				}

				break;
			}
			default: {

				break;
			}

			}// end switch
		}

	}

	public void runVM(String file, int setCycles) {
		readFile(file);
		cycles = setCycles;
		executeOpCode(memory);
	}

	public void writeProfilerFile() {
		// create writer
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("myProfiler.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		double percent;
		BigDecimal value;
		writer.println("Perc. " + "Commands");
		for (int i = 0; i < 4095; i++) {
			// compute percent
			percent = ((double) profilerArray[i] / cycles * 100);
			// format percent to two decimal places
			value = new BigDecimal(percent);
			value = value.setScale(2, RoundingMode.CEILING);
			if (linesOfCode[i] != null) {
				writer.println(value + "% " + linesOfCode[i]);
			}
		}
		writer.close();
	}

	/**
	 * This method checks if rx/ry is a two digit or one digit register when moving
	 * from/to a register and returns the register number as int.
	 * 
	 * @param part
	 *            Part of Assembler command for register
	 * @return register number as int
	 */
	public int computeReg(String part) {
		int reg = 0;
		if (part.length() == 3) {
			for (int i = 1; i <= 2; i++) {
				reg = reg * 10 + part.charAt(i) - '0';
			}
		} else
			reg = part.charAt(1) - '0';
		return reg;
	}

	/**
	 * This method checks if rx/ry is a two digit or one digit register when moving
	 * from/to memory and returns the register number as int.
	 * 
	 * @param part
	 *            Part of Assembler command for register
	 * @return register number as int
	 */
	public int computeMem(String part) {
		int reg = 0;
		if (part.length() == 5) {
			for (int i = 2; i <= 3; i++) {
				reg = reg * 10 + part.charAt(i) - '0';
			}
		} else
			reg = part.charAt(2) - '0';
		return reg;
	}

}
