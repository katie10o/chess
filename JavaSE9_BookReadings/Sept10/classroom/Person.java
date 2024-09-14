package classroom;

public class Person {
    //private - cant access it directly - only expose it when its needed
    private String name;
    
    //this is the constructor 
    public Person(String name) {
        //this. makes sure there is no collisions - maybe a pointer? a little unsure...
        this.name = name;
    }
    public void sleep() {
        System.out.printf("%s is sleeping%n", name);
    }
}