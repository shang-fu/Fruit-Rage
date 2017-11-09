import java.util.Random;

public class RandomGen {

	public static void main(String[] args) {
		Random random = new Random();
		int size = 24;
		int bound = 2;
		
		for (int i = 1; i <= size; i++) {
			for (int j = 1; j <= size; j++) {
				System.out.print(random.nextInt(bound));
			}
			System.out.println();
		}

	}

}
