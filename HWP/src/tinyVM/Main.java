package tinyVM;

public class Main {

	public static void main(String[] args) {
		int cycles =1000;
		VM vm = new VM();
		
		vm.runVM("../HWP/src/fibAsm.txt", cycles); 
		
		for (int i = 1000; i < 1022; i++)
			System.out.println("Stelle " + i + ": " + vm.getMemory(i));

	}

}
