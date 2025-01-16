import java.sql.SQLOutput;

/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean testTown;
    private boolean easyTown;
    private boolean lose;
    private boolean playerDug;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        //set the conditions for the test town where the chance of brawl is 100%
        if (toughness == 1) {
            testTown = true;
        }
        if (toughness == 0.3) {
            easyTown = true;
        }

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    public boolean getLose() {
        return lose;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + Colors.PURPLE + item + Colors.RESET + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + Colors.PURPLE + item + Colors.RESET;
            }
            playerDug = false;
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + Colors.PURPLE + terrain.getNeededItem() + Colors.RESET + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        }
        if (testTown) {
            noTroubleChance = 1;
        }
        else {
            noTroubleChance = 0.33;
        }
        if (Math.random() < noTroubleChance) {
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (hunter.getInventory().contains("sword")) {
                printMessage += "\nThe brawler, seeing your sword, realizes he picked a losing fight and gives you his gold";
                hunter.changeGold(goldDiff);
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
            } else {
                printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
                if (Math.random() > noTroubleChance) {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                    printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(goldDiff);
                    if(hunter.isLose()) {
                        lose = true;
                    }
                }
                else {
                    printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                    printMessage += "\nYou lost the brawl and pay " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(-goldDiff);
                    if(hunter.isLose()) {
                        lose = true;
                    }
                }
            }
        } else {
            printMessage = "You couldn't find any trouble";
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .16) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .33) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .50) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .66) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .83){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    public void digForGold() {
        if (hunter.getInventory().contains("shovel")) {
            if (playerDug) {
                System.out.println("You already dug for gold in this town.");
            } else {
                if (((int) (Math.random() * 2) + 1) < 1) {
                    int goldDiff = (int) (Math.random() * 20) + 1;
                    System.out.println("You dug up " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.");
                } else {
                    System.out.println("You dug but only found dirt");
                }
                playerDug = true;
            }
        } else {
            System.out.println("You can't dig for gold without a shovel");
        }
    }


    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        if (easyTown) {
            return false;
        } else {
            return (rand < 0.5);
        }
    }
}