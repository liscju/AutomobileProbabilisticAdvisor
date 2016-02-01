package pl.edu.agh;

public class CarMatchingResult {
    private final String name;
    private final double buyValue;
    private final double notBuyValue;

    public CarMatchingResult(String name, double buyValue, double notBuyValue) {
        this.name = name;
        this.buyValue = buyValue;
        this.notBuyValue = notBuyValue;
    }

    public String getName() {
        return name;
    }

    public double getBuyValue() {
        return buyValue;
    }

    public double getNotBuyValue() {
        return notBuyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarMatchingResult that = (CarMatchingResult) o;

        if (Double.compare(that.buyValue, buyValue) != 0) return false;
        if (Double.compare(that.notBuyValue, notBuyValue) != 0) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits(buyValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(notBuyValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CarMatchingResult{" +
                "name='" + name + '\'' +
                ", buyValue=" + buyValue +
                ", notBuyValue=" + notBuyValue +
                '}';
    }
}
