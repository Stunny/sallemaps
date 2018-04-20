import java.util.Scanner;

public class SalleMaps {

    public SalleMaps(){}

    public void menu(){

        String option = "";

        do{
            printMenu();
            System.out.print("Option: ");
            option = readOpt();
            System.out.println();

            switch(option){
                case "1":
                    importMap();
                    break;
                case "2":
                    searchCity();
                    break;
                case "3":
                    calculateRoute();
                    break;

                case "4":
                    System.out.println("Bye");
                    System.exit(1);
                    break;

                default:
                    System.out.println();
                    System.err.println("Option "+option+" does not exist.");
                    System.out.println();
            }

        }while(true);

    }

    private void calculateRoute() {

    }

    private void searchCity() {

    }

    private void importMap() {

    }

    private void printMenu(){
        System.out.println("SALLE MAPS");
        System.out.println();
        System.out.println("1. Import map");
        System.out.println("2. Search city");
        System.out.println("3. Calculate route");
        System.out.println("4. Shut down");
        System.out.println();
    }

    private String readOpt(){
        Scanner kb = new Scanner(System.in);
        return kb.nextLine();
    }

}
