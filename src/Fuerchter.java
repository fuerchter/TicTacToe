import java.util.ArrayList;
import java.util.List;

import de.ovgu.dke.teaching.ml.tictactoe.api.IBoard;
import de.ovgu.dke.teaching.ml.tictactoe.api.IMove;
import de.ovgu.dke.teaching.ml.tictactoe.api.IPlayer;
import de.ovgu.dke.teaching.ml.tictactoe.api.IllegalMoveException;
import de.ovgu.dke.teaching.ml.tictactoe.game.Move;

/**
 * Some comments ...
 * 
 * @author Name of your team members
 */
public class Fuerchter implements IPlayer {

	private int w1=1;
	private int w2=-1;
	
	public String getName() {
		return "Fuerchter";
	}

	public int[] makeMove(IBoard board) {
		//Create a clone of the board that can be modified
		IBoard copy = board.clone();
		
		if(copy.getMoveHistory().isEmpty())
		{
			return new int[]{2, 2, 2};
		}
		
		List<IMove> moveHistory=copy.getMoveHistory();
		IPlayer opponent=moveHistory.get(moveHistory.size()-1).getPlayer();
		
		int bSize=board.getSize();
		int[] bestMove=new int[3];
		int bestScore=-100;
		//Only try to put stones near other stones? Don't execute makeMove for fields that are known to be occupied
		//Will assume weakest enemy move for now!
		for(int z=0; z<bSize; z++)
		{
			for(int y=0; y<bSize; y++)
			{
				for(int x=0; x<bSize; x++)
				{
					IBoard myBoard=copy.clone();
					try {
						myBoard.makeMove(new Move(this, new int[]{x, y, z}));
					} catch (IllegalMoveException e) {
						continue;
					}
					
					if(myBoard.isFinalState())
					{
						return new int[]{x, y, z};
					}
					
					for(int oz=0; oz<bSize; oz++)
					{
						for(int oy=0; oy<bSize; oy++)
						{
							for(int ox=0; ox<bSize; ox++)
							{
								IBoard opBoard=myBoard.clone();
								try {
									opBoard.makeMove(new Move(opponent, new int[]{ox, oy, oz}));
								} catch (IllegalMoveException e) {
									continue;
								}
								
								int score=getScore(opBoard);
								if(score > bestScore)
								{
									bestMove=new int[]{x, y, z};
									bestScore=score;
								}
							}
						}
					}
				}
			}
		}

		System.out.println(bestScore);
		return bestMove;
	}

	public void onMatchEnds(IBoard board) {
		//Adjust weights
		return;
	}

	private int getScore(IBoard board)
	{		
		int x1=getAdjacent(this, board).size();
		List<IMove> moveHistory=board.getMoveHistory();
		IPlayer opponent=moveHistory.get(moveHistory.size()-1).getPlayer();
		int x2=getAdjacent(opponent, board).size();
		//System.out.println(x1+ " " +x2);
		
		return w1*x1+w2*x2;
	}
	
	private List<Integer> getAdjacent(IPlayer player, IBoard board)
	{
		List<Integer> result=new ArrayList<Integer>();
		if(board.getDimensions()==3)
		{
			int bSize=board.getSize();
			List<int[]> checked=new ArrayList<int[]>(); //Add already checked stones to a list so we don't get multiple stones?
			//For each field on the board
			for(int z=0; z<bSize; z++)
			{
				for(int y=0; y<bSize; y++)
				{
					for(int x=0; x<bSize; x++)
					{
						int[] p=new int[]{x, y, z};
						if(board.getFieldValue(p)==player)
						{
							//1-10 directions
							for(int i=1; i<=10; i++)
							{
								int[] newP=p.clone();
								int count=0;
								
								boolean inbounds=true;
								do
								{
									//checked.add(newP);
									count++; //Adjacent stone found
									
									//Single direction
									if(i==1)
									{
										newP[0]++;
										inbounds=newP[0]<bSize;
									}
									else if(i==2)
									{
										newP[1]++;
										inbounds=newP[1]<bSize;
									}
									else if(i==3)
									{
										newP[2]++;
										inbounds=newP[2]<bSize;
									}
									//Two directions
									else if(i==4)
									{
										newP[0]++;
										newP[2]++;
										inbounds=newP[0]<bSize && newP[2]<bSize;
									}
									else if(i==5)
									{
										newP[1]++;
										newP[2]++;
										inbounds=newP[1]<bSize && newP[2]<bSize;
									}
									else if(i==6)
									{
										newP[0]++;
										newP[1]++;
										inbounds=newP[0]<bSize && newP[1]<bSize;
									}
									//Diagonal
									else if(i==7)
									{
										newP[0]++;
										newP[1]++;
										newP[2]++;
										inbounds=newP[0]<bSize && newP[1]<bSize && newP[2]<bSize;
									}
									else if(i==8)
									{
										newP[0]++;
										newP[1]--;
										newP[2]++;
										inbounds=newP[0]<bSize && newP[1]>=0 && newP[2]<bSize;
									}
									else if(i==9)
									{
										newP[0]--;
										newP[1]++;
										newP[2]++;
										inbounds=newP[0]>=0 && newP[1]<bSize && newP[2]<bSize;
									}
									else if(i==10)
									{
										newP[0]--;
										newP[1]--;
										newP[2]++;
										inbounds=newP[0]>=0 && newP[1]>=0 && newP[2]<bSize;
									}
								}
								while(inbounds && board.getFieldValue(newP)==player);
								
								if(count>=2) //Only return 4 or more adjacent
								{
									result.add(count);
									//System.out.println(p[0]+ " " +p[1]+ " " +p[2]+ " direction " +i+ " count: " +count);
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
}
