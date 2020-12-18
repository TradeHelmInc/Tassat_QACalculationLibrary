package TDPCalculator.businessEntities;

import java.text.ParseException;
import java.util.Date;

public class Trade {

    ///region Public static Consts

    public static char _SIDE_BUY = 'B';

    public static char _SIDE_SELL = 'S';

    //endregion

    ///region Constructor

    public Trade(Date pDate,String pSymbol,double pExecutionPrice,double pExecutionSize,char pSide) throws ParseException {
        Date=pDate;
        Symbol=pSymbol;
        ExecutionPrice=pExecutionPrice;
        ExecutionSize=pExecutionSize;
        Side=pSide;
    }

    ///endregion



    //region Public Attributes

    private Date Date;
    public final Date getDate()
    {
        return Date;
    }
    public final void setDate(Date value)
    {
        Date = value;
    }

    private String Symbol;
    public final String geSymbol()
    {
        return Symbol;
    }
    public final void setSymbol(String value)
    {
        Symbol = value;
    }

    private double ExecutionPrice;
    public final double getExecutionPrice()
    {
        return ExecutionPrice;
    }
    public final void setExecutionPrice(double value)
    {
        ExecutionPrice = value;
    }

    private double ExecutionSize;
    public final double getExecutionSize()
    {
        return ExecutionSize;
    }
    public final void setExecutionSize(double value)
    {
        ExecutionSize = value;
    }

    private char Side;
    public final char getSide()
    {
        return Side;
    }
    public final void setSide(char value)
    {
        Side = value;
    }


    ///endregion
}
