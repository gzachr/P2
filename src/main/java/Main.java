import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static int nInstances;
    static int tankCount;
    static int healerCount;
    static int dpsCount;
    static int t1;
    static int t2;

    static int totalPartyCount;
    static int remainingPartyCounter;

    static DungeonInstance[] instances;

    public static void main(String[] args) {
        getAllInputs();

        totalPartyCount = Math.min(tankCount, Math.min(healerCount, dpsCount / 3));
        remainingPartyCounter = totalPartyCount;

        try {
            instances = new DungeonInstance[nInstances];
        } catch (OutOfMemoryError e) {
            System.out.println("Device does not have memory to create that many instances, exiting program");
            return;
        }

        for(int i = 0; i < nInstances; i++) {
            instances[i] = new DungeonInstance(i, t1, t2);
        }

        processParties();


        displayEndStats();
    }

    static void processParties() {

        while(remainingPartyCounter != 0) {
            for (DungeonInstance instance : instances) {
                if (instance.isAvailable() && remainingPartyCounter != 0) {
                    instance.start();
                    remainingPartyCounter--;
                }
            }
        }

        // make sure all dungeons finish processing before returning control back to main
        while (!areDungeonsFinished()) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    static boolean areDungeonsFinished() {
        for (DungeonInstance instance : instances) {
            if (!instance.isAvailable()) {
                return false;
            }
        }
        return true;
    }


    static void displayEndStats() {
        System.out.println("\n--------------------------------------------");
        System.out.println("Leftover Tanks: " + (tankCount - totalPartyCount));
        System.out.println("Leftover Healers: " + (healerCount - totalPartyCount));
        System.out.println("Leftover Dps: " + (dpsCount  - totalPartyCount * 3));

        System.out.println("\nDungeon Instances Information");

        for(DungeonInstance instance : instances) {
            System.out.println("Instance#" + instance.getId());
            System.out.println("Parties Served: " + instance.getPartiesServed());
            System.out.println("Total Running Time (seconds): " + instance.getTotalRunningTime() + "\n");
        }
    }

    static void getAllInputs() {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print("Enter all inputs separated by commas (n, t, h, d, t1, t2): ");
            String inputLine = scanner.nextLine();
            String[] inputs = inputLine.split(",\s*");

            if (inputs.length != 6) {
                System.out.println("Error: You must enter exactly 6 values.");
                continue;
            }

            try {
                nInstances = validateSingleInput(inputs[0], 'n');
                tankCount = validateSingleInput(inputs[1], 't');
                healerCount = validateSingleInput(inputs[2], 'h');
                dpsCount = validateSingleInput(inputs[3], 'd');
                t1 = getClearTime(inputs[4], 1);
                t2 = getClearTime(inputs[5], 2);
                System.out.println("Valid input received.\n");
                break;
            } catch (Exception e) {
                System.out.println("Please try again.\n");
            }
        }
    }

    static int validateSingleInput(String input, char type) {
        try {
            int value = Integer.parseInt(input.trim());
            if (type != 'd' && value < 1) {
                System.out.println("Error, Instance/Healer/Tank count must be a valid Integer greater than 0.");
                throw new InputMismatchException();
            }
            else if (type == 'd' && value < 3) {
                System.out.println("Error, Dps Count must be a valid Integer >= 3");
                throw new LackingDpsException();
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Error, an input is not an integer");
            throw new InputMismatchException();
        }
    }

    // 1 for t1, 2 for t2
    static int getClearTime(String input, int type) {
        try {
            int value = Integer.parseInt(input.trim());
            if (value < 1 || value > 15) {
                System.out.println("Error, times must be a valid integer within the 1-15 range");
                throw new InputMismatchException();
            }
            //error if t2 is less than t1
            if(type == 2 && value < t1) {
                System.out.println("Error, T2 cannot be slower than T1");
                throw new InvalidT2Exception();
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Error, an input is not an integer");
            throw new InputMismatchException();
        }
    }
}
