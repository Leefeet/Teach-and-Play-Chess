package game;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Space
{
	//enum designates display color
	public enum DisplayColor
	{
		Black,
		White
	}	
	
	private DisplayColor mDisplayColor = null;
	
	private Piece mPiece = null;
	
	//the view that displays the piece image
	private ImageView mImageView = new ImageView();
	
	//the link to the entire board
	private Board mBoard = null;
	
	//location of Space on the board
	private int mRow = -1;
	private int mColumn = -1;
	
	//the pane that is the space (square)
	private Pane mPane = new Pane();
	private int mWidth = 100;
	
	//the name of this space on the board
	private String mSpaceCode = "";
	
	//tells if the space is a destination for piece movement
	private boolean mIsPotentialMoveDestination = false;
	
	//constructor
	public Space(Board board, int row, int column, DisplayColor color, int width)
	{
		mWidth = width;
		
		mDisplayColor = color;
		
		mBoard = board;
		
		//location on board
		setRow(row);
		setColumn(column);
		
		//empty, no piece to start
		mPiece = null;
		
		setUpPane();
	}
	//copy constructor
	public Space(Space otherSpace, Board newBoard)
	{
		mWidth = otherSpace.mWidth;
		
		mDisplayColor = otherSpace.mDisplayColor;
		
		mBoard = newBoard;
		
		//location on board
		setRow(otherSpace.getRow());
		setColumn(otherSpace.getColumn());
		
		//empty, no piece to start
		if (otherSpace.getPiece() == null)
		{
			mPiece = null;
		}
		else //there's a piece, we need to make a copy of it
		{
			//calling different constructor depending on the piece type
			if (otherSpace.getPiece() instanceof Pawn)
			{
				mPiece = new Pawn(newBoard, this, otherSpace.getPiece().getColor());
			}
			else if (otherSpace.getPiece() instanceof Rook)
			{
				mPiece = new Rook(newBoard, this, otherSpace.getPiece().getColor());
			}
			else if (otherSpace.getPiece() instanceof Knight)
			{
				mPiece = new Knight(newBoard, this, otherSpace.getPiece().getColor());
			}
			else if (otherSpace.getPiece() instanceof Bishop)
			{
				mPiece = new Bishop(newBoard, this, otherSpace.getPiece().getColor());
			}
			else if (otherSpace.getPiece() instanceof Queen)
			{
				mPiece = new Queen(newBoard, this, otherSpace.getPiece().getColor());
			}
			else if (otherSpace.getPiece() instanceof King)
			{
				mPiece = new King(newBoard, this, otherSpace.getPiece().getColor());
			}
		}
		
		setUpPane();
	}

	
	private void setUpPane()
	{
	   mPane.setPrefSize(mWidth,mWidth);
	   setDisplayNormal();   
       
	   mPane.addEventHandler((MouseEvent.MOUSE_CLICKED),(MouseEvent m)->
       {
    	   mBoard.spaceClicked(this);
       }
       );
	   
	   //hover
	   mPane.addEventHandler((MouseEvent.MOUSE_ENTERED),(MouseEvent m)->
       {
    	   mBoard.spaceHovered(this);
       }
       );
	}
	
	public void removePiece()
	{
		mPiece = null;
		
		//clearing image view
		mImageView.setImage(null);
		
		//clearing pane
		mPane.getChildren().clear();
	}
	
	public void setPiece(Piece piece)
	{
		mPiece = piece;
		
		//setting image on pane
		mImageView.setImage(mPiece.getSprite());
		mImageView.setPreserveRatio(true);
		mImageView.setFitWidth(mWidth);
		
		mPane.getChildren().add(mImageView);
	}
	
	//functions to change the look of spaces
	//The normal look of the space
	public void setDisplayNormal()
	{
		   Color color = new Color(0.0, 0.0, 0.0, 1.0); // Black
		   
		   //if white, make the space white
		   if (mDisplayColor == DisplayColor.White)
		   {
			   color = new Color(1.0, 1.0, 1.0, 1.0); // White
		   }
		   
			mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
			
			mPane.setBorder(new Border(new BorderStroke(Color.GREY,
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when selected
	public void setDisplaySelected()
	{
		   Color color = new Color(0.196, 0.659, 0.306, 1.0); // Green
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.RED,
					BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when Potential Space
	public void setDisplayDestination()
	{
		   Color color = new Color(0.271, 0.271, 1.0, 1.0); // Blue
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when Enemy Space
	public void setDisplayEnemy()
	{
		   Color color = new Color(1.0, 0.259, 0.259, 1.0); // Red
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when Possible Space to stop Check
	public void setDisplayCheckSave()
	{
		   Color color = new Color(0.949, 0.886, 0.0, 1.0); // Yellow
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when Disabled Space, cannot move there at this time
	public void setDisplayDisabled()
	{
		   Color color = new Color(0.5, 0.5, 0.5, 1.0); // Grey
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when a move was made
	public void setDisplayMoved()
	{
		   Color color = new Color(0.0, 0.882, 1.0, 1.0); // Light Blue
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}

	//Look when a space is hovered over ally Piece
	public void  setDisplayHoveredAlly()
	{
			Color color = new Color(0.431, 1.0, 0.431, 1.0); // Super Light Green
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when a space is hovered over enemy Piece
	public void  setDisplayHoveredEnemy()
	{
		   Color color = new Color(1.0, 0.666, 0.0, 1.0); // Orange
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when a space is potential Move
	public void  setDisplayPotentialMove()
	{
		Color color = new Color(0.75, 0.75, 1.0, 1.0); // Super Light Blue
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}
	
	//Look when a space is potential Enemy Move that kills
	public void  setDisplayPotentialKill()
	{
		   Color color = new Color(1.0, 0.75, 0.75, 1.0); // Super Light Red
		   
		   mPane.setBackground(new Background(new BackgroundFill(
					color, CornerRadii.EMPTY, Insets.EMPTY)));
		   
		   mPane.setBorder(new Border(new BorderStroke(Color.BLUE,
					BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(2))));
	}

	
	public void setRow(int row) { mRow = row; }
	public void setColumn(int column) { mColumn = column; }
	public void setIsPotentialMoveDestination(boolean isPotential) { mIsPotentialMoveDestination = isPotential; }
	
	public Piece getPiece() { return mPiece; }
	public Pane getPane() { return mPane; }
	public int getRow() { return mRow; }
	public int getColumn() { return mColumn; }
	public boolean getIsPotentialMoveDestination() { return mIsPotentialMoveDestination; }
	
}
