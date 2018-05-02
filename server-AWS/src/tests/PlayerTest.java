package tests;

import static org.junit.Assert.*;

import game.InvestmentProperty;
import game.RentalProperty;
import noc_db.NOC_Manager;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.awt.Color;
import java.io.IOException;

import game.Player;
import noc_db.Character_noc;

public class PlayerTest {

	private Player player;
	private NOC_Manager noc;
	private Character_noc ch;

	@Before
	public void setup() throws IOException {

		noc = NOC_Manager.getNocManager();
		noc.setup();

		ch = noc.getRandomChar();
		player = new Player(1, ch, noc.getVehicle(ch.getVehicle()), Color.BLUE);
	}


	@After
	public void tearDown() {
		player = null;
		noc = null;
		ch = null;
	}

	@Test
	public void constructorTest() {
		assertNotNull(player);
		assertEquals(1, player.getID());
		assertEquals(1000, player.getNetWorth());
		assertEquals(0, player.getPos());
	}

	@Test
	public void hasRolled() {
		assertFalse(player.hasRolled());
		player.useRoll();
		assertTrue(player.hasRolled());
	}

	@Test
	public void reset() {
		player.useRoll();
		player.useBoost();
		RentalProperty prop = new RentalProperty("UCD", 150);
		player.addNewPropertyBought(prop);

		player.reset();

		assertFalse(player.hasRolled());
		assertFalse(player.hasBoosted());
		assertFalse(player.hasBoosted());
	}

	@Test
	public void hasBought() {
		assertFalse(player.hasBought());

		RentalProperty prop = new RentalProperty("UCD", 150);
		player.addNewPropertyBought(prop);

		assertTrue(player.hasBought());
	}

	@Test
	public void hasBoosted() {
		assertFalse(player.hasBoosted());

		player.useBoost();

		assertTrue(player.hasBoosted());
	}

	@Test
	public void useRoll() {
		assertFalse(player.hasRolled());

		player.useRoll();

		assertTrue(player.hasRolled());
	}

	@Test
	public void topUpFuel() {
		player.topUpFuel();
		assertEquals(3, player.getFuel());
	}

	@Test
	public void getFuel() {
		assertEquals(1, player.getFuel());

		player.topUpFuel();

		assertEquals(3, player.getFuel());
	}

	@Test
	public void sendToJail() {
		assertNotEquals(29, player.getPos());

		player.sendToJail();

		assertEquals(29, player.getPos());
	}

	@Test
	public void releaseFromJail() {
		player.sendToJail();
		assertTrue(player.isInJail());

		player.releaseFromJail();
		assertFalse(player.isInJail());
	}

	@Test
	public void isInJail() {
		assertFalse(player.isInJail());
		player.sendToJail();
		assertTrue(player.isInJail());
	}

	@Test
	public void changeDirection() {
		player.moveForward(1);
		assertEquals(1, player.getPos());

		player.changeDirection();

		player.moveForward(1);
		assertEquals(0, player.getPos());
	}

	@Test
	public void incrementJailTurns() {
		player.sendToJail();
		assertTrue(player.isInJail());

		player.incrementJailTurns();
		player.incrementJailTurns();
		player.incrementJailTurns();

		assertFalse(player.isInJail());
	}

	@Test
	public void ownsThree() {

		InvestmentProperty prop1 = new InvestmentProperty("UCD1");
		InvestmentProperty prop2 = new InvestmentProperty("UCD2");
		InvestmentProperty prop3 = new InvestmentProperty("UCD3");

		prop1.setColour(Color.BLUE);
		prop2.setColour(Color.BLUE);
		prop3.setColour(Color.BLUE);

		player.addNewPropertyBought(prop1);
		player.addNewPropertyBought(prop2);
		assertFalse(player.ownsThree(Color.BLUE));


		player.addNewPropertyBought(prop3);
		assertTrue(player.ownsThree(Color.BLUE));

		assertFalse(player.ownsThree(Color.RED));

	}

	@Test
	public void getInfo() {
		RentalProperty prop = new RentalProperty("UCD", 150);
		prop.setOwner(player);

		player.addNewPropertyBought(prop);
		player.topUpFuel();
		player.moveForward(10);


		try {
			JSONObject obj = player.getInfo();
			assertEquals(Color.BLUE.getRGB(), obj.get("colour"));
			assertEquals(850, obj.getInt("balance"));
			assertEquals(3, obj.getInt("fuel"));
			assertEquals(player.getCharName(), obj.getString("character"));
			assertEquals(10, obj.getInt("position"));
			assertTrue(obj.getBoolean("moving_forward"));
			assertEquals(player.getID(), obj.getInt("id"));
			assertNotNull(obj.get("properties"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getPos() {
		assertEquals(0, player.getPos());
		player.moveForward(2);
		assertEquals(2, player.getPos());
	}

	@Test
	public void getID() {
		assertEquals(1, player.getID());
	}

	@Test
	public void getBalance() {
		assertEquals(1000, player.getBalance());
		player.receiveMoney(500);
		assertEquals(1500, player.getBalance());
	}

	@Test
	public void useBoost() {
		assertEquals(0, player.getPos());
		player.useBoost();
		assertEquals(1, player.getPos());
	}

	@Test
	public void moveForward() {
		assertEquals(0, player.getPos());
		player.moveForward(10);
		assertEquals(10, player.getPos());
	}

	@Test
	public void getCharName() {
		assertEquals(ch.getName(), player.getCharName());
	}

	@Test
	public void getCanName() {
		assertEquals(ch.getCanName(), player.getCanName());
	}

	@Test
	public void getId() {
		assertEquals(1, player.getID());
	}

	@Test
	public void getNetWorth() {
		assertEquals(1000, player.getNetWorth());
		RentalProperty prop = new RentalProperty("UCD",150);
		player.addNewPropertyBought(prop);
		assertEquals(850, player.getBalance());
		assertEquals(1000, player.getNetWorth());
	}

	@Test
	public void payMoney() {
		assertEquals(1000, player.getBalance());
		player.payMoney(300);
		assertEquals(700, player.getBalance());
	}

	@Test
	public void receiveMoney() {
		assertEquals(1000, player.getBalance());
		player.receiveMoney(300);
		assertEquals(1300, player.getBalance());
	}

	@Test
	public void getOwnedProperties() {
		RentalProperty prop1 = new RentalProperty("UCD1", 150);
		RentalProperty prop2 = new RentalProperty("UCD2", 150);

		player.addNewPropertyBought(prop1);
		player.addNewPropertyBought(prop2);

		assertEquals(prop1, player.getOwnedProperties().get(0));
		assertEquals(prop2, player.getOwnedProperties().get(1));
	}

	@Test
	public void addNewPropertyBought() {
		RentalProperty prop1 = new RentalProperty("UCD1", 150);
		RentalProperty prop2 = new RentalProperty("UCD2", 150);

		assertEquals(0,player.getOwnedProperties().size());
		player.addNewPropertyBought(prop1);
		player.addNewPropertyBought(prop2);

		assertEquals(2,player.getOwnedProperties().size());
	}

	@Test
	public void removePropertySold() {
		RentalProperty prop1 = new RentalProperty("UCD1", 150);
		RentalProperty prop2 = new RentalProperty("UCD2", 150);

		player.addNewPropertyBought(prop1);
		player.addNewPropertyBought(prop2);

		assertEquals(prop2, player.getOwnedProperties().get(1));

		player.removePropertySold(prop2);

		assertEquals(1,player.getOwnedProperties().size());
	}

	@Test
	public void getCharacter() {
		assertEquals(ch, player.getCharacter());
	}

	@Test
	public void isInDebt() {
		assertFalse(player.isInDebt());
		player.setDebt(100);
		assertTrue(player.isInDebt());
	}

	@Test
	public void setDebt() {
		Player player = new Player(2, ch, noc.getVehicle(ch.getVehicle()), Color.BLUE);
		player.setDebt(100, player);
		assertTrue(player.isInDebt());
	}

	@Test
	public void setDebt1() {
		player.setDebt(100, player);
		assertTrue(player.isInDebt());
	}

	@Test
	public void getPossessive() {
		String result;
		if (ch.getGender().equals("female")){
			result = "Her";
		} else {
			result = "His";
		}

		assertEquals(result, player.getPossessive());
	}

	@Test
	public void payDebt() {
		player.setDebt(100, player);
		assertTrue(player.isInDebt());
		player.payDebt();
		assertFalse(player.isInDebt());
	}
}
