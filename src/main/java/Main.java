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

        instances = new DungeonInstance[nInstances];

        for(int i = 0; i < nInstances; i++) {
            instances[i] = new DungeonInstance(i, t1, t2);
        }

        processParties();


        displayEndStats();
    }

    static void processParties() {
        Thread checkDungeonStatus = getStatusThread();

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

        try {
            checkDungeonStatus.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    private static Thread getStatusThread() {
        int sleepTimer = 1000;
        Thread checkDungeonStatus = new Thread(() -> {
            while(remainingPartyCounter > 0 && !areDungeonsFinished()) {
                System.out.println("\nDungeon Availability Status:");
                for (DungeonInstance instance : instances) {
                    System.out.println("Instance " + instance.getId() + " - Status: " + instance.getStatus());
                }

                try {
                    Thread.sleep(sleepTimer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        checkDungeonStatus.start();
        return checkDungeonStatus;
    }

    static void displayEndStats() {
        System.out.println("--------------------------------------------");
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
                System.out.println("Error, input must fit in an integer and be greater than 0\n");
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
