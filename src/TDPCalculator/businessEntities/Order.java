package TDPCalculator.businessEntities;


import javax.crypto.Cipher;

public class Order {

    //Constructors
    public Order(String pSymbol,char pSide, double pCumQty,double pLeavesQty,char pStatus)
    {
        Symbol=pSymbol;
        cSide=pSide;
        CumQty=pCumQty;
        LvsQty=pLeavesQty;
        cStatus=pStatus;
    }

    public Order(String pSymbol,char pSide, double pQty)
    {
        Symbol=pSymbol;
        cSide=pSide;
        CumQty=0;
        LvsQty=pQty;
        cStatus=Order._STATUS_OPEN;
    }
    //endregion


    ///region Public static Consts

    public static char _STATUS_OPEN = 'O';

    public static char _STATUS_REJECTED = 'R';

    public static char _STATUS_CANCELED = 'C';

    public static char _STATUS_FILLED = 'F';

    //public static char _STATUS_PARTIALLY_FILLED = 'P';

    public static char _STATUS_EXPIRED = 'E';

    public static char _SIDE_BUY = 'B';

    public static char _SIDE_SELL = 'S';

    public static char _TIMEINFORCE_DAY = '0';

    public static char _ORDER_TYPE_LIMIT = '1';

    //endregion

    //region Public Attributes

    private String Symbol;
    public final String getSymbol()
    {
        return Symbol;
    }
    public final void setSymbol(String value)
    {
        Symbol = value;
    }

    private char cSide;
    public final char getCSide()
    {
        return cSide;
    }
    public final void setCSide(char value)
    {
        cSide = value;
    }

    private double CumQty;
    public final double getCumQty()
    {
        return CumQty;
    }
    public final void setCumQty(double value)
    {
        CumQty = value;
    }

    private double LvsQty;
    public final double getLvsQty()
    {
        return LvsQty;
    }
    public final void setLvsQty(double value)
    {
        LvsQty = value;
    }

    private char cStatus;
    public final char getCStatus()
    {
        return cStatus;
    }
    public final void setCStatus(char value)
    {
        cStatus = value;
    }

    public final double getQty()
    {
        return LvsQty+ CumQty;
    }
    //endregion

}
