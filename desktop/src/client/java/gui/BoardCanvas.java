package client.java.gui;

import client.java.main.Game;
import client.java.gameObjects.Location;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.json.JSONException;

/*
	This class represents the Game board.
	Displays:
			Board Background.
			Board Shape.
			Board Locations.
 */

public class BoardCanvas extends ResizableCanvas {

	private ArrayList<Text> titles = new ArrayList<>();
	private HashMap<Integer, Label> mortLabels = new HashMap();
	private HashMap<Integer, Label> typeLabels = new HashMap();

	public BoardCanvas() {
		// Redraw canvas when size changes
		widthProperty().addListener(evt -> {
			try {
				removeTileTitles();
				draw();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		});

		heightProperty().addListener(evt -> {
			try {
				removeTileTitles();
				draw();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		});
	}

	// Called on location changes
	public void draw() throws IOException, JSONException {
		double width = getWidth();
		double height = getHeight();

		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);

		Image background = new Image("/client/resources/images/space.jpg");
		gc.drawImage(background, 0, 0, width, height);

		drawInfinityShape(gc);
		drawTiles(gc);
	}

	// Draws infinity curve
	private void drawInfinityShape(GraphicsContext g) {
		double width = getWidth();
		double height = getHeight();

		PixelWriter pw = g.getPixelWriter();

		for (double t = -PI; t < PI; t += 0.03) {
			Point2D point = lemniscate(t);

			int pixel_x = (int) Math.round(point.getX() + width / 2);
			int pixel_y = (int) Math.round(point.getY() + height / 2);

			g.setFill(Color.rgb(45, 88, 158,0.2));
			g.fillOval(pixel_x-width/50,pixel_y-width/50,width/25,width/25);
		}
		for (double t = -PI; t < PI; t += 0.0001) {
			Point2D point = lemniscate(t);

			int pixel_x = (int) Math.round(point.getX() + width / 2);
			int pixel_y = (int) Math.round(point.getY() + height / 2);

			pw.setColor(pixel_x, pixel_y, Color.GOLD);
		}
	}

	// Draws all tiles.
	private void drawTiles(GraphicsContext g) throws IOException, JSONException {
		int locIndex = 0;
		for (double t = -PI / 2; t < PI - step; t += step) {
			if (Math.abs(t - PI / 2) > 0.000001) {    // so we don't draw two tiles in the centre.
				Point2D point = lemniscate(t);
				if(Game.locationsSet){
					drawTile(point, g, Game.locations.get(locIndex));
					drawImagedTile(point,g,Game.locations.get(locIndex));
				}
				else{
					drawTile(point, g, Game.locations.get(locIndex));
				}
				locIndex+=1;
			}
		}
		for (double t = -PI; t < -PI / 2 - step; t += step) {
			Point2D point = lemniscate(t);
			if(Game.locationsSet){
				drawTile(point, g, Game.locations.get(locIndex));
				drawImagedTile(point,g,Game.locations.get(locIndex));
			}
			else{
				drawTile(point, g, Game.locations.get(locIndex));
				g.setFill(Color.BLACK);
			}
			locIndex+=1;
		}
	}

	// Draws Tile Auras and adds houses, mortgage banners and location types.
	private void drawTile(Point2D point, GraphicsContext g, Location location) throws IOException, JSONException {

		double width = getWidth();
		double height = getHeight();
		double x = point.getX();
		double y = point.getY();

		Color locColour = location.getColour();
		double re = locColour.getRed();
		double gr = locColour.getGreen();
		double bl = locColour.getBlue();
		Color faded = Color.color(re,gr,bl,0.08);
		ArrayList<Stop> stops = new ArrayList<>();
		stops.add(new Stop(0,locColour));
		stops.add(new Stop(1, faded));
		RadialGradient aura = new RadialGradient(0,0,.5,.5, .5,true, CycleMethod.NO_CYCLE, stops );

		g.setEffect(new Glow(.8));
		g.setFill(aura);
		g.setStroke(location.getColour());

		g.setEffect(null);
		g.fillOval(x + (width / 2) - width / 28, y + (height / 2) - width / 28, width / 14, width / 14);

         // Drawing houses
        int numHouses = location.getHouses();
        g.setStroke(Color.GOLD);

        switch(numHouses){
            case 0:
                break;
            case 1:
                g.strokeOval(x + (width / 2) - width / 28, y + (height / 2) - width / 28, width / 14, width / 14);
                break;
            case 2:
                g.strokeOval(x + (width / 2) - width / 28, y + (height / 2) - width / 28, width / 14, width / 14);
                g.strokeOval(x + (width / 2) - width / 30, y + (height / 2) - width / 30, width / 15, width / 15);
                break;
            case 3:
                g.strokeOval(x + (width / 2) - width / 28, y + (height / 2) - width / 28, width / 14, width / 14);
                g.strokeOval(x + (width / 2) - width / 30, y + (height / 2) - width / 30, width / 15, width / 15);
                g.strokeOval(x + (width / 2) - width / 32, y + (height / 2) - width / 32, width / 16, width / 16);
                break;
			case 4:
				g.strokeOval(x + (width / 2) - width / 28, y + (height / 2) - width / 28, width / 14, width / 14);
				g.strokeOval(x + (width / 2) - width / 30, y + (height / 2) - width / 30, width / 15, width / 15);
				g.strokeOval(x + (width / 2) - width / 32, y + (height / 2) - width / 32, width / 16, width / 16);
				g.strokeOval(x + (width / 2) - width / 34, y + (height / 2) - width / 34, width / 17, width / 17);
            case 5:
                g.setLineWidth(5);
                g.strokeOval(x + (width / 2) - width / 30, y + (height / 2) - width / 30, width / 15, width / 15);
                g.setLineWidth(1);
                break;
            default:
                break;
        }

		if(location.isMortgaged() && location.isMortgagedLabelled()){
			relocateMortgageLabel(point, location);
		}

		if(location.isMortgaged() && !location.isMortgagedLabelled()){
			addMortgageLabel(point , location);
		}

		if(!location.isMortgaged() && location.isMortgagedLabelled()){
			removeMortgageLabel(location);
		}

		if((location.getType().equals("utility") || location.getType().equals("tax") || location.getType().equals("station")) && location.isTypeLabelled()) {
			relocateTypeLabels(point,location);
		}

		if((location.getType().equals("utility") || location.getType().equals("tax") || location.getType().equals("station")) && !location.isTypeLabelled()){
			addTypeLabel(point, location);
		}

	}

	// Adds Image and Titles to Location tiles
	private void drawImagedTile(Point2D point, GraphicsContext g, Location location){

		double width = getWidth();
		double height = getHeight();
		double x = point.getX();
		double y = point.getY();

		Image image = location.getImage();
		if(image == null){
			location.setImage(getUrlImage(location));
			image = location.getImage();
		}

		if(image != null) {
			try {
				ImagePattern imgPattern = new ImagePattern(image);
				g.setEffect(new Glow(.4));
				g.setFill(imgPattern);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		else{
			g.setFill(Color.BLACK);
		}
		
		if(location.getType().equals("investment")){
			g.setStroke(Color.TRANSPARENT);
		}
		else{
			g.setStroke(location.getColour());
			g.setLineWidth(10);
		}

		g.fillOval(x + (width / 2) - width / 42, y + (height / 2) - width / 42, width / 21, width / 21);
		g.strokeOval(x + (width / 2) - width / 42, y + (height / 2) - width / 42, width / 21, width / 21);

		g.setEffect(null);
		g.setStroke(null);
		g.setLineWidth(1);

        double titleRadius = width / 36;
        addTileTitle(location.getName(), titleRadius, point);
	}

	// Adds Galactic Core to Game
	public void addCenterTile(){
		Pane parent = (Pane) this.getParent();

		Image centerImage = new Image("client/resources/images/Galactic Core.gif");
		ImagePattern cImage = new ImagePattern(centerImage);

		Circle centerCircle = new Circle();
		centerCircle.centerXProperty().bind(parent.widthProperty().divide(2));
		centerCircle.centerYProperty().bind(parent.heightProperty().divide(2));
		centerCircle.radiusProperty().bind(parent.widthProperty().divide(41));
		centerCircle.setFill(cImage);
		parent.getChildren().add(centerCircle);
	}

	// Wraps title around location tiles
	private void addTileTitle(String title, double radius, Point2D center){

		int titleLength = title.length();
		int fontSize = 16;

		double degreeStep;
		double degree;

		if(titleLength >16){
			degreeStep = 220.0/titleLength;
			degree = 160.0 + degreeStep/2;
		}
		else if(titleLength > 7){
			degreeStep = 180.0/titleLength;
			degree = 180.0 + degreeStep/2;
		}
		else{
			degreeStep = 90.0/titleLength;
			degree = 225.0 + degreeStep/2;
		}

		for(int i = 0; i < titleLength; i++){
			// Letter position
			double pointX = center.getX() + getWidth()/2 + radius*Math.cos(Math.toRadians(degree));
			double pointY = center.getY() + getHeight()/2 + radius*Math.sin(Math.toRadians(degree));

			// Offset to align Letter properly
			pointX -= (fontSize / 3);
			pointY += (fontSize / 3);

			Text letter = new Text(pointX, pointY, String.valueOf(title.charAt(i)));
			titles.add(letter);
			letter.setTextAlignment(TextAlignment.CENTER);
			letter.setFont(new Font("Verdana", fontSize));
			letter.setFill(Color.WHITE);
			letter.setEffect(new Glow(.8));
			letter.setRotate(degree + 90);

			Pane wrap = (Pane) this.getParent();
			wrap.getChildren().add(letter);
			degree += degreeStep;
		}
	}

	// Removes Tile Titles on screen resize
	public void removeTileTitles() {
		Pane wrap = (Pane) this.getParent();
		wrap.getChildren().removeAll(titles);
	}

	// Adds mortgage label to property when mortgaged
	private void addMortgageLabel(Point2D center, Location location){
		Pane parent = (Pane) this.getParent();
		Label mort = new Label("MORTGAGED");
		mort.setFont(new Font("Verdana", 15));
		mort.setTextFill(Color.RED);
		mort.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255,0.6), null, null)));

		double pointX = center.getX() + (getWidth()/2) - (getWidth() / 42);
		double pointY = center.getY() + getHeight()/2;

		mort.setLayoutX(pointX);
		mort.setLayoutY(pointY);
        
		mortLabels.put(location.getPosition(), mort);
		parent.getChildren().add(mort);

		location.setMortgagedLabelled(true);
	}

	// removes mortgage label on redeem
	private void removeMortgageLabel(Location location){
		Pane parent = (Pane) this.getParent();
		Label mortLabel = mortLabels.get(location.getPosition());
		mortLabels.remove(location.getPosition());
		parent.getChildren().remove(mortLabel);
		location.setMortgagedLabelled(false);
	}

	// Relocates Mortgage label on screen resize / draw call
	private void relocateMortgageLabel(Point2D center, Location location){
		Label mortLabel = mortLabels.get(location.getPosition());

		double pointX = center.getX() + getWidth()/2 - mortLabel.getWidth()/2;
		double pointY = center.getY() + getHeight()/2;

		mortLabel.setLayoutX(pointX);
		mortLabel.setLayoutY(pointY);
	}

	// Adds Property Types to non investment property tiles.
	private void addTypeLabel(Point2D center, Location location){
		Pane parent = (Pane) this.getParent();
		Label typeLabel = new Label(location.getType().toUpperCase());
		typeLabel.setFont(new Font("Verdana", 15));
		typeLabel.setTextFill(Color.GREEN);
		typeLabel.setBackground(new Background(new BackgroundFill(Color.rgb(255,255,255,1.0), null, null)));

		double pointX = center.getX() + (getWidth()/2) - (getWidth() / 42);
		double pointY = center.getY() + getHeight()/2 - getWidth() / 43;

		typeLabel.setLayoutX(pointX);
		typeLabel.setLayoutY(pointY);

		typeLabels.put(location.getPosition(), typeLabel);
		parent.getChildren().add(typeLabel);

		location.setTypeLabelled(true);
	}

	// Relocates Location type labels on Screen resize
	private void relocateTypeLabels(Point2D center, Location location){
		Label typeLabel = typeLabels.get(location.getPosition());

		double pointX = center.getX() + getWidth()/2 - typeLabel.getWidth()/2;
		double pointY = center.getY() + getHeight()/2 - getWidth() / 43;

		typeLabel.setLayoutX(pointX);
		typeLabel.setLayoutY(pointY);
	}

	// Loads properties Image from the server
	private Image getUrlImage(Location location){
		if(location.getPosition() == 0){
			return new Image("/client/resources/images/Galactic Core.gif");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("http://52.48.249.220/worlds/");
		sb.append(location.getName().replace(" ","%20"));
		sb.append(".jpg");

		try {
			URL url = new URL(sb.toString());
			try{
				return new Image( url.toString() );
			}catch(Exception e){
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Loads all property Images from server
	public void getImages(){
		for(Location loc : Game.locations){
			Image urlImg = getUrlImage(loc);
			loc.setImage(urlImg);
		}
	}

	// Depreciated
	private Image getImage(Location location){

		if(location.getPosition() == 0){
			return new Image("/client/resources/images/Galactic Core.gif");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("/client/resources/images/worlds/");
		sb.append(location.getName());
		sb.append(".jpg");

		try{
			return new Image( sb.toString() );
		}catch(Exception e){
			return null;
		}
	}

}
