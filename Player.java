import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Bot summary:
 * Each friendly factory attacks the closest non-friendly factory with all available troops.
 * If no non-friendly factories are available a random move is made.
 * A bomb is send to a enemy factory with the most number of units. This is done from the closest friendly factory.
 **/
class Player {
    
    private static Random r = new Random();
    
    private static int nFactories;
    private static int nLinks;
    
    private static int[][] factoryInfo;
    private static int[][] link;
    
    private static int availableBombs = 2;
    
    
    
    private static boolean isFactory(final String entityType) {
        return entityType.equals("FACTORY");
    }
    
    
    private static boolean isFriendly(int factory) {
        return factoryInfo[factory][0] == 1;
    }
    
    
    private static boolean isEnemy(int factory) {
        return factoryInfo[factory][0] == -1;
    }
    
    
    private static boolean isTarget(int factory) {
        return !isFriendly(factory);
    }
    
    
    private static boolean isUpgradable(int factory) {
        // check if enough troops and not already at max production
        return factoryInfo[factory][1] >= 10 && factoryInfo[factory][2] < 3;
    }
    
    
    private static void updateFactoryInfo(int entityId, int arg1, int arg2, int arg3, int arg4, int arg5, int[][] info) {
        info[entityId][0] = arg1;
        info[entityId][1] = arg2;
        info[entityId][2] = arg3;
        info[entityId][3] = arg4;
        info[entityId][4] = arg5;
    }
    
    
    private static int getProduction(int factory) {
        return factoryInfo[factory][1];
    }
    
    
    private static int getClosest(int from, int destA, int destB) {
        if (link[from][destA] <= link[from][destB]) {
            return destA;
        }
        // else
        return destB;
    }
    
    
    private static Set<Integer> getTargets(int minTargetProduction) {
        Set<Integer> targets = new HashSet();
        for (int factory = 0; factory < nFactories; ++factory) {
            if (isTarget(factory) && getProduction(factory) > minTargetProduction) {
                targets.add(factory);
            }
        }
        return targets;
    }
    
    
    private static Set<Integer> getEnemies() {
        Set<Integer> enemies = new HashSet();
        for (int factory = 0; factory < nFactories; ++factory) {
            if (isEnemy(factory)) {
                enemies.add(factory);
            }
        }
        return enemies;
    }
    
    
    private static int getHighestProducingEnemy() {
        Set<Integer> enemies = getEnemies();
        int highestProducing = -1;
        for (int enemy: enemies) {
            if (highestProducing == -1 || getProduction(enemy) > getProduction(highestProducing)) {
                highestProducing = enemy;
            }
        }
        if (highestProducing == -1) {
            return 0; // TODO fixme
        }
        return highestProducing;
    }
    
    
    private static int getClosestFriend(int target) {
        int closestFriend = -1;
        for (int factory = 0; factory < nFactories; ++factory) {
            if (isFriendly(factory)) {
                if (closestFriend == -1) {
                    closestFriend = factory;
                }
                else {
                    closestFriend = getClosest(target, closestFriend, factory);
                }
            }
        }
        return closestFriend;
    }
    
    private static int getAvailableTroops(int factory) {
        return factoryInfo[factory][1];
    }
    
    
    private static String attackCommand(int from, int target, int nTroops) {
        return ";MOVE " + from + " " + target + " " + nTroops;
    }
    
    
    private static String randomAttackCommand(int from) {
        // get random number of troops to send, or 0 if none available
        int nTroopsAvailable = getAvailableTroops(from);
        int nTroops = 0;
        if (nTroopsAvailable > 0) nTroops = r.nextInt(nTroopsAvailable);
        // get random target
        int target = r.nextInt(nFactories);
        while (target == from) target = r.nextInt(nFactories);
        // create command
        return attackCommand(from, target, nTroops);
    }
    
    
    private static String closestTargetAttackCommand(int from, int nTroops, int minTargetProduction) {
        Set<Integer> targets = getTargets(minTargetProduction);
        
        // find closest target
        int target = from;
        for (int availableTarget: targets) {
            if (target == from) {
                target = availableTarget;
            }
            else {
                target = getClosest(from, target, availableTarget);
            }
        }
        if (target == from) {
            // no targets available, move random
            return randomAttackCommand(from);
        }
        // else
        // attack closest target
        return attackCommand(from, target, nTroops);
    }
    
    
    private static String bombCommand(int from, int target) {
        if (availableBombs > 0) {
            --availableBombs;
            return ";BOMB " + from + " " + target;
        }
        // else
        return "";
    }
    
    
    private static String bombHighestProducingEnemy() {
        int enemy = getHighestProducingEnemy();
        int from = getClosestFriend(enemy);
        return bombCommand(from, enemy);
    }
    
    
    private static String upgradeCommand(int factory) {
        return ";INC " + factory;
    }
    
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        nFactories = in.nextInt(); // the number of factories
        nLinks = in.nextInt(); // the number of links between factories
        link = new int[nFactories][nFactories];
        for (int i = 0; i < nLinks; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();
            link[factory1][factory2] = distance;
            link[factory2][factory1] = distance;
        }

        boolean isFirstTurn = true;
        int stepsSinceLastBomb = 6;

        // game loop
        while (true) {
            String command = "WAIT";
            
            factoryInfo = new int[nFactories][5];
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();
                
                if (isFactory(entityType)) {
                    updateFactoryInfo(entityId, arg1, arg2, arg3, arg4, arg5, factoryInfo);
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            for (int factory = 0; factory < nFactories; ++factory) {
                if (isFriendly(factory)) {
                    command += closestTargetAttackCommand(factory, Math.max(factoryInfo[factory][1] - 3,0), 1);
                }
            }
            // overwrite action of the closest factory to the highest producing enemy with bombing
            if (stepsSinceLastBomb > 5) {
                command += bombHighestProducingEnemy();
                stepsSinceLastBomb = 0;
            }
            System.out.println(command);
            
            isFirstTurn = false;
            ++stepsSinceLastBomb;
        }
    }
}
