import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.ovgu.dke.teaching.ml.tictactoe.api.IBoard;
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
		
		getScore(copy);
		
		int[] move=new int[3];
		boolean legalMove=false;
		//Check which move results in the highest score
		while(!legalMove)
		{
			legalMove=true;
			Random rand=new Random();
			move[0]=rand.nextInt(board.getSize());
			move[1]=rand.nextInt(board.getSize());
			move[2]=rand.nextInt(board.getSize());
			try {
				copy.makeMove(new Move(this, move));
			} catch (IllegalMoveException e) {
				// move was not allowed
				legalMove=false;
			}
		}
		
		return move;
	}

	public void onMatchEnds(IBoard board) {
		//Adjust
		return;
	}

	private int getScore(IBoard board)
	{
		int x1=0;
		int x2=0;
		
		//Find all 4-in-a-row
		getAdjacent(this, board);
		
		return w1*x1+w2*x2;
	}
	
	private List<Integer> getAdjacent(IPlayer player, IBoard board)
	{
		List<Integer> result=new ArrayList<Integer>();
		if(board.getDimensions()==3)
		{
			List<int[]> checked=new ArrayList<int[]>();
			//For each field on the board
			for(int z=0; z<board.getSize(); z++)
			{
				for(int y=0; y<board.getSize(); y++)
				{
					for(int x=0; x<board.getSize(); x++)
					{
						int[] p=new int[]{x, y, z};
						if(!checked.contains(p) && board.getFieldValue(p)==player)
						{
							//0-6 directions
							for(int i=0; i<2; i++)
							{
								int[] newP=p.clone();
								int count=0;
								
								boolean inbounds=true;
								do
								{
									checked.add(newP);
									count++;
									
									if(i==0)
									{
										//X direction
										newP[0]++;
										inbounds=newP[0]<board.getSize();
									}
									if(i==1)
									{
										//Y direction
										newP[1]++;
										inbounds=newP[1]<board.getSize();
									}
								}
								while(inbounds && board.getFieldValue(newP)==player);
								
								if(count>1)
								{
									result.add(count);
									System.out.println(p[0]+ " " +p[1]+ " " +p[2]+ " direction " +i+ " count: " +count);
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
