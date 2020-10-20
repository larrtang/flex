#!/usr/bin/python3
import QuantLib as ql
import matplotlib.pyplot as plt
from datetime import date, timedelta
from AlphaVantage import getDailyData, parseData




def getMaturityDate(calculation_date : date, days_to_expire : int):
    maturity_date = calculation_date + timedelta(days = days_to_expire)
    print("maturity_date:", (maturity_date.day, maturity_date.month, maturity_date.year))
    return (maturity_date.day, maturity_date.month, maturity_date.year)
    
def getImplVolatility(vix=True):
    return

def getTheoOptionsPrice(calc_date, spot_price, strike_price,contract_type,days_to_expire, vix):
    # option data
    #spot_price = 2970.27
    #strike_price = 2900
    volatility = vix # the historical vols for a year
    dividend_rate =  0.0
    if contract_type == 'c':
        option_type = ql.Option.Call
    else:
        option_type = ql.Option.Put

    risk_free_rate = 0.000
    day_count = ql.Actual365Fixed()
    calendar = ql.UnitedStates()

    calculation_date = ql.Date(calc_date.day, calc_date.month, calc_date.year)
    (d,m,y) = getMaturityDate(calc_date, days_to_expire)
    maturity_date = ql.Date(d, m, y)
    ql.Settings.instance().evaluationDate = calculation_date
    settlement = calculation_date
    # construct the European Option
    payoff = ql.PlainVanillaPayoff(option_type, strike_price)
    exercise = ql.EuropeanExercise(maturity_date)
    european_option = ql.VanillaOption(payoff, exercise)


    am_exercise = ql.AmericanExercise(settlement, maturity_date)
    american_option = ql.VanillaOption(payoff, am_exercise)

    spot_handle = ql.QuoteHandle(
        ql.SimpleQuote(spot_price)
    )
    flat_ts = ql.YieldTermStructureHandle(
        ql.FlatForward(calculation_date, risk_free_rate, day_count)
    )
    dividend_yield = ql.YieldTermStructureHandle(
        ql.FlatForward(calculation_date, dividend_rate, day_count)
    )
    flat_vol_ts = ql.BlackVolTermStructureHandle(
        ql.BlackConstantVol(calculation_date, calendar, volatility, day_count)
    )
    bsm_process = ql.BlackScholesMertonProcess(spot_handle, 
                                            dividend_yield, 
                                            flat_ts, 
                                            flat_vol_ts)


    european_option.setPricingEngine(ql.AnalyticEuropeanEngine(bsm_process))
    bs_price = european_option.NPV()
    print ("The theoretical price is ", bs_price)
    steps = 200
    binomial_engine = ql.BinomialVanillaEngine(bsm_process, "crr", steps)
    american_option.setPricingEngine(binomial_engine)
    print (american_option.NPV())

    return bs_price
    
    
    



if __name__ == "__main__":
    
    og = False
    if not og:
        timestamp, data = getDailyData('SPX')
        vix_timestamp, vix_data = getDailyData('VIX')
        mktData = parseData(data)
        vix_mktData = parseData(vix_data)

        c = mktData.c
        vix = vix_mktData.c
        print(len(c))

        #[year, month, date]
        i = -1
        datelist = timestamp[i].split('-')
        d = date(int(datelist[0]), int(datelist[1]), int(datelist[2]))
        print(d)
        print('spot_price:', c[i])
        print(vix[i]*0.01)
        price = getTheoOptionsPrice(calc_date=d, spot_price=c[i], strike_price=3000, contract_type='c', days_to_expire=17, vix=vix[i]*0.01)
        print(price)
    else:
        # option data
        maturity_date = ql.Date(28, 10, 2019)
        spot_price = 2970.27
        strike_price = 3000
        volatility = 0.16 # the historical vols for a year
        dividend_rate =  0.0
        option_type = ql.Option.Put

        risk_free_rate = 0.000
        day_count = ql.Actual365Fixed()
        calendar = ql.UnitedStates()

        calculation_date = ql.Date(11, 10, 2019)
        ql.Settings.instance().evaluationDate = calculation_date
        settlement = calculation_date
        # construct the European Option
        payoff = ql.PlainVanillaPayoff(option_type, strike_price)
        exercise = ql.EuropeanExercise(maturity_date)
        european_option = ql.VanillaOption(payoff, exercise)


        am_exercise = ql.AmericanExercise(settlement, maturity_date)
        american_option = ql.VanillaOption(payoff, am_exercise)

        spot_handle = ql.QuoteHandle(
            ql.SimpleQuote(spot_price)
        )
        flat_ts = ql.YieldTermStructureHandle(
            ql.FlatForward(calculation_date, risk_free_rate, day_count)
        )
        dividend_yield = ql.YieldTermStructureHandle(
            ql.FlatForward(calculation_date, dividend_rate, day_count)
        )
        flat_vol_ts = ql.BlackVolTermStructureHandle(
            ql.BlackConstantVol(calculation_date, calendar, volatility, day_count)
        )
        bsm_process = ql.BlackScholesMertonProcess(spot_handle, 
                                                dividend_yield, 
                                                flat_ts, 
                                                flat_vol_ts)


        european_option.setPricingEngine(ql.AnalyticEuropeanEngine(bsm_process))
        bs_price = european_option.NPV()
        print ("The theoretical price is ", bs_price)
        steps = 200
        binomial_engine = ql.BinomialVanillaEngine(bsm_process, "crr", steps)
        american_option.setPricingEngine(binomial_engine)
        print (american_option.NPV())

        def binomial_price(bsm_process, steps):
            binomial_engine = ql.BinomialVanillaEngine(bsm_process, "crr", steps)
            european_option.setPricingEngine(binomial_engine)
            return european_option.NPV()

        steps = range(2, 100, 1)
        prices = [binomial_price(bsm_process, step) for step in steps]


        plt.plot(steps, prices, label="Binomial Tree Price", lw=2, alpha=0.6)
        plt.plot([0,100],[bs_price, bs_price], "r--", label="BSM Price", lw=2, alpha=0.6)
        plt.xlabel("Steps")
        plt.ylabel("Price")
        plt.title("Binomial Tree Price For Varying Steps")
        plt.legend()
        plt.show()
