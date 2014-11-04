package tinyVM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class VM {

	private int register[] = new int[16];

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
			System.out.println("LOAD gefunden!");
			opCode = Integer.parseInt(commandSplitted[1]);
			// Shift for R0 and LOAD
			opCode = opCode << 5;
			// LOAD Bit
			opCode += 1;
			return opCode;
		}
		case "MOV": {
			
//			int dest = Integer.parseInt(commandSplitted[1]);
//			int source = Integer.parseInt(commandSplitted[2]);
		
			
			return opCode;
		}
		case "ADD": {
			// ADD Rx,Ry => Rx = Rx + Ry
			
			
			return opCode;
		}
		case "SUB": {
			// SUB Rx,Ry => Rx = Rx - Ry
			return opCode;
		}
		case "MUL": {
			// MUL Rx,Ry => Rx = Rx * Ry
			return opCode;
		}
		case "DIV": {
			// DIV Rx,Ry => Rx = Rx / Ry
			return opCode;
		}
		case "PUSH": {
			// PUSH Rx => Rx on the Stack
			return opCode;
		}
		case "POP": {
			// POP Rx => Rx from Stack
			return opCode;
		}
		case "JMP": {
			// JMP #num => Jumps to Memory @#num
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
			switch (command & 15) {
			case 0: {
				break;
			}
			// LOAD
			case 1: {
				// Mask value
				register[0] = command & 65520;
				// shift back
				register[0] = register[0] >> 5;
				break;
			}
			// MOV
			case 2: {

			}
			// ADD
			case 3: {

			}
			// SUB
			case 4: {

			}
			// MUL
			case 5: {

			}
			// DIV
			case 6: {

			}
			// PUSH
			case 7: {

			}
			// POP
			case 8: {

			}
			// Jump
			case 9: {

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

			}

			}// end switch
		}

	}

	public void runVM(String file) {
		readFile(file);
		executeOpCode(memory);
	}
}
