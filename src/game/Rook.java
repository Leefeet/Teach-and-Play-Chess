package game;

import java.io.File;
import java.util.ArrayList;

import game.Space.DisplayColor;
import javafx.scene.image.Image;

public class Rook extends Piece
{

	public Rook(Board board, Space space, DisplayColor color)
	{
		super(board, space, color);
		
		//setting the piece's character
		setPieceCharacter("R");
		
		//setting the sprite to the rook, depending on color of side
		File spriteFile;
		if (color == DisplayColor.White) //WHITE
		{
			spriteFile = new File("data/pieces/whiteRook.png");
			
			//Setting Unicode character white
			setUnicodeCharacter("\u2656");
		}
		else //BLACK
		{
			spriteFile = new File("data/pieces/blackRook.png");
			
			//Setting Unicode character black
			setUnicodeCharacter("\u265C");
		}
		String spriteURI = spriteFile.toURI().toString();
		Image spriteImage = new Image(spriteURI);

		setSprite(spriteImage);
	}
	
	// Rook can only move Horizontally or vertically
	@Override
	public ArrayList<Space> getPossibleMoves()
	{
		//array of potential spaces
		ArrayList<Space> possibleMoves = new ArrayList<Space>();
		
		//we're going to check each of the four possible directions
		//	if we hit an ally piece, don't keep that space and stop checking in that direction
		//	if we hit an enemy piece, keep that space but stop checking in that direction
		//	if we hit null, stop checking in that direction
		//	if we hit an open space, keep and and continue in that direction
		
		// Up
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), -1, 0));
		// Down
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), 1, 0));
		// Left
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), 0, -1));
		// Right
		possibleMoves.addAll(getPossibleMovesInLine(getSpace(), 0, 1));
		
		//now that we have all potential moves, return
		return possibleMoves;
	}

	
	
	
	
	
}
