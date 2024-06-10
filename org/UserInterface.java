import java.util.List;
import java.util.Scanner;

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
				System.out.println("Enter the fund number to see more information.");
			}
			System.out.println("Enter 0 to create a new fund");
			// reads full line instead of expecting an int
			String input = in.nextLine();
			// closes program
			if (input.equals("quit") || input.equals("q")) {
				System.out.println("Goodbye!");
				return;
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


		Fund fund = dataManager.createFund(org.getId(), name, description, target);
//		if (fund != null) {
//			org.getFunds().add(fund);
//			System.out.println("Fund created successfully!");
//		} else {
//			System.out.println("Failed to create fund. Please try again.");
//		}
		org.getFunds().add(fund);


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
	
		
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
		
		
		
	}
	
	
	public static void main(String[] args) {

		DataManager ds = new DataManager(new WebClient("localhost", 3001));

		String login = args[0];
		String password = args[1];
		
		
		try {
			Organization org = ds.attemptLogin(login, password);

			if (org == null) {
				System.out.println("Login failed.");
			}
			else {

				UserInterface ui = new UserInterface(ds, org);

				ui.start();

			}

		} catch (IllegalStateException e) {
			System.out.println("Error in communicating with server");
		}

	}

}