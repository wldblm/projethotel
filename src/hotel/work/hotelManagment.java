package hotel.work;

import java.io.FileOutputStream;
import java.io.IOException;

import java.net.MalformedURLException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import hotel.customers.Customer;

import hotel.rooms.Room;

import utils.Credentials;


public class hotelManagment {
	
	private Room hotel[];
	
	public hotelManagment() throws MalformedURLException, DocumentException, IOException, MessagingException {
		hotel = generate();
		Scanner in = new Scanner(System.in);
		String userChoice = "";
		String employeLoggin = "emLog";
		System.out.println("Entrez votre identifiant pour vous connecter ou connaitre les détails de votre réservation :");
		while(true) { // boucle sur l'identification
			userChoice = in.next();
			
			if (userChoice.equals(employeLoggin)) { // Vérifie s'il s'agit d'un employé et affiche le menu jusqu'à ce qu'il le quitte
		        while(userChoice.toUpperCase() != "Q"){
		    		if(userChoice.equalsIgnoreCase("Q")) { // Pour sortir du while et revenir à l'écran d'authentification
		    			break;
		    		}

		            System.out.println("");
		            System.out.println("----------------------------------------  MENU HOTEL CDA JAVA  ----------------------------------------");
		            System.out.println("A - Afficher l'état de l'hôtel");
		            System.out.println("B - Afficher le nombre de chambres réservées");
		            System.out.println("C - Afficher le nombre de chambres libres");
		            System.out.println("D - Afficher le numéro de la première chambre vide");
		            System.out.println("E - Afficher le numéro de la dernière chambre vide");
		            System.out.println("F - Réserver une chambre (Le programme doit réserver la première chambre videselon les critéres choisis)");
		            System.out.println("G - Libérer une chambre (Le programme doit libérer la chambre en utilisant le numéro de chambre ou le nom du client)");
		            System.out.println("H - Modifier une réservation");
		            System.out.println("I - Annuler une réservation");
		            System.out.println("Q - Quitter");
		            System.out.println("----------------------------------------  MENU HOTEL CDA JAVA  ----------------------------------------");
		            System.out.println("");
		            System.out.println("Que voulez-vous faire ?");
		
		            userChoice = in.next();
		
					switch(userChoice.toUpperCase()){
						case "A" :
							hotelStatus(in);
							break;
						case "B" :
							getOccupiedRooms(in);
							break;
						case "C" :
							getFreeRooms(in);
							break;
						case "D" :
							firstFreeRoom(in);
							break;
						case "E" :
							lastFreeRoom(in);
							break;
						case "F" :
							login(in);
							doAReservation(in, userChoice);
							break;
						case "G" :
							login(in);
							freeAnOccupiedRoom(in, userChoice);
							break;
						case "H" :
							login(in);
							editReservation(in);
							break;
						case "I" :
							login(in);
							deleteReservation(in);
							break;
					}
		        } // fin du menu pour un employé
			        
			} // fin de l'identification
			else {
				displayClientResa(userChoice); // Vérifie s'il s'agit d'un client enregistré et affiche sa résa pdt quelques secondes
			}
		}
	        
    }
	
	// Generation des chambres d'hotel
	public Room[] generate() {
		
		String listeChambresCsv [] ={"Type chambre;Taille;Vues;Occupation;tarif;Nombre de chambre;Options",
				"Chambre Vue Piscine;44 mètres carrés;Piscine Centrale;2 adultes et 2 enfants de moins de 12 ans;300;7;Fer et planche à repasser sur demande|Téléphone |Télévision par câble|Climatisation|Bouilloire électrique|Concierge 24h/24",
				"Chambre Vue Jardin;44 mètres carrés;Jardin, Forêt ou Golf;2 adultes et 2 enfants de moins de 12 ans;314;12;Fer et planche à repasser sur demande|Téléphone |Télévision par câble|Climatisation|Bouilloire électrique|Concierge 24h/24",
				"Chambre Vue Océan;44 mètres carrés;Partielle Océan et Forêt ou Golf;2 adultes et 2 enfants de moins de 12 ans;350;8;Fer et planche à repasser sur demande|Téléphone |Accès Internet haut débit sans fil|Lecteur DVD sur demande|Télévision par câble|Climatisation|Service aux chambres 24h/24|Concierge 24h/24",
				"Chambre vue imprenable sur l'océan;44 mètres carrés;Océan;2 adultes et 2 enfants de moins de 12 ans;350;10;Fer et planche à repasser sur demande|Téléphone |Accès Internet haut débit sans fil|Lecteur DVD sur demande|Télévision par câble|Climatisation|Service aux chambres 24h/24|Concierge 24h/24",
				"Suite CDA;82 mètres carrés;Océan et Golf;2 adultes et 2 enfants de moins de 12 ans;450;12;Sèche-cheveux|Coffre-fort dans la chambre|Minibar|Téléphone |Accès Internet haut débit sans fil|Lecteur DVD sur demande|Télévision par câble|Climatisation|Service aux chambres 24h/24|Concierge 24h/24",
				"Suite Executive;140 mètres carrés;Océan;2 adultes et 2 enfants de moins de 12 ans;550;5;Sèche-cheveux|Coffre-fort dans la chambre|Minibar|Téléphone |Accès Internet haut débit sans fil|Lecteur DVD sur demande|Télévision par câble|Climatisation|Service aux chambres 24h/24|Concierge 24h/24",
				"Suite Ambassadeur;230 mètres carrés;Océan;2 adultes et 2 enfants de moins de 12 ans;1650;7;Sèche-cheveux|Coffre-fort dans la chambre|Minibar|Téléphone |Accès Internet haut débit sans fil|Lecteur DVD sur demande|Télévision par câble|Climatisation|Service aux chambres 24h/24|Concierge 24h/24",
				"Suite Royale;342 mètres carrés;Océan;2 adultes et 2 enfants de moins de 12 ans;2400;4;Sèche-cheveux|Coffre-fort dans la chambre|Minibar|Téléphone |Accès Internet haut débit sans fil|Lecteur DVD sur demande|Télévision par câble|Climatisation|Service aux chambres 24h/24|Concierge 24h/24"};
		
		hotel = new Room[65];
		// nbPrev correspond à la dernière chambre instanciée
		int nbPrev = 0;

		//On boucle sur le Csv et à chaque itération on crée un tableau de chaque attributs de classe qu'on met dans des variables puis on instance une room nb fois dans le tableau hotel
		for (int i = 1; i < listeChambresCsv.length; i++) {
			
			String tab[] = listeChambresCsv[i].split(";");
			String roomType = tab[0];
			String size = tab[1];
			String view = tab[2];
			String maxPer = tab[3];
			String price = tab[4];
			int nb = Integer.parseInt(tab[5]);
			String options[] = tab[6].split("\\|");
				for (int k = 0; k < nb; k++) {
					hotel[nbPrev] = new Room(roomType, view, maxPer, size, price, options);
					nbPrev++;
				}
			
		}
		
		return hotel;
	}

	public void hotelStatus(Scanner in) {
		LocalDate response = askDate(in);
		for (int i = 0; i < hotel.length; i++) {
			
			Customer customers[] = hotel[i].getCustomers();
			LocalDate startDates[] = hotel[i].getStartDates();
			LocalDate endDates[] = hotel[i].getEndDates();
			boolean isFree = true;

			// si la date donnée est entre la date de debut et de fin alors la chambre est occupée
			for (int j = 0; j < customers.length; j++) {
				if(customers[j] != null && startDates[j].isBefore(response.plusDays(1)) && endDates[j].isAfter(response.minusDays(1))) {
					isFree = false;
					System.out.println("La chambre " + i + " de type " + hotel[i].getRoomType() + " est occupé par " + customers[j].getFirstName() + " " + customers[j].getLastName() + " du " + startDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + " au " + endDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)));
				}
				
			}
			// Si elle est vide on affiche la chambre vide
			if(isFree) {
				System.out.println("La chambre numéro " + i + " de type " + hotel[i].getRoomType() + " est libre.");
			}
		}
	}

	public void getOccupiedRooms(Scanner in) {
		// On recupère la date à laquelle on veut afficher les chambres occupée;
		LocalDate response = askDate(in);	
		boolean allFree = true;
		int count = 0;
		for (int i = 0; i < hotel.length; i++) {
			
			Customer customers[] = hotel[i].getCustomers();
			LocalDate startDates[] = hotel[i].getStartDates();
			LocalDate endDates[] = hotel[i].getEndDates();

			// // si la date donnée est entre la date de debut et de fin alors la chambre est occupée
			for (int j = 0; j < customers.length; j++) {
				if(customers[j] != null && startDates[j].isBefore(response.plusDays(1)) && endDates[j].isAfter(response.minusDays(1))) {
					allFree = false;
					count ++; // On incrémente un compteur
					if(i == hotel.length-1) {
						System.out.println(count + " chambres réservées de type " + hotel[i].getRoomType());
						break;
					}
					// Si la chambre d'apres n'est pas du meme type on afficher le nombre de chambre occupées du type actuel et on remet le compteur a 0
					else if(!hotel[i].getRoomType().equals(hotel[i+1].getRoomType())) {
						System.out.println(count + " chambres réservées de type " + hotel[i].getRoomType());
						count = 0;
					}
				}
				
			}
			
		}
		if(allFree) {
			System.out.println("Toutes les chambres sont libres");
		}
		
	}
	
	public void firstFreeRoom(Scanner  in) {

		// On recupère la date à laquelle on veut afficher la chambre libre;
				LocalDate response = askDate(in);	
				boolean allOccupied = true;
				boolean free = true;
				for (int i = 0; i < hotel.length; i++) {
					Customer customers[] = hotel[i].getCustomers();
					LocalDate startDates[] = hotel[i].getStartDates();
					LocalDate endDates[] = hotel[i].getEndDates();
					
					for (int j = 0; j < customers.length; j++) {
						// si on entre dans cette condition c'est que la chambre est occupée
						if(customers[j] != null && startDates[j].isBefore(response.plusDays(1)) && endDates[j].isAfter(response.minusDays(1))) {
							free = false;
							break;
						}		
					}
					// Sinon c'est que la chambre est libre alors on l'affiche
					if(free) {
						System.out.println("La première chambre libre est la chambre numéro " + i + " de type " + hotel[i].getRoomType());
						allOccupied = false;
						break;
					}
					if(!allOccupied){
						break;
					}
				}
				if(allOccupied) {
					System.out.println("Aucune chambre n'est libre");
				}
	}
	
	public void lastFreeRoom(Scanner  in) {

		// On recupère la date à laquelle on veut afficher la chambre libre;
		LocalDate response = askDate(in);	
		boolean allOccupied = true;
		boolean free = true;
		for (int i = hotel.length - 1; i > 0; i++) {
			Customer customers[] = hotel[i].getCustomers();
			LocalDate startDates[] = hotel[i].getStartDates();
			LocalDate endDates[] = hotel[i].getEndDates();
			
			for (int j = 0; j < customers.length; j++) {
				// si on entre dans cette condition c'est que la chambre est occupée
				if(customers[j] != null && startDates[j].isBefore(response.plusDays(1)) && endDates[j].isAfter(response.minusDays(1))) {
					free = false;
					break;
				}		
			}
			// Sinon c'est que la chambre est libre alors on l'affiche
			if(free) {
				System.out.println("La dernière chambre libre est la chambre numéro " + i + " de type " + hotel[i].getRoomType());
				allOccupied = false;
				break;
			}
			if(!allOccupied){
				break;
			}
		}
		if(allOccupied) {
			System.out.println("Aucune chambre n'est libre");
		}
	}
	
	public void getFreeRooms(Scanner in) {
		// On recupère la date à laquelle on veut afficher les chambres libre;
		LocalDate response = askDate(in);
		boolean allOccupied = true;
		int count = 0;
		boolean free = true;
		
		for (int i = 0; i < hotel.length; i++) {
			Customer customers[] = hotel[i].getCustomers();
			LocalDate startDates[] = hotel[i].getStartDates();
			LocalDate endDates[] = hotel[i].getEndDates();
			free = true;
			for (int j = 0; j < customers.length; j++) {

				// Si on rentre dans cette condition c'est que la chambre est occupée
				if(customers[j] != null && startDates[j].isBefore(response.plusDays(1)) && endDates[j].isAfter(response.minusDays(1))) {
					free = false;		
					break;
				}
			}
			
			
			// Si on a bouclé sur les 3 clients et que la chambre n'est pas occupée alors on incremente count
			if(free) {
				allOccupied = false;
				count ++;
				if(i == hotel.length-1) {
					System.out.println(count + " chambres libre de type " + hotel[i].getRoomType());
					break;
				}
				// Si la chambre d'apres n'est pas du meme type on afficher le nombre de chambre libre du type actuel et on remet le compteur a 0
				else if(!hotel[i].getRoomType().equals(hotel[i+1].getRoomType())) {
					System.out.println(count + " chambres libre de type " + hotel[i].getRoomType());
					count = 0;
				}
			}
		}
		
		if(allOccupied) {
			System.out.println("Toutes les chambres sont occupées pour cette date");
		}
	}
	
	public void deleteReservation(Scanner in) {
		System.out.println("Veuillez saisir le nom auquel la reservation à été faite");
		String lastName = in.next();

		System.out.println("Veuillez saisir le prénom auquel la reservation à été faite");
		String firstName  = in.next();

		System.out.println("Veuillez saisir la date du début de la réservation que vous souhaitez annuler");
		LocalDate startDate = askDate(in);

		System.out.println("Veuillez saisir la date de fin de la réservation que vous souhaitez annuler");
		LocalDate endDate = askDate(in);
		
		System.out.println("Veuillez saisir le numéro de votre chambre");
		int i = in.nextInt();
		
		boolean notFound = true;
		
		
			Customer customers[] = hotel[i].getCustomers();
			LocalDate startDates[] = hotel[i].getStartDates();
			LocalDate endDates[] = hotel[i].getEndDates();
			
				for (int j = 0; j < customers.length; j++) {
					if(customers[j] != null && firstName.equalsIgnoreCase(customers[j].getFirstName()) && lastName.equalsIgnoreCase(customers[j].getLastName()) && startDate.equals(startDates[j]) && endDate.equals(endDates[j])) {
						customers[j] = null;
						startDates[j] = null;
						endDates[j] = null;
						notFound = false;
						System.out.println("La réservation de " + lastName + " " + firstName + " du " +  startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE))  + " au " + endDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + " à bien été supprimée.");
						break;
					}
				}
		
		if(notFound) {
			System.out.println("Pas de réservation correspondante");
		}
	}
	
	public void editReservation(Scanner in) {
		System.out.println("Veuillez saisir le nom auquel la reservation à été faite");
		String lastName = in.next();

		System.out.println("Veuillez saisir le prénom auquel la reservation à été faite");
		String firstName  = in.next();

		System.out.println("Veuillez saisir la date du début de la réservation que vous souhaitez modifer");
		LocalDate startDate = askDate(in);

		System.out.println("Veuillez saisir la date de fin de la réservation que vous souhaitez modifier");
		LocalDate endDate = askDate(in);
		
		System.out.println("Veuillez saisir le numéro de la chambre");
		int i = in.nextInt();
		
		boolean notFound = true;
		
		
			Customer customers[] = hotel[i].getCustomers();
			LocalDate startDates[] = hotel[i].getStartDates();
			LocalDate endDates[] = hotel[i].getEndDates();
			int diff = 0;
			int diff2 = 0;
			int price = 0;
			
				for (int j = 0; j < customers.length; j++) {
					if(customers[j] != null && firstName.equalsIgnoreCase(customers[j].getFirstName()) && lastName.equalsIgnoreCase(customers[j].getLastName()) && startDate.equals(startDates[j]) && endDate.equals(endDates[j])) {
						
						notFound = false;
						// On calcule la difference entre les date actuel	
						diff = Period.between(startDate, endDate).getDays();
						
						// On demande a l'utilisateur de saisir une nouvelle date de début
						System.out.println("Veuillez saisir la nouvelle date de debut de reservation");
						startDate = askDate(in);

						// Tant que la date de début est inferieur a la date du jour on demande une nouvelle date
						while(startDate.isBefore(LocalDate.now())) {
							System.out.println("La date de début est inferieur a la date du jour veuillez saisir une autre date");
							startDate = askDate(in);
						}
						
						// On demande a l'utilisateur de saisir une nouvelle date de fin
						System.out.println("Veuillez saisir la nouvelle date de fin de reservation");
						endDate = askDate(in);
						// Tant que la date de début est inferieur a la date du debut on demande une nouvelle date
						while(endDate.isBefore(startDate)) {
							System.out.println("La date de fin est inferieur a la date du début du séjour veuillez saisir une autre date de fin" );
							endDate = askDate(in);
						}
						
						for(int k = 0; k < startDates.length; k++) {
							System.out.println(j);
							System.out.println(k);
							if(customers[k] == null || j == k) {
								//System.out.println("customers " + k +" est null");
							}
							else if(( startDate.isAfter(startDates[k]) && startDate.isBefore(endDates[k]) ) || ( endDate.isAfter(startDates[k]) && endDate.isBefore(endDates[k]))) {
								System.out.println(startDates[k]);
								System.out.println(endDates[k]);
								System.out.println("Pas de reservation disponible a cette date veuillez saisir d'autres dates" );
								
								System.out.println("Nouvelle date de début de reservationn");
								startDate = askDate(in);
								
								System.out.println("Nouvelle date de fin de réservation");
								endDate = askDate(in);
								k = 0;
							}
							
						}
						
						startDates[j] = startDate;
						endDates[j] = endDate;
					
						diff2 = Period.between(startDate, endDate).getDays();
						
						if(diff2 < diff) {
							diff = diff - diff2;
							price = diff*Integer.parseInt(hotel[i].getPrice());
							System.out.println("La réservation de   " + lastName + " " + firstName + " à bien été modifié du " +  startDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE))  + " au " + endDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + " la somme à rembourser est de " + price);
							break;
						}
						
						if(diff < diff2) {
							diff = diff2 - diff;
							price = diff*Integer.parseInt(hotel[i].getPrice());
							System.out.println("La réservation de   " + lastName + " " + firstName + " à bien été modifié du " +  startDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE))  + " au " + endDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + " la somme à règler est de " + price);
							break;
						}
						else {
							System.out.println("La réservation de   " + lastName + " " + firstName + " à bien été modifié du " +  startDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE))  + " au " + endDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)));
							break;
						}
						
					
				}
				
		}
		if(notFound) {
			System.out.println("Pas de réservation correspondante");
		
		}
	}
		
	public LocalDate askDate(Scanner in) {
		System.out.println("Insérer l'année ");
		String year = in.next();
		System.out.println("Insérer le mois ");
		String month = in.next();
		if(month.length() == 1) {
			month = "0" + month;
		}
		System.out.println("Insérer le jour ");
		String day = in.next();
		if(day.length() == 1) {
			day = "0" + day;
		}
		String date = year + "-" + month + "-" + day;
		LocalDate response = LocalDate.parse(date);
		return response;
	}
	
	public int firstAvailableRoomByType(Scanner in, LocalDate currentDate, LocalDate resaStart, LocalDate resaEnd, String userChoice) {
		int count = 0;
		int fARBTIndex = 0;
		String selection = "";
		// Compteur du nombre de créneaux ok pour le client par chambre.
		// Pour que la chambre puisse être réservée, tous les crénaux doivent être validée
		// le compteur doit être à 3.
		for (int i = 0; i < hotel.length; i++) {
			if(hotel[i].getRoomType().equals("Chambre Vue Piscine")) {
				selection = "1";
			} else if (hotel[i].getRoomType().equals("Chambre Vue Jardin")) {
				selection = "2";
			} else if (hotel[i].getRoomType().equals("Chambre Vue Océan")) {
				selection = "3";
			} else if (hotel[i].getRoomType().equals("Chambre vue imprenable sur l'océan")) {
				selection = "4";
			} else if (hotel[i].getRoomType().equals("Suite CDA")) {
				selection = "5";
			} else if (hotel[i].getRoomType().equals("Suite Executive")) {
				selection = "6";
			} else if (hotel[i].getRoomType().equals("Suite Ambassadeur")) {
				selection = "7";
			} else if (hotel[i].getRoomType().equals("Suite Royale")) {
				selection = "8";
			}
			if(userChoice.equals(selection)) {
				Customer customers[] = hotel[i].getCustomers();
				LocalDate startDates[] = hotel[i].getStartDates();
				LocalDate endDates[] = hotel[i].getEndDates();
				count = 0;
				for (int j = 0; j < customers.length; j++) {
					if(customers[j] == null) {
						count++;
					}
					if((customers[j] != null) && ((endDates[j].isBefore(resaStart)) || (startDates[j].isAfter(resaEnd)))) {
						count++;
					}
					if(count == 3) {
						fARBTIndex = i;
						count = 0;
						i = hotel.length;
						break;
					}
				}
			}
		}
		return fARBTIndex;
	}
	
	public void allAvailableRoomsToReserve(LocalDate resaStart, LocalDate resaEnd) {
		// On recupère la date à laquelle on veut afficher les chambres libre;
		boolean allOccupied = true;
		int count = 0;
		boolean free = true;
		
		for (int i = 0; i < hotel.length; i++) {
			Customer customers[] = hotel[i].getCustomers();
			LocalDate startDates[] = hotel[i].getStartDates();
			LocalDate endDates[] = hotel[i].getEndDates();
			free = true;
			for (int j = 0; j < customers.length; j++) {

				// Si on rentre dans cette condition c'est que la chambre est occupée
				if((customers[j] != null) && ((endDates[j].isBefore(resaStart)) || (startDates[j].isAfter(resaEnd)))) {
					free = false;		
					break;
				}
			}
			
			// Si on a bouclé sur les 3 clients et que la chambre n'est pas occupée alors on incremente count
			if(free) {
				allOccupied = false;
				count ++;
				if(i == hotel.length-1) {
					System.out.println(count + " chambres libre de type " + hotel[i].getRoomType());
					break;
				}
				// Si la chambre d'apres n'est pas du meme type on afficher le nombre de chambre libre du type actuel et on remet le compteur a 0
				else if(!hotel[i].getRoomType().equals(hotel[i+1].getRoomType())) {
					System.out.println(count + " chambres libre de type " + hotel[i].getRoomType());
					count = 0;
				}
			}
		}
		
		if(allOccupied) {
			System.out.println("Toutes les chambres sont occupées pour ces dates");
		}
	}

	public void doAReservation(Scanner in, String userChoice) throws MalformedURLException, DocumentException, IOException, MessagingException {

		LocalDate currentDate = LocalDate.now(); // date d'aujourd'hui
		System.out.println("Date du début de la réservation :");
		LocalDate resaStart = askDate(in);
		System.out.println("Date de fin de la réservation :");
		LocalDate resaEnd = askDate(in);
		String mail = "";
		String login = "";
		// vérifie que le début de la résa n'est pas après la date de fin et le tout n'est pas avant aujourd'hui.
		while((resaEnd.isBefore(resaStart)) || (resaStart.isBefore(currentDate)) || (resaEnd.isBefore(currentDate))) { 
			System.out.println("Veuillez entrer une date de fin de réservation ultérieur à celle de début et à la date d'aujourd'hui.");
			System.out.println("Date du début de la réservation :");
			resaStart = askDate(in);
			System.out.println("Date de fin de la réservation :");
			resaEnd = askDate(in);
		}
		System.out.println("Recherche de toutes les chambres libres à cette date :");
		allAvailableRoomsToReserve(resaStart, resaEnd);
		System.out.println(" ");
		System.out.println("Nombre d'adultes ?");
		int adultBeds = in.nextInt();
		System.out.println("Nombre d'enfants ?");
		int childBeds = in.nextInt();
		int bedroomCount = 0; // initialisation du nombre de chambre à réserver.
		if((adultBeds > 2) || (childBeds > 2)) { // si le nombre d'adulte ou d'enfant est > à 2, il faudra réserver + de chambres :
			System.out.println("Les clients sont trop nombreux pour la capacité de la chambre.");
			// arrondir au chiffre suppérieur
			if(adultBeds % 2 != 0) { adultBeds = adultBeds+1; } // si en divisant le nombre d'adulte par 2, il reste plus de 0. On ajoute 1 au nombre.
			if(childBeds % 2 != 0) { childBeds = childBeds+1; } // si en divisant le nombre d'enfant par 2, il reste plus de 0. On ajoute 1 au nombr.
			if(((adultBeds/2) > (childBeds/2)) || ((adultBeds/2) == (childBeds/2))) { // Si le nb d'adulte/2 est > que le nb d'enfant/2 ou s'ils ont le même nb:
				bedroomCount = (adultBeds/2); // Le nombre de chambre restante est égale au nombre d'adulte/2
			} else {
				bedroomCount = (childBeds/2); // Le nombre de chambre restante est égale au nombre d'enfant/2
			}
			System.out.println("Ils devront réserver " + (bedroomCount-1) + " chambre(s) supplémentaire(s)."); // nb de chambre supplémentaire est égal au nb de chambre -1.
		} else {
			bedroomCount = 1;
		}
		System.out.println("Montrer au client la carte des types de chambre en lui indiquant celles qui seront diponible à la date qu'il souhaite.");
		System.out.println("Nom du client");
		String lastName = in.next();
		System.out.println("Prénom du client :");
		String firstName = in.next();
		System.out.println("Pour quitter la réservation appuyer sur 'Q'.");
		System.out.println(" ");
		int fARBTIndex = 0; // initialisation de l'index de la première chambre libre par type.
		int rBedroomsIndex = 0;
		int reservationBedrooms[] = new int[bedroomCount]; // initialisation du tableau qui va stocker les chambres de la réservation.
		do { // permet de rentrer au moins 1 fois dans le menu
			if (userChoice.toUpperCase().charAt(0) == 'Q') {
				System.out.println("La réservation a été annulée.");
				break;
			}
			if(bedroomCount > 0) {
				System.out.println("Il reste " + bedroomCount + " chambre(s) à réserver.");
				System.out.println(" ");
			}
			System.out.println("Voici les chambres encore disponibles à cette date :");
			allAvailableRoomsToReserve(resaStart, resaEnd);
			System.out.println(" ");
			System.out.println("Choisir le type de chambre :");
			System.out.println("-------------------------------");
			System.out.println("1 - Chambre Vue Piscine");
			System.out.println("2 - Chambre Vue Jardin");
			System.out.println("3 - Chambre Vue Océan");
			System.out.println("4 - Chambre Vue imprenable sur l'océan");
			System.out.println("5 - Suite CDA");
			System.out.println("6 - Suite Executive");
			System.out.println("7 - Suite Ambassadeur");
			System.out.println("8 - Suite Royale");
			System.out.println("-------------------------------");
			userChoice = in.next();
			fARBTIndex = firstAvailableRoomByType(in, currentDate, resaStart, resaEnd, userChoice); // Index de la 1ere chambre libre par type
			Customer fARBTCustomers[] = hotel[fARBTIndex].getCustomers(); // initialisation d'un nouveau client
			LocalDate fARBTStartingDate[] = hotel[fARBTIndex].getStartDates(); // initialisation d'un nouveau début de date de résa 
			LocalDate fARBTEndingDate[] = hotel[fARBTIndex].getEndDates(); // initialisation d'une nouvelle fin de date se résa
			for (int i = 0; i < 3; i++) {
				if(fARBTCustomers[i] == null) { // si l'emplacement de résa est libre...
					fARBTCustomers[i] = new Customer(firstName, lastName);
					login = fARBTCustomers[i].getLogin();
					fARBTStartingDate[i] = resaStart;
					fARBTEndingDate[i] = resaEnd;
					break;
				}
			}
			reservationBedrooms[rBedroomsIndex] = fARBTIndex;
			bedroomCount--;
			rBedroomsIndex++;
		} while (bedroomCount > 0); // sort du while si il n'y a plus de chambre à réserver.
		System.out.println(" ");
		System.out.println("Réservation effectuée avec succés.");
		System.out.println(" ");
		System.out.println(lastName + " " + firstName);
		System.out.println("Réservation effectuée pour la(les) chambre(s) :");
		int total = 0;
		for (int i = 0; i < reservationBedrooms.length; i++) {
			System.out.println(hotel[reservationBedrooms[i]].getRoomType() + " numéro " + reservationBedrooms[i] + ". Pour un prix de : " + hotel[reservationBedrooms[i]].getPrice() + "€ pour " + Period.between(resaStart, resaEnd).getDays() + " nuits.");
			total = total + Integer.parseInt(hotel[reservationBedrooms[i]].getPrice()) * Period.between(resaStart, resaEnd).getDays();
		}
		bill(firstName, lastName, resaStart, resaEnd, total, reservationBedrooms, login);
		System.out.println("Le reste à payer de " + total + ".");
		System.out.println(" ");
		System.out.println("Veuillez saisir votre mail pour recevoir votre facture par email");
		mail = in.next();
		mail(mail, firstName, lastName, login);
		System.out.println("Retour au menu employé de l'hôtel.");
		System.out.println(" ");
	}

	public void freeAnOccupiedRoom(Scanner in, String userChoice) {
		boolean notFound = true; // De base le client n'est pas trouvé
		String confirmation = "";
		System.out.println(" ");
		System.out.println("Entrer le loggin du client");
		System.out.println(" ");
		userChoice = in.next();
		int reservationLeft = nbOfClientReservation(in, userChoice); // récupère le nombre de chambre occupé par le client
		while(reservationLeft > 0) { // tant que la résa est supérieur à 0
			for (int i = 0; i < hotel.length; i++) {
				Customer customers[] = hotel[i].getCustomers();
				LocalDate startDates[] = hotel[i].getStartDates();
				LocalDate endDates[] = hotel[i].getEndDates();
				for (int j = 0; j < customers.length; j++) {
					if(customers[j] != null && customers[j].getLogin().equalsIgnoreCase(userChoice)) { // si utilisateur correspond au loggin client
						System.out.println(" ");
						System.out.println(customers[j].getFirstName() + " " + customers[j].getLastName() + " a " + reservationLeft + " réservation(s)."); // Nombre de résa restante et affichage du nom et prénom du client 
						System.out.println(" ");
						System.out.println("Ce client a réservé une chambre du " + startDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + " au " + endDates[j].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)));
						System.out.println(" ");
						System.out.println("Confirmer le checkout de cette chambre :");
						System.out.println("('C' pour confirmer le checkout, 'N' pour passer directement à la chambre suivante, 'Q' pour annuler le checkout.)");
						confirmation = in.next();
						if(confirmation.equalsIgnoreCase("c")) { // permet de libérer la chambre
							customers[j] = null;
							startDates[j] = null;
							endDates[j] = null;
							reservationLeft--;
							if(reservationLeft == 0) { // si le client n'a qu'un checkout à faire, passer le boolean à true
								notFound = false;
							} else {
								System.out.println("La chambre" + i + " de type : " + hotel[i].getRoomType() + " a été libéré avec succès.");
							}
							break;
						} else if (confirmation.equalsIgnoreCase("n")) { // permet de passer à la résa suivante si le client ne souhaite pas checkout de cette chambre
							if(reservationLeft == 1) {// empêche de passer à la chambre suivante s'il n'y en a pas d'autres.
								System.out.println("Il n'y a pas d'autres chambres réservées pour ce client.");
								System.out.println("Choisissez : ('C' pour confirmer le checkout, 'Q' pour annuler le checkout.)");
								confirmation = in.next();
								if(confirmation.equalsIgnoreCase("c")) { // permet de libérer la chambre
									customers[j] = null;
									startDates[j] = null;
									endDates[j] = null;
									reservationLeft--;
									if(reservationLeft == 0) { // si le client n'a qu'un checkout à faire, passer le boolean à false
										notFound = false;
									}
									break;
								} else if (confirmation.equalsIgnoreCase("q")) { // permet d'arreter la fonction de checkout
									reservationLeft = 0;
									break;
								}
							} else { 
								i++; // passe à la chambre suivante
								notFound = false; // passe le boolean à false (le client a été trouvé)
								reservationLeft--; // décrémente le nombre de chambre restante pour le client
							}
							break;
						} else if (confirmation.equalsIgnoreCase("q")) {
							reservationLeft = 0;
							break;
						} else {
							System.out.println("Choisissez : ('C' pour confirmer le checkout, 'N' pour passer directement à la chambre suivante, 'Q' pour annuler le checkout.)");
						}
					}
				}
				if(confirmation.equalsIgnoreCase("q")) {
					System.out.println("Checkout annulé.");
					break;
				}
				if(!notFound) {
					System.out.println(" ");
					System.out.println("La chambre" + i + " de type : " + hotel[i].getRoomType() + " a été libéré avec succès.");
					System.out.println(" ");
					System.out.println("Retour au menu employé.");
					System.out.println(" ");
					break;
				}
			}
		}
		if(notFound && confirmation.toUpperCase().charAt(0) != 'Q') {
			System.out.println(" ");
			System.out.println("Le client ne semble pas être enregistré dans l'hôtel.");
			System.out.println(" ");
		}
	}
	
	public int nbOfClientReservation(Scanner in, String userChoice) {
		int nbOfClientReservation = 0;
		for (int i = 0; i < hotel.length; i++) {
			Customer customers[] = hotel[i].getCustomers();
			for (int j = 0; j < customers.length; j++) {
				if(customers[j] != null && customers[j].getLogin().equalsIgnoreCase(userChoice)) {
					nbOfClientReservation++;
				}
			}
		}
		return nbOfClientReservation;
	}

	public void displayClientResa(String userChoice) {
		int userResaCount = 0;
		boolean clientFound = false;
		for (int k = 0; k < hotel.length; k++) {
			Customer customers[] = hotel[k].getCustomers();
			LocalDate startDates[] = hotel[k].getStartDates();
			LocalDate endDates[] = hotel[k].getEndDates();
			for (int l = 0; l < customers.length; l++) {
				if((customers[l] != null) && (userChoice.equals(customers[l].getLogin()))) {
					clientFound = true;
					if(userResaCount == 0) {
						System.out.println("Bienvenue " + customers[l].getFirstName() + " " + customers[l].getLastName() +  ".");
						System.out.println("Vous avez réservé la chambre : " + hotel[k].getRoomType() + " au num�ro " + k + ". Vos dates de réservation pour ce bien vont du " + startDates[l].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + " au " + endDates[l].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + ".");
						userResaCount++;
					}
					else {
						System.out.println("Vous avez aussi réservé la chambre : " + hotel[k].getRoomType() + " au numéro " + k + ". Vos dates de réservation pour ce bien vont du " + startDates[l].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + " au " + endDates[l].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + ".");
					}
				}
				else {
					if((k == hotel.length-1) && (clientFound == false)){
						System.out.println("Vous ne semblez pas encore faire parti des clients de l'hôtel.");
						k = hotel.length;
						break;
					}
				}
			}
		}
        TimerTask task = new TimerTask() {
            public void run() {  
        		System.out.println("Entrez votre identifiant pour vous connecter ou connaitre les détails de votre réservation :");
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 4000L;
        timer.schedule(task, delay);
	}

	public void getClientByNames(String clientFirstName, String clientLastName) {
		int userResaCount = 0;
		boolean clientFound = false;
		for (int k = 0; k < hotel.length; k++) {
			Customer customers[] = hotel[k].getCustomers();
			LocalDate startDates[] = hotel[k].getStartDates();
			LocalDate endDates[] = hotel[k].getEndDates();
			for (int l = 0; l < customers.length; l++) {
				if((customers[l] != null) && (clientFirstName.equals(customers[l].getFirstName())) && (clientLastName.equals(customers[l].getLastName()))) {
					clientFound = true;
					if(userResaCount == 0) {
						System.out.println(customers[l].getFirstName() + " " + customers[l].getLastName() +  ". Identifiant client : " + customers[l].getLogin());
						System.out.println("Les dates de réservation pour ce bien vont du " + startDates[l].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + " au " + endDates[l].format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) + ".");
						System.out.println("Voici la liste des chambres qu'il a réservé :");
						System.out.println(hotel[k].getRoomType() + " au numéro " + k + ".");
						userResaCount++;
					} else {
						System.out.println(hotel[k].getRoomType() + " au numéro " + k + ".");
					}
				} else {
					if((k == hotel.length-1) && (clientFound == false)){
						System.out.println("Désolé il semble qu'il y ai eu une erreur dans la réservation.");
						k = hotel.length;
						break;
					}
				}
			}
		}
	}

	public void login(Scanner in) {
		System.out.println("Veuillez saisir votre mot de passe");
		String response = in.next();
		boolean validation = false;
		while(validation == false) {
			for (int i = 2; i < 5; i++) {
				if(!response.startsWith("GH") || response.charAt(i) < 48 || response.charAt(i) > 57) {
					System.out.println("Mot de passe érroné veuillez ressayer");
					response = in.next();
					break;
				}
				else {
					validation = true;
				}
			}
		}
	}

	public void mail(String mail, String firstName, String lastName, String login) throws MessagingException {
		Credentials credentials = new Credentials();
		Properties properties = new Properties();
		String from = credentials.getLogin();
		String to = mail;
		
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", 587);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.transport.protocol", "smtp");
		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(credentials.getLogin(), credentials.getPassword());
			}
		});
		
		Message message = new MimeMessage(session);
		message.setSubject("Confirmation de réservation");
		message.setContent("<h1> Votre facture </h1>", "text/html");
		message.setFrom(new InternetAddress(from));
		
		 message.setRecipients(Message.RecipientType.TO,
		            InternetAddress.parse(to));
		
		BodyPart messageBodyPart = new MimeBodyPart();
		 messageBodyPart.setText("Bonjour " + firstName + " " + lastName + ", voici votre facture concernant votre réservation chez Wamy hotels. Votre identifiant est " + login );
		 
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		
		messageBodyPart = new MimeBodyPart();
		String filename = "factureHotel.pdf";
		DataSource source = new FileDataSource(filename);
		
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		
		multipart.addBodyPart(messageBodyPart);
		message.setContent(multipart);
		
		
		Transport.send(message);

	}
	
	public void bill(String firstName, String lastName, LocalDate startDate,LocalDate endDate,int total, int tab[], String login) throws DocumentException, MalformedURLException, IOException {
		Document document  = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("factureHotel.pdf"));
		document.open();
		
		Paragraph titre = new Paragraph(new Chunk("Wamy hotels" , FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));
		titre.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(titre);
		document.add(Chunk.NEWLINE);
		
		Paragraph par = new Paragraph(new Chunk(lastName + " " + firstName  , FontFactory.getFont(FontFactory.COURIER, 13)));
		document.add(par);
		
		
		Paragraph par1 = new Paragraph(new Chunk("Séjour du : " + startDate , FontFactory.getFont(FontFactory.COURIER, 13)));
		document.add(par1);
		
		
		Paragraph par2 = new Paragraph(new Chunk("au : " + endDate , FontFactory.getFont(FontFactory.COURIER, 13)));
		document.add(par2);
	
		
		Paragraph par3 = new Paragraph(new Chunk("                                       Facture numéro : " + lastName.substring(0, 2) + login.substring(5, 9) + lastName , FontFactory.getFont(FontFactory.COURIER, 13)));
		document.add(par3);
		
		
		Paragraph par4 = new Paragraph(new Chunk("                             Editée le : " + LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)) , FontFactory.getFont(FontFactory.COURIER, 13)));
		document.add(par4);
		document.add(Chunk.NEWLINE);
		
		
		PdfPTable table = new PdfPTable(4);
		table.addCell("Début du séjour");
		table.addCell("Fin du séjour ");
		table.addCell("Type de chambre ");
		table.addCell("Prix ");
		
		
		// Je recupere le tableau du numéro des chambre réservé
		for (int i = 0; i < tab.length; i++) {
			Customer customers[] = hotel[tab[i]].getCustomers();
			LocalDate startDates[] = hotel[tab[i]].getStartDates();
			LocalDate endDates[] = hotel[tab[i]].getEndDates();
				for (int j = 0; j < customers.length; j++) {
					// si les info de la personne qui a reservé correspondent aux info de la chambre j'affiche dans la facture 
					if(customers[j] != null && firstName.equals(customers[j].getFirstName()) && lastName.equals(customers[j].getLastName())&& startDate.equals(startDates[j]) && endDate.equals(endDates[j])) {
						table.addCell(startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)));
						table.addCell(endDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.FRANCE)));
						table.addCell(hotel[tab[i]].getRoomType());
						table.addCell(hotel[tab[i]].getPrice());
					}
				}
		}
		
		
		document.add(table);
		document.add(Chunk.NEWLINE);
		
		Paragraph price = new Paragraph(new Chunk("total : " + total , FontFactory.getFont(FontFactory.HELVETICA, 13)));
		price.setAlignment(Paragraph.ALIGN_RIGHT);
		document.add(price);
		document.close();
	}
	
}