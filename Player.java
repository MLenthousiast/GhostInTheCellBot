import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    
    private static boolean isFactory(String entityType) {
        return entityType.equals("FACTORY");
    }
    
    private static void updateFactoryInfo(int entityId, int arg1, int arg2, int arg3, int arg4, int arg5, int[][] info) {
        info[entityId][0] = arg1;
        info[entityId][1] = arg2;
        info[entityId][2] = arg3;
        info[entityId][3] = arg4;
        info[entityId][4] = arg5;
    }
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int nFactories = in.nextInt(); // the number of factories
        int nLinks = in.nextInt(); // the number of links between factories
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


            // Any valid action, such as "WAIT" or "MOVE source destination cyborgs"
            System.out.println("WAIT");
        }
    }
}
