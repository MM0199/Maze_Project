package project3;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Maze {
	
	// variables for maze 
	private static int cols, rows;
	private int degree = 180;
	private boolean isFinished = false;
	private Vertex[][] maze;
	private Stack<String> path = new Stack<String>();
	
	// constructor
	public Maze(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
		this.maze = new Vertex[cols + 2][rows + 2]; 
		int height = 800;
        int width = (int) Math.round(1.0 * height * cols / rows);
        StdDraw.setCanvasSize(width, height);

        StdDraw.setXscale(0, cols + 2);
        StdDraw.setYscale(0, rows + 2);
		init();
		generate();
	}
	// vertex class represent each cell
	public class Vertex {
		boolean North = true, 
				South = true,
				East = true,
				West = true, 
				isVisited = false;
	}
	// initialize the maze
	private void init() {
		for(int i = 0; i < maze.length; i++) {
			for(int j = 0; j < maze[i].length; j++)
				maze[i][j] = new Vertex();
		}
		// set border cells as visited
		for(int col = 0; col < cols + 2; col++) {
			maze[col][0].isVisited = true;
			maze[col][rows + 1].isVisited = true;
		}
		for (int row = 0; row < rows + 2; row++) {
			maze[0][row].isVisited = true;
			maze[cols + 1][row].isVisited = true;
		}
		
		// open the south of the bottom right and north of the top left
        maze[cols][1].South = false;   
        maze[1][rows].North = false;
	}

	// generate the maze
	private void generate(int col, int row) {
		
		maze[col][row].isVisited = true;
		
		// while there is an unvisited neighbor
		while(!maze[col + 1][row].isVisited || !maze[col][row +1].isVisited ||
				!maze[col - 1][row].isVisited || !maze[col][row - 1].isVisited)
		
			// pick random neighbor and delete the wall
			while(true) {
				double r = StdRandom.uniformInt(4);
				if(r == 0 && !maze[col + 1][row].isVisited) {
					maze[col][row].East = false;
					maze[col + 1][row].West = false;
					generate(col + 1, row);
					break;
				}
				else if(r == 1 && !maze[col][row + 1].isVisited) {
					maze[col][row].North = false;
					maze[col][row + 1].South = false;
					generate(col, row + 1);
					break;
				}
				else if(r == 2 && !maze[col - 1][row].isVisited) {
					maze[col][row].West = false;
					maze[col - 1][row].East = false;
					generate(col - 1, row);
					break;
				}
				else if(r == 3 && !maze[col][row - 1].isVisited) {
					maze[col][row].South = false;
					maze[col][row - 1].North = false;
					generate(col, row - 1);
					break;
				}
			}
	}
	// driver code for generate
	private void generate() {
		generate(1, 1);
	
		// delete some random walls
		for(int i = 0; i < rows; i++) {
			int col = 1 + StdRandom.uniformInt(cols - 1);
			int row = 1 + StdRandom.uniformInt(rows - 1);;
			maze[col][row].North = false;
			maze[col][row + 1].South = false;
		}
	}
	// find the path from top left to bottom right
	private void findPath(int col, int row) {
		if(isFinished) return;
		if (col == 0 || row == 0 || col == cols + 1 || row == rows + 1) return;
		if(maze[col][row].isVisited) return;
		maze[col][row].isVisited = true;
		
        StdDraw.picture(col + 0.5, row + 0.5, "footstep.png", 0.7, 0.7, degree);
        StdDraw.show();
        
        // use stack to store the path
        switch(degree) {
        case 0:
        		path.push("N");
        		break;
        case -90:
        		path.push("E");
        		break;
        case 180:
        		path.push("S");
        		break;
        case 90:
        		path.push("W");
        		break;
        }
        
		// reached lower right
		if(col == cols && row == 1) isFinished = true;
		
		// no wall North
		if(!maze[col][row].North) { 
			degree = 0;
			findPath(col, row + 1);
		}
		// no wall East
		if(!maze[col][row].East) {
			degree = -90;
			findPath(col + 1, row);
		}
		// no wall South
		if(!maze[col][row].South) {
			degree = 180;
			findPath(col, row - 1);
		}
		// no wall West
		if(!maze[col][row].West) {
			degree = 90;
			findPath(col - 1, row);
		}
		
		if(isFinished) return;
        
		// if facing deadend mark the cell and pop from the stack
		StdDraw.picture(col + 0.5, row + 0.5, "wrong.png", 0.7, 0.7);
        StdDraw.show();
        
        path.pop();
	}
	// driver code for findPath
	public void findPath() {	
		for(int col = cols; col >= 1; col--)
			for(int row = rows; row >= 1; row--)
				maze[col][row].isVisited = false;
		isFinished = false;
		findPath(1, rows);
	}
	// draw the maze
	public void draw() {        
		StdDraw.setPenColor(StdDraw.BLACK);
		for(int col = 1; col <= cols; col++) {
			for(int row = 1; row <= rows; row++) {
				if(maze[col][row].South) StdDraw.line(col, row, col + 1, row);
				if(maze[col][row].North) StdDraw.line(col, row + 1, col + 1, row + 1);
				if(maze[col][row].West) StdDraw.line(col, row, col, row + 1);
				if(maze[col][row].East) StdDraw.line(col + 1, row, col + 1, row + 1);
			}
		}
		
        StdDraw.show();
	}
	// print the path from the stack
	public String printPath() {
		String[] arrPath = new String[path.size()];
		String PATH = "";
		for(int i = arrPath.length - 1; i >= 0; i--) {
			arrPath[i] = path.pop();
		}
		for(int i = 0; i < arrPath.length; i++) {
			if(PATH.length() % 10 == 0) PATH += "\n";
			PATH += " " + arrPath[i];
		}
		return PATH;
	}
	// frame for input dimensions and output path
	public static class mazeFrame extends JFrame {
		// variables for display panel
		private JLabel label;
		private JTextArea textArea;
		private JButton generateBt;
		private JTextField tf_1 = new JTextField(10);
		private JTextField tf_2 = new JTextField(10);
			
		public mazeFrame() {
			JPanel widthPanel = new JPanel();
			JPanel heightPanel = new JPanel();
			JPanel buttonPanel = new JPanel();
			JPanel mainPanel = new JPanel();
			JPanel textAreaPanel = new JPanel();
			JPanel cPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			
			label = new JLabel("MAZE GENERATOR");
			mainPanel.add(label, BorderLayout.NORTH);
			label.setFont(new Font("Arial", Font.BOLD, 20));
			
			widthPanel.add(tf_1);
			widthPanel.setBorder(new TitledBorder(new EtchedBorder(), "WIDTH"));
			
			heightPanel.add(tf_2);
			heightPanel.setBorder(new TitledBorder(new EtchedBorder(), "HEIGHT"));
			
			textArea = new JTextArea(10, 20);
			textAreaPanel.add(textArea);
			textAreaPanel.setBorder(new TitledBorder(new EtchedBorder(), "PATH"));
			
			generateBt = new JButton("Generate");
			generateBt.addActionListener(new generateListener());
			buttonPanel.add(generateBt);
			buttonPanel.setBorder(new EtchedBorder());
			mainPanel.add(buttonPanel, BorderLayout.SOUTH);
			
			cPanel.setLayout(new GridLayout(1,3));
			cPanel.add(widthPanel);
			cPanel.add(heightPanel);
			cPanel.add(textAreaPanel);
			mainPanel.add(cPanel, BorderLayout.WEST);
			
			add(mainPanel);
		}
		
		// action listener for generate button & findPath button
		private class generateListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				cols = Integer.parseInt(tf_1.getText());	
				rows = Integer.parseInt(tf_2.getText());
				Maze maze = new Maze(cols, rows);
				dispose();
				setVisible(false);
				StdDraw.enableDoubleBuffering();
				maze.draw();
				maze.findPath();
				textArea.setText(maze.printPath());
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				setVisible(true);
			}	
		}
	}
	public static void createFrame() {
		mazeFrame frame = new mazeFrame();
		frame.setTitle("Maze Generator & Solver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		createFrame();
	}
}