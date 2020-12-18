import TDPCalculator.businessEntities.DailySettlementPrice;
import TDPCalculator.businessEntities.NetPosition;
import TDPCalculator.businessEntities.SecurityMasterRecord;
import TDPCalculator.businessEntities.Trade;
import TDPCalculator.logicLayer.credit.PositionsLogic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PositionsPOC {
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

    public static ArrayList<DailySettlementPrice> GetSettlementPrices( ArrayList<SecurityMasterRecord> securities)
    {
        ArrayList<DailySettlementPrice> DSPs= new ArrayList<>();
        DailySettlementPrice dsp1 = new DailySettlementPrice(securities.get(0).getSymbol(),15161.48);
        DailySettlementPrice dsp2 = new DailySettlementPrice(securities.get(0).getSymbol(),13365.87);
        DailySettlementPrice dsp3 = new DailySettlementPrice(securities.get(0).getSymbol(),13881.62);
        DailySettlementPrice dsp4 = new DailySettlementPrice(securities.get(0).getSymbol(),14884.81);

        DSPs.add(dsp1);
        DSPs.add(dsp2);
        DSPs.add(dsp3);
        DSPs.add(dsp4);

        return DSPs;
    }

    public static ArrayList<NetPosition> GetPrevDayPositions(ArrayList<SecurityMasterRecord> securities) throws ParseException {


        NetPosition pos1 = new NetPosition(_FIRMID, securities.get(0),8790);
        NetPosition pos2 = new NetPosition(_FIRMID, securities.get(1),5);
        NetPosition pos3 = new NetPosition(_FIRMID, securities.get(2),-100);
        NetPosition pos4 = new NetPosition(_FIRMID, securities.get(3),-10);

        ArrayList<NetPosition> firmPositions = new ArrayList<>();
        firmPositions.add(pos1);
        firmPositions.add(pos2);
        firmPositions.add(pos3);
        firmPositions.add(pos4);

        return firmPositions;

    }

    public static final Date GetDate(String date) throws ParseException {
        Date dDate = null;

        if (date != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            dDate = sdf.parse(date);
        }
        return dDate;

    }


    public static ArrayList<Trade> GetTodayTrades(SecurityMasterRecord security) throws ParseException {

        ArrayList<Trade> trades = new ArrayList<>();

        Trade trade1 = new Trade(GetDate("2020-12-01"),security.getSymbol(),7200,10,Trade._SIDE_BUY);

        trades.add(trade1);

        return trades;
    }

    public static void main(String[] args) throws Exception {

        ArrayList<SecurityMasterRecord> securities = GetSecurityMasterRecords();
        ArrayList<DailySettlementPrice> settlPrices = GetSettlementPrices(securities);
        ArrayList<NetPosition> prevDayPositions = GetPrevDayPositions(securities);
        ArrayList<Trade> todayTrades = GetTodayTrades(securities.get(0));


        PositionsLogic pl = new PositionsLogic(securities,settlPrices,prevDayPositions);

        double pandL = pl.CalculateIncrementalProfitsAndLosses("F21",new Double(7200),todayTrades);


        //1- First we get the used credit for the positions in GetPositions

        System.out.print(String.format("Profits and Losses: %.2f\n",pandL));

        System.in.read();


    }
}
