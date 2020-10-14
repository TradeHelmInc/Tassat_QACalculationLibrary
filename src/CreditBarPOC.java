import TDPCalculator.businessEntities.*;
import TDPCalculator.logicLayer.credit.CreditLogic;

import java.text.ParseException;
import java.util.ArrayList;

public class CreditBarPOC {

    private static String _FIRMID="firm1";

    public static ArrayList<SecurityMasterRecord> GetSecurityMasterRecords()
    {
        SecurityMasterRecord sec1 = new SecurityMasterRecord("DF-XBT-USD-F21","2021-01-20");
        SecurityMasterRecord sec2 = new SecurityMasterRecord("DF-XBT-USD-G21","2021-02-20");
        SecurityMasterRecord sec3 = new SecurityMasterRecord("DF-XBT-USD-H21","2021-03-20");
        SecurityMasterRecord sec4 = new SecurityMasterRecord("DF-XBT-USD-J21","2021-04-20");

        ArrayList<SecurityMasterRecord> securities = new ArrayList<>();
        securities.add(sec1);
        securities.add(sec2);
        securities.add(sec3);
        securities.add(sec4);

        return securities;


    }

    public static ArrayList<NetPosition> GetPositions(ArrayList<SecurityMasterRecord> securities) throws ParseException {


        NetPosition pos1 = new NetPosition(_FIRMID, securities.get(0),-290);
        NetPosition pos2 = new NetPosition(_FIRMID, securities.get(1),0);
        NetPosition pos3 = new NetPosition(_FIRMID, securities.get(2),0);
        NetPosition pos4 = new NetPosition(_FIRMID, securities.get(3),0);

        ArrayList<NetPosition> firmPositions = new ArrayList<>();
        firmPositions.add(pos1);
        firmPositions.add(pos2);
        firmPositions.add(pos3);
        firmPositions.add(pos4);

        return firmPositions;

    }

    public static ArrayList<DailySettlementPrice> GetSettlementPrices( ArrayList<SecurityMasterRecord> securities)
    {
        double basePrice=6166.82;
        ArrayList<DailySettlementPrice> DSPs= new ArrayList<>();

        for (SecurityMasterRecord sec : securities)
        {

            DailySettlementPrice dsp = new DailySettlementPrice(sec.getSymbol(),basePrice);

            DSPs.add(dsp);
            basePrice+=10;
        }

        return DSPs;

    }

    public static Config GetConfig()
    {
        return new Config(0.298,0.85,0.825,0.8);

    }

    public static ArrayList<PriorDayMargin> GetPriorDayMargin()
    {

        ArrayList<PriorDayMargin> margins= new ArrayList<PriorDayMargin>();
        margins.add( new PriorDayMargin(_FIRMID,532936.58));

        return margins;

    }

    //In this example these orders are already in the market
    public static ArrayList<Order> GetOrders(ArrayList<SecurityMasterRecord> securities)
    {
        ArrayList<Order> orders= new ArrayList<Order>();
        orders.add( new Order(securities.get(0).getSymbol(),Order._SIDE_BUY,0,290,Order._STATUS_OPEN));

        return orders;
    }

    //Orders that are just about to be sent to the exhange
    public static Order GetPotentialOrder(ArrayList<SecurityMasterRecord> securities)
    {
        return new Order(securities.get(0).getSymbol(),Order._SIDE_BUY,0);
    }

    public static void main(String[] args) throws Exception {

        ArrayList<SecurityMasterRecord> securities = GetSecurityMasterRecords();
        ArrayList<DailySettlementPrice> settlPrices = GetSettlementPrices(securities);
        Config config = GetConfig();
        ArrayList<PriorDayMargin> priorMargins = GetPriorDayMargin();
        ArrayList<NetPosition> positions =GetPositions(securities);

        CreditLogic cl = new CreditLogic(securities,settlPrices,config,priorMargins);


        //1- First we get the used credit for the positions in GetPositions
        double usedCredit = cl.GetUsedCredit(_FIRMID,positions);

        System.out.print(String.format("Credit Used: %.2f\n",usedCredit));

        //2- Then we create N orders in GetOrders. These orders are supposed to be already at the market
        ArrayList<Order> orders = GetOrders(securities);

        double buyExposure =cl.GetTotalSideExposure(Order._SIDE_BUY,_FIRMID,positions,orders);
        double sellExposure =cl.GetTotalSideExposure(Order._SIDE_SELL,_FIRMID,positions,orders);

        System.out.print(String.format("Buy Exposure: %.2f \n",buyExposure));
        System.out.print(String.format("Sell Exposure: %.2f \n",sellExposure));


        //3- Then we create an order that is just about to be sen to the exchange
        Order potOrder = GetPotentialOrder(securities);
        double exposureChange =cl.GetExposureChange(potOrder.getCSide(),potOrder.getQty(),potOrder.getSymbol(),_FIRMID,positions,orders);

        System.out.print(String.format("Exposure Change: %.2f \n",exposureChange));

        System.in.read();


    }
}
