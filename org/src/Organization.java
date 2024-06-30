import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Organization {
	
	private String id;
	private String name;
	private String description;
	private String password;
	
	private List<Fund> funds;
	private DataManager dataManager;
	
	public Organization(String id, String name, String description, String password, DataManager dataManager) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.password = password;
		this.dataManager = dataManager;

		funds = new LinkedList<>();
	}

	public String getId() {
		return id;
	}
	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<Fund> getFunds() {
		return funds;
	}

	public void deleteFund(Fund fund) {
		funds.remove(fund);
	}
	
	public void addFund(Fund fund) {
		funds.add(fund);
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public List<Donation> listAllDonations() {
		List<Donation> allDonationsList = new ArrayList<>();

		// loop through each fund and add donations to list
		for (Fund fund : funds) {
			allDonationsList.addAll(fund.getDonations());

		}

		// set up date format for parsing dates
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		//sort by date in descending order
		Collections.sort(allDonationsList, new Comparator<Donation>() {
			@Override
			public int compare(Donation o1, Donation o2) {
				try {
					Date date1 = dateFormat.parse(o1.getDate());
					Date date2 = dateFormat.parse(o2.getDate());
					return date2.compareTo(date1);

//					return dateFormat.parse(o2.getDate()).compareTo(dateFormat.parse(o1.getDate()));
				} catch (ParseException e) {
					System.err.println("Trouble with date sorting / format");
					throw new RuntimeException(e);
				}
			}
		});

		return allDonationsList;


	}

	// New method to make donation on the organization level
	public boolean makeDonation(String contributorId, String fundId, String amount) {
		try {
			return dataManager.makeDonation(contributorId, fundId, amount);
		} catch (Exception e) {
			System.err.println("An error occured making donation " + e.getMessage());
			return false;
		}
	}




	

}
