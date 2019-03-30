package metas;

import data.reader.Instances;
import metas.BBO.BBO;
import metas.GA.GA;

public class Demo {

	public static void main(String[] args) {

        String path = "bench_marks\\100\\50_1.txt";

        new Instances(path);

		BBO bbo = new BBO(300,10,(float) 0.005);
		bbo.BBO_Exec();


		GA ga = new GA(50,10000);
        //ga.Exec();



	}

}	
	
