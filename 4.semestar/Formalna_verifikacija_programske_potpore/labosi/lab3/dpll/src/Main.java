import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        for(String filePath: args){
            int variableNum;
            int clausesNum = 0;
            List<String> literals = new ArrayList<>();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                System.out.printf("Reading file %s\n", filePath);
                String line;
                do {
                    line = reader.readLine();
                    if(line == null){
                        break;
                    }
                    if(line.startsWith("p")){
                       String[] lineBreaked = line.split(" ");
                       variableNum = Integer.parseInt(lineBreaked[2]);
                       clausesNum = Integer.parseInt(lineBreaked[3]);
                       System.out.printf("Variables: %s, clauses: %s\n", variableNum, clausesNum);
                   }else if(!line.startsWith("c")){
                       literals.addAll(Arrays.asList(line.split(" ")));
                   }
                }while(true);

            } catch (IOException e) {
                e.printStackTrace();
            }

            List<List<String>> clauses = new LinkedList<>();
            clauses.add(new ArrayList<>());
            int clauseIndex = 0;
            for (String literal : literals) {
                if (Objects.equals(literal, "0")) {
                    clauses.add(new ArrayList<>());
                    clauseIndex += 1;
                } else {
                    clauses.get(clauseIndex).add(literal);
                }
            }

            clauses.remove(clauses.size() - 1);


            String[][] clausesArray = new String[clausesNum][];
            for (int i = 0; i < clauses.size(); i++){
                String[] literalArray = new String[clauses.get(i).size()];
                for (int j = 0; j < clauses.get(i).size(); j++){
                    literalArray[j] = clauses.get(i).get(j);
                }

                clausesArray[i] = literalArray;
            }

            Formula f = new Formula(clausesArray);

            final long timeStart = System.currentTimeMillis();
            DpllResult result = Dpll.solve(f);
            final long timeEnd = System.currentTimeMillis();
            final long timeTaken = timeEnd - timeStart;

            StringBuilder sb = new StringBuilder();

            sb.append("Satisfiable: ").append(result != null).append(", took ").append(timeTaken).append(" ms.\n");
            if(result != null){
                sb.append(result);
            }

            try {
                String problemName = filePath.replace(".cnf", "").replace("./benchmarks/", "");
                File file = new File("./results/" + problemName);

                file.createNewFile();

                PrintWriter writer = new PrintWriter("./results/" + problemName);
                writer.print(sb);
                writer.close();
            }
            catch(IOException e) {
                System.out.println("Exception Occurred:");
                e.printStackTrace();
            }

            System.out.println(sb);
        }
    }
}