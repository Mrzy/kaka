
package cn.zmdx.kaka.locker.content.view;

public class O2oMarketItemInfo {

    private int marketIcon;

    private String marketCoupon;

    private String marketCouponEffectiveDate;

    private String marketCouponNum;

    private boolean isEffective;

    public String getMarketCouponNum() {
        return marketCouponNum;
    }

    public void setMarketCouponNum(String marketCouponNum) {
        this.marketCouponNum = marketCouponNum;
    }

    public boolean isEffective() {
        return isEffective;
    }

    public void setEffective(boolean isEffective) {
        this.isEffective = isEffective;
    }

    public int getMarketIcon() {
        return marketIcon;
    }

    public void setMarketIcon(int marketIcon) {
        this.marketIcon = marketIcon;
    }

    public String getMarketCoupon() {
        return marketCoupon;
    }

    public void setMarketCoupon(String marketCoupon) {
        this.marketCoupon = marketCoupon;
    }

    public String getMarketCouponEffectiveDate() {
        return marketCouponEffectiveDate;
    }

    public void setMarketCouponEffectiveDate(String marketCouponEffectiveDate) {
        this.marketCouponEffectiveDate = marketCouponEffectiveDate;
    }

}
