package TDPCalculator.businessEntities;


public class DailySettlementPrice {

    //Constructors
    public DailySettlementPrice(String  symbol,double price)
    {
        Symbol=symbol;
        Price=price;
    }
    //endregion


    //region Public Attributes

    private String Symbol;
    public final String getSymbol()
    {
        return Symbol;
    }


    private Double  Price ;
    public final Double  getPrice()
    {
        return Price;
    }
    public final void setPrice(Double  value)
    {
        Price = value;
    }


    //endregion



}
