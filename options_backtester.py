#!/usr/bin/python3
from AlphaVantage import getDailyData, parseData
from mpl_finance import candlestick2_ohlc
import matplotlib.pyplot as plt
from MarketDataWrapper import MarketDataWrapper


def plot_analysis(mktData, losses, cumPnL):
    o = mktData.o
    h = mktData.h
    l = mktData.l
    c = mktData.c 
    v = mktData.v

    fig = plt.figure()

    #PRICE (line of close price)
    ax1 = fig.add_subplot(311)
    ax1.plot(c)
    ax1.grid(True)
    #candlestick2_ohlc(ax1,o,h,l,c, width=2.4, colorup='green', colordown='red')

    # VOLUME
    #ax2 = fig.add_subplot(212)
    #ax2.bar([i for i in range(len(v))], v)


    ax2 = fig.add_subplot(312)
    ax2.plot(losses, color="orange")


    ax2 = fig.add_subplot(313)
    ax2.plot(cumPnL, color="green")
    plt.show()


#some constants for backtesting
MAX_PROFIT = 65
MAX_LOSS = 1000 - MAX_PROFIT
PUT_STRIKE_DISTANCE = 160
CALL_STRIKE_DISTANCE = 160
EXPIRATION = 16



if __name__ == "__main__":
    timestamp, data = getDailyData('SPX')
    vix_timestamp, vix_data = getDailyData('VIX')
    mktData = parseData(data)
    vix_mktData = parseData(data)

    c = mktData.c
    vix = vix_mktData.c
    #c = c[-750:]
    PnL = 0
    numLosses = 0
    numWins = 0
    totalTrades = 0
    losses = [0] * len(c)
    cumPnL = [0] * len(c)


    for i in range(0, len(c), EXPIRATION//2):
        try:
            totalTrades += 1
            if c[i+EXPIRATION] - c[i] > CALL_STRIKE_DISTANCE or c[i + EXPIRATION] - c[i] < -PUT_STRIKE_DISTANCE:
                numLosses += 1
                PnL -= MAX_LOSS
                losses[i:i+EXPIRATION] = [1] * EXPIRATION
                cumPnL[i:i+EXPIRATION] = [PnL] * EXPIRATION
            else:
                numWins += 1
                PnL += MAX_PROFIT
            cumPnL[i:i+EXPIRATION] = [PnL] * EXPIRATION
        except:
            break
        
    percent_losers = numLosses / totalTrades

    print("P and L=", PnL)
    print("percent losers=", percent_losers)
    print("num losers=", numLosses)
    print("num trades=", totalTrades)


    plot_analysis(mktData, losses, cumPnL)