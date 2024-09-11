// package ch01.sec01; --> this is the directory, package belongs to chapt1, section1 (from the book)
// first JAva program
import classroom.Person;
import java.util.Random;

// public - can be called from other classes
public class HelloWorld {
    //static - an object doesn't need to be created in order to run this
    //void - it is not returning anything
    public static void main(String[] args){
//        System.out.println("Hello World!");
//        System.out.println("Hello World!".length());
//
//        int num = new Random().nextInt();
//        System.out.println(num);
        Person p = new Person("Katie");
        p.sleep();
    }

}