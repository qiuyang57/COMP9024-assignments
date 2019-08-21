import java.util.Scanner;

/**
 * Created by qiuya on 3/29/2017.
 */
class myProg {

    public static void main(String[] args) {
        String current;
        Scanner sc = new Scanner(System.in);
        System.out.println("Printing the file passed in:");
        while(sc.hasNext()&&!((current=sc.next()).equals(""))){
            System.out.print(current);}
    }
}
