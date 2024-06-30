import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;

public class UserInterface {


	private DataManager dataManager;

	private Organization org;
	private static   Scanner input;

	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		input = new Scanner(System.in);
		this.org = org;
	}

	public void start() {
		//System.out.println("Please enter 1");


		while (true) {
			System.out.println("\n\n");
			int count = 1;
			if (org.getFunds().size() > 0) {
				System.out.println("There are " + org.getFunds().size() + " funds in this organization:");


				for (Fund f : org.getFunds()) {

					System.out.println(count + ": " + f.getName());

					count++;
				}
				System.out.println("\nEnter the fund number to see more information.");
			}
			System.out.println("Enter 0 to create a new fund");
			System.out.println("Enter \"p\" to change this organisation's password");
			System.out.println("Enter \"l\" or \"logout\" to logout");
			System.out.println("Enter \"a\" or \"all\" to list out all contributors to this organization");
			// reads full line instead of expecting an int
			String info = input.nextLine();
			// closes program
			if (info.equals("quit") || info.equals("q")) {
				System.out.println("Goodbye!");
				return;
			} else if (info.equals("logout") || info.equals("l")) {
				if (!login(null, null)) {
					return;
				}
				continue;
			} else if (info.equals("all") || info.equals("a")) {
				displayOrgContributions();
				continue;
			} else if (info.equals("p")) {
				changePassword();
				continue;
			}
			int option;
			try {
				option = Integer.parseInt(info);
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
		String oldPassword = input.nextLine();
		if (dataManager.attemptLogin(org.getName(), oldPassword) == null) {
			System.out.println("Unable to recognise current password. Returning you to the main menu.");
			return;
		}

		//get new password
		System.out.println("\nPassword accepted. Please enter your new password: ");
		String newPassword = input.nextLine();

		//confirm new password
		System.out.println("Please confirm your new password: ");
		String confirmPassword = input.nextLine();

		while (!confirmPassword.equals("q") && !confirmPassword.equals(newPassword)) {
			System.out.println("Your new password does not match. Please re-enter your new password, or press 'q' to return to the main menu.");
			confirmPassword = input.nextLine();
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
		input.nextLine();
	}


	public void createFund() {
		String name = "";

		while (name.isBlank()
		) {
			System.out.print("Enter the fund name: ");
			name = input.nextLine().trim();
			// if name is blank re-prompt user
			if (name.isBlank()) {
				System.out.println("Fund name is blank, please enter a valid non-blank name");
			}
		}

		String description= "";
		while (description.isBlank()) {
			System.out.print("Enter the fund description: ");
			description = input.nextLine().trim();

			if (description.isBlank()) {
				System.out.println("Fund description is blank, please enter a valid non-blank name");
			}

		}


		long target = -1;
		// Handle non-numeric value and negative values
		while (target < 0) {
			System.out.print("Enter the fund target: ");
			String targetString = input.nextLine().trim();

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
		String newInfo = input.nextLine();
		if (newInfo.equals("1")) {
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

			command = input.nextLine();

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
				login = input.nextLine();
				System.out.println("Please enter the password");
				password = input.nextLine();
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
			String Input = input.nextLine();
			if (Input.equals("quit") || Input.equals("q")) {
				System.out.println("Goodbye!");
				return false;
			}
			login = null;
			password = null;

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

		while (true) {
			System.out.println("1. Login");
			System.out.println("2. Create New Organization");
			System.out.println("3. Quit");
			System.out.println("Please Enter 1, 2, or 3 for your choice");

			int userChoice = input.nextInt();
			input.nextLine();

			switch (userChoice) {
				case 1:
					if (ui.login(null, null)) {
						ui.start();
					}
					break;
				case 2:
					ui.createNewOrganization();
					break;
				case 3:
					System.out.println("Goodbye!");
					System.exit(0);
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private void createNewOrganization() {
		System.out.println("Please enter the login: ");
		String loginName = input.nextLine();

		System.out.println("Please enter the password: ");
		String passwordData = input.nextLine();

		// params request

		// req.body

		System.out.println("Please enter the name of the organisation: ");
		String name = input.nextLine().trim();

		System.out.println("Please enter the description of the organisation: ");
		String description = input.nextLine().trim();



		Organization organization = new Organization(null, name, description);

		try {
			Organization createdOrg = dataManager.createOrg(organization, loginName, passwordData);
			if (createdOrg != null) {
				System.out.println("Organization created successfully!");
				while (true) {
					System.out.println("What would you like to do now?");
					System.out.println("1. Log in with the new credentials");
					System.out.println("2. Log in with different login credentials");
					System.out.println("3. Go back to the main menu");
					String choice = input.nextLine().trim();

					switch (choice) {
						case "1":
							if (login(loginName, passwordData)) {
								start();
							}
							return;
						case "2":
							System.out.println("Please enter your login: ");
							loginName = input.nextLine();
							System.out.println("Please enter your password: ");
							passwordData = input.nextLine();
							if (login(loginName, passwordData)) {
								start();
							}
							return;
						case "3":
							return; // This will now return to the main menu loop
						default:
							System.out.println("Invalid choice. Please try again.");
					}
				}
			} else {
				System.out.println("Failed to create organization. The login name may already exist.");
			}
		} catch (IllegalStateException e) {
			System.out.println("Error creating organization: " + e.getMessage());
		}
	}

}

		//dataManager.createOrg(organization, loginName, passwordData);


	//}

//}