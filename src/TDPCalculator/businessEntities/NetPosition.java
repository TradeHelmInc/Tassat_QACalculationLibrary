package TDPCalculator.businessEntities;


import java.text.ParseException;
import java.util.Date;

public class NetPosition
{

    //Consturctors
    public NetPosition(String pFirmId,SecurityMasterRecord pSecurity,double pNetContracts) throws ParseException {
        FirmId=pFirmId;
        Symbol=pSecurity.getSymbol();
        MaturityDate=pSecurity.GetMaturityDate();
        NetContracts=pNetContracts;

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


    private String Symbol;
    public final String getSymbol()
    {
        return Symbol;
    }
    public final void setSymbol(String value)
    {
        Symbol = value;
    }

    private Date MaturityDate = null;
    public final Date getMaturityDate()
    {
        return MaturityDate;
    }
    public final void setMaturityDate(Date value)
    {
        MaturityDate = value;
    }

    private double NetContracts;
    public final double getNetContracts()
    {
        return NetContracts;
    }
    public final void setNetContracts(double value)
    {
        NetContracts = value;
    }

    public final void updateNetContracts(double value)
    {
        NetContracts -= value;
    }

    //endregion


}