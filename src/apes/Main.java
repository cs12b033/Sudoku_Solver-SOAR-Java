package apes;

import sml.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;


public class Main {

    private static final String PATH = "files\\sudoku.soar";

    public Main() throws OopsException {

    }

    public static void main(String[] args) {
    	
    	int i = 0;
    	int no_of_test_cases = 5;
    	while(i<no_of_test_cases){
	        try {
	        	System.out.println("Running Test"+i);
	        	String inp = "files\\tests\\test"+i+".txt";
	        	//System.out.println(inp);
	            SudokuBrain s = new SudokuBrain(new BufferedReader(new FileReader(inp)));
	            
	//            Point p = new Point(3, 5);
	//            for (int i : s.findConstraints(p))
	//                System.out.print(i + " ");
	//            System.out.println();
	            System.out.println(s);
	            s.solve();	
	            System.out.println(s);
	            PrintWriter writer = null;
				try {
					String  out = "files\\output\\out"+i+".txt";
					writer = new PrintWriter(out, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            writer.println(s);
	            writer.close();
	        } catch (OopsException e) {
	            e.printStackTrace();  
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();  
	        }
	        i++;
    	}
    }
}
