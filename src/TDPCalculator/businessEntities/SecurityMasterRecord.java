package TDPCalculator.businessEntities;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SecurityMasterRecord
{

    //Consturctors
    public SecurityMasterRecord(String symbol,String maturityDate)
    {
        Symbol=symbol;
        MaturityDate=maturityDate;

    }
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

    private String AssetClass;
    public final String getAssetClass()
    {
        return AssetClass;
    }
    public final void setAssetClass(String value)
    {
        AssetClass = value;
    }

    private String MaturityDate=null;
    public final String getMaturityDate()
    {
        return MaturityDate;
    }
    public final void setMaturityDate(String value)
    {
        MaturityDate = value;
    }

    public final Date GetMaturityDate() throws ParseException {
        Date date = null;

        if (this.MaturityDate != null)
        {
            if (this.MaturityDate.length() == 10)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                date = sdf.parse(this.MaturityDate);
            }
        }
        return date;

    }





    //endregion

}