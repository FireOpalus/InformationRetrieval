/**
 *
 * @author FireOpalus
 * @date 2024/5/23
 */
package Command;

import LuceneMain.LuceneQuery;

import java.util.Scanner;

public class Program {
    public static void main(String[] args) throws Exception {
        // scanner to get user's input
        Scanner scanner = new Scanner(System.in);
        String input;
        LuceneQuery luceneQuery = new LuceneQuery();

        while(true) {
            // DOS prompt
            System.out.print("# ");
            // get input
            input = scanner.nextLine();
            // exit
            if(input.equalsIgnoreCase("exit")){
                break;
            }
            // if query is valid
            try {
                luceneQuery.ProcQuery(input);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }

        scanner.close();
        System.out.println("Program exited.");
    }
}
