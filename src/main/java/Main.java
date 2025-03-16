import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static int nInstances;
    static int tankCount;
    static int healerCount;
    static int dpsCount;
    static int t1;
    static int t2;

    public static void main(String[] args) {
        getAllInputs();

    }

    static void getAllInputs() {
        nInstances = validateSingleInput("Input the number of dungeon instances (n): ", 'n');
        tankCount = validateSingleInput("Input the number of tank players (t): ", 't');
        healerCount = validateSingleInput("Input the number of healer players (h): ", 'h');
        dpsCount = validateSingleInput("Input the number of dps players(d): ", 'd');
        t1 = getClearTime(1, "Input the fastest dungeon clear time (t1): ");
        t2 = getClearTime(2, "Input the slowest dungeon clear time (t2): ");
    }

    static int validateSingleInput(String prompt, char type) {
        Scanner scanner = new Scanner(System.in);
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = scanner.nextInt();
                if (type != 'd' && value < 1) throw new InputMismatchException();
                else if (type == 'd' && value < 3) throw new LackingDpsException();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Error, must input a valid integer greater than 0\n");
                scanner.nextLine(); // Clear invalid input
            } catch (LackingDpsException e) {
                System.out.println("Error, minimum dps input is 3\n");
                scanner.nextLine();
            }
        }
    }

    // 1 for t1, 2 for t2
    static int getClearTime(int type, String prompt) {
        Scanner scanner = new Scanner(System.in);
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = scanner.nextInt();
                if (value < 1 || value > 15) throw new InputMismatchException();
                //error if t2 is less than t1
                if(type == 2 && value < t1) throw new InvalidT2Exception();
                return value;

            } catch (InputMismatchException e) {
                System.out.println("Error, must input a valid integer within the 1-15 range\n");
                scanner.nextLine(); // Clear invalid input
            } catch (InvalidT2Exception e) {
                System.out.println("Error, T2 cannot be slower than T1\n");
                scanner.nextLine();
            }
        }
    }
}
