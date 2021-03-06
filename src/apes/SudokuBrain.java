package apes;

import sml.Agent;
import sml.Identifier;
import sml.Kernel;

import java.awt.Point;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.BufferOverflowException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created in Eclipse
 * Programmer: Apes
 * Date: 4/3/15
 * Time: 2:31 AM
 * Run this file and update the test cases in files\tests folder to test%x%.txt as unsolved sudoku test, where %x% = 'x'th number of test case
 * If you have trouble compiling these files, try googling it and you can visit the lisk below 
 * http://soar.eecs.umich.edu/articles/articles/soar-markup-language-sml/79-how-to-compile-sml-clients
 * @param <T>
 */
public class SudokuBrain<T>{

	private static final String PATH = "files\\sudoku.soar";
    private static final int INSERTION = 0;

    private int n = 9;
    private int[][] sudoku = new int[n][n];
    private int[][] sudoku_original = new int[n][n];
    private Stack<Point> places = null;

    
    BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(System.in));
    
    
    public SudokuBrain(BufferedReader reader) throws OopsException {
        try {
        	int counter = 0;
            for (int i = 0; i < n; i++) {
                String[] nums = reader.readLine().split(" ");
                for (int j = 0; j < n; j++) {
                	//System.out.println("|"+nums[j]+"|"+"("+i+","+j+")");
                	char char_temp = nums[j].charAt(0);
                	int ascii = (int) char_temp;
                	//System.out.println(ascii);
                	if(ascii==46)
                	{
                		sudoku[i][j] = 0;
                		sudoku_original[i][j] = 0;
                		counter++;
                	}
                	else
                	{	
                		sudoku[i][j] = Integer.parseInt(nums[j]);
                		sudoku_original[i][j] = Integer.parseInt(nums[j]);
                		//sudoku[i][j] = nums[j];
                	}
                }
            }
            System.out.println("No of blanks: "+counter);
        } catch (IOException e) {
            throw new OopsException("Cannot read!", e);
        } catch (NumberFormatException nfe) {
            throw new OopsException("Cannot convert!", nfe);
        }
        places = getInsertionPlaces();
    }

    
    
    public void solve() throws OopsException {
        Stack<Point> steps = new Stack<Point>();
        Stack<Point> insertionPlaces = getInsertionPlaces();
        Stack<ArrayList<Integer>> invalid_stack = new Stack<ArrayList<Integer>>();
        ArrayList<Integer> invalid_list = new ArrayList<Integer>();
        //Set<Integer> invalid_value = new HashSet<Integer>();
        
        //TODO: Add history
        
        @SuppressWarnings("unused")
		int backtracking_immovable = 0;
        //int[] possible_values_of_sudoku_blank = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int constraint_counter = 0;
        int backtracking_continuous = 0;
        
        //invalid_list.add(null);
        invalid_stack.push(invalid_list);
        while (!insertionPlaces.isEmpty()) {
        	System.out.println("Blanks: "+ insertionPlaces.size());
        	
        	System.out.println("invalid_stack_size: "+invalid_stack.size());
        	invalid_list = (ArrayList<Integer>) invalid_stack.pop();
        	if(backtracking_immovable == 1)
        	{
        		invalid_list = (ArrayList<Integer>) invalid_stack.pop();        		
        	}
        	
        	//System.out.println("insertion-list:"+insertionPlaces);
        	if(constraint_counter>2*9)
        	{
        		System.out.println("This seems to be going in an infinite while loop. Haulting now...");
        		break;
        		
        	}
        	//System.out.println("Insertion Top-A0: "+ insertionPlaces.peek());
        	Point place = insertionPlaces.pop();
        	
        	System.out.println("Place: "+place+"Invalid_List: "+ invalid_list);
        	
        	//if(backtracking_immovable == 1)
        	//{
        		//place = insertionPlaces.pop();
        	//}
            System.out.println("Selecting: ["+place.x+", "+place.y+"]");
            /*try {
            	System.out.println("Insertion Top-A1: "+ insertionPlaces.peek());
			} catch (Exception e) {
				// TODO: handle exception
				//do nothing, just move foreward
			}*/
            
            steps.push(place);
            Iterable<Integer> horizontal_constraints = findhorizontalConstraints(place);
            Iterable<Integer> vertical_constraints = findverticalConstraints(place);
            Iterable<Integer> box_constraints = findboxConstraints(place);
            Iterable<Integer> hardconstraints = findhardConstraints(place);
            //Iterable<Integer> constrains_all = null;
            Set<Integer> constraints = new HashSet<Integer>();
            constraints.addAll((Collection<? extends Integer>) horizontal_constraints);
            constraints.addAll((Collection<? extends Integer>) vertical_constraints);
            constraints.addAll((Collection<? extends Integer>) box_constraints);
            //constraints.addAll(invalid_value);
            constraints.addAll(invalid_list);
            System.out.println("Hard Before: "+hardconstraints+", "+invalid_list);
            //((Set<Integer>) hardconstraints).addAll(invalid_value);
            ((Set<Integer>) hardconstraints).addAll(invalid_list);
            
            System.out.println("Hard After: "+hardconstraints);
            //invalid list is reset after appending to constrains
            
                
            int all_constrain_size = constraints.size();
            System.out.println("Constraints All: "+constraints);
            System.out.println("Size of All_Constrains: "+all_constrain_size);
            int insertion = 0;
            //Iterable<Integer> constraints = new IteratorIterator<Integer>(horizontal_constraints, vertical_constraints, box_constraints);
            
            //int horizontal_insertion = makeChoice(horizontal_constraints);
            //int vertical_insertion = makeChoice(vertical_constraints);
            //int box_insertion = makeChoice(box_constraints);
            int all_insertion = makeChoice(constraints);
            int hard_insertion = makeChoice(hardconstraints);
            int hard_counter = 0;
            System.out.println("backtracking_continuous: "+backtracking_continuous );
            
            if(all_constrain_size == 9)
            {
            	backtracking_continuous++;
            	backtracking_immovable = 1;
            	System.out.println("backtracking...[Code: No possible Moves]");
            	//System.out.println("Insertion Top0: "+ insertionPlaces.peek());
            	Point back = steps.pop();
                sudoku[back.x][back.y] = INSERTION;
                insertionPlaces.push(back);
                //System.out.println("Insertion Top1: "+ insertionPlaces.peek());
                System.out.println("poping: ["+back.x+", "+back.y+"]");
            	back = steps.pop();
            	System.out.println("invalidating: ["+back.x+", "+back.y+"]"+" = "+ sudoku[back.x][back.y]);
            	//invalid_value.add(sudoku[back.x][back.y]);
            	invalid_list = (ArrayList<Integer>) invalid_stack.pop();
                sudoku[back.x][back.y] = INSERTION;
                insertionPlaces.push(back);
                //System.out.println("Insertion Top2: "+ insertionPlaces.peek());
                /*
                System.out.println("Do you wish to continue: ");
                try {
    				String buffer_reader_string = buffer_reader.readLine();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			*/
            }
            else if(all_constrain_size == 8)
            {
            	
            	
            	backtracking_continuous = 0;
            	backtracking_immovable = 0;
            	insertion = all_insertion;
            	
            	invalid_list.add(insertion);
            	System.out.println("In size [8], back: "+backtracking_continuous);
            	System.out.println("Inserting: "+all_insertion);
            }
            else{
            	
            	backtracking_immovable = 0;
            	backtracking_continuous = 0;
            	System.out.println("Inserting: "+all_insertion);
            	insertion = all_insertion;
	            /*
            	do{
	            	System.out.println("All and Hard: "+all_insertion+", "+hard_insertion);
	            	all_insertion = makeChoice(constraints);
	                hard_insertion = makeChoice(hardconstraints);
	                hard_counter++;
	            	if(hard_counter>9)
	            	{
	            		break;
	            	}
	            }while(all_insertion != hard_insertion);
	            if(hard_counter<9)
	            {
	            	System.out.println("Inserting: "+all_insertion);
	            	insertion = all_insertion;
	            }
	            else
	            {
	            	System.out.println("backtracking... [Code: Sir, i'm confused]");
	            	Point back = steps.pop();
	                sudoku[back.x][back.y] = INSERTION;
	                insertionPlaces.push(back);
	                /*back = steps.pop();
	                sudoku[back.x][back.y] = INSERTION;
	                insertionPlaces.push(back);*/
	            /*}
            	*/
	            invalid_list.add(insertion);
            }
            /*if(backtracking_continuous > 1)
        	{
                invalid_value.clear();
                System.out.println("Clearing Invalid List ***");
        	}*/
            
            
            /*
            else if(hard_insertion == horizontal_insertion && hard_insertion == vertical_insertion && hard_insertion == box_insertion)
            {
            	insertion = hard_insertion;
            }
            else if(horizontal_insertion == vertical_insertion && horizontal_insertion == box_insertion)
            {
            	insertion = horizontal_insertion;
            }
            */
//            else
//            {	
//            	insertion = hard_insertion;
//            
//            }
            
            /*
             * The "all_constrain_size" below is added to make sure that backtracking way does not get 
             * inside here.
             * After two continuous backtracking, the first invalid list is made null, in order to get a fresh start.
             * */
            if (insertion == INSERTION && all_constrain_size!=9) {
                Point back = steps.pop();
                sudoku[back.x][back.y] = INSERTION;
                insertionPlaces.push(back);
                constraint_counter++;
                System.out.println("constraint_counter: "+constraint_counter);
            }
            else 
            {
            	//invalid_value.clear();
                sudoku[place.x][place.y] = insertion;
                constraint_counter = 0;
                System.out.println("Invalid List before insertion at ["+place.x+", "+place.y+"]: "+ invalid_list);
                invalid_stack.push((ArrayList<Integer>) invalid_list.clone());
                invalid_list.clear();
                invalid_stack.push((ArrayList<Integer>) invalid_list.clone());
                System.out.println("Invalid_Stack: "+invalid_stack);
            }
            
            System.out.println("Enter something:");
        	
            //System.out.println("hi");
            //sudoku.toString();
            
            
            //print_sudoku();
            
            //System.out.println(sudoku[0][0]);
        }

    }

    private void print_sudoku() {
		// TODO Auto-generated method stub
    	int i = 0, j = 0;
    	//System.out.println("---------------------");
    	for(i=0;i<9;i++)
    	{
    		System.out.print("|");
    		if((i)%3==0)
    		{
    			System.out.println("-----+-----+-----|");
    			//i--;
    			System.out.print("|");
    		}
    		for(j=0;j<9;j++)
    		{
    			System.out.print(sudoku[i][j]);
    			if((j+1)%3!=0)
    			{
    				System.out.print(" ");
    			}
    			else
    				System.out.print("|");
    		}
    		System.out.println(" ");
    	}
    	System.out.println("|-----+-----+-----|");
    	
	}
    
    private Stack<Point> getInsertionPlaces() {
        Stack<Point> stack = new Stack<Point>();
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                if (sudoku[i][j] == INSERTION) {
                    stack.add(new Point(i, j));
                }
            }
        }
        System.out.println("Stack_size: "+ stack.size());
        //System.out.println("1234");
        Stack<Point> stack2 = new Stack<Point>();
        Stack<Point> stack3 = new Stack<Point>();
        int[][] constrain_array = new int[9][9];
        while(!stack.isEmpty())
        {
        	Point popy = stack.pop();
        	Set<Integer> hardconstraints = (Set<Integer>) findhardConstraints(popy);
        	int size = hardconstraints.size();
        	System.out.println("Size: "+size+", popy: ["+popy.x+", "+popy.y+"] $ ");
        	stack2.push(popy);
        	constrain_array[popy.x][popy.y] = size;
        }
        System.out.println("Stack_size2: "+stack2.size());
        //System.out.println("5678");
        int i, j , level = 1;
        do{
        	i = 0;
        	j = 0;
	        for(i=0;i<getSize();i++)
	        {
	        	for(j=0;j<getSize();j++)
	        	{
	        		if(constrain_array[i][j] == level)
	        		{
	        			stack3.push(new Point(i, j));
	        			System.out.println("Elements: "+stack3.peek()+", level: "+level);
	        		}
	        	}
	        }
	        level++;
        }while(level!=10);
        System.out.println("Stack_size3: "+stack3.size());
        //System.out.println("00");
        //System.out.println();
        return stack3;
        
        //return stack;
    }

    private int makeChoice(Iterable<Integer> constraints) throws OopsException {
        int choice = INSERTION;

        int agent_counter = 0;
        while(choice==INSERTION)
        {
        
        	
        	
	        Kernel kernel = Kernel.CreateKernelInNewThread();
	        if (kernel.HadError())
	            throw new OopsException(kernel.GetLastErrorDescription());
	
	        Agent agent = kernel.CreateAgent("soar");
	        if (agent.HadError())
	            throw new OopsException(agent.GetLastErrorDescription());
	
	        boolean status = agent.LoadProductions(PATH);
	        if (!status)
	            throw new OopsException(agent.GetLastErrorDescription());
	
	        for (int constraint : constraints) {
	            agent.GetInputLink().CreateIntWME("constraint", constraint);
	        }
	
	        agent.Commit();
	        agent.RunSelfTilOutput();
	
	        Identifier id = agent.GetOutputLink();
	        if (id == null)
	            throw new OopsException("There is no output-link!");
	
	        try {
	        	//System.out.println("ID.Agent: "+id.GetChild(0).GetValueAsString());
	            choice = Integer.parseInt(id.GetChild(0).GetValueAsString());
	            agent.ClearOutputLinkChanges();
	        } catch (NumberFormatException nfe) {
	            throw new OopsException(nfe);
	        } catch (NullPointerException npe) {
	            throw new OopsException(npe);
	        } finally {
	            kernel.DestroyAgent(agent);
	            kernel.Shutdown();
	        }
	        agent_counter++;
            if(agent_counter>8)
            {
            	System.out.println("Breaking");
            	break;
            }
	        
	        
	        
	        //return choice;
	        
            
        }
        System.out.println("Choosing: "+ choice);
		return choice;
    }

    public Iterable<Integer> findhorizontalConstraints(Point p) {
        Set<Integer> horizontal_constraints = new HashSet<Integer>();
        
        // Horizontal constraints
        for (int j = 0; j < getSize(); j++) {
            if (sudoku[p.x][j] != INSERTION) {
                horizontal_constraints.add(sudoku[p.x][j]);
            }
        }
        System.out.println("horizontal_constraints: "+horizontal_constraints);
        
        
        return horizontal_constraints;
    }
    
    
    public Iterable<Integer> findverticalConstraints(Point p) {
        Set<Integer> vertical_constraints = new HashSet<Integer>();

        // Vertical constraints
        //correction, int i =1 was mistakenly written by me. Status: Corrected
        for (int i = 0; i < getSize(); i++) {
            if (sudoku[i][p.y] != INSERTION) {
            	vertical_constraints.add(sudoku[i][p.y]);
            }
        }
        System.out.println("vertical_constraints: "+vertical_constraints);
        
        
        return vertical_constraints;
    }
    
    public Iterable<Integer> findboxConstraints(Point p) {
        Set<Integer> box_constraints = new HashSet<Integer>();        

        // Constraints in square
        int i = getNumberLteAndDividedOnThree(p.x);
        int bJ = getNumberLteAndDividedOnThree(p.y);
        do {
            int j = bJ;
            do {
                if (sudoku[i][j] != INSERTION) {
                    box_constraints.add(sudoku[i][j]);
                }
                j++;
            } while (j % 3 != 0);
            i++;
        } while (i % 3 != 0);
        System.out.println("box_constarints: "+box_constraints);
        
        
        return box_constraints;
    }
    

    public Iterable<Integer> findhardConstraints(Point p) {
        Set<Integer> constraints = new HashSet<Integer>();

        // Horizontal constraints
        for (int j = 0; j < getSize(); j++) {
            if (sudoku_original[p.x][j] != INSERTION) {
                constraints.add(sudoku[p.x][j]);
            }
        }

        // Vertical constraints
        for (int i = 1; i < getSize(); i++) {
            if (sudoku_original[i][p.y] != INSERTION) {
                constraints.add(sudoku[i][p.y]);
            }
        }

        // Constraints in square
        int i = getNumberLteAndDividedOnThree(p.x);
        int bJ = getNumberLteAndDividedOnThree(p.y);
        do {
            int j = bJ;
            do {
                if (sudoku_original[i][j] != INSERTION) {
                    constraints.add(sudoku[i][j]);
                }
                j++;
            } while (j % 3 != 0);
            i++;
        } while (i % 3 != 0);
        System.out.println("Hard_Constraints: "+constraints);
        return constraints;
    }

    
    private int getNumberLteAndDividedOnThree(int n) {
        int num = n;
        while (num % 3 != 0)
            num--;
        return num;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("+-------------+-------------+-------------+\n");
        for (int i = 0; i < getSize(); i++) {
            builder.append("| ");
            for (int j = 0; j < getSize(); j++) {
                if (sudoku[i][j] != INSERTION)
                    if(places.contains(new Point(i, j)))
                        builder.append("[").append(sudoku[i][j]).append("] ");
                    else
                        builder.append(" ").append(sudoku[i][j]).append("  ");
                else
                    builder.append("[_]").append(" ");
                if ((j + 1) % 3 == 0)
                    builder.append("| ");
            }
            builder.deleteCharAt(builder.length() - 1).append("\n");
            if ((i + 1) % 3 == 0)
                builder.append("+-------------+-------------+-------------+\n");
        }
        return builder.toString();
    }

    public int getSize() {
        return n;
    }


	public void forEachRemaining(Consumer<? super T> arg0) {
		// TODO Auto-generated method stub
		
	}


	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}


	public T next() {
		// TODO Auto-generated method stub
		return null;
	}


	public void remove() {
		// TODO Auto-generated method stub
		
	}
}

