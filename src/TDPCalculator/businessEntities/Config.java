package TDPCalculator.businessEntities;


public class Config
{

    //Constructors
    public Config(double  marginPct,double oneWideCalDisc,double twoWideCalDisc,double threeWideCalDisc)
    {
        MarginPct=marginPct;
        OneWideCalDisc=oneWideCalDisc;
        TwoWideCalDisc=twoWideCalDisc;
        ThreeWideCalDisc=threeWideCalDisc;
        ImplementCalendarMarginDiscount=true;
    }
    //endregion

    //region Public Attributes

    private double MarginPct;
    public final double getMarginPct()
    {
        return MarginPct;
    }
    public final void setMarginPct(double value)
    {
        MarginPct = value;
    }

    private double OneWideCalDisc;
    public final double getOneWideCalDisc()
    {
        return OneWideCalDisc;
    }
    public final void setOneWideCalDisc(double value)
    {
        OneWideCalDisc = value;
    }

    private double TwoWideCalDisc;
    public final double getTwoWideCalDisc()
    {
        return TwoWideCalDisc;
    }
    public final void setTwoWideCalDisc(double value)
    {
        TwoWideCalDisc = value;
    }

    private double ThreeWideCalDisc;
    public final double getThreeWideCalDisc()
    {
        return ThreeWideCalDisc;
    }
    public final void setThreeWideCalDisc(double value)
    {
        ThreeWideCalDisc = value;
    }

    private boolean ImplementCalendarMarginDiscount;
    public final boolean getImplementCalendarMarginDiscount()
    {
        return ImplementCalendarMarginDiscount;
    }
    public final void setImplementCalendarMarginDiscount(boolean value)
    {
        ImplementCalendarMarginDiscount = value;
    }

    //endregion


}