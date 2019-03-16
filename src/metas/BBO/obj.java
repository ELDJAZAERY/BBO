package metas.BBO;

public class obj{
	
	int distance;
	int pop ;
	
	
	public obj()
	{
		pop = 0;
		distance = 0;
	}
	
	
	
	public int compareTo(Object other) 
	{ 
	   	 float nombre1 = ((obj) other).distance; 
	   	 float nombre2 = this.distance; 
	     if (nombre1 < nombre2)  return -1; 
	     else if(nombre1 == nombre2) return 0; 
	     else return 1; 
	}
	
}
