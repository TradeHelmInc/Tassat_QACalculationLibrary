
package TDPCalculator.logicLayer.credit;

import TDPCalculator.businessEntities.*;
import TDPCalculator.common.enums.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;


public class BaseCreditLogic {

    //region Protected Attributes

    /*
    private ILogSource Logger;
    public final ILogSource getLogger()
    {
        return Logger;
    }
    public final void setLogger(ILogSource value)
    {
        Logger = value;
    }

     */

    private ArrayList<SecurityMasterRecord> SecurityMasterRecords;
    public final ArrayList<SecurityMasterRecord> getSecurityMasterRecords()
    {
        return SecurityMasterRecords;
    }
    protected final void setSecurityMasterRecords( ArrayList<SecurityMasterRecord> value)
    {
        SecurityMasterRecords = value;
    }

    public Optional<SecurityMasterRecord> GetSecurityMasterRecord(String maturityCode)
    {
        return SecurityMasterRecords.stream().filter((x) -> x.getSymbol().endsWith(maturityCode)).map(Optional::ofNullable).findFirst().orElse(null);
    }



    private Config Config;
    protected final Config getConfig()
    {
        return Config;
    }
    protected final void setConfig(Config value)
    {
        Config = value;
    }

    private ArrayList<DailySettlementPrice> DailySettlementPrices;
    protected final ArrayList<DailySettlementPrice> getDailySettlementPrices()
    {
        return DailySettlementPrices;
    }
    protected final void setDailySettlementPrices(ArrayList<DailySettlementPrice> value)
    {
        DailySettlementPrices = value;
    }

    private ArrayList<PriorDayMargin> PriorDayMargins;
    protected final ArrayList<PriorDayMargin> getPriorDayMargins()
    {
        return PriorDayMargins;
    }
    protected final void setPriorDayMargins(ArrayList<PriorDayMargin> value)
    {
        PriorDayMargins = value;
    }


    //endregion

    //region Protected Methods

    protected final void DoLog(String msg, MessageType type)
    {
        //Logger.Debug(msg, type);
    }

    protected final double GetFundedMargin(String firmId)
    {
        return GetPriorDayCredit(firmId) * Config.getMarginPct();
    }

    protected final double GetPotentialxMargin(char side, String firmId, ArrayList<NetPosition> Positions, ArrayList<Order> Orders)
    {
        double acumMargin = 0;

        for (SecurityMasterRecord security : SecurityMasterRecords)
        {
            double  potentialNetContracts = 0;

            potentialNetContracts += Positions.stream().filter((x) -> x.getSymbol().equals(security.getSymbol()) )
                                    .collect(Collectors.toList()).stream().mapToDouble(NetPosition::getNetContracts).sum();


            potentialNetContracts += Orders.stream().filter(x ->   x.getCSide() == side
                                                                && x.getCStatus() == Order._STATUS_OPEN
                                                                && x.getSymbol().equals(security.getSymbol()))
                                                    .collect(Collectors.toList()).stream()
                                                    .mapToDouble(x -> (x.getCSide() == Order._SIDE_BUY) ? x.getLvsQty() : (-1 * x.getLvsQty()))
                                                    .sum();

            DoLog(String.format("Potential Contracts for Security %1$s  after Orders:%2$s", security.getSymbol(), potentialNetContracts), MessageType.Information);

            Optional<DailySettlementPrice> DSP = DailySettlementPrices.stream().filter((x) -> x.getSymbol().equals(security.getSymbol()))
                                                            .map(Optional::ofNullable).findFirst().orElse(null);

            if (DSP.isPresent() && DSP.get().getPrice() != null)
            {
                acumMargin += Math.abs(potentialNetContracts) * DSP.get().getPrice() * Config.getMarginPct();
            }
        }

        DoLog(String.format("Acum Margin for FirmId %1$s after Orders:%2$s", firmId, acumMargin), MessageType.Information);
        return acumMargin - GetFundedMargin(firmId);
    }

    protected final double GetBaseMargin(String firmId, ArrayList<NetPosition>  Positions)
    {
        double acumMargin = 0;


        for (SecurityMasterRecord security : SecurityMasterRecords)
        {
            double netContracts = 0;

            netContracts += Positions.stream().filter((x) -> x.getSymbol().equals(security.getSymbol()) )
                                     .collect(Collectors.toList()).stream().mapToDouble(NetPosition::getNetContracts).sum();

            Optional<DailySettlementPrice> DSP = DailySettlementPrices.stream().filter((x) -> x.getSymbol().equals(security.getSymbol()))
                                                                               .map(Optional::ofNullable).findFirst().orElse(null);

            if (DSP.isPresent() && DSP.get().getPrice() != null)
            {
                acumMargin += Math.abs(netContracts) * DSP.get().getPrice() * Config.getMarginPct();
            }

            DoLog(String.format("Net Contracts for Security %1$s :%2$s", security.getSymbol(), netContracts), MessageType.Information);

        }

        DoLog(String.format("Base Margin for FirmId %1$s:%2$s", firmId, acumMargin), MessageType.Information);

        return acumMargin;
    }

    private double CalculateSpreadDiscount(NetPosition currContract, NetPosition nextContract, int spreadIndex) throws Exception {
        double totalDiscount = 0;
        if ((int)Math.signum(nextContract.getNetContracts()) != (int)Math.signum(currContract.getNetContracts())) //we have a spread
        {

            double netSpread = Math.min(Math.abs(currContract.getNetContracts()), Math.abs(nextContract.getNetContracts()));


            if (netSpread != 0)
            {
                Optional<DailySettlementPrice> DSP1 = DailySettlementPrices.stream().filter( (x) -> x.getSymbol().equals(currContract.getSymbol()))
                                                                                    .map(Optional::ofNullable).findFirst().orElse(null);


                Optional<DailySettlementPrice> DSP2 = DailySettlementPrices.stream().filter( (x) -> x.getSymbol().equals(nextContract.getSymbol()))
                                                                                    .map(Optional::ofNullable).findFirst().orElse(null);

                if(!DSP1.isPresent())
                    throw new Exception(String.format("Missing DSP for symbol %s",currContract.getSymbol() ));

                if(!DSP2.isPresent())
                    throw new Exception(String.format("Missing DSP for symbol %s",nextContract.getSymbol() ));


                if (spreadIndex == 1) //1-wide spread
                {
                    totalDiscount += netSpread * Config.getMarginPct() * Config.getOneWideCalDisc() * (DSP1.get().getPrice() + DSP2.get().getPrice());
                }
                else if (spreadIndex == 2) //2-wide spread
                {
                    totalDiscount += netSpread * Config.getMarginPct() * Config.getTwoWideCalDisc() * (DSP1.get().getPrice() + DSP2.get().getPrice());
                }
                else if (spreadIndex >= 3) //3-wide spread or wider
                {
                    totalDiscount += netSpread * Config.getMarginPct() * Config.getThreeWideCalDisc() * (DSP1.get().getPrice() + DSP2.get().getPrice());
                }
                ;

                currContract.updateNetContracts((currContract.getNetContracts() > 0) ? netSpread : (-1 * netSpread));
                nextContract.updateNetContracts((nextContract.getNetContracts() > 0) ? netSpread : (-1 * netSpread));
            }
        }

        return totalDiscount;
    }

    protected final double CalculateCalendarMarginDiscounts(ArrayList<NetPosition> netPositionsArr, String assetClass) throws Exception {
        double totalDiscount = 0;
        int spreadIndex = 1;


        for (int i = 0; i < netPositionsArr.size(); i++)
        {

            for (int j = 0; j < netPositionsArr.size(); j++)
            {
                NetPosition currContract = (NetPosition) netPositionsArr.stream()
                                                          .sorted(Comparator.comparing(NetPosition::getMaturityDate))
                                                           .toArray()[j];

                if ((j + spreadIndex) < netPositionsArr.size())
                {

                    NetPosition nextContract = (NetPosition) netPositionsArr.stream()
                                                            .sorted(Comparator.comparing(NetPosition::getMaturityDate))
                                                            .toArray()[j + spreadIndex];
                    totalDiscount += CalculateSpreadDiscount(currContract, nextContract, spreadIndex);
                }
            }

            spreadIndex += 1;
        }

        return totalDiscount;
    }

    //endregion

    //region AuxMethods

    protected final String[] GetAvailableAssetClasses()
    {
        ArrayList<String> assetClasses = new ArrayList<String>();

        for (SecurityMasterRecord sec : SecurityMasterRecords)
        {
            assetClasses.add(sec.getAssetClass());
        }

        return assetClasses.toArray(new String[0]);
    }


    //endregion

    //region Public Methods
    public final double GetPriorDayCredit(String firmId)
    {
        Optional<PriorDayMargin> fundedMargin = PriorDayMargins.stream().filter((x) -> firmId.equals(x.getFirmId()))
                                                                        .map(Optional::ofNullable)
                                                                        .findFirst().orElse(null);

        if (fundedMargin.isPresent())
            return fundedMargin.get().getMargin() / Config.getMarginPct();
        else
            return 0;

    }

    //endregion
}
