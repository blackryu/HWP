package tinyVM;

public class Main {

	public static void main(String[] args) {
		VM vm = new VM();
		vm.runVM("../HWP/src/newFibAsm.txt"); 
		
		for (int i = 1000; i < 1022; i++)
			System.out.println("Stelle " + i + ": " + vm.getMemory(i));

	}

}
