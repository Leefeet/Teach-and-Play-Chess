package game;

import java.io.File;
import java.util.ArrayList;

import game.Space.DisplayColor;
import javafx.scene.image.Image;

public class Knight extends Piece
{

	public Knight(Board board, Space space, DisplayColor color)
	{
		super(board, space, color);
		
		//setting the piece's character
		setPieceCharacter("N");
		
		//setting the sprite to the Knight, depending on color of side
		File spriteFile;
		if (color == DisplayColor.White) //WHITE
		{
			spriteFile = new File("data/pieces/whiteHorse.png");
			
			//Setting Unicode character white
			setUnicodeCharacter("\u2658");
		}
		else //BLACK
		{
			spriteFile = new File("data/pieces/blackHorse.png");
			
			//Setting Unicode character black
			setUnicodeCharacter("\u265E");
		}
		String spriteURI = spriteFile.toURI().toString();
		Image spriteImage = new Image(spriteURI);

		setSprite(spriteImage);
	}
	
	// Knight has different movement. Moves 2 spaces in one direction then 1 in another. Can jump over pieces
	@Override
	public ArrayList<Space> getPossibleMoves()
	{
		//array of potential spaces
		ArrayList<Space> possibleMoves = new ArrayList<Space>();
		
		//we're going to check each of the four possible diagonal directions
		//	if we hit an ally piece, don't keep that space and stop checking in that direction
		//	if we hit an enemy piece, keep that space but stop checking in that direction
		//	if we hit null, stop checking in that direction
		//	if we hit an open space, keep and and continue in that direction
				
		//getting all 8 possible locations
		ArrayList<Space> adjacentSpaces = new ArrayList<Space>();
		adjacentSpaces.add(getBoard().getSpaceFromOffset(getSpace(), -2, -1));
		adjacentSpaces.add(getBoard().getSpaceFromOffset(getSpace(), -2, 1));
		adjacentSpaces.add(getBoard().getSpaceFromOffset(getSpace(), -1, -2));
		adjacentSpaces.add(getBoard().getSpaceFromOffset(getSpace(), 1, -2));
		adjacentSpaces.add(getBoard().getSpaceFromOffset(getSpace(), 2, -1));
		adjacentSpaces.add(getBoard().getSpaceFromOffset(getSpace(), 2, 1));
		adjacentSpaces.add(getBoard().getSpaceFromOffset(getSpace(), -1, 2));
		adjacentSpaces.add(getBoard().getSpaceFromOffset(getSpace(), 1, 2));
		
		//checking which spaces are valid
		for (Space s : adjacentSpaces)
		{
			//if space is null, it doesn't exist so don't add
			if (s == null)
			{
				continue;
			}
			//if piece is null, add piece
			if (s.getPiece() == null)
			{
				possibleMoves.add(s);
				continue;
			}
			//if an ally, don't add
			else if (s.getPiece().getColor() == this.getColor())
			{
				continue;
			}
			//if it's an enemy, add 
			else if (s.getPiece().getColor() != this.getColor())
			{
				possibleMoves.add(s);
				continue;
			}
		}
		
		//now that we have all potential moves, return
		return possibleMoves;
	}

	
	
	
	
	
}
