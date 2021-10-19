package utils;

public class Credentials {
	private String login;
	private String password;
		
		public Credentials() {
			login = "test@gmail.com";
			password = "test";
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}
}
