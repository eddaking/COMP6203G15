package group15;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.bidding.BidDetails;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.boaframework.OutcomeSpace;
import java.util.List;

public class MyAgent extends AbstractNegotiationParty {

    private final String description = "Group 15's Agent, A simple time dependent agent";
    private double minUtil, maxUtil;
    private Bid bestReceivedBid = null;
    private OutcomeSpace os;

    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        os = new negotiator.boaframework.OutcomeSpace(utilitySpace);
        os.generateAllBids(utilitySpace);
    }

    /**
     * When this function is called, it is expected that the Party chooses one of the actions from the possible
     * action list and returns an instance of the chosen action.
     *
     * @param list
     * @return
     */
    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        try {
            Bid minBid = utilitySpace.getMinUtilityBid();
            minUtil = utilitySpace.getUtility(minBid);
            Bid maxBid = utilitySpace.getMaxUtilityBid();
            maxUtil = utilitySpace.getUtility(maxBid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double TM = TimeModifier();
        double acceptUtil = minUtil + (1-TM)*(maxUtil-minUtil);
        if (bestReceivedBid == null) {
            try {
                return new Offer(getPartyId(), utilitySpace.getMaxUtilityBid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.utilitySpace.getUtility(bestReceivedBid) >= acceptUtil){
            return  new Accept(getPartyId(), bestReceivedBid);
        }else{
            return new Offer(getPartyId(), os.getBidNearUtility(acceptUtil).getBid());
        }
    }

    private double TimeModifier(){
        double alpha = 0.1;
        double beta = 1.4;

        double time = this.timeline.getTime();

        double TM = alpha + ((1-alpha)*(Math.pow(time,1.0/beta)));

        return TM;
    }

    /**
     * A human-readable description for this party.
     * @return
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * This method is called to inform the party that another NegotiationParty chose an Action.
     * @param sender
     * @param act
     */
    @Override
    public void receiveMessage(AgentID sender, Action act) {
        super.receiveMessage(sender, act);
        if (act instanceof Offer) {
            Offer offer = (Offer) act;
            Bid bid = offer.getBid();
            bestReceivedBid = offer.getBid();
        }
    }
}
