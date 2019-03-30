package metas;

import data.reader.Instances;
import metas.BBO.BBO;

public class Demo {

	public static void main(String[] args) {

        String path = "bench_marks\\100\\50_1.txt";

        Instances instances = new Instances(path);

		BBO bbo = new BBO(instances);

		bbo.BBO_Exec();
	}

}	
	
