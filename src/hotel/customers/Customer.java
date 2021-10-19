package hotel.customers;

public class Customer {
	private String firstName;
	private String lastName;
	private String login;
	
	public Customer(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
		this.setLogin(generateLogin());
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	// generation d'un login aléatoire a 10 chiffres
	private String generateLogin() {
		String newLog = "";
		for (int i = 0; i < 10; i++) {
			newLog = randInt(0,9) + newLog;
		}
		return newLog;
	}
	public static int randInt(int min, int max) {
		int x = (int) ((Math.random() * ((max - min) + 1)) + min);
		return x;
	}
}
