/*
 * Class: CS561 Artificial Intelligence
 * Homework 2
 * Author: Shang-Fu Hsieh
 * Date: 10/15/2017
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class homework {
	private class Index {
		private short i;
		private short j;
		
		public Index(int i, int j) {
			this.i = (short)i;
			this.j = (short)j;
		}
		public boolean equals(Object obj) {
			Index comp = (Index) obj;
			if (comp.j == j && comp.i == i) {
				return true;
			}
			else {
				return false;
			}
		}
		public int hashCode() {
			return 100 * j + i;
		}
	}
	
	private class LinkedListComparator implements Comparator<LinkedList<Index>> {
        public int compare(LinkedList<Index> list1, LinkedList<Index> list2) {
            return list2.size() - list1.size();
        }
    }
	
	private class AlphaBetaReturn {
		private int eval;
		private LinkedList<Index> list;
		public AlphaBetaReturn() {
			this.eval = 0;
			this.list = null;
		}
	}
	
	public PriorityQueue<LinkedList<Index>> findStep(char[][] board) {
		int boardSize = board.length;
		HashMap<Index, LinkedList<Index>> store = new HashMap<Index, LinkedList<Index>>();
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[j][i] == '*') {
					continue;
				}
				Index index = new Index(i, j);
				if (i > 0 && board[j][i] == board[j][i - 1] && j > 0 && board[j][i] == board[j - 1][i]) {
					Index up_index = new Index(i - 1, j);
					Index left_index = new Index(i, j - 1);
					LinkedList<Index> up   = store.get(up_index);
					LinkedList<Index> left = store.get(left_index);
					if (up == left) {
						up.add(index);
						store.put(index, up);
					}
					else {
						for (Index ind : left) {
							up.add(ind);
							store.replace(ind, up);
						}
						up.add(index);
						store.put(index, up);
					}
				}
				else if (i > 0 && board[j][i] == board[j][i - 1]) {
					Index up_index = new Index(i - 1, j);
					LinkedList<Index> up = store.get(up_index);
					up.add(index);
					store.put(index, up);
				}
				else if (j > 0 && board[j][i] == board[j - 1][i]) {
					Index left_index = new Index(i, j - 1);
					LinkedList<Index> left = store.get(left_index);
					left.add(index);
					store.put(index, left);
				}
				else {
					LinkedList<Index> list = new LinkedList<Index>();
					list.add(index);
					store.put(index, list);
				}
			}
		}
		HashSet<LinkedList<Index>> set = new HashSet<LinkedList<Index>>();
		for (Map.Entry<Index, LinkedList<Index>> element : store.entrySet()) {
			set.add(element.getValue());
		}
		PriorityQueue<LinkedList<Index>> queue = new PriorityQueue<LinkedList<Index>>(new LinkedListComparator());
		for (LinkedList<Index> list : set) {
			queue.add(list);
		}
		return queue;
	}
	
	public char[][] updateBoard(char[][] board, LinkedList<Index> list) {
		int boardSize = board.length;
		for(Index index : list) {
			board[index.j][index.i] = '*';
		}
		for (int j = 0; j < boardSize; j++) {
			int indexNum  = board.length - 1;
			int indexStar = board.length - 1;
			while (indexNum >= 0 && indexStar >= 0) {
				while (indexStar >= 0 && board[j][indexStar] != '*') {
					indexStar--;
				}
				while (indexNum >= 0 && (board[j][indexNum] == '*' || indexNum > indexStar)) {
					indexNum--;
				}
				if (indexNum >= 0 && indexStar >= 0) {
					char temp = board[j][indexStar];
					board[j][indexStar] = board[j][indexNum];
					board[j][indexNum] = temp;
				}
			}
		}
		return board;
	}
	
	public AlphaBetaReturn alphaBeta(char[][] board, int alpha, int beta, boolean isMaxPlayer, boolean firstNode, double remainTime, int depth, boolean limit) {
		int boardSize = board.length;
		AlphaBetaReturn result = new AlphaBetaReturn();
		if (depth <= 0) {
			return result;
		}
		boolean isEnd = true;
		for (int i = boardSize - 1; i >= 0 && isEnd; i--) {
			for (int j = 0; j < boardSize && isEnd; j++) {
				if (board[j][i] != '*') {
					isEnd = false;
				}
			}
		}
		
		if (isEnd) {
			return result;
		}
		else {
			PriorityQueue<LinkedList<Index>> queue = findStep(board);
//			System.out.println("solution size: " + queue.size());
			int solutionSize = queue.size();
			int nextDepth = depth - 1;
			if (firstNode) {
				if (remainTime >= 30) {
					if (solutionSize >= 500) {
						nextDepth = 3;
					}
					else if (solutionSize < 500 && solutionSize >= 400) {
						nextDepth = 4;
					}
					else if (solutionSize < 400 && solutionSize >= 300) {
						nextDepth = 4;
					}
					else if (solutionSize < 300 && solutionSize >= 200) {
						nextDepth = 4;
					}
					else if (solutionSize < 200 && solutionSize >= 100) {
						nextDepth = 5;
					}
					else if (solutionSize < 100 && solutionSize >= 50) {
						nextDepth = 5;
					}
					else {
						nextDepth = 6;
					}
				}
				else {
					nextDepth = 2;
				}
			}

			boolean maxNotOne = false; 
			
			if (isMaxPlayer) {
				while (!queue.isEmpty()) {
					LinkedList<Index> list = queue.poll();
					int size = list.size();
					if (size >= 5) {
						maxNotOne = true;
					}
					int score = (size * size);
					int max_return = 0;
					
					if (!maxNotOne || size != 1 || !limit) {
						char[][] oldBoard = new char[boardSize][boardSize];
						for (int i = 0; i < boardSize; i++) {
							for (int j = 0; j < boardSize; j++) {
								oldBoard[j][i] = board[j][i];
							}
						}
						char[][] nextBoard = updateBoard(oldBoard, list);
						AlphaBetaReturn nextResult = alphaBeta(nextBoard, alpha, beta, false, false, remainTime, nextDepth, limit);
						max_return = score + nextResult.eval;
						if (alpha < max_return) {
							alpha = max_return;
							result.list = list;
						}
						if (alpha >= beta) {
//							System.out.println("max: depth: " + depth + ", alpha=" + alpha + ", beta=" + beta + ", return=" + (score + beta));
							result.eval = score + beta;
							return result;
						}
					}
					
				}
//				System.out.println("max: depth: " + depth + ", alpha=" + alpha + ", beta=" + beta + ", return=" + alpha);
				result.eval = alpha;
				return result;
			}
			else {
				while (!queue.isEmpty()) {
					LinkedList<Index> list = queue.poll();
					int size = list.size();
					if (size >= 5) {
						maxNotOne = true;
					}
					int score = (size * size);
					int min_return = 0;
					
					if (!maxNotOne || size != 1 || !limit) {
						char[][] oldBoard = new char[boardSize][boardSize];
						for (int i = 0; i < boardSize; i++) {
							for (int j = 0; j < boardSize; j++) {
								oldBoard[j][i] = board[j][i];
							}
						}
						char[][] nextBoard = updateBoard(oldBoard, list);
						AlphaBetaReturn nextResult = alphaBeta(nextBoard, alpha, beta, true, false, remainTime, nextDepth, limit);
						min_return = (-1) * score + nextResult.eval;
						if (beta > min_return) {
							beta = min_return;
							result.list = list;
						}
						if (alpha >= beta) {
//							System.out.println("min: depth: " + depth + ", alpha=" + alpha + ", beta=" + beta + ", return=" + ((-1) * score + alpha));
							result.eval = (-1) * score + alpha;
							return result;
						}
					}
					
					
				}
//				System.out.println("min: depth: " + depth + ", alpha=" + alpha + ", beta=" + beta + ", return=" + beta);
				result.eval = beta;
				return result;
			}
		}
	}
	public void printBoard(char[][] board) {
		int boardSize = board.length;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				System.out.print(board[j][i]);
			}
			System.out.println();
		}
	}
	
	private void output(char[][] board, LinkedList<Index> list) {
		updateBoard(board, list);
		try{
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			Index head = list.peek();
			char ch = 'A';
			for(int i = 0 ; i < head.j ; i++) {
				ch++;
			}
			writer.print(ch);
			writer.println(head.i + 1);
	    	int boardSize = board.length;
	    	for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					writer.print(board[j][i]);
				}
				writer.println();
			}
		    writer.close();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}

	
	private void go() {
		File file = new File("input.txt");
		int boardSize = 0;
		int fruitNum = 0;
		double remainTime = 0;
		char[][] board = null;
		try {
			Scanner sc = new Scanner(file);
			boardSize = Integer.parseInt(sc.nextLine());
			fruitNum = Integer.parseInt(sc.nextLine());
			remainTime = Double.parseDouble(sc.nextLine());
			board = new char[boardSize][boardSize];
			
			for (int i = 0; i < boardSize; i++) {
				String row = sc.nextLine();
				for (int j = 0; j < boardSize; j++) {
					board[j][i] = row.charAt(j);
				}
			}
			sc.close();
			
		} 
		catch (FileNotFoundException exp) {
			exp.printStackTrace();
		}

		AlphaBetaReturn result = alphaBeta(board, Integer.MIN_VALUE, Integer.MAX_VALUE, true, true, remainTime, 7, true);
		output(board, result.list);

	}


	public static void main(String[] args) {
//		long startTime = System.currentTimeMillis();
		
		homework project = new homework();
		project.go();
		
//		long deltaTime = System.currentTimeMillis() - startTime;
//		System.out.println("Time consumption: " + (deltaTime / 1000.0));
	}

}
