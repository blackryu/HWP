package tinyVM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class VM {

	private int register[] = new int[16];

	private int programCounter = 0;

	private short memory[] = new short[4096];

	// public short getMemory(int i){
	// return memory[i];
	// }

	Stack<Integer> registerStack = new Stack<Integer>();
	Stack<Integer> subroutineStack = new Stack<Integer>();

	public void readFile(String datei) {
		// initialize FileReader and BufferedReader
		FileReader fr = null;
		try {
			fr = new FileReader(datei);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);

		// read file and call returnOpCode
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				memory[programCounter] = returnOpCode(line);
				programCounter++;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// close FileReader and BufferedReader
		try {
			br.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		try {
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public short returnOpCode(String command) {
		String[] firstPart = new String[0];
		firstPart = command.split(" ");
		short opCode = 0;

		switch (firstPart[0]) {
		case "NOP": {
			return 0;
		}
		case "LOAD": {
			System.out.println("LOAD gefunden!");
			// LOAD 99999999 ?
			opCode = 0001;
			return opCode;
		}
		case "MOV": {
			System.out.println("MOV gefunden!");
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

}
