package tinyVM;

public class Main {

	public static void main(String[] args) {
		VM vm = new VM();

		vm.readFile("C:\\Users\\Philip\\workspace\\HWP\\src\\tinyVM\\text.txt");

		// for(int i = 0; i < 4096; i++)
		// System.out.println("Stelle " + i +": " + vm.getMemory(i));
	}

}
