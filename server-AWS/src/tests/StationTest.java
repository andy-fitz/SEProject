package tests;

import game.Player;
import game.Station;
import noc_db.Character_noc;
import noc_db.NOC_Manager;
import noc_db.Vehicle_noc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class StationTest {

	private Station station1;
	private Station station2;
	private Station station3;
	private Station station4;
	private Player player;
	private NOC_Manager noc;



	@Before
	public void setUp() throws IOException {
		station1 = new Station("UCD1", 100, new int[]{10,20,30,40});
		station2 = new Station("UCD2", 100, new int[]{10,20,30,40});
		station3 = new Station("UCD3", 100, new int[]{10,20,30,40});
		station4 = new Station("UCD4", 100, new int[]{10,20,30,40});


		noc = new NOC_Manager();
		noc.setup();
		Character_noc ch = noc.getRandomChar();
		player = new Player(1, "1.1.1.1",noc.getRandomChar(), noc.getVehicle(ch.getVehicle()));
	}

	@After
	public void tearDown(){
		station1 = station2 = station3 = station4 = null;
		player = null;

	}

	@Test
	public void constructorTest() {
		assertNotNull(station1);
		assertNotNull(station2);
		assertNotNull(station3);
		assertNotNull(station4);
		assertEquals("UCD1", station1.getId());
		assertEquals("UCD2", station2.getId());
		assertEquals("UCD3", station3.getId());
		assertEquals("UCD4", station4.getId());

	}

	@Test
	public void typeTest() {
		assertEquals("Station", station1.getType());
	}


    @Test
    public void getRentalAmount() {
		player.addNewPropertyBought(station1);
		station1.setOwner(player);
		assertEquals(10, station1.getRentalAmount());

		player.addNewPropertyBought(station2);
		station2.setOwner(player);
		assertEquals(20, station1.getRentalAmount());

		player.addNewPropertyBought(station3);
		station3.setOwner(player);
		assertEquals(30, station1.getRentalAmount());

		player.addNewPropertyBought(station4);
		station4.setOwner(player);
		assertEquals(40, station1.getRentalAmount());
    }
}
