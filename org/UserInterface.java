import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.text.SimpleDateFormat;
public class UserInterface {


	private DataManager dataManager;
	private Organization org;
	private Scanner in = new Scanner(System.in);

	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
	}

	public void start() {

		while (true) {
			System.out.println("\n\n");
			int count = 1;
			if (org.getFunds().size() > 0) {
				System.out.println("There are " + org.getFunds().size() + " funds in this organization:");


				for (Fund f : org.getFunds()) {

					System.out.println(count + ": " + f.getName());

					count++;
				}
				System.out.println("\nEnter the fund number to see more information");
			}
			System.out.println("Enter 0 to create a new fund");
			System.out.println("Enter \"p\" to change this organisation's password");
			System.out.println("Enter \"e\" or \"edit\" to list out all contributors to this organization");
			System.out.println("Enter \"l\" or \"logout\" to logout");
			System.out.println("Enter \"a\" or \"all\" to list out all contributors to this organization");
			System.out.println("Enter \"d\" or \"donation\" to make a donation.");
			// reads full line instead of expecting an int
			String input = in.nextLine();
			// closes program
			if (input.equals("quit") || input.equals("q")) {
				System.out.println("Goodbye!");
				return;
			} else if (input.equals("logout") || input.equals("l")) {
				if (!login(null, null)) {
					return;
				}
				continue;
			} else if (input.equals("all") || input.equals("a")) {
				displayOrgContributions();
				continue;
			} else if (input.equals("p")) {
				changePassword();
				continue;
			} else if (input.equals("donation") || input.equals("d")) {
				// make donation
				makeDonation();
			} else if (input.equals("e") || input.equals("edit")) {
				editOrgInformation();
				continue;
			}

		int option;
			try {
				option = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				option = -1; // used to indicate an invalid option was given
			}
			if (option < 0 || option >= count) {
				System.out.println("Error! Please input:");
				System.out.println("\"0\" if you wish to create a new fund");
				System.out.println("The integer corresponding with the fund you want to select");
				System.out.println("Or either \"q\" or \"quit\" to exit");
			} else if (option == 0) {
				createFund();
			} else {
				displayFund(option);
			}

		}

	}

	public void changePassword() {

		//check current password
		System.out.println("\n\nPlease enter your current password to continue:");
		String oldPassword = in.nextLine();
		if (dataManager.attemptLogin(org.getName(), oldPassword) == null) {
			System.out.println("Unable to recognise current password. Returning you to the main menu.");
			return;
		}

		//get new password
		System.out.println("\nPassword accepted. Please enter your new password: ");
		String newPassword = in.nextLine();

		//confirm new password
		System.out.println("Please confirm your new password: ");
		String confirmPassword = in.nextLine();

		while (!confirmPassword.equals("q") && !confirmPassword.equals(newPassword)) {
			System.out.println("Your new password does not match. Please re-enter your new password, or press 'q' to return to the main menu.");
			 confirmPassword = in.nextLine();
			if (confirmPassword.equals("q")) {
				return;
			}
		}

		//if here, password verification complete - send to the database
		dataManager.updatePassword(org.getId(), newPassword);
		System.out.println("Password update successful. Returning you to the main menu.");

	}


	public void displayOrgContributions() {
		System.out.println("\n\nAll contributions to the organization's funds:");
		List<Donation> donations = org.listAllDonations();
		for (Donation donation : donations) {
			System.out.println(donation.getFundId() + ": $" + donation.getAmount() + " on " + donation.getDate() + " by " + donation.getContributorName());
		}
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
	}


	public void createFund() {
		String name = "";

		while (name.isBlank()) {
			System.out.print("Enter the fund name: ");
			name = in.nextLine().trim();
			// if name is blank re-prompt user
			if (name.isBlank()) {
				System.out.println("Fund name is blank, please enter a valid non-blank name");
			}
		}

		String description= "";
		while (description.isBlank()) {
			System.out.print("Enter the fund description: ");
			description = in.nextLine().trim();

			if (description.isBlank()) {
				System.out.println("Fund description is blank, please enter a valid non-blank name");
			}

		}


		long target = -1;
		// Handle non-numeric value and negative values
		while (target < 0) {
			System.out.print("Enter the fund target: ");
			String targetString = in.nextLine().trim();

			try {
				target = Long.parseLong(targetString);

				if (target < 0) {
					System.out.println("Target fund can not be negative, re-enter a non-negative value");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input, please enter a non-negative numeric value");
			}
		}

		try {
			Fund fund = dataManager.createFund(org.getId(), name, description, target);
			if (fund != null) {
				org.getFunds().add(fund);
				return;
			}
			System.out.println("We were unable to create the fund");

		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Enter 1 if you would like to try again, and anything else otherwise");
		String input = in.nextLine();
		if (input.equals("1")) {
			createFund();
		}



	}


	public void displayFund(int fundNumber) {

		Fund fund = org.getFunds().get(fundNumber - 1);

		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());

		long raised = 0;
		List<Donation> donations = fund.getDonations();
		System.out.println("Number of donations: " + donations.size());
		for (Donation donation : donations) {
			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
			raised += donation.getAmount();
		}
		double portionRaised =  (double) raised / fund.getTarget();
		long percent = Math.round(portionRaised * 1000) / 10;
		if (percent < 100 || raised >= fund.getTarget()) {
			System.out.println("Total donation amount: $" + raised + " (" + percent + "% of target)");
		} else {
			System.out.println("Total donation amount: $" + raised + " (>99% of target)");
		}

		String command;
		
		while (true) {
			System.out.println("\nPress 1 to delete this fund");
			System.out.println("Press 2 to see the aggregate donations");
			System.out.println("Press 3 to go back to the listing of funds");

			command = in.nextLine();

			if (command.equals("1")) {

				if (confirmSelection()) {
					if (dataManager.deleteFund(fund.getId())) {
						org.deleteFund(fund);
					};
				}

				return;
			}

			if (command.equals("2")) {
				for (Donation donation : fund.getAggregateDonations()) {
					System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount());
				}

			}
			if (command.equals("3")) {
				return;

			}
		}
	}

	public boolean confirmSelection() {

		//confirm that user would like to delete fund
		int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure?");

		//if they select no, terminate early
		if (confirmation == 1 || confirmation == 2) {
			return false;
		}

		return true;
	}

	public boolean login(String login, String password) {
		while (true) {
			// pass null arguments for a logout and new login
			if (login == null || password == null) {
				System.out.println("Please enter the login");
				login = in.nextLine();
				System.out.println("Please enter the password");
				password = in.nextLine();
			}
			try {
				org = dataManager.attemptLogin(login, password);

				if (org == null) {
					System.out.println("Login failed.");
				} else {
					return true;
				}

			} catch (IllegalStateException e) {
				System.out.println("Error in communicating with server");
			}
			System.out.println("Enter either \"q\" or \"quit\" to exit or press enter to try again.");
			String Input = in.nextLine();
			if (Input.equals("quit") || Input.equals("q")) {
				System.out.println("Goodbye!");
				return false;
			}
			login = null;
			password = null;

		}
	}

	public void makeDonation() {

		int fundNumber = -1;

		while (fundNumber <= 0 || fundNumber > org.getFunds().size()) {
			System.out.println("Enter the fund number you'd like to donate to: ");
			try {
				fundNumber = Integer.parseInt(in.nextLine());

				if (fundNumber <= 0 || fundNumber > org.getFunds().size()) {
					System.out.println("Fund number must be within range.");
				}

			} catch (NumberFormatException e) {
				System.out.println("Invalid fund number!");
				fundNumber = -1;
			}
		}

//		if (fundNumber <= 0 || fundNumber > org.getFunds().size()) {
//			System.out.println("Invalid fund number!");
//		}

		// Get fund
		Fund fund = org.getFunds().get(fundNumber - 1);
		System.out.println("You are making a donation to " + fund.getName() + " fund.");

		// COntributor ID
		String contributorId = "";
		while (contributorId.isBlank()) {
			System.out.print("Enter contributor ID: ");
			contributorId = in.nextLine().trim();

			if (contributorId.isBlank()) {
				System.out.println("Contributor ID cannot be blank.");
			}

		}

		// Amount
		long amount = -1;
		while (amount < 0) {
			System.out.print("Enter the amount you'd like to donate: ");
			String amtString = in.nextLine().trim();

			try {
				amount = Long.parseLong(amtString);

				if (amount < 0) {
					System.out.println("Amount cannot be negative.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Enter numeric value");
			}

		}
		String fundId = fund.getId();

		boolean status = org.makeDonation(contributorId, fundId, String.valueOf(amount));

		if (status) {
			System.out.println("Sucessful transaction, thank you for your donation!");
			// today's date
			// Get the current date and time in the desired format
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
			String formattedDate = dateFormat.format(new Date());

			Donation e = new Donation(fundId, fund.getName(), amount, formattedDate);
			List<Donation> donations = fund.getDonations();
			donations.add(e);
			fund.setDonations(donations);
			displayFund(fundNumber);
		} else {
			System.out.println("Error processing donation, please try again");
			makeDonation();
		}


	}

	public void editOrgInformation() {
		System.out.println("\n\nPlease enter your current password");
		String input = in.nextLine();
		if (!input.equals(org.getPassword())) {
			System.out.println("The password is incorrect");
			return;
		}
		String name;
		String info;
		System.out.println("The current name is \"" + org.getName() + "\".");
		System.out.println("Please type in a new name or press \"enter\" to keep this name");
		name = in.nextLine();
		if (name.isEmpty()) {
			name = org.getName();
		}
		System.out.println("The current description is:\n" + org.getDescription());
		System.out.println("Please type in a new description or press \"enter\" to keep this description");
		info = in.nextLine();
		if (info.isEmpty()) {
			info = org.getDescription();
		}

		if (dataManager.changeOrgInfo(name, info, org.getId())) {
			System.out.println("An error occurred when trying to change the data. Please try again");
		}

	}




	public static void main(String[] args) {

		DataManager ds = new DataManager(new WebClient("localhost", 3001));
		String login = null;
		String password = null;
		if (args.length >= 2) {
			login = args[0];
			password = args[1];
		}

		UserInterface ui = new UserInterface(ds, null);
		if (ui.login(login, password)) {
			ui.start();
		}

	}

}
