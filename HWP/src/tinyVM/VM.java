package tinyVM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class VM {

	private int register[] = new int[16];
	private int rx, ry;
	private int programCounter = 0;

	private int memory[] = new int[4096];

	public int getMemory(int i) {
		return memory[i];
	}

	Stack<Integer> registerStack = new Stack<Integer>();
	Stack<Integer> subroutineStack = new Stack<Integer>();

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
			while ((line = bufferedReader.readLine()) != null) {
				// write commands in memory
				memory[programCounter] = returnOpCode(line);
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
		String[] commandSplitted = new String[0];
		commandSplitted = command.split(" ");
		int opCode = 0;

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
				rx = commandSplitted[1].charAt(1) - '0';
				ry = commandSplitted[2].charAt(1) - '0';
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
				rx = commandSplitted[1].charAt(1) - '0';
				ry = commandSplitted[2].charAt(2) - '0';
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
				rx = commandSplitted[1].charAt(2) - '0';
				ry = commandSplitted[2].charAt(1) - '0';
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
				rx = commandSplitted[1].charAt(2) - '0';
				ry = commandSplitted[2].charAt(2) - '0';
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
			rx = commandSplitted[1].charAt(1) - '0';
			ry = commandSplitted[2].charAt(1) - '0';
			opCode += ry;
			opCode <<= 4;
			opCode += rx;
			opCode <<= 4;
			opCode += 3;
			return opCode;
		}
		case "SUB": {
			rx = commandSplitted[1].charAt(1) - '0';
			ry = commandSplitted[2].charAt(1) - '0';
			opCode += ry;
			opCode <<= 4;
			opCode += rx;
			opCode <<= 4;
			opCode += 4;
			return opCode;
		}
		case "MUL": {
			rx = commandSplitted[1].charAt(1) - '0';
			ry = commandSplitted[2].charAt(1) - '0';
			opCode += ry;
			opCode <<= 4;
			opCode += rx;
			opCode <<= 4;
			opCode += 5;
			return opCode;
		}
		case "DIV": {
			rx = commandSplitted[1].charAt(1) - '0';
			ry = commandSplitted[2].charAt(1) - '0';
			opCode += ry;
			opCode <<= 4;
			opCode += rx;
			opCode <<= 4;
			opCode += 6;
			return opCode;
		}
		case "PUSH": {
			rx = commandSplitted[1].charAt(1) - '0';
			opCode += rx;
			opCode <<= 4;
			opCode += 7;
			return opCode;
		}
		case "POP": {
			rx = commandSplitted[1].charAt(1) - '0';
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
			// JMZ R0
			return opCode;
		}
		case "JIH": {

			return opCode;
		}
		case "JSR": {

			return opCode;
		}
		case "RTS": {
			System.out.println("RTS gefunden");
			opCode = 13;
			return opCode;
		}

		}
		return opCode;
	}

	public void executeOpCode(int[] filledMemory) {
		// go to first memory entry
		programCounter = 0;
		//
		for (programCounter = 0; programCounter < 4095; programCounter++) {
			int command = filledMemory[programCounter];
			switch (command & 0b0000_0000_0000_1111) {
			case 0: {
				break;
			}
			// LOAD
			case 1: {
				// Mask value
				register[0] = (command & 0b1111_1111_1111_0000) >> 4;
				System.out.println("Register 0: " + register[0]);
				break;
			}
			// MOV
			case 2: {
				// reg to reg
				if (command >> 12 == 0) {
					rx = (command & 0b0000_0000_1111_0000) >> 4;
					ry = (command & 0b0000_1111_0000_0000) >> 8;
					register[rx] = register[ry];
					System.out.println("Register " + rx + ": " + register[rx]);
					break;
				}
				// from mem to reg
				else if (command >> 12 == 1) {
					rx = (command & 0b0000_0000_1111_0000) >> 4;
					ry = (command & 0b0000_1111_0000_0000) >> 8;
					register[rx] = memory[register[ry]];
					System.out.println("Register " + register[rx] + ": "
							+ register[rx]);
					break;
				}
				// reg to mem
				else if (command >> 12 == 2) {
					rx = (command & 0b0000_0000_1111_0000) >> 4;
					ry = (command & 0b0000_1111_0000_0000) >> 8;
					memory[register[rx]] = register[ry];
					System.out.println("Memory " + register[rx] + ": "
							+ memory[register[rx]]);
					break;
				}
				// memory to memory
				else {
					rx = (command & 0b0000_0000_1111_0000) >> 4;
					ry = (command & 0b0000_1111_0000_0000) >> 8;
					memory[register[rx]] = memory[register[ry]];
					System.out.println("Memory " + register[rx] + ": "
							+ memory[register[rx]]);
					break;
				}
			}
			// ADD
			case 3: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				ry = (command & 0b0000_1111_0000_0000) >> 8;
				register[rx] += register[ry];
				System.out.println("Register " + rx + " nach ADD: "
						+ register[rx]);
				break;
			}
			// SUB
			case 4: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				ry = (command & 0b0000_1111_0000_0000) >> 8;
				register[rx] -= register[ry];
				System.out.println("Register " + rx + " nach SUB: "
						+ register[rx]);
				break;

			}
			// MUL
			case 5: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				ry = (command & 0b0000_1111_0000_0000) >> 8;
				register[rx] *= register[ry];
				System.out.println("Register " + rx + " nach MUL: "
						+ register[rx]);
				break;
			}
			// DIV
			case 6: {
				rx = (command & 0b0000_0000_1111_0000) >> 4;
				ry = (command & 0b0000_1111_0000_0000) >> 8;
				register[rx] /= register[ry];
				System.out.println("Register " + rx + " nach DIV: "
						+ register[rx]);
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
				System.out.println(programCounter);
				subroutineStack.push(programCounter);
				programCounter = (command & 0b1111_1111_1111_0000) >> 4;
				programCounter -= 1;
				System.out.println(programCounter);
				break;
			}
			// Jump if zero
			case 10: {

			}
			// Jump if higher
			case 11: {

			}
			// Jump subroutine
			case 12: {

			}
			// Return subroutine
			case 13: {
				if (!subroutineStack.isEmpty())
					programCounter = subroutineStack.pop();
				System.out.println(programCounter);
				break;
			}
			default:{
				
				break;
			}

			}// end switch
		}

	}

	public void runVM(String file) {
		readFile(file);
		executeOpCode(memory);
	}
}
