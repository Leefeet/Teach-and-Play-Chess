package game;

import java.io.File;
import java.text.ParsePosition;
import java.util.ArrayList;

import game.Space.DisplayColor;
import javafx.scene.image.Image;

public class King extends Piece
{

	public King(Board board, Space space, DisplayColor color)
	{
		super(board, space, color);
		
		//since this is a King, setting boolean
		setIsKing(true);
		
		//setting the piece's character
		setPieceCharacter("K");
		
		//setting the sprite to the King, depending on color of side
		File spriteFile;
		if (color == DisplayColor.White) //WHITE
		{
			spriteFile = new File("data/pieces/whiteKing.png");
			
			//Setting Unicode character white
			setUnicodeCharacter("\u2654");
		}
		else //BLACK
		{
			spriteFile = new File("data/pieces/blackKing.png");
			
			//Setting Unicode character black
			setUnicodeCharacter("\u265A");
		}
		String spriteURI = spriteFile.toURI().toString();
		Image spriteImage = new Image(spriteURI);

		setSprite(spriteImage);
	}
	
	// King can only move one space in any direction, BUT has a few extra rules
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
		
		//ALSO
		//	We must check for a potential castle move
		//	We must not move the King in a spot that's dangerous
		
		//getting add adjacent spaces
		ArrayList<Space> adjacentSpaces = getBoard().getAdjacentSpaces(getSpace());
		
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
		
		//determining if castling is possible
		
		// TODO: Implement castling ability
		
		//now that we have all potential moves, return
		return possibleMoves;
	}

	
	
	
	
	
}
