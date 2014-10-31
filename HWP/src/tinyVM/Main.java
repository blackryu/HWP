package tinyVM;

public class Main {

	public static void main(String[] args) {
		VM vm = new VM();

		vm.readFile("../HWP/src/asm.txt");
		vm.runVM();

		 for(int i = 0; i < 5; i++)
		 System.out.println("Stelle " + i +": " + vm.getMemory(i));
	}

}
