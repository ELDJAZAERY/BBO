package metas;

import data.reader.Instances;
import metas.BBO.BBO;
import metas.GA.GA;
import metas.defaultMetha.Heuristic;

public class Demo {

	public static void main(String[] args) {

        String path = "bench_marks\\100\\50_1.txt";

        new Instances(path);

		BBO bbo = new BBO(300,10,(float) 0.005);
        GA ga = new GA(50,10000);
        Heuristic heuristic = new Heuristic(15,1000);


        ga.Exec();
		//bbo.BBO_Exec();
        //heuristic.Exec();

	}

}	
	
