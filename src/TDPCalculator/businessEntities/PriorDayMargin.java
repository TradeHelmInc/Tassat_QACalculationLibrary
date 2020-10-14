package TDPCalculator.businessEntities;


public class PriorDayMargin
{
    //Constructors
    public PriorDayMargin(String  firmId,double margin)
    {
        FirmId=firmId;
        Margin=margin;
    }
    //endregion

    //region Public Attributes

    private String FirmId;
    public final String getFirmId()
    {
        return FirmId;
    }
    public final void setFirmId(String value)
    {
        FirmId = value;
    }

    private double Margin;
    public final double getMargin()
    {
        return Margin;
    }
    public final void setMargin(double value)
    {
        Margin = value;
    }

    //endregion
}