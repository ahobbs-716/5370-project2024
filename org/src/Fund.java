import java.util.*;

public class Fund {

	private String id;
	private String name;
	private String description;
	private long target;
	private List<Donation> donations;
	private List<Donation> aggregateDonations;

	public Fund(String id, String name, String description, long target) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.target = target;
		donations = new LinkedList<>();
		aggregateDonations = null;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public long getTarget() {
		return target;
	}

	public void setDonations(List<Donation> donations) {
		this.donations = donations;
	}

	public List<Donation> getDonations() {
		return donations;
	}

	public void aggregateDonationCalculator (){
		HashMap<String, Long> aggregate = new HashMap<>();
		for (Donation donation : donations){
//			Organization.ContributorInfo information;

			String contributorName = donation.getContributorName();
			double amount = donation.getAmount();

			if(!aggregate.containsKey(contributorName)){
				aggregate.put(contributorName, donation.getAmount());
			}
			else{
				aggregate.put(contributorName, aggregate.get(contributorName) + donation.getAmount());
			}

		}
		aggregateDonations = new ArrayList<>();
		for (String key : aggregate.keySet()) {
			Donation donation = new Donation("", key, aggregate.get(key), "");
			aggregateDonations.add(donation);
		}
		aggregateDonations.sort(new Comparator<Donation>() {
			@Override
			public int compare(Donation o1, Donation o2) {
				// this is done instead of returning o1.getAmount() - o2.getAmount() as it is a long
				if (o1.getAmount() - o2.getAmount() < 0) {
					return 1;
				} else if (o1.getAmount() - o2.getAmount() > 0) {
					return -1;
				}
				return 0;
			}

		});

	}
	public   List<Donation> getAggregateDonations (){
		if (aggregateDonations == null){
			aggregateDonationCalculator();
		}
		return aggregateDonations;
	}

}
