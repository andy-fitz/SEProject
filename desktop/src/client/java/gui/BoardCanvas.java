package client.java.gui;

import client.java.main.Game;
import client.java.gameObjects.Location;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

public class BoardCanvas extends ResizableCanvas {

	private int currentTile;
	private Pane wrapperPane = (Pane) this.getParent();

	public BoardCanvas() {
		// Redraw canvas when size changes
		currentTile = 0;

		widthProperty().addListener(evt -> {
			try {
				draw();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		});

		heightProperty().addListener(evt -> {
			try {
				draw();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		});
	}

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
				g.fillText(Game.locations.get(locIndex).getName(), point.getX() + (getWidth() / 2) - 20, point.getY() + (getHeight() / 2));
			}
			locIndex+=1;
		}
	}

	// Draws individual tiles.
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
                g.strokeOval(x + (width / 2) - width / 30, y + (height / 2) - width / 30, width / 15, width /15);
                break;
            case 3:
                g.strokeOval(x + (width / 2) - width / 28, y + (height / 2) - width / 28, width / 14, width / 14);
                g.strokeOval(x + (width / 2) - width / 30, y + (height / 2) - width / 30, width / 15, width /15);
                g.strokeOval(x + (width / 2) - width / 32, y + (height / 2) - width / 32, width / 16, width / 16);
                break;
            case 4:
                g.setLineWidth(5);
                g.strokeOval(x + (width / 2) - width / 30, y + (height / 2) - width / 30, width / 15, width / 15);
                g.setLineWidth(1);
                break;
            default:
                break;
        }
	}

	private void drawImagedTile(Point2D point, GraphicsContext g, Location location){

		double width = getWidth();
		double height = getHeight();
		double x = point.getX();
		double y = point.getY();

		Image image = location.getImage();
		ColorAdjust ca = new ColorAdjust();

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

		g.setStroke(Color.TRANSPARENT);
		g.fillOval(x + (width / 2) - width / 42, y + (height / 2) - width / 42, width / 21, width / 21);
		g.strokeOval(x + (width / 2) - width / 42, y + (height / 2) - width / 42, width / 21, width / 21);

		g.setEffect(null);

		g.setFill(Color.GOLD);
		if(location.getPosition()<20)
			g.fillText(location.getName(), x + (width / 2), y + (height / 2) - width / 30);
		else
			g.fillText(location.getName(), x + (width / 2), y + (height / 2) - width / 30);
	}

	private Image getImage(Location location){

		StringBuilder sb = new StringBuilder();
		sb.append("/client/resources/images/worlds/");
		sb.append(location.getName().trim().replace(":", ""));
		sb.append(".jpg");

		try{
			return new Image( sb.toString() );
		}catch(Exception e){
			return null;
		}
	}

	public void getImages(){
		for(Location loc : Game.locations){
			loc.setImage(getImage(loc));
		}
	}


}