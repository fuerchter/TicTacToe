import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ovgu.dke.teaching.ml.tictactoe.api.IBoard;
import de.ovgu.dke.teaching.ml.tictactoe.api.IMove;
import de.ovgu.dke.teaching.ml.tictactoe.api.IPlayer;
import de.ovgu.dke.teaching.ml.tictactoe.api.IllegalMoveException;
import de.ovgu.dke.teaching.ml.tictactoe.game.Move;

/**
 * @author Sebastian Fritz
 */
public class Fuerchter implements IPlayer {

	//Weights
	private float w1=103.175f;
	private float w2=39.2181f;
	private float w3=41.3384f;
	private float w4=-85.9388f;
	private float w5=28.2253f;
	private float w6=-30.9788f;
	
	public String getName() {
		return "Fuerchter";
	}

	public int[] makeMove(IBoard board) {
		//Create a clone of the board that can be modified
		IBoard copy = board.clone();
		
		List<IMove> moveHistory=copy.getMoveHistory();
		//First move
		if(moveHistory.isEmpty())
		{
			return new int[]{2, 2, 2};
		}
		
		IPlayer opponent=moveHistory.get(moveHistory.size()-1).getPlayer();
		
		int bSize=board.getSize();
		int[] bestMove=new int[3];
		int bestScore=-100;
		//Will assume weakest enemy move? Will also analyze boards with odd movecounts
		
		//Go through each possible move of me and opponent
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
					
					//Winning move
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
								//Make move with best score
								if(score > bestScore)
								{
									bestMove=new int[]{x, y, z};
									bestScore=score;
									//Stop as soon as 100 scoring board is found
									if(bestScore>=100)
									{
										return bestMove;
									}
								}
							}
						}
					}
				}
			}
		}

		return bestMove;
	}

	public void onMatchEnds(IBoard board) {
		return;
	}

	//Calls getAdjacent for me and opponent and combines it with the weights
	private int getScore(IBoard board)
	{		
		Map<Integer, Integer> myAdjacents=getAdjacent(this, board);
		
		List<IMove> moveHistory=board.getMoveHistory();
		IPlayer opponent=moveHistory.get(moveHistory.size()-1).getPlayer();
		Map<Integer, Integer> opAdjacents=getAdjacent(opponent, board);
		
		return	(int)(w1*myAdjacents.get(4)+w2*myAdjacents.get(3)+w3*myAdjacents.get(2)+
				w4*opAdjacents.get(4)+w5*opAdjacents.get(3)+w6*opAdjacents.get(2));
	}
	
	private Map<Integer, Integer> getAdjacent(IPlayer player, IBoard board)
	{
		Map<Integer, Integer> result=new HashMap<Integer, Integer>();
		result.put(2, 0);
		result.put(3, 0);
		result.put(4, 0);
		boolean[][][][] checked=new boolean[5][5][5][10]; //Preventing that xxxx will get recognized as 4, 3 and 2 adjacents at the same time
		
		if(board.getDimensions()==3)
		{
			int bSize=board.getSize();
			for(int z=0; z<bSize; z++)
			{
				for(int y=0; y<bSize; y++)
				{
					for(int x=0; x<bSize; x++)
					{
						int[] p=new int[]{x, y, z};
						if(board.getFieldValue(p)==player)
						{
							//0-9 directions
							for(int i=0; i<=9; i++)
							{
								if(checked[x][y][z][i])
								{
									continue;
								}
								
								int[] newP=p.clone();
								int count=0;
								
								boolean inbounds=true;
								do
								{
									count++; //Adjacent stone found
									if(count>1)
									{
										checked[newP[0]][newP[1]][newP[2]][i]=true;
									}
									
									//Single direction
									if(i==0)
									{
										newP[0]++;
										inbounds=newP[0]<bSize;
									}
									else if(i==1)
									{
										newP[1]++;
										inbounds=newP[1]<bSize;
									}
									else if(i==2)
									{
										newP[2]++;
										inbounds=newP[2]<bSize;
									}
									//Two directions
									else if(i==3)
									{
										newP[0]++;
										newP[2]++;
										inbounds=newP[0]<bSize && newP[2]<bSize;
									}
									else if(i==4)
									{
										newP[1]++;
										newP[2]++;
										inbounds=newP[1]<bSize && newP[2]<bSize;
									}
									else if(i==5)
									{
										newP[0]++;
										newP[1]++;
										inbounds=newP[0]<bSize && newP[1]<bSize;
									}
									//Diagonal
									else if(i==6)
									{
										newP[0]++;
										newP[1]++;
										newP[2]++;
										inbounds=newP[0]<bSize && newP[1]<bSize && newP[2]<bSize;
									}
									else if(i==7)
									{
										newP[0]++;
										newP[1]--;
										newP[2]++;
										inbounds=newP[0]<bSize && newP[1]>=0 && newP[2]<bSize;
									}
									else if(i==8)
									{
										newP[0]--;
										newP[1]++;
										newP[2]++;
										inbounds=newP[0]>=0 && newP[1]<bSize && newP[2]<bSize;
									}
									else if(i==9)
									{
										newP[0]--;
										newP[1]--;
										newP[2]++;
										inbounds=newP[0]>=0 && newP[1]>=0 && newP[2]<bSize;
									}
								}
								while(inbounds && board.getFieldValue(newP)==player);
								
								if(count>=2 && count<=4) //Minimum and maximum count of adjacent stones
								{
									result.put(count, result.get(count)+1);
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
