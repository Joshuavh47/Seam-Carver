import java.util.*;
import java.io.*;

public class Runner {

    public static void main(String[] args) throws IOException{
        Scanner scan=new Scanner(System.in);
        System.out.println("File name: ");
        String fileName=scan.next();
        System.out.println("How many vertical seams should be removed: ");
        String vert=scan.next();
        int vertRem=Integer.parseInt(vert);

        System.out.println("How many horizontal seams should be removed: ");
        String hor=scan.next();
        int horRem=Integer.parseInt(hor);

        System.out.println("Output file name: ");
        String outName=scan.next();
        Picture p=new Picture(fileName);
        SeamCarver s=new SeamCarver(p);
        long startTime=System.currentTimeMillis();

        for(int i=0;i<vertRem;i++){
            int[] arr=s.findVerticalSeam();
            s.removeVerticalSeam(arr);
        }

        for(int i=0;i<horRem;i++){
            int[] arr=s.findHorizontalSeam();
            s.removeHorizontalSeam(arr);
        }


        s.picture().save(outName);
        long endTime=System.currentTimeMillis();
        System.out.println("Image saved in "+(endTime-startTime)+" ms.");
    }
}
