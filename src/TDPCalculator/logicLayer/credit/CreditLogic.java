package TDPCalculator.logicLayer.credit;

import TDPCalculator.businessEntities.*;
import TDPCalculator.common.enums.MessageType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class CreditLogic extends BaseCreditLogic{


    //Region Constructors
    public CreditLogic(ArrayList<SecurityMasterRecord> pSecurityMasterRecords,ArrayList<DailySettlementPrice> pDailySettlementPrices,
                       Config pConfig,ArrayList<PriorDayMargin> pPriorDayMargins)
    {
        setSecurityMasterRecords(pSecurityMasterRecords);
        setDailySettlementPrices(pDailySettlementPrices);
        setConfig(pConfig);
        setPriorDayMargins(pPriorDayMargins);

    }

    //region Public Methods

    public final double GetExposureChange(char side, double qty, String symbol, String firmId, ArrayList<NetPosition> Positions, ArrayList<Order> Orders)
    {
        double finalExposure = 0;
        double currentExposure = 0;
        double netContracts;
        qty = (Order._SIDE_BUY == side) ? qty : -1 * qty;


        netContracts = Positions.stream().filter((x) -> symbol.equals(x.getSymbol())).collect(Collectors.toList()).stream().mapToDouble(NetPosition::getNetContracts).sum();

        //open orders too
        netContracts += Orders.stream().filter((x) -> x.getCSide() == side && x.getCStatus() == Order._STATUS_OPEN && symbol.equals(x.getSymbol()))
                .collect(Collectors.toList()).stream().mapToDouble(x -> (x.getCSide() == Order._SIDE_BUY) ? x.getLvsQty() : (-1 * x.getLvsQty())).sum();


        Optional<DailySettlementPrice> DSP = getDailySettlementPrices().stream().filter((x) -> symbol.equals( x.getSymbol())).map(Optional::ofNullable).findFirst().orElse(null);

        if (DSP.isPresent() && DSP.get().getPrice()!=null) {
            currentExposure = (Math.abs(netContracts) * DSP.get().getPrice());
            finalExposure = (Math.abs(netContracts + qty) * DSP.get().getPrice());
        }
        return finalExposure - currentExposure;
    }

    //endregion

    public final double GetTotalSideExposure(char side, String firmId, ArrayList<NetPosition> Positions, ArrayList<Order> Orders)
    {
        //1-Get Base Margin
        double BM = GetBaseMargin(firmId,Positions) - GetFundedMargin(firmId);

        //2-Calculate the exposure for the Buy/Sell orders
        double PxM = GetPotentialxMargin(side, firmId,Positions,Orders);

        //3- Calculate the potential x Exposure
        //double exposure = Math.Max(Convert.ToDouble(PxM - BM), 0) / Config.MarginPct;
        double exposure = (double)(PxM - BM) / getConfig().getMarginPct();
        DoLog(String.format("Side %1$s exposure:%2$s", side, exposure), MessageType.Information);
        return (double)(PxM - BM) / getConfig().getMarginPct();

    }


    public final double GetUsedCredit(String firmId, ArrayList<NetPosition> Positions) throws Exception {

        double creditUsed = 0;


        for (SecurityMasterRecord security : getSecurityMasterRecords())
        {
            double netContracts = 0;

            netContracts += Positions.stream().filter((x) -> x.getSymbol().equals(security.getSymbol()))
                                                .collect(Collectors.toList()).stream()
                                                .mapToDouble(NetPosition::getNetContracts).sum();


            Optional<DailySettlementPrice> DSP = getDailySettlementPrices().stream().filter((x) -> security.getSymbol().equals(x.getSymbol()))
                                                                            .map(Optional::ofNullable).findFirst().orElse(null);


            if (DSP.isPresent() && DSP.get().getPrice() != null)
            {
                creditUsed += Math.abs(netContracts) * DSP.get().getPrice();
            }

            DoLog(String.format("Final Net Contracts for Security Id %1$s:%2$s", security.getSymbol(), netContracts), MessageType.Information);
        }

        if (getConfig().getImplementCalendarMarginDiscount())
        {
            String[] assetClasses = GetAvailableAssetClasses();

            for (String assetClass : assetClasses)
            {
                creditUsed -= (CalculateCalendarMarginDiscounts(Positions, assetClass) / getConfig().getMarginPct());
            }
        }

        return creditUsed - GetPriorDayCredit(firmId);
    }

}
