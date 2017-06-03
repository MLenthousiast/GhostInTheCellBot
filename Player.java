import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    
    private static Random r = new Random();
    
    private static int nFactories;
    private static int nLinks;
    
    private static boolean isFactory(final String entityType) {
        return entityType.equals("FACTORY");
    }
    
    private static boolean isFriendly(final int factory, final int[][] info) {
        return info[factory][0] == 1;
    }
    
    private static void updateFactoryInfo(int entityId, int arg1, int arg2, int arg3, int arg4, int arg5, int[][] info) {
        info[entityId][0] = arg1;
        info[entityId][1] = arg2;
        info[entityId][2] = arg3;
        info[entityId][3] = arg4;
        info[entityId][4] = arg5;
    }
    
    private static int getAvailableTroops(final int factory, final int[][] info) {
        return info[factory][1];
    }
    
    private static String attackCommand(final int factory, final int target, final int nTroops) {
        return ";MOVE " + factory + " " + target + " " + nTroops;
    }
    
    private static String randomAttackCommand(final int factory, final int[][] info) {
        // get random number of troops to send, or 0 if none available
        final int nTroopsAvailable = getAvailableTroops(factory, info);
        int nTroops = 0;
        if (nTroopsAvailable > 0) nTroops = r.nextInt(nTroopsAvailable);
        // get random target
        int target = r.nextInt(nFactories);
        while (target == factory) target = r.nextInt(nFactories);
        // create command
        return attackCommand(factory, target, nTroops);
    }
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        nFactories = in.nextInt(); // the number of factories
        nLinks = in.nextInt(); // the number of links between factories
        int[][] link = new int[nFactories][nFactories];
        for (int i = 0; i < nLinks; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();
            link[factory1][factory2] = distance;
            link[factory2][factory1] = distance;
        }

        // game loop
        while (true) {
            String command = "WAIT";
            
            int[][] factoryInfo = new int[nFactories][5];
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


            // Let all friendly factories fire at random
            for (int factory = 0; factory < nFactories; ++factory) {
                if (isFriendly(factory, factoryInfo)) {
                    command += randomAttackCommand(factory, factoryInfo);
                }
            }
            System.out.println(command);
        }
    }
}
