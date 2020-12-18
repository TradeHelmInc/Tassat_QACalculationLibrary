package TDPCalculator.logicLayer.credit;
import TDPCalculator.businessEntities.DailySettlementPrice;
import TDPCalculator.businessEntities.NetPosition;
import TDPCalculator.businessEntities.SecurityMasterRecord;
import TDPCalculator.businessEntities.Trade;

import java.util.ArrayList;
import java.util.Optional;

public class PositionsLogic extends BaseCreditLogic{

    ///Region Protected Attributes

    private ArrayList<NetPosition> PrevDayPositions;
    public final ArrayList<NetPosition> getPrevDayPositions()
    {
        return PrevDayPositions;
    }
    protected final void setPrevDayPositions( ArrayList<NetPosition> value)
    {
        PrevDayPositions = value;
    }

    ///endregion

    ///region Constructors

    public PositionsLogic(ArrayList<SecurityMasterRecord> pSecurityMasterRecords, ArrayList<DailySettlementPrice> pDailySettlementPrices,
                          ArrayList<NetPosition> pPrevDayPositions )
    {
        setSecurityMasterRecords(pSecurityMasterRecords);
        setDailySettlementPrices(pDailySettlementPrices);
        setPrevDayPositions(pPrevDayPositions);
    }

    ///endregion

    ///region Private Methods

    private Optional<NetPosition> GetPreviousDayPosition(String maturityCode)
    {

        return PrevDayPositions.stream().filter((x) -> x.getSymbol().endsWith(maturityCode)).map(Optional::ofNullable).findFirst().orElse(null);
    }

    ///endregion

    ///region Public Methods

    public Double CalculateIncrementalProfitsAndLosses(String maturityCode,Double currentPrice,ArrayList<Trade> todayTrades) throws Exception {

        Optional<SecurityMasterRecord> security = GetSecurityMasterRecord(maturityCode);

        if(!security.isPresent())
            throw new Exception(String.format("Unknown maturity code: %s \n",maturityCode));

        Optional<DailySettlementPrice> DSP = getDailySettlementPrices().stream().filter((x) -> x.getSymbol().equals(security.get().getSymbol()))
                                                                        .map(Optional::ofNullable).findFirst().orElse(null);

        if(!DSP.isPresent())
            throw new Exception(String.format("Unknown DSP for security : %s \n",security.get().getSymbol()));

        Optional<TDPCalculator.businessEntities.NetPosition> prevNetPos  = GetPreviousDayPosition(maturityCode);


        NetPosition prevPos =null;
        if (prevNetPos.isPresent())
            prevPos= prevNetPos.get();
        else
            prevPos=new NetPosition(null,security.get(),0);


        if (todayTrades == null || currentPrice==null)
        {
            return null;
        }

        double prevPAndL = prevPos.getNetContracts() > 0 ? prevPos.getNetContracts() * (currentPrice - DSP.get().getPrice())
                                                         : prevPos.getNetContracts() * (DSP.get().getPrice() - currentPrice);

        for (Trade trade : todayTrades)
        {
            double currPAndL = trade.getSide() == Trade._SIDE_BUY ? trade.getExecutionSize() * (currentPrice - trade.getExecutionPrice())
                                                                  : trade.getExecutionSize()  * (trade.getExecutionPrice() - currentPrice);

            prevPAndL += currPAndL;

        }
        return  Math.round(prevPAndL*100)/100.0;
    }

    ///endregion
}
